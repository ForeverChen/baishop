package com.baixc.framework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import net.rubyeye.xmemcached.utils.AddrUtil;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.baixc.framework.web.wrapper.MemcachedHttpServletRequestWrapper;

/**
 * Memcached集群拦截器
 * @author Linpn
 */
public class MemcachedSessionClusterFilter implements Filter {
	
	private static MemcachedClient cache;
	private String confPath;
	private String servers;
	private String filterSuffix;
	private int connectionPoolSize = 5;

	private final String CONFIG_SERVERS = "memcached.session.servers";
	private final String CONFIG_REGX = ".*\\$\\{(.+)#(.+)}.*";
	private final String CONTEXT_PATH = this.getClass().getResource("/").toString().replaceAll("WEB-INF/classes/", "");
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		try {
			//获取服务器列表
	    	if(servers==null){
				InputStream is = this.getFileResourceAsStream(this.processConfigFile(confPath));  
				Properties props = new Properties();
				props.load(is);
				is.close();
				servers = props.getProperty(CONFIG_SERVERS);
	    	}

	    	//创建memcached客户端
		    if(cache==null){
			    MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(servers));
			    builder.setConnectionPoolSize(connectionPoolSize);
			    builder.setSessionLocator(new KetamaMemcachedSessionLocator());
			    builder.setTranscoder(new SerializingTranscoder());
			    cache = builder.build();
		    }
		} catch (Exception e) {
			throw new ServletException("创建MemcachedClient出错", e);
		} 
	}

	@Override
	public void destroy() {
	}
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest _request = (HttpServletRequest)request;
		String url = _request.getRequestURI();
		
		if(this.filterSuffix!=null && !url.matches(filterSuffix)){
			chain.doFilter(new MemcachedHttpServletRequestWrapper(_request, cache), response);
		}else{
			chain.doFilter(request, response);
		}
	}



	/**
	 * 配置文件
	 */
	public void setConfPath(String confPath) {
		this.confPath = confPath;
	}
	
	/**
	 * 服务器名
	 */
	public void setServers(String servers) {
		this.servers = servers;
	}

	/**
	 * 要过滤的扩展名
	 */
	public void setFilterSuffix(String filterSuffix) {
		this.filterSuffix = filterSuffix.replaceAll("\\s+", "")
				.replaceAll("\\*\\.", ".+\\\\.")
				.replaceAll(",", "\\$|") + "$";
	}

	/**
	 * 连接池
	 */
	public void setConnectionPoolSize(int connectionPoolSize) {
		this.connectionPoolSize = connectionPoolSize;
	}
	
	
	
	private String processConfigFile(String configPath) throws IOException{
		if(StringUtils.isBlank(configPath))
			throw new IOException("configPath不能为空");
		
		String val = configPath;
		if(val.matches(CONFIG_REGX)){
			String filename = val.replaceAll(CONFIG_REGX, "$1");
			String propsKey = val.replaceAll(CONFIG_REGX, "$2");
			
			try {
				InputStream is = this.getFileResourceAsStream(filename);  
				Properties props = new Properties();
				props.load(is);
				is.close();
				
				String propsVal = props.getProperty(propsKey);
				if(StringUtils.isNotBlank(propsVal)){
					val = val.replaceAll("(.*)\\$\\{.+#.+}(.*)", "$1" + propsVal + "$2");
				}
			} catch (IOException e){
				throw e;
			}
		}
		
		return val;
	}
	
	
	private InputStream getFileResourceAsStream(String filename) throws IOException{
		InputStream is = null;
		if(filename.startsWith("file:/") || filename.startsWith("classpath:") || filename.startsWith("classpath*:")){
			is = this.resourcePatternResolver.getResources(filename)[0].getInputStream();
		}else{
			is = this.resourcePatternResolver.getResources(CONTEXT_PATH + filename)[0].getInputStream();
		}
		
		return is;
	}

}
