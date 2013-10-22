package com.baixc.ucenter.web.controller;

import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.framework.web.controller.BaseController;
import com.baixc.ucenter.model.user.User;

/**
 * 页面MVC控制层基类，主要编写业务公共方法
 * 
 * @author Linpn
 */
public abstract class PageController extends BaseController {
	
	/**
	 *  Spring MVC的DispatcherServlet加载的ApplicationContext上下文 。
	 *  DispatcherServlet加载的Bean是MVC私有的上下文，不能通过注入的方式获取。
	 *  如想要获取系统用户的Action，可用 appMvcContext.getBean("/admin/SysAdmins.jspx")。
	 */
	public ApplicationContext appMvcContext;

	/** 系统基础配置，读取app.conf里的信息 */
	@Resource
	protected Properties appConf;

	/**
	 * 初始化ModelAndView
	 * @param request
	 * @param response
	 * @param modelview
	 */
	@Override
	protected void initModelAndView(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modelview) throws Exception {

		//Spring MVC的DispatcherServlet加载的ApplicationContext上下文
		if(appMvcContext!=null){
			appMvcContext = (WebApplicationContext)request.getServletContext().getAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.spring-mvc");
		}
		
		//登录用户
		modelview.addObject("user",this.getCurrUser());
		
		//请求地址
		modelview.addObject("page_context", request.getContextPath());
		modelview.addObject("page_action", request.getContextPath() + request.getServletPath());
		modelview.addObject("page_location", this.getFullURL(request));

		//加载app.conf参数
		for (Entry<Object, Object> entry : appConf.entrySet()) {
			String key = entry.getKey().toString().replaceAll("\\.", "_");
			String value = entry.getValue().toString();
			modelview.addObject(key, value);
		}
	}

	
	/**
	 * 获取当前用户
	 * @return
	 */
	public User getCurrUser(){
		try{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(authentication==null)
				return null;
			if(authentication.getPrincipal().equals("anonymousUser"))
				return null;
	
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return (User)userDetails;
		}catch(Exception e){
			logger.error("保存当前用户出错", e);
		}
		
		return null;
	}
	

	/**
	 *	获取当前网页的完整URL
	 * @param request HttpServletRequest对象
	 * @return
	 */
	public String getFullURL(HttpServletRequest request){
		String url = request.getAttribute("javax.servlet.forward.request_uri")==null?null:request.getAttribute("javax.servlet.forward.request_uri").toString();
		String appUrl = appConf.getProperty("app.url");
		if(StringUtils.isNotBlank(url)){
			String context = url.replaceAll("(/\\w+)(.*)", "$1");
			String appContext = appUrl.replaceAll("(.+)(/\\w+)(.*)", "$2");
			if(appContext.equals(context)){
				url = appUrl + url.replaceAll("(/\\w+)(.*)", "$2");
			}
		}else{
			url = appUrl + request.getServletPath() + (StringUtils.isBlank(request.getQueryString())?"":"?"+request.getQueryString());
		}
		return url;
	}
}
