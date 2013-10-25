package com.baixc.ucenter.web.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.framework.json.JsonGlobal;
import com.baixc.framework.utils.TreeRecursiveHandle;
import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.framework.web.controller.BaseController;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.service.admin.AdminsService;
import com.baixc.ucenter.service.admin.ModulesService;
import com.baixc.ucenter.service.admin.RolesService;

/**
 * 页面MVC控制层基类，主要编写业务公共方法
 * @author Linpn
 */
public abstract class PageAdmController extends BaseController {
	
	/**
	 *  Spring MVC的DispatcherServlet加载的ApplicationContext上下文 。
	 *  DispatcherServlet加载的Bean是MVC私有的上下文，不能通过注入的方式获取。
	 *  如想要获取系统用户的Action，可用 appMvcContext.getBean("/admin/SysAdmins.jspx")。
	 */
	public ApplicationContext appMvcContext;
	
	/** 系统基础配置，读取app.conf里的信息 */
	@Resource 	
	protected Properties appConf;
	/** 系统用户服务类 */
	@Resource
	protected AdminsService adminsService;	
	/** 用户角色服务类 */
	@Resource
	protected RolesService rolesService;	
	/** 应用模块服务类 */
	@Resource
	protected ModulesService modulesService;

	/** 加密对象 */
	protected final Md5PasswordEncoder md5 = new Md5PasswordEncoder();
	
	
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
		modelview.addObject("admins",this.getCurrUser());
		
		//请求路径
		modelview.addObject("page_context", request.getContextPath());
		modelview.addObject("page_action", request.getContextPath() + request.getServletPath());
		modelview.addObject("page_location", request.getRequestURL()+"?"+request.getQueryString());

		//加载app.conf参数
		for (Entry<Object, Object> entry : appConf.entrySet()) {
			String key = entry.getKey().toString().replaceAll("\\.", "_");
			String value = entry.getValue().toString();
			modelview.addObject(key, value);
		}
		
		//模块信息  
		Admins user = this.getCurrUser();
		if(user!=null){
			Modules module = this.getCurrModule(request);
			if(module!=null){
				if(ModulesService.FUNCTION.equals(module.getType()) || ModulesService.CONSTANT.equals(module.getType())){
					module = modulesService.getModules(module.getModulePid());
				}
				if(ModulesService.MODULE.equals(module.getType())){
					modelview.addObject("module_title", module.getText());		//标题
					modelview.addObject("module_function", this.getFunctionByModuleIdOfJSON(this.getCurrUser(), module.getModuleId()));	//操作功能
					modelview.addObject("module_constant", this.getConstantByModuleIdOfJSON(this.getCurrUser(), module.getModuleId()));	//操作常量
				}
			}
		}
	}
	
	
	/**
	 * 获取当前用户
	 * @return
	 */
	public Admins getCurrUser(){
		try{
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if(authentication==null)
				return null;
			if(authentication.getPrincipal().equals("anonymousUser"))
				return null;
	
			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			return (Admins)userDetails;
			
		}catch(Exception e){
			logger.error("获取当前用户出错", e);
		}
		
		return null;
	}
	
	
	/**
	 * 获取当前系统
	 * @param request request对象
	 */
	public Modules getCurrSystem(HttpServletExtendRequest request) {
		String func = request.getParameter("func");
		String path = request.getContextPath() + request.getServletPath() + (func!=null ? "?func="+func : "");  
		Modules module = modulesService.getModulesByUrl(path);
		return module;
	}
	
	
	/**
	 * 获取当前模块
	 * @param request request对象
	 */
	public Modules getCurrModule(HttpServletExtendRequest request) {
		String func = request.getParameter("func");
		String path = request.getContextPath() + request.getServletPath() + (func!=null ? "?func="+func : "");  
		Modules module = modulesService.getModulesByUrl(path);
		return module;
	}
	

	//------------------------------------- 公共方法 ----------------------------------------------//
	
	/**
	 * 获取当前用户
	 * @param request request对象
	 * @param response response对象
	 * @throws IOException 
	 */
	public void getCurrUser(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			Admins admins = (Admins)this.getCurrUser().clone();
			admins.setPassword("");
			admins.setRoles(Collections.<Roles>emptyList());
			admins.setModules(Collections.<Modules>emptyList());
			
			//输出数据
			out.println("{success: true, data: '"+ JSONObject.fromObject(admins, JsonGlobal.jsonConfig) +" '}");
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			logger.error("获取当前用户出错", e);
		}finally{		
			out.close();
		}
	}
	
	
	/**
	 * 保存当前用户
	 * @param request request对象
	 * @param response response对象
	 * @throws IOException 
	 */
	public void saveCurrUser(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{						
			//获取对象
			Admins user = request.getBindObject(Admins.class, "user");
			
			//修改密码
			boolean checkPassword = request.getBooleanParameter("checkPassword", false, "on");
			if(checkPassword){
				String oldPassword = request.getParameter("oldPassword");
				String password = request.getParameter("password");
				String rePassword = request.getParameter("rePassword");
				
				if(this.getCurrUser().getPassword().equals(md5.encodePassword(oldPassword, user.getUsername()))){
					if(StringUtils.isNotBlank(password) && password.equals(rePassword)){
						user.setPassword(md5.encodePassword(password, user.getUsername()));
					}else{
						out.println("{success: false, msg: '再次输入的密码不正确'}");
						return;
					}
				}else{
					out.println("{success: false, msg: '旧密码不正确'}");
					return;
				}
			}
			
			//保存到数据库
			user.setUsername(null);
			user.setCode(null);
			user.setUpdateTime(new Date());
			adminsService.editAdmins(user);
			
			//更新当前登录的用户
			Admins admins = this.getCurrUser();
			admins.setName(user.getName());
			admins.setSex(user.getSex());
			admins.setMobile(user.getMobile());
			admins.setEmail(user.getEmail());			
			
			//输出数据
			out.println("{success: true}");

		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			logger.error("保存当前用户出错", e);
		}finally{		
			out.close();
		}
	}

	
	/**
	 * 获取首页上有权限的模块列表，并转换成指定的JSON格式
	 * @param user 后台用户对象,如果user为null，则查出所有的模块
	 * @return 返回JSON对象
	 */
	public JSONObject getSystemModulesOfJSON(Admins user, String sysname, String sysurl) {
 		final JSONObject json = new JSONObject();
		
		try {			
			json.put("id", 0);
			json.put("items", new JSONArray());
			json.put("casass", new JSONArray());
			json.put("shopmng", new JSONArray());
			json.put("erpcrm", new JSONArray());
			json.put("search", new JSONArray());
			
			final List<Modules> list;
			if(user==null)
				list = modulesService.getModulesList(null);
			else
				list = modulesService.getModulesListByUser(user, false);
						
			//递归加载
			TreeRecursiveHandle<JSONObject> treeRecursiveHandle = new TreeRecursiveHandle<JSONObject>(){
				public void recursive(JSONObject treeNode) throws Exception{
					for(Modules module : list){
						if(module.getModulePid().equals(treeNode.getInt("id"))){
							//只查系统、模块组、模块
							if(!(ModulesService.SYSTEM.equals(module.getType()) || 
									ModulesService.GROUP.equals(module.getType()) ||
									ModulesService.MODULE.equals(module.getType())) ){
								break;
							}
							
							//获取JSON对象
							JSONObject node = new JSONObject();
							node.put("id", module.getModuleId());
							node.put("title", module.getText());

							if(ModulesService.GROUP.equals(module.getType())){
								node.put("items", new JSONArray());
							}else
							if(ModulesService.MODULE.equals(module.getType())){
								node.put("name", module.getUrl().replaceFirst("^/[^/]+/", ""));  //去除context
								node.put("url", module.getUrl());
								node.put("icon", module.getImage());
								node.put("description", module.getDescription());
								
								//如果是模块，就添加到搜索池里
								JSONObject searchNode = new JSONObject();
								searchNode.put("fullName", node.getString("name"));
								searchNode.put("name", node.getString("title"));
								searchNode.put("url", "#!" + node.getString("url").replaceFirst("/ass/", "/casass/"));  //EXT搜索的URL格式
								searchNode.put("icon", "icon-singleton");
								searchNode.put("sort", 3);
								searchNode.put("meta", new JSONObject());
								json.getJSONArray("search").add(searchNode);
							}
														
							//递归
							this.recursive(node);
							
							//添加到树中
							if(!treeNode.has("items")){
								treeNode.put("items", new JSONArray());
							}
							JSONArray items = treeNode.getJSONArray("items");
							items.add(node);
						}
					}
				}
			};
			treeRecursiveHandle.recursive(json);
			
			//手动整理各个子系统的模块
			JSONArray items = json.getJSONArray("items");
			for(int i=0;i<items.size();i++){
				JSONObject system = items.getJSONObject(i);
				String name = system.getString("title");
				JSONArray sysitems = system.getJSONArray("items");
				
				if("应用支撑系统".equals(name)){
					json.put("casass", sysitems);
				}else
				if("电商管理后台".equals(name)){
					json.put("shopmng", getLocUrlModule("shopmng", sysitems, sysname, sysurl));
				}else
				if("企业资源计划".equals(name)){
					json.put("erpcrm", getLocUrlModule("erpcrm", sysitems, sysname, sysurl));
				}
			}
			
			//删除临时数据
			json.remove("items");

		} catch (Exception e) {
			throw new RpcException("查询模块出错", e);
		}
		
		return json;
	}
	
	
	/**
	 * 获取操作功能列表
	 * @param user 用户
	 * @param moduleId 模块ID
	 * @return 返回操作功能列表
	 */
	public JSONObject getFunctionByModuleIdOfJSON(Admins user, int moduleId) {
		// 操作功能列表
		JSONObject json = new JSONObject();
		
		//模块列表
		List<Modules> list = modulesService.getModulesListByUser(user, false);		
		for(Modules module : list){		
			//获取有权限的操作功能和操作常量
			if(module.getModulePid().equals(moduleId)){
				if(ModulesService.FUNCTION.equals(module.getType())){
					json.put(module.getText(), module.getUrl());
				}
			}
		}

		return json;
	}

	/**
	 * 获取操作常量列表
	 * @param user 用户
	 * @param moduleId 模块ID
	 * @return 返回操作常量列表
	 */
	public JSONObject getConstantByModuleIdOfJSON(Admins user, int moduleId) {
		// 操作常量列表
		JSONObject json = new JSONObject();
		
		//模块列表
		List<Modules> list = modulesService.getModulesListByUser(user, false);		
		for(Modules module : list){		
			//获取有权限的操作功能和操作常量
			if(module.getModulePid().equals(moduleId)){
				if(ModulesService.CONSTANT.equals(module.getType())){
					json.put(module.getText(), module.getUrl());
				}
			}
		}
		
		return json;
	}
	
	
	
	
	
	/**
	 * 如果是本地系统，将模块更改为本地的URL，如果不是，则不做更改
	 * @param items 系统下的模块列表
	 * @param thanSysname 要比对的系统
	 * @param sysname 当前的系统
	 * @param sysurl 当前系统的URL
	 * @return 返回本地系统修改过的URL
	 * @throws Exception
	 */
	private JSONArray getLocUrlModule(String thanSysname, JSONArray items, final String sysname, final String sysurl) throws Exception{
		if(StringUtils.isNotBlank(thanSysname) && StringUtils.isNotBlank(sysname) && StringUtils.isNotBlank(sysurl)){
			if(sysname.equals(thanSysname)){
				TreeRecursiveHandle<JSONArray> trh = new TreeRecursiveHandle<JSONArray>(){
					public void recursive(JSONArray treeNode) throws Exception{
						for(int i=0;i<treeNode.size();i++){
							JSONObject node = treeNode.getJSONObject(i);
							if(node.has("url") && StringUtils.isNotBlank(node.getString("url"))){
								node.put("url", sysurl +"/"+ node.getString("url").replaceFirst("^/[^/]+/", ""));
							}
							if(node.has("items")){
								this.recursive(node.getJSONArray("items"));
							}
						}
					}
				};
				trh.recursive(items);
			}
		}
		return items;
	}
	
}
