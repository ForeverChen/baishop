package com.baixc.ucenter.service.admin.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.service.BaseService;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.service.admin.AdminsService;
import com.baixc.ucenter.service.admin.ModulesService;

/**
 * 功能模块服务类
 * @author Linpn
 */
public class ModulesServiceImpl extends BaseService implements ModulesService {

	private static final long serialVersionUID = 2218426418456488310L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientAdmin;
	
	@Resource
	private AdminsService adminsService;
	
	/**
	 * 获取功能模块
	 * @param moduleId 功能模块ID
	 * @return 返回功能模块对象
	 */
	@Override
	public Modules getModules(int moduleId) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("moduleId", moduleId);
			Modules module = (Modules)this.sqlMapClientAdmin.queryForObject("bai_modules.getModules", params);
			return module;
		}catch(Exception e){
			throw new RpcException("查询模块出错", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Modules getModules(String text) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("text", text);
			List<Modules> list = this.sqlMapClientAdmin.queryForList("bai_modules.getModules", params);
			if(list!=null && list.size()>0)
				return list.get(0);
			else
				return null;
		}catch(Exception e){
			throw new RpcException("查询模块出错", e);
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public Modules getModulesByCode(String code) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("code", code);
			List<Modules> list = this.sqlMapClientAdmin.queryForList("bai_modules.getModules", params);
			if(list!=null && list.size()>0)
				return list.get(0);
			else
				return null;
		}catch(Exception e){
			throw new RpcException("查询模块出错", e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Modules getModulesByUrl(String url) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("url", url);
			List<Modules> list = this.sqlMapClientAdmin.queryForList("bai_modules.getModules", params);
			for(Modules module : list){
				if(module.getUrl().trim().equals(url.trim()))
					return module;
			}
			return null;
		}catch(Exception e){
			throw new RpcException("查询模块出错", e);
		}
	}

	@Override
	public List<Modules> getModulesListByType(String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("type", type);
		return this.getModulesList(params);
	}
	
	@Override
	public List<Modules> getModulesList(Map<String,Object> params) {
		try{
			@SuppressWarnings("unchecked")
			List<Modules> list = this.sqlMapClientAdmin.queryForList("bai_modules.getModules", params);
			return list;			
		}catch(Exception e){
			throw new RpcException("查询模块出错", e);
		}
	}
	

	
	@Override
	public List<Modules> getModulesListByPid(int modulePid){
		// 子模块列表
		List<Modules> listSubModules = new ArrayList<Modules>();
		
		//模块列表
		List<Modules> list = this.getModulesList(null);		
		for(Modules module : list){		
			//添加最叶子节点到列表中
			if(module.getModulePid().equals(modulePid)){
				listSubModules.add(module);
			}
		}
		
		return listSubModules;
	}
	
	@Override
	public List<Modules> getModulesListByUser(Admins user, boolean requery) {
		// 模块列表
		List<Modules> list = new ArrayList<Modules>();
		
		// 重新查询
		if(requery){
			user = adminsService.getAdmins(user.getUserId());
		}
		
		// 获取用户中的模块列表
		list.addAll(user.getModules());
		
		// 获取用色中的模块列表
		List<Roles> roles = user.getRoles();
		for(Roles role : roles){
			list.addAll(role.getModules());
		}			

		//去除重复
		list = removeRepeatModule(list);
		
		return list;
	}
	
	@Override
	public List<Modules> getModulesListByRoleIds(int[] roleIds) {
		if(roleIds==null || roleIds.length<=0)
			return new ArrayList<Modules>();
		
		// 模块列表
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("roleIds", roleIds);			
		List<Modules> list = this.getModulesList(params);
		
		//去除重复
		list = removeRepeatModule(list);
		
		return list;
	}
	
	
	@Override
	@Transactional
	public void delModules(int moduleId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("modulePid", moduleId);
		
		List<Modules>	list = this.getModulesList(params);
		if(list.size()>0){
			throw new RpcException("请先删除子节点");
		}
		
		this.delModules(new int[]{moduleId});
	}
	
	@Override
	@Transactional
	public void delModules(int[] moduleIds) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("moduleIds", moduleIds);
			
			this.sqlMapClientAdmin.delete("bai_modules.delModules", params);
			this.sqlMapClientAdmin.delete("bai_roles_modules.delRolesModules", params);
		}catch(Exception e){
			throw new RpcException("删除模块失败", e);
		}
	}

	
	@Override
	@Transactional
	public int addModules(Modules modules) {
		try{
			int moduleId = (Integer)this.sqlMapClientAdmin.insert("bai_modules.addModules", modules);
			
			//更新排序
			modules.setModuleId(moduleId);
			modules.setSort(Integer.valueOf(modules.getModulePid().toString() + moduleId));
			this.sqlMapClientAdmin.update("bai_modules.editModules", modules);
			
			return moduleId;
			
        }catch(DuplicateKeyException e){
			throw new RpcException("模块名称或编号重复", e);	
		}catch(Exception e){
			throw new RpcException("添加模块失败", e);
		}
	}

	@Override
	@Transactional
	public int editModules(Modules modules) {
		try{
			return this.sqlMapClientAdmin.update("bai_modules.editModules", modules);

        }catch(DuplicateKeyException e){
			throw new RpcException("模块名称或编号重复", e);
		}catch(Exception e){
			throw new RpcException("编辑模块失败", e);
		}
	}

	
	/**
	 * 去除重复模块
	 * @param list 模块列表
	 * @return
	 */
	private List<Modules> removeRepeatModule(List<Modules> list) {
		// 去除重复
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = list.size() - 1; j > i; j--) {
				if (list.get(j).getModuleId().equals(list.get(i).getModuleId()))
					list.remove(j);
			}
		}
		
		// 排序
		Collections.sort(list, new Comparator<Modules>() {
			@Override
			public int compare(Modules o1, Modules o2) {
				return o1.getSort().compareTo(o2.getSort());
			}
		});		

		return list;
	}

}
