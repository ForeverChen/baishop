package com.baixc.ucenter.service.admin.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.service.BaseService;
import com.baixc.ucenter.model.admin.Modules;
import com.baixc.ucenter.model.admin.Roles;
import com.baixc.ucenter.model.admin.RolesModules;
import com.baixc.ucenter.service.admin.RolesService;
import com.ibatis.sqlmap.client.SqlMapExecutor;

/**
 * 后台角色服务类
 * 
 * @author Linpn
 */
public class RolesServiceImpl extends BaseService implements RolesService {

	private static final long serialVersionUID = -6044363077079748792L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientAss;
	
	@Override
	public Roles getRoles(int roleId) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleId", roleId);
			Roles role = (Roles) this.sqlMapClientAss.queryForObject("bai_roles.getRoles", params);
			return role;
		} catch (Exception e) {
			throw new RpcException("查询角色出错", e);
		}
	}

	@Override
	public Roles getRoles(String roleName) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleName", roleName);
			Roles role = (Roles) this.sqlMapClientAss.queryForObject("bai_roles.getRoles", params);
			return role;
		} catch (Exception e) {
			throw new RpcException("查询角色出错", e);
		}
	}

	@Override
	public List<Roles> getRolesList(Map<String, Object> params) {
		try {
			@SuppressWarnings("unchecked")
			List<Roles> list = this.sqlMapClientAss.queryForList("bai_roles.getRoles", params);
			return list;
		} catch (Exception e) {
			throw new RpcException("查询角色出错", e);
		}
	}

	@Override
	public List<Roles> getRolesListByUserId(int userId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);

		List<Roles> list = this.getRolesList(params);
		return list;
	}

	@Override
	@Transactional
	public void delRoles(int roleId) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("rolePid", roleId);

		List<Roles> list = this.getRolesList(params);
		if (list.size() > 0) {
			throw new RpcException("请先删除子节点");
		}

		this.delRoles(new int[] { roleId });
	}

	@Override
	@Transactional
	public void delRoles(int[] roleIds) {
		try {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("roleIds", roleIds);

			this.sqlMapClientAss.delete("bai_roles.delRoles", params);
			this.sqlMapClientAss.delete("bai_admins_roles.delAdminsRoles",params);
			this.sqlMapClientAss.delete("bai_roles_modules.delRolesModules", params);

		} catch (Exception e) {
			throw new RpcException("删除角色出错", e);
		}

	}

	@Override
	@Transactional
	public int addRoles(final Roles roles, boolean syncModules) {
		try {
			// 添加角色
			int roleId = (Integer) this.sqlMapClientAss.insert("bai_roles.addRoles", roles);

			// 更新排序
			roles.setRoleId(roleId);
			roles.setSort(Integer.valueOf(roles.getRolePid().toString() + roleId));
			this.sqlMapClientAss.update("bai_roles.editRoles", roles);

			// 添加角色模块记录
			if (syncModules) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("roleIds", new int[] { roles.getRoleId() });

				this.sqlMapClientAss.delete("bai_roles_modules.delRolesModules", params);
				this.sqlMapClientAss.execute(
						new SqlMapClientCallback<Integer>() {
							@Override
							public Integer doInSqlMapClient(SqlMapExecutor executor) throws SQLException {
								executor.startBatch();

								for (Modules modules : roles.getModules()) {
									RolesModules rolesModules = new RolesModules();
									rolesModules.setRoleId(roles.getRoleId());
									rolesModules.setModuleId(modules.getModuleId());
									executor.insert("bai_roles_modules.addRolesModules", rolesModules);
								}

								return executor.executeBatch();
							}
						});
			}

			return roleId;
			
		} catch (DuplicateKeyException e) {
			throw new RpcException("角色名重复", e);
		} catch (Exception e) {
			throw new RpcException("添加角色出错", e);
		}
	}

	@Override
	@Transactional
	public int editRoles(final Roles roles, boolean syncModules) {
		try {
			// 编辑角色
			int count = this.sqlMapClientAss.update("bai_roles.editRoles", roles);

			// 编辑角色模块记录
			if (syncModules) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("roleIds", new int[] { roles.getRoleId() });

				this.sqlMapClientAss.delete("bai_roles_modules.delRolesModules", params);
				this.sqlMapClientAss.execute(
						new SqlMapClientCallback<Integer>() {
							@Override
							public Integer doInSqlMapClient(SqlMapExecutor executor)throws SQLException {
								executor.startBatch();

								for (Modules modules : roles.getModules()) {
									RolesModules rolesModules = new RolesModules();
									rolesModules.setRoleId(roles.getRoleId());
									rolesModules.setModuleId(modules.getModuleId());
									executor.insert("bai_roles_modules.addRolesModules",rolesModules);
								}

								return executor.executeBatch();
							}
						});
			}
			
			return count;

		} catch (DuplicateKeyException e) {
			throw new RpcException("角色名重复", e);
		} catch (Exception e) {
			throw new RpcException("编辑角色出错", e);
		}
	}

}
