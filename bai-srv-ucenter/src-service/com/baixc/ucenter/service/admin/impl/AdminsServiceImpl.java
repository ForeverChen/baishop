package com.baixc.ucenter.service.admin.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.annotation.Resource;

import net.rubyeye.xmemcached.exception.MemcachedException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.service.BaseService;
import com.baixc.framework.ibatis.cache.MemcachedController;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.AdminsModules;
import com.baixc.ucenter.model.admin.AdminsRoles;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.service.admin.AdminsService;
import com.baixc.ucenter.service.admin.ModulesService;
import com.baixc.ucenter.service.admin.RolesService;
import com.ibatis.sqlmap.client.SqlMapExecutor;


/**
 * 后台用户服务类
 * @author Linpn
 */
public class AdminsServiceImpl extends BaseService implements AdminsService, UserDetailsService {

	private static final long serialVersionUID = -5609524598183928386L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientAdmin;
	
	@Resource
	private RolesService rolesService;
	@Resource
	private ModulesService modulesService;

	private Md5PasswordEncoder md5 = new Md5PasswordEncoder();	
	private final static String SPRING_SECURITY_METADATA_SOURCE = "SPRING_SECURITY_METADATA_SOURCE";

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
    	// 获取用户
    	Admins user = this.getAdmins(username);
//    	if(user!=null){
//	    	user.setLastLoginIp(lastLoginIp);
//	    	user.setLastLoginTime(new Date());
//	    	user.setVisitCount(user.getVisitCount()+1);
//	    	this.editAdmins(user, false);
//    	}
    	
    	return  user;
	}

	@Override
	public Admins getAdmins(int userId) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userId", userId);
			Admins admin = (Admins)this.sqlMapClientAdmin.queryForObject("bai_admins.getAdmins", params);
			return admin;
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}

	@Override
	public Admins getAdmins(String username) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("username", username);
			Admins admin = (Admins)this.sqlMapClientAdmin.queryForObject("bai_admins.getAdmins", params);
			return admin;
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}

	@Override
	public Admins getAdmins(String username, String password) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("username", username);
			params.put("password", md5.encodePassword(password, username));
			Admins admin = (Admins)this.sqlMapClientAdmin.queryForObject("bai_admins.getAdmins", params);
			return admin;
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}

	@Override
	public List<Admins> getAdminsList(Map<String, Object> params, Map<String, String> sorters, Long start, Long limit) {
		try{
			if(params==null)
				params = new HashMap<String,Object>();

			params.put("sort", this.getDbSort(sorters));
			params.put("start", start);
			params.put("limit", limit);
			
			@SuppressWarnings("unchecked")
			List<Admins> list = this.sqlMapClientAdmin.queryForList("bai_admins.getAdmins", params);
			return list;
			
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}
	
	@Override
	public long getAdminsCount(Map<String, Object> params) {
		try{
			long count = (Long)this.sqlMapClientAdmin.queryForObject("bai_admins.getAdminsCount", params);			
			return count;		
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}
	
	
	@Override
	public List<Admins> getAdminsListByRoleId(int roleId) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("roleId", roleId);
		
		List<Admins> list = this.getAdminsList(params, null, null, null);
		return list;
	}

	@Override
	@Transactional
	public void delAdmins(int userId) {
		this.delAdmins(new int[]{userId});
	}	

	@Override
	@Transactional
	public void delAdmins(int[] userIds) {
		try{
			if(userIds==null || userIds.length<=0)
				throw new RpcException("userIds不能为空");
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userIds", userIds);
			
			this.sqlMapClientAdmin.delete("bai_admins.delAdmins", params);
			
		}catch(Exception e){
			throw new RpcException("删除用户出错", e);
		}
	}
	
	@Override
	@Transactional
	public int addAdmins(final Admins users) {
		return this.addAdmins(users, false, false);		
	}
	
	@Override
	@Transactional
	public int addAdmins(final Admins users, boolean syncRoles, boolean syncModules) {
		try{
			// 添加用户记录
			int userId = (Integer)this.sqlMapClientAdmin.insert("bai_admins.addAdmins", users);		
			users.setUserId(userId);	

			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userIds", new int[]{users.getUserId()});
			
			// 添加角色记录
			if(syncRoles){
				this.sqlMapClientAdmin.delete("bai_admins_roles.delAdminsRoles", params);
				this.sqlMapClientAdmin.execute(new SqlMapClientCallback<Integer>(){
					@Override
					public Integer doInSqlMapClient(SqlMapExecutor executor)
							throws SQLException {
						executor.startBatch();
						
						for(Roles roles : users.getRoles()){
							AdminsRoles adminsRoles = new AdminsRoles();
							adminsRoles.setUserId(users.getUserId());
							adminsRoles.setRoleId(roles.getRoleId());
							executor.insert("bai_admins_roles.addAdminsRoles", adminsRoles);
						}
						
						return executor.executeBatch();
					}
				});
			}			

			// 添加模块记录
			if(syncModules){
				this.sqlMapClientAdmin.delete("bai_admins_modules.delAdminsModules", params);
				this.sqlMapClientAdmin.execute(new SqlMapClientCallback<Integer>(){
					@Override
					public Integer doInSqlMapClient(SqlMapExecutor executor)
							throws SQLException {
						executor.startBatch();
						
						for(Modules modules : users.getModules()){
							AdminsModules adminsModules = new AdminsModules();
							adminsModules.setUserId(users.getUserId());
							adminsModules.setModuleId(modules.getModuleId());
							executor.insert("bai_admins_modules.addAdminsModules", adminsModules);
						}
						
						return executor.executeBatch();
					}
				});
			}
			
			return userId;

        }catch(DuplicateKeyException e){
			throw new RpcException("用户名或工号重复", e);	
        } catch (Exception e) {
			throw new RpcException("添加用户出错", e);
		}
	}

	@Override
	@Transactional
	public int editAdmins(final Admins users) {
		return this.editAdmins(users, false, false);
	}

	@Override
	@Transactional
	public int editAdmins(final Admins users, boolean syncRoles, boolean syncModules) {
		try{
			// 编辑用户记录
			int count = this.sqlMapClientAdmin.update("bai_admins.editAdmins", users);

			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userIds", new int[]{users.getUserId()});			

			// 编辑角色记录
			if(syncRoles){
				this.sqlMapClientAdmin.delete("bai_admins_roles.delAdminsRoles", params);
				this.sqlMapClientAdmin.execute(new SqlMapClientCallback<Integer>(){
					@Override
					public Integer doInSqlMapClient(SqlMapExecutor executor)
							throws SQLException {
						executor.startBatch();
						
						for(Roles roles : users.getRoles()){
							AdminsRoles adminsRoles = new AdminsRoles();
							adminsRoles.setUserId(users.getUserId());
							adminsRoles.setRoleId(roles.getRoleId());
							executor.insert("bai_admins_roles.addAdminsRoles", adminsRoles);
						}
						
						return executor.executeBatch();
					}
				});
			}
			
			// 添加模块记录
			if(syncModules){
				this.sqlMapClientAdmin.delete("bai_admins_modules.delAdminsModules", params);
				this.sqlMapClientAdmin.execute(new SqlMapClientCallback<Integer>(){
					@Override
					public Integer doInSqlMapClient(SqlMapExecutor executor)
							throws SQLException {
						executor.startBatch();
						
						for(Modules modules : users.getModules()){
							AdminsModules adminsModules = new AdminsModules();
							adminsModules.setUserId(users.getUserId());
							adminsModules.setModuleId(modules.getModuleId());
							executor.insert("bai_admins_modules.addAdminsModules", adminsModules);
						}
						
						return executor.executeBatch();
					}
				});
			}
			
			return count;

        }catch(DuplicateKeyException e){
			throw new RpcException("用户名或工号重复", e);
        } catch (Exception e) {
			throw new RpcException("编辑用户出错", e);
		}
	}
	

	/**
	 * 获取spring security用户权限资源
	 */
	public Map<String, Collection<ConfigAttribute>> getSecuritySource() {
		Map<String, Collection<ConfigAttribute>> httpMethodMap = null;
		
		try{
			//从Memcached获取资源
			httpMethodMap = MemcachedController.getMemcached().get(SPRING_SECURITY_METADATA_SOURCE);
			
			//如果资源为空，则重新载入
			if(httpMethodMap==null || httpMethodMap.size()==0){
				httpMethodMap = this.loadSecuritySource();
			}
			
		}catch(Exception e){
			logger.error("获取spring security用户权限资源 出错", e);
		}
		
		return httpMethodMap;
	}
	

	/**
	 * 清空spring security用户权限资源
	 */
	public void clearSecuritySource() {
		try {
			MemcachedController.getMemcached().delete(SPRING_SECURITY_METADATA_SOURCE);
		} catch (Exception e) {
			logger.error("清空spring security用户权限资源 出错", e);
		}
	}

	
	
	/**
	 * 同步载入spring security用户权限资源
	 */
	private synchronized Map<String, Collection<ConfigAttribute>> loadSecuritySource() throws TimeoutException, InterruptedException, MemcachedException{
		//同步完再做一次判断
		Map<String, Collection<ConfigAttribute>> httpMethodMap = MemcachedController.getMemcached().get(SPRING_SECURITY_METADATA_SOURCE);
		
		//为空，读取数据库
		if(httpMethodMap==null || httpMethodMap.size()==0){
			httpMethodMap = new HashMap<String, Collection<ConfigAttribute>>();
			
			//获取全部资源
			List<Modules> modules = this.modulesService.getModulesList(null);
			for (Modules module : modules) {
				if(ModulesService.MODULE.equals(module.getType()) || ModulesService.FUNCTION.equals(module.getType())){		
					if(module.getUrl().trim().equals(""))
						continue;
					
					//获取资源
					Collection<ConfigAttribute> atts = httpMethodMap.get(module.getUrl());
					if(atts==null){
						httpMethodMap.put(module.getUrl(), new ArrayList<ConfigAttribute>());
					}
				}
			}
			
			//获取角色中的资源
			List<Roles> roles = this.rolesService.getRolesList(null);
			for (Roles role : roles) {
				modules = role.getModules();
				for (Modules module : modules) {
					if(ModulesService.MODULE.equals(module.getType()) || ModulesService.FUNCTION.equals(module.getType())){	
						if(module.getUrl().trim().equals(""))
							continue;
						
						//获取资源
						Collection<ConfigAttribute> atts = httpMethodMap.get(module.getUrl());
						if(atts==null){
							httpMethodMap.put(module.getUrl(), new ArrayList<ConfigAttribute>());
							atts = httpMethodMap.get(module.getUrl());
						}
						
						//添加资源角色，并判断资源中是否已经存在角色
						boolean isHad = false;
						for(ConfigAttribute ca : atts){
							if(ca.getAttribute().equals(role.getRoleName())){
								isHad = true;
								break;
							}
						}
						if(!isHad){
							atts.add(new SecurityConfig(role.getRoleName()));
						}						
					}
				}
			}
			
			//将资源保存到Memcached
			MemcachedController.getMemcached().set(SPRING_SECURITY_METADATA_SOURCE, 3600, httpMethodMap);
		}
		
		return httpMethodMap;
	}
}
