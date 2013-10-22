package com.baixc.framework.web.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import net.rubyeye.xmemcached.MemcachedClient;


/**
 * Memcached Session 装饰类
 * @author Linpn
 */
@SuppressWarnings("deprecation")
public class MemcachedHttpSessionWrapper implements HttpSession {
	
	protected MemcachedClient cache;
	
	protected String jsessionid;
	protected ServletContext servletContext;
	protected boolean isNew = false;
	private int interval = 7200;
	
	public final static String SESSION_ROOT = "/SESSION";
	private String SESSION_ID;
	private String SESSION_META_DATA;
	
	

	/**
	 * 创建session
	 */
	public static HttpSession create(MemcachedClient cache, HttpSession session){
		return new MemcachedHttpSessionWrapper(cache, session.getServletContext(), session.getId(), session.getMaxInactiveInterval(), true);
	}
	
	/**
	 * 获取session
	 */
	public static HttpSession get(MemcachedClient cache, ServletContext servletContext, String jsessionid){
		return new MemcachedHttpSessionWrapper(cache, servletContext, jsessionid, 0, false);
	}
	

	/**
	 * 创建session
	 * @param jsessionid SESSION ID
	 * @param cache SESSION 的存在介质
	 * @param servletContext ServletContext 对象 
	 * @param isNew 是否是新创建, 新创建的会设置一些值
	 */
	protected MemcachedHttpSessionWrapper(MemcachedClient cache, ServletContext servletContext, String jsessionid, int interval, boolean isNew) {
		try {
			this.cache = cache;
			
			this.jsessionid = jsessionid;
			this.servletContext = servletContext;
			this.isNew = isNew;
			
			//设置Session Id和Session meta data的 path
			SESSION_ID = SESSION_ROOT + "/" + this.getId();
			SESSION_META_DATA = SESSION_ID + "/META_DATA";

			//设置session过期间隔
			//if(interval!=0){
			//	this.setMaxInactiveInterval(interval);
			//}
			//设置创建时间
			if(isNew){
			    cache.set(SESSION_META_DATA + "/creationTime", this.getMaxInactiveInterval(), System.currentTimeMillis());
			}
			
			//设置最后访问时间
			cache.set(SESSION_META_DATA + "/lastAccessedTime", this.getMaxInactiveInterval(), System.currentTimeMillis());
			
	    } catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	
	@Override
	public String getId() {
		return jsessionid;
	}

	@Override
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public boolean isNew() {
		return this.isNew;
	}	
	
	
	@Override
	public Object getAttribute(String name) {
		try {
			return cache.get(SESSION_ID +"/"+ name);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		try {
			cache.set(SESSION_ID +"/"+ name, this.getMaxInactiveInterval(), value);
			this.setAttributeName(name);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void removeAttribute(String name) {
		try {
			cache.delete(SESSION_ID +"/"+ name);
			this.removeAttributeName(name);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}	
	
	
	private void setAttributeName(String name) {
		List<String> names = Collections.list(this.getAttributeNames());
		
		for(String n : names){
			if(n.equals(name))
				return;
		}
		
		names.add(name);
		this.setAttributeNames(names);	
	}
	
	private void removeAttributeName(String name) {
		List<String> names = Collections.list(this.getAttributeNames());
		int size = names.size();
		
		for(Iterator<String> it=names.iterator(); it.hasNext();){
			if(name.equals(it.next()))
				it.remove();
		}
		
		if(size != names.size())
			this.setAttributeNames(names);		
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> getAttributeNames() {
		Object o = null;
		
		try{
		    o = cache.get(SESSION_META_DATA + "/attributeNames");
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
		
	    if(o==null){
	    	o = new ArrayList<String>();
	    	this.setAttributeNames((List<String>)o);
	    }
	    
	    if(o!=null && o instanceof List){
	    	return Collections.enumeration((List<String>)o);
	    }else{
	    	throw new IllegalStateException("invoke getAttributeNames method was error!");
	    }
	}
	
	private void setAttributeNames(List<String> list){
		try{
			cache.set(SESSION_META_DATA + "/attributeNames", this.getMaxInactiveInterval(), list);
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}
	

	
	@Override
	public long getCreationTime() {
		try{
			Long creationTime = cache.get(SESSION_META_DATA + "/creationTime");
			if(creationTime==null)
				creationTime = 0L;
			
			return creationTime;
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	@Override
	public long getLastAccessedTime() {
		try{
			Long lastAccessedTime = cache.get(SESSION_META_DATA + "/lastAccessedTime");
			if(lastAccessedTime==null)
				lastAccessedTime = 0L;
			
			return lastAccessedTime;
		}catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	@Override
	public int getMaxInactiveInterval() {
		return interval;
	}
	
	@Override
	public void setMaxInactiveInterval(int interval) {
		this.interval = interval;
	}

	@Override
	public void invalidate() {
		Enumeration<String> e = this.getAttributeNames();
		while (e.hasMoreElements()){
			this.removeAttribute(e.nextElement());
		}
	}
	

	
	@Override
	@Deprecated
	public Object getValue(String name) {
		return this.getAttribute(name);
	}
	
	@Override
	@Deprecated
	public String[] getValueNames() {
		List<String> list = Collections.list(this.getAttributeNames());
		return list.toArray(new String[0]);
	}
	
	@Override
	@Deprecated
	public void putValue(String name, Object value) {
		this.setAttribute(name, value);
	}
	
	@Override
	@Deprecated
	public void removeValue(String name) {
		this.removeAttribute(name);
	}
	
	@Override
	@Deprecated
	public HttpSessionContext getSessionContext() {
		return null;
	}
	
}
