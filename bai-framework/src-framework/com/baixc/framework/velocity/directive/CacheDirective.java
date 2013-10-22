package com.baixc.framework.velocity.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import net.rubyeye.xmemcached.MemcachedClient;

import org.apache.oro.util.Cache;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.TemplateInitException;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.directive.Directive;
import org.apache.velocity.runtime.parser.node.Node;


/**
 * 对velocity进行页面缓存
 *  #cache('key', 毫秒) 
 *  	html_body 
 *  #end
 * 其中key为指定的cache key，如果为空，则以宏的使用位置(行号和列号)为key
 * @author linpn
 */
public class CacheDirective extends Directive {

	private static final String VELOCITY_CACHED_KEY = "VELOCITY_CACHED_KEY";
	
	private static final String CACHE_PROVIDER = "direcitive.cache.provider";
	private static final String CACHE_TIME = "direcitive.cache.time";
	
	//TODO: 需要优化
	private MemcachedClient cache;
	private Long time = 1800000L; //默认时间为半小时
	

	/**
	 * 指令的名称
	 */
	@Override
	public String getName() {
		return "cache";
	}

	/**
	 * 指令类型为块指令
	 */
	@Override
	public int getType() {
		return BLOCK;
	}

	@Override
	public void init(RuntimeServices rs, InternalContextAdapter context,
			Node node) throws TemplateInitException {
		super.init(rs, context, node);
		
		//获取缓存提供者
		Object _cache = rs.getProperty(CACHE_PROVIDER);
		if (_cache != null && _cache instanceof Cache){
			cache = (MemcachedClient)_cache;
		}else{
			throw new TemplateInitException(CACHE_PROVIDER + " Cannot find the direcitive.cache.provider, or the direcitive.cache.provider object is incorrect.", "cache", 0, 0);
		}
		
		//获取缓存提供者
		Object _time = rs.getProperty(CACHE_TIME);
		if (_time != null){
			time = Long.valueOf(_time.toString());
		}
	}

	@Override
	public boolean render(InternalContextAdapter context, Writer writer, Node node) 
			throws IOException, ResourceNotFoundException, ParseErrorException, MethodInvocationException {

		//获取参数
		String key = getCacheKey(context, node);
		Long time = getCacheTime(context, node);
		
		//获取缓存数据
		try {
			String value = (String)this.cache.get(key);
			if (value == null) {
				Node bodyNode = getBodyNode(context, node);
				StringWriter sw = new StringWriter();
				bodyNode.render(context, sw);
				value = sw.toString();
				this.cache.set(key, (int)(time/1000), value);
			}
			writer.write(value);
		} catch (Exception e) {
			return false;
		}
		
		return true;
	}

	protected String getCacheKey(InternalContextAdapter context, Node node) {
		//参数个数
		int count = node.jjtGetNumChildren(); 
				
		if (count >= 2) {
			//如果参数有二个,则获取第一个参数当key,当且仅当这个参数是字符串类型
			Object key = node.jjtGetChild(0).value(context);
			if(key instanceof String){
				return VELOCITY_CACHED_KEY +"|"+ key.toString();
			}
		}
		
		//宏的使用位置(行号和列号)为key
		String defaultKey = new StringBuilder(VELOCITY_CACHED_KEY +"|").append(context.getCurrentTemplateName()).append(':').append(node.getLine()).append(':').append(node.getColumn()).toString();
		return defaultKey;
	}
	
	protected Long getCacheTime(InternalContextAdapter context, Node node) {
		//参数个数
		int count = node.jjtGetNumChildren(); 
		
		if (count >= 3) {
			//如果参数有三个,则获取第二个参数当time,当且仅当这个参数是整数类型
			Object time = node.jjtGetChild(1).value(context);
			if(time instanceof Integer){
				return (Long)time;
			}
		}else if(count >= 2) {
			//如果参数有二个,则获取第一个参数当time,当且仅当这个参数是整数类型
			Object time = node.jjtGetChild(0).value(context);
			if(time instanceof Integer || time instanceof Long){
				return Long.valueOf(time.toString());
			}
		}

		return time;
	}

	protected Node getBodyNode(InternalContextAdapter context, Node node) {
		return node.jjtGetChild(node.jjtGetNumChildren() - 1);
	}
}
