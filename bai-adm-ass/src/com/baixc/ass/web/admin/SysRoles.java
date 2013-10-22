package com.baixc.ass.web.admin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.framework.json.JsonGlobal;
import com.baixc.framework.utils.ConvertUtils;
import com.baixc.framework.utils.TreeRecursiveHandle;
import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.web.controller.PageAdmController;

public class SysRoles extends PageAdmController {
	

	@Override
	public void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) {
	}
	
	
	/**
	 * Ajax请求：获取用户角色列表
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void getRoles(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//组装JSON
			JSONObject json = this.getTreeRolesOfJSON();

			//输出数据
			out.println(json);
			
		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}	
	
		
	/**
	 * Ajax请求：保存角色
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void saveRoles(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{			
			//创建对象
			Roles role = request.getBindObject(Roles.class, "role");
			int[] moduleIds = ConvertUtils.toArrInteger(request.getParameter("moduleIds").split(","));
			boolean moduleModified = request.getBooleanParameter("moduleModified");
			
			//添加模块
			role.setModules(new ArrayList<Modules>());			
			for(Integer moduleId : moduleIds){
				Modules modules = new Modules();
				modules.setModuleId(moduleId);
				role.getModules().add(modules);
			}			
			
			if(role.getRoleId()==null){
				//添加对象
				rolesService.addRoles(role, moduleModified);
			}else{
				//编辑对象
				rolesService.editRoles(role, moduleModified);
			}
			
			//输出数据
			adminsService.clearSecuritySource();
			
			//输出数据
            out.println("{success: true, role:"+ JSONObject.fromObject(role, JsonGlobal.jsonConfig).toString() +"}");

		}catch(Exception e){
			out.println("{success: false, msg: '"+ e.getMessage() +"'}");
			e.printStackTrace();
		}
		
		out.close();
	}
	
	
	/**
	 * Ajax请求：删除角色
	 * @param request
	 * @param response
	 * @throws IOException 
	 */
	public void delRoles(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			Integer roleId = request.getIntParameter("roleId");	
			
			//判断是否是管理员
			if(roleId==1){
				out.println("{success: false, msg: '不能删除[管理员]角色！'}");
				return;
			}	

			rolesService.delRoles(roleId);
			
			//输出数据
			out.println("{success: true}");
			adminsService.clearSecuritySource();
			
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
	public void upRoles(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			String roleId = request.getParameter("roleId");
			String prevRoleId = request.getParameter("prevRoleId");
			
			Roles role = rolesService.getRoles(Integer.valueOf(roleId));
			Roles prevRole = rolesService.getRoles(Integer.valueOf(prevRoleId));
			
			//排序对调
			int temp = role.getSort();
			role.setSort(prevRole.getSort());
			prevRole.setSort(temp);
			
			rolesService.editRoles(role, false);
			rolesService.editRoles(prevRole, false);
			
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
	public void downRoles(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		
		try{
			//获取参数
			String roleId = request.getParameter("roleId");
			String nextRoleId = request.getParameter("nextRoleId");
			
			Roles role = rolesService.getRoles(Integer.valueOf(roleId));
			Roles nextRole = rolesService.getRoles(Integer.valueOf(nextRoleId));
			
			//排序对调
			int temp = role.getSort();
			role.setSort(nextRole.getSort());
			nextRole.setSort(temp);
			
			rolesService.editRoles(role, false);
			rolesService.editRoles(nextRole, false);
			
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
	public void getUsersByRoles(HttpServletExtendRequest request, 
			HttpServletExtendResponse response) throws IOException {
		PrintWriter out = response.getWriter();
		String callback=request.getParameter("callback");
		
		try{
			Integer roleId = request.getIntParameter("roleId");
			
			List<Admins> list;			
			if(roleId!=null){
				Map<String,Object> params = new HashMap<String,Object>();
				params.put("roleId", roleId);		
				list = adminsService.getAdminsList(params, null, null, null);
			}else{
				list = new ArrayList<Admins>();
			}
			
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
	 * 获取JSON格式的树型角色,EXTJS中使用
	 * @return 返回JSON对象，json.get("leafMap")可以获取叶子节点集合
	 */
	public JSONObject getTreeRolesOfJSON() {
		final JSONObject json = new JSONObject();

		try {
			json.put("id", 0);
			json.put("text", "用户角色");
			json.put("roleId", 0);
			json.put("roleName", "用户角色");
			json.put("iconCls", "icon-role-root");
			json.put("children", new JSONArray());
			json.put("leafMap", new JSONObject());
			json.put("roles", new JSONArray());
			
			final List<Roles> list = rolesService.getRolesList(null);

			// 递归加载
			TreeRecursiveHandle<JSONObject> treeRecursiveHandle = new TreeRecursiveHandle<JSONObject>() {
				public void recursive(JSONObject treeNode)
						throws Exception {
					for (Roles role : list) {
						if (role.getRolePid().equals(treeNode.getInt("id"))) {

							JSONObject node = JSONObject.fromObject(role);

							node.put("id", role.getRoleId());
							node.put("text", role.getRoleName());
							node.put("expanded", false);
							node.put("leaf", true);
							node.put("nLeaf", role.getLeaf());

							// 图标
							if (role.getLeaf() == 0)
								node.put("iconCls", "icon-role-group");
							else
								node.put("iconCls", "icon-role-leaf");

							// 角色中的模块ID
							JSONArray modules = new JSONArray();
							for (Modules module : role.getModules()) {
								modules.add(module.getModuleId());
							}
							node.put("modules", modules);

							// 递归
							this.recursive(node);

							// 添加到树中
							JSONArray children;
							try {
								children = treeNode.getJSONArray("children");
							} catch (JSONException e) {
								treeNode.put("children", new JSONArray());
								children = treeNode.getJSONArray("children");
							}
							children.add(node);
							treeNode.put("leaf", false);

							// 添加最叶子节点到列表中
							if (role.getLeaf() == 1) {
								json.getJSONObject("leafMap").put(
										role.getRoleId(), role.getRoleName());
							}
							json.getJSONArray("roles").add(
									JSONArray.fromObject(new Object[] {
											role.getRoleId(),
											role.getRoleName() }));

						}
					}
				}
			};

			treeRecursiveHandle.recursive(json);

		} catch (Exception e) {
			throw new RpcException("查询角色出错", e);
		}

		return json;
	}
}
