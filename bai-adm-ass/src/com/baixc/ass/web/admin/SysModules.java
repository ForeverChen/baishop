package com.baixc.ass.web.admin;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.framework.utils.TreeRecursiveHandle;
import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.service.admin.ModulesService;
import com.baixc.ucenter.web.controller.PageAdmController;

public class SysModules extends PageAdmController {

	@Override
	public void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) {
	}
	
	
	/**
	 * Ajax请求：获取用户应用模块列表
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void getModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//组装JSON
			JSONObject json = this.getTreeModulesOfJSON();

			//输出数据
			out.println(json);
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}
	
		
	/**
	 * Ajax请求：保存应用模块
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void saveModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{			
			//创建对象
			Modules module = request.getBindObject(Modules.class, "module");	
			Modules parent = modulesService.getModules(module.getModulePid());
			
			//判断编码是否正确
			if(parent!=null && StringUtils.isNotBlank(parent.getCode())){
				String code = module.getCode();
				if(!parent.getCode().equals(code.substring(0, code.length()-2))){
					out.println("{success: false, msg: '编码输入不正确，编码格式为：父编码+两位字符！'}");
					return;
				}
			}
			
			//保存图片
			MultipartFile multipartFile = request.getFile("module.image");
			if(multipartFile!=null && !multipartFile.isEmpty()){
				module.setImage("WEB-RES/images/module_icon_" + module.getModuleId() + multipartFile.getOriginalFilename().replaceAll(".*(\\.\\w+)$", "$1"));
				FileUtils.writeByteArrayToFile(new File(request.getServletContext().getRealPath("/")+ module.getImage()), multipartFile.getBytes());
			}
			
			if(module.getModuleId()==null){
				//添加对象
				int moduleId = modulesService.addModules(module);
				module.setModuleId(moduleId);
			}else{
				//编辑对象
				modulesService.editModules(module);
			}
            
			//清空安全资源数据
			adminsService.clearSecuritySource();
			
			//输出数据
            out.println("{success: true, module:"+ JSONObject.fromObject(module).toString() +"}");

		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}	
	
	
	/**
	 * Ajax请求：删除应用模块
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void delModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			Integer moduleId = request.getIntParameter("moduleId");	
			
			modulesService.delModules(moduleId);
			adminsService.clearSecuritySource();
			
			//输出数据
			out.println("{success: true}");
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}
	
	
	/**
	 * Ajax请求：上移应用模块
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void upModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			String moduleId = request.getParameter("moduleId");
			String prevModuleId = request.getParameter("prevModuleId");
			
			Modules module = modulesService.getModules(Integer.valueOf(moduleId));
			Modules prevModule = modulesService.getModules(Integer.valueOf(prevModuleId));
			
			//排序对调
			int temp = module.getSort();
			module.setSort(prevModule.getSort());
			prevModule.setSort(temp);
			
			modulesService.editModules(module);
			modulesService.editModules(prevModule);
			
			//输出数据
			out.println("{success: true}");
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}
	
	
	/**
	 * Ajax请求：下移应用模块
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void downModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			String moduleId = request.getParameter("moduleId");
			String nextModuleId = request.getParameter("nextModuleId");
			
			Modules module = modulesService.getModules(Integer.valueOf(moduleId));
			Modules nextModule = modulesService.getModules(Integer.valueOf(nextModuleId));
			
			//排序对调
			int temp = module.getSort();
			module.setSort(nextModule.getSort());
			nextModule.setSort(temp);
			
			modulesService.editModules(module);
			modulesService.editModules(nextModule);
			
			//输出数据
			out.println("{success: true}");
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}
	
	
	/**
	 * Ajax请求：通过模块ID获取用户ID，包含角色关联的用户
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void getUsersByModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String callback=request.getParameter("callback");
		
		try{
			Integer moduleId = request.getIntParameter("moduleId");
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("moduleId", moduleId);		
			List<Admins> list = adminsService.getAdminsList(params, null, null, null);
			
			//组装JSON
			JSONObject json = new JSONObject();
			json.put("records", new JSONArray());
			
			JSONArray records = json.getJSONArray("records");
			for(Admins admins : list) {
				JSONObject record = new JSONObject();
				record.put("userId", admins.getUserId());
				record.put("username", admins.getUsername());
				record.put("code", admins.getCode());
				record.put("name", admins.getName());
				
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
	 * Ajax请求：通过模块ID获取角色ID
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void getRolesByModules(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			Integer moduleId = request.getIntParameter("moduleId");
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("moduleId", moduleId);			
			List<Roles> list = rolesService.getRolesList(params);
			
			//组装JSON
			JSONArray json = new JSONArray();
			
			for(Roles role : list){
				json.add(role.getRoleId());
			}
			
			//输出数据
			out.println(json);
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}

	
	/**
	 * 获取JSON格式的树型模块
	 * @return 返回JSON对象
	 */
	public JSONObject getTreeModulesOfJSON() {
		final JSONObject json = new JSONObject();
		
		try {
			json.put("moduleId", 0);
			json.put("id", 0);
			json.put("text", "应用模块");
			json.put("iconCls", "icon-docs");
			json.put("children", new JSONArray());
			
			final List<Modules> list = modulesService.getModulesList(null);
						
			//递归加载
			TreeRecursiveHandle<JSONObject> treeRecursiveHandle = new TreeRecursiveHandle<JSONObject>(){
				public void recursive(JSONObject treeNode) throws Exception{
					for(Modules module : list){
						if(module.getModulePid().equals(treeNode.getInt("id"))){						
							//获取JSON对象
							JSONObject node = JSONObject.fromObject(module);
							
							node.put("id", module.getModuleId());
							node.put("text", module.getText());
							json.put("iconCls", module.getIconCls());
							node.put("leaf", true);

							node.put("nExpanded", module.getExpanded());
							if(module.getExpanded()==1)
								node.put("expanded", true);
							else
								node.put("expanded", false);
							
							if(module.getType().equals(ModulesService.SYSTEM))
								node.put("expanded", false);
														
							//递归
							this.recursive(node);
							
							//添加到树中
							JSONArray children;
							try {
								children = treeNode.getJSONArray("children");
							} catch (JSONException e) {
								treeNode.put("children", new JSONArray());
								children = treeNode.getJSONArray("children");
							}							
							children.add(node);
							treeNode.put("leaf", false);
						}
					}
				}
			};
			
			treeRecursiveHandle.recursive(json);
			
		} catch (Exception e) {
			throw new RpcException("查询模块出错", e);
		}
		
		return json;		
	}

}
