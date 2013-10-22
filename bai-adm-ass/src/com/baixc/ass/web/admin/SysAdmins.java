package com.baixc.ass.web.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.baixc.framework.json.JsonGlobal;
import com.baixc.framework.utils.ConvertUtils;
import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.web.controller.PageAdmController;

public class SysAdmins extends PageAdmController {
	
	@Override
	public void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) {
	}
	
	
	/**
	 * Ajax请求：获取后台用户列表
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void getAdmins(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String callback=request.getParameter("callback");
		
		try{
			//获取参数
			String sort = request.getParameter("sort");
			String dir = request.getParameter("dir");
			Long start = request.getLongParameter("start");
			Long limit = request.getLongParameter("limit");
			String searchKey = request.getParameter("searchKey", "UTF-8");
			
			//查询参数
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("searchKey", searchKey);
			
			//排序数据
			Map<String,String> sorters = new HashMap<String,String>();
			sorters.put(sort, dir);
			
			//查询数据
			List<Admins> list = adminsService.getAdminsList(params, sorters, start, limit);
			long count = adminsService.getAdminsCount(params);
			
			//组装JSON
			JSONObject json = new JSONObject();
			json.put("count", count);
			json.put("records", new JSONArray());
			
			JSONArray records = json.getJSONArray("records");
			for(Admins admins : list) {
				JSONObject record = JSONObject.fromObject(admins, JsonGlobal.jsonConfig);

				//角色
				JSONArray roleIds = new JSONArray();
				JSONArray roleNames = new JSONArray();
				for(Roles role : admins.getRoles()){
					roleIds.add(role.getRoleId());
					roleNames.add(role.getRoleName());
				}				
				record.put("roleIds", roleIds);
				record.put("roleNames", roleNames);

				//角色模块
				JSONArray roleModuleIds = new JSONArray();
				for(Roles role : admins.getRoles()){
					for(Modules module : role.getModules()){
						roleModuleIds.add(module.getModuleId());
					}
				}
				record.put("roleModuleIds", roleModuleIds);	

				//个人模块
				JSONArray userModuleIds = new JSONArray();
				for(Modules module : admins.getModules()){
					userModuleIds.add(module.getModuleId());
				}				
				record.put("userModuleIds", userModuleIds);	
				
				records.add(record);
			}
			
			//输出数据
			out.println(callback + "("+ json +")");
			
		}catch(Exception e){
			out.println(callback + "({success: false, msg: '"+ e.getMessage() +"'})");
			e.printStackTrace();
		}
		
		out.close();
	}
	
		
	/**
	 * Ajax请求：保存用户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void saveAdmins(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{						
			//创建对象
			Admins user = request.getBindObject(Admins.class, "user");
			int[] roleIds = ConvertUtils.toArrInteger(request.getParameter("roleIds").split(","));
			int[] moduleIds = ConvertUtils.toArrInteger(request.getParameter("moduleIds").split(","));
			boolean roleModified = request.getBooleanParameter("roleModified");
			boolean moduleModified = request.getBooleanParameter("moduleModified");
									
			//密码加密
			user.setPassword(StringUtils.isBlank(user.getPassword()) || user.getPassword().equals("********") ? null : md5.encodePassword(user.getPassword(), user.getUsername()));
			
			//添加角色
			user.setRoles(new ArrayList<Roles>());			
			for(int roleId : roleIds){
				Roles roles = new Roles();
				roles.setRoleId(roleId);
				user.getRoles().add(roles);
			}
			
			//过滤角色中的模块
			List<Modules> roleModulesList = modulesService.getModulesListByRoleIds(roleIds);			
			for(Modules roleModule : roleModulesList){
				for(int moduleId : moduleIds){
					if(roleModule.getModuleId()==moduleId){
						moduleIds = ArrayUtils.removeElement(moduleIds, moduleId);
						break;
					}
				}
			}
			
			//添加个人模块
			user.setModules(new ArrayList<Modules>());			
			for(Integer moduleId : moduleIds){
				Modules modules = new Modules();
				modules.setModuleId(moduleId);
				user.getModules().add(modules);
			}
			
			
			if(user.getUserId()==null){
				//添加对象
				user.setRegTime(new Date());
				user.setUpdateTime(new Date());
				adminsService.addAdmins(user, roleModified, moduleModified);
			}else{
				//编辑对象
				user.setUpdateTime(new Date());
				adminsService.editAdmins(user, roleModified, moduleModified);
			}
			
			//输出数据
			out.println("{success: true}");

		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}
	
	
	/**
	 * Ajax请求：删除用户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void delAdmins(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			String userIds = request.getParameter("userIds");	
			int[] _userIds = ConvertUtils.toArrInteger(userIds.split(","));
			
			//判断是否是管理员
			for(int userId : _userIds){
				if(userId==1){
					out.println("{success: false, msg: '不能删除[admin]用户！'}");
					return;
				}	
			}
			
			adminsService.delAdmins(_userIds);
			
			//输出数据
			out.println("{success: true}");
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}

}
