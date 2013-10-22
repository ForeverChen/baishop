package com.baixc.ucenter.service.admin;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.baixc.ucenter.model.admin.Roles;

/**
 * 后台角色服务类
 * @author Linpn
 */
public interface RolesService extends Serializable {
	
	/** 管理员 */
	public final static String ROLE_ADMIN = "ROLE_ADMIN";
	/** 普通用户 */
	public final static String ROLE_USER = "ROLE_USER";
	
	
	/**
	 * 获取后台角色
	 * @param roleId 后台角色ID
	 * @return 返回后台角色对象
	 */
	public Roles getRoles(int roleId);
	
	/**
	 * 获取后台角色
	 * @param roleName 后台角色名
	 * @return 返回后台角色对象
	 */
	public Roles getRoles(String roleName);

	/**
	 * 获取后台角色列表
	 * @param params 查询参数
	 * @return 返回后台角色列表
	 */
	public List<Roles> getRolesList(Map<String,Object> params);

	/**
	 * 获取后台角色列表
	 * @param userId 后台用户ID
	 * @return 返回后台角色列表
	 */
	public List<Roles> getRolesListByUserId(int userId);

	/**
	 * 删除后台角色
	 * @param roleId 后台角色ID
	 */
	public void delRoles(int roleId);
	
	/**
	 * 删除后台角色
	 * @param roleIds 后台角色ID列表
	 */
	public void delRoles(int[] roleIds);

	/**
	 * 添加后台角色
	 * @param roles 后台角色对象
	 * @return 返回插入的主键ID
	 */
	public int addRoles(final Roles roles, boolean syncModules);

	/**
	 * 修改后台角色
	 * @param roles 后台角色对象
	 * @return 返回受影响的行数
	 */
	public int editRoles(final Roles roles, boolean syncModules);
	
}
