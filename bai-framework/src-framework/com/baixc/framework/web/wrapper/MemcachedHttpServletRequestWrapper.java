package com.baixc.framework.web.wrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * Memcached request 装饰类
 * @author Administrator
 *
 */
public class MemcachedHttpServletRequestWrapper extends HttpServletRequestWrapper {

	private MemcachedClient cache;
	
	public MemcachedHttpServletRequestWrapper(HttpServletRequest request, MemcachedClient cache) {
		super(request);
	    this.cache = cache;
	}
	
	
    /**
     * The default behavior of this method is to return getSession()
     * on the wrapped request object.
     */
    public HttpSession getSession() {
    	return this.getSession(true);
    }
    
	
    /**
     * The default behavior of this method is to return getSession(boolean create)
     * on the wrapped request object.
     * 当你设置了<Context sessionCookieDomain=".mydomain.com" sessionCookiePath="/"></Context>(tomcat7) 时，可以实现同系统集群和不同系统集群；
     * 如果不设置，则只能同系统集群。
     */
    public HttpSession getSession(boolean create) {
    	HttpSession session = null;
    	String jsessionid = this.getJsessionid();
    	
    	if(jsessionid==null){
    		//cookie中的jsessionid不存在时，判断服务器端有没有
    		session = super.getSession(false);
    		if(session==null){
    			//如果不存在则创建
    			if(create){
    				session =  MemcachedHttpSessionWrapper.create(cache, super.getSession(create));
    			}
    		}else{
    			//如果存在则直接使用
				session =  MemcachedHttpSessionWrapper.get(cache, this.getServletContext(), session.getId());
    		}
    	}else{
    		//cookie中的jsessionid如果存在，直接使用
			session =  MemcachedHttpSessionWrapper.get(cache, this.getServletContext(), jsessionid);
    	}
		
		return session;
    }
	
    
    /**
     * 从Cookie或url中获取JSESSIONID
     * @return
     */
    private String getJsessionid(){
    	String jsessionid = null;
    	Cookie[] cookies = super.getCookies();
    	if(cookies!=null){
    		for(Cookie cookie : cookies){
    			if(cookie.getName().equals("JSESSIONID")){
    				jsessionid = cookie.getValue();
    				break;
    			}
    		}
    	}
    	
    	return jsessionid;
    }

}
