package com.baixc.framework.web.wrapper;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.apache.zookeeper.CreateMode;

import com.baixc.framework.zkclient.ZkClient;



/**
 * Zookeeper Session 装饰类
 * @author Linpn
 */
@SuppressWarnings("deprecation")
public class ZookeeperHttpSessionWrapper implements HttpSession {
	
	protected ZkClient cache;
	protected ServletContext servletContext;
	protected String jsessionid;
	protected boolean isNew = false;
	
	private String SESSION_ID;
	private String SESSION_META_DATA;
	
	
	/**
	 * 创建session
	 */
	public static HttpSession create(ZkClient cache, HttpSession session){
		return new ZookeeperHttpSessionWrapper(cache, session.getServletContext(), session.getId(), session.getMaxInactiveInterval(), true);
	}
	
	/**
	 * 获取session
	 */
	public static HttpSession get(ZkClient cache, ServletContext servletContext, String jsessionid){
		return new ZookeeperHttpSessionWrapper(cache, servletContext, jsessionid, 0, false);
	}
	

	/**
	 * 创建session
	 * @param jsessionid SESSION ID
	 * @param cache SESSION 的存在介质
	 * @param servletContext ServletContext 对象 
	 * @param isNew 是否是新创建, 新创建的会设置一些值
	 */
	protected ZookeeperHttpSessionWrapper(ZkClient cache, ServletContext servletContext, String jsessionid, int interval, boolean isNew) {
		this.cache = cache;
		this.servletContext = servletContext;			
		this.jsessionid = jsessionid;
		this.isNew = isNew;
		
		//设置Session Id和Session meta data的 path
		SESSION_ID = "/SESSION/" + this.getId();
		SESSION_META_DATA = SESSION_ID + "/META_DATA";
					
		//设置session过期间隔
		if(interval!=0){
			this.setMaxInactiveInterval(interval);
		}
		//设置创建时间
		if(isNew){
		    cache.writeData(SESSION_META_DATA + "/creationTime", System.currentTimeMillis(), CreateMode.PERSISTENT);
		}
		
		//最后访问时间
		cache.writeData(SESSION_META_DATA + "/lastAccessedTime", System.currentTimeMillis(), CreateMode.PERSISTENT);
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
		return cache.readData(SESSION_ID +"/"+ name, true);
	}
	
	@Override
	public void setAttribute(String name, Object value) {
		cache.writeData(SESSION_ID +"/"+ name, value, CreateMode.PERSISTENT);
	}

	@Override
	public void removeAttribute(String name) {
		cache.delete(SESSION_ID +"/"+ name);
	}
	

	@Override
	public Enumeration<String> getAttributeNames() {
	    List<String> list = cache.getChildren(SESSION_ID);
    	return Collections.enumeration((List<String>)list);	
	}
	
	@Override
	public long getCreationTime() {
		return cache.readData(SESSION_META_DATA + "/creationTime", 0);
	}

	@Override
	public long getLastAccessedTime() {
		return cache.readData(SESSION_META_DATA + "/lastAccessedTime", 0);
	}

	@Override
	public int getMaxInactiveInterval() {
		return cache.readData(SESSION_META_DATA + "/interval", 0);
	}
	
	@Override
	public void setMaxInactiveInterval(int interval) {
	    cache.writeData(SESSION_META_DATA + "/interval", interval, CreateMode.PERSISTENT);
	}

	@Override
	public void invalidate() {
		cache.deleteRecursive(SESSION_ID);
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
