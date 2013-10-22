package com.baixc.framework.web.controller;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.framework.web.wrapper.DefaultHttpServletExtendRequest;
import com.baixc.framework.web.wrapper.DefaultHttpServletExtendResponse;


/**
 * 页面MVC控制层基类
 * 
 * 当请求无func参数时,执行execute方法,并返回ModelAndView,当不指定view时,默认找跟url中请求的资源名称相同的view。
 * 
 * 当请求有func参数时,执行func参数的方法，func参数的方法一般有三种形式，如func=getAdmins时，如下：
 * 用于Ajax请求:					public void getAdmins(HttpServletExtendRequest request, HttpServletExtendResponse response);
 * 用于页面请求,返回View的路径:		public String getAdmins(HttpServletExtendRequest request, HttpServletExtendResponse response);
 * 用于页面请求,返回ModelAndView:	public ModelAndView getAdmins(HttpServletExtendRequest request, HttpServletExtendResponse response);
 * 
 * @author Linpn
 */
public abstract class BaseController implements Controller {

	/**
	 * log4j对象
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
	
	/**
	 * 处理DispatcherServlet的请求，并返回ModelAndView对象 
	 */
	public ModelAndView handleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		this.setHTMLHeader(request,response,"utf-8");
		
		//开始业务处理
		String func = request.getParameter("func");
		if(func==null){
			return this.doExecute(new DefaultHttpServletExtendRequest(request), new DefaultHttpServletExtendResponse(response));							
		}else{
			return this.doExecuteFunc(new DefaultHttpServletExtendRequest(request), new DefaultHttpServletExtendResponse(response), func);
		}		
	}
		
	
	/**
	 * 当请求无func参数时,执行execute方法,并返回view页面,不指定view时,默认找跟url中请求的资源名称相同的view。
	 */
	private ModelAndView doExecute(HttpServletExtendRequest request,
			HttpServletExtendResponse response) throws Exception {
		ModelAndView modeview = new ModelAndView();
		this.initModelAndView(request, response, modeview);
		this.execute(request,response,modeview);		
		return modeview;
	}
	
	
	/**
	 * 当请求有func参数时,执行func参数的方法
	 * func参数的方法一般有三种形式，如func=getAdmins时，如下：
	 * 用于Ajax请求:					public void getAdmins(HttpServletExtendRequest request, HttpServletExtendResponse response);
	 * 用于页面请求,返回View的路径:		public String getAdmins(HttpServletExtendRequest request, HttpServletExtendResponse response);
	 * 用于页面请求,返回ModelAndView:	public ModelAndView getAdmins(HttpServletExtendRequest request, HttpServletExtendResponse response);
	 */
	private ModelAndView doExecuteFunc(HttpServletExtendRequest request,
			HttpServletExtendResponse response, String func) throws Exception {
		Method method = this.getClass().getMethod(func, HttpServletExtendRequest.class, HttpServletExtendResponse.class);
		Object result = method.invoke(this, request, response);
		
		if(result instanceof ModelAndView){
			ModelAndView modeview = (ModelAndView)result;
			this.initModelAndView(request, response, modeview);
			return modeview;
		}else if(result instanceof String){
			ModelAndView modeview = new ModelAndView((String)result);
			this.initModelAndView(request, response, modeview);
			return modeview;
		}else{
			return null;
		}
	}
	
	
	/**
	 * 初始化ModelAndView
	 * @param request HttpServletExtendRequest对象
	 * @param response HttpServletExtendResponse对象
	 * @param modeview ModelAndView对象
	 */
	protected void initModelAndView(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) throws Exception {
	}
	
	
	/**
	 * 页面入口方法
	 * @param request HttpServletExtendRequest对象
	 * @param response HttpServletExtendResponse对象
	 * @param modeview ModelAndView对象
	 */
	public abstract void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview);	
		
	
	
	/**
	 * 设置输出XML
	 * @param request HttpServletRequest对象
	 * @param response HttpServletResponse对象
	 * @param encoding 编码,如"utf-8"
	 * @throws UnsupportedEncodingException 
	 */
	public void setXMLHeader(HttpServletRequest request,
			HttpServletResponse response,String encoding) throws UnsupportedEncodingException{
			request.setCharacterEncoding(encoding);
			response.setContentType("text/xml");
			response.setCharacterEncoding(encoding);
			response.setHeader("Cache-Control","no-cache");             
			response.setHeader("Pragma","no-cache"); 
			response.setDateHeader("Expires",0); 
	}
	
	
	/**
	 * 设置输出HTML
	 * @param request HttpServletRequest对象
	 * @param response HttpServletResponse对象
	 * @param encoding 编码,如"utf-8"
	 * @throws UnsupportedEncodingException 
	 */
	public void setHTMLHeader(HttpServletRequest request,
			HttpServletResponse response,String encoding) throws UnsupportedEncodingException{
			request.setCharacterEncoding(encoding);
			response.setContentType("text/html");
			response.setCharacterEncoding(encoding);
			response.setHeader("Cache-Control","no-cache");             
			response.setHeader("Pragma","no-cache"); 
			response.setDateHeader("Expires",0); 
	}
	
}
