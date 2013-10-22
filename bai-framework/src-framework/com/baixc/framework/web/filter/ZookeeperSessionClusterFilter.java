package com.baixc.framework.web.filter;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.filter.GenericFilterBean;

import com.baixc.framework.web.wrapper.ZookeeperHttpServletRequestWrapper;
import com.baixc.framework.zkclient.ZkClient;

/**
 * Zookeeper集群拦截器
 * @author Linpn
 */
public class ZookeeperSessionClusterFilter extends GenericFilterBean {
	
	private static ZkClient cache;
	private String confPath;
	private String servers;	
	private String filterSuffix;
	private int interval = 7200;
	private float cleanup = 0.1f;
	
	private final String CONFIG_SERVERS = "zookeeper.session.servers";
	private final String CONFIG_REGX = ".*\\$\\{(.+)#(.+)}.*";
	private final String CONTEXT_PATH = this.getClass().getResource("/").toString().replaceAll("WEB-INF/classes/", "");
	private ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();


	@Override
	protected void initFilterBean() throws ServletException {
		try {
			//获取服务器列表
	    	if(servers==null){
				InputStream is = this.getFileResourceAsStream(this.processConfigFile(confPath));  
				Properties props = new Properties();
				props.load(is);
				is.close();
				servers = props.getProperty(CONFIG_SERVERS);
	    	}
	    	
	    	//创建zookeeper客户端
	    	if(cache==null){
	    		cache = new ZkClient(servers);
	    	}
		} catch (Exception e) {
			throw new ServletException("创建ZkClient出错", e);
		} 
	}	


	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest _request = (HttpServletRequest)request;
		String url = _request.getRequestURI();
		
		if(StringUtils.isNotBlank(filterSuffix) && !url.matches(filterSuffix)){
			chain.doFilter(new ZookeeperHttpServletRequestWrapper(_request, cache), response);
			this.cleanupExpiredSessions(cleanup);
		}else{
			chain.doFilter(request, response);
		}
		
	}
	
	
	/**
	 * 有一定的机率清理过期会话
	 */
	protected void cleanupExpiredSessions (float probability){
		if((Math.random() * 100)<probability){
			Thread thread = new Thread(new Runnable(){
				@Override
				public void run() {
					//清理过期session
					List<String> list = cache.getChildren("/");
					for(String jsessionid : list){
						long _lastAccessedTime = cache.readData("/" + jsessionid + "/META_DATA/lastAccessedTime", 0);
						int _interval = cache.readData("/" + jsessionid + "/META_DATA/interval", interval);
						if(_lastAccessedTime + _interval*1000 < System.currentTimeMillis()){
							cache.deleteRecursive("/" + jsessionid);
						}
					}
				}
			});
			thread.start();
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
	 * 过期间隔，秒
	 */
	public void setInterval(int interval) {
		this.interval = interval;
	}

	/**
	 * 清理过期session的机率，如设置为1，则表示每请求100次会有一次清理过期session的机会
	 */
	public void setCleanup(int cleanup) {
		this.cleanup = cleanup;
	}

	
	
	private String processConfigFile(String confPath) throws IOException{
		if(StringUtils.isBlank(confPath))
			throw new IOException("confPath不能为空");
		
		String val = confPath;
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
