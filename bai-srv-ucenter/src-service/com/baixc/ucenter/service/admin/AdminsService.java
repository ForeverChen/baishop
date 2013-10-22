package com.baixc.ucenter.service.admin;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.baixc.ucenter.model.admin.Admins;


/**
 * 后台用户服务类
 * @author Linpn
 */
public interface AdminsService extends UserDetailsService, Serializable {
	/**
	 * 获取后台用户
	 * @param userId 后台用户ID
	 * @return 返回后台用户对象
	 */
	public Admins getAdmins(int userId);

	/**
	 * 获取后台用户
	 * @param username/code 后台用户名或用户工号
	 * @return 返回后台用户对象
	 */
	public Admins getAdmins(String username);

	/**
	 * 获取后台用户
	 * @param username/code 后台用户名或用户工号
	 * @param password 密码
	 * @return 返回后台用户对象
	 */
	public Admins getAdmins(String username, String password);
	
	/**
	 * 获取后台用户列表
	 * @param params 查询参数
	 * @param sorters 记录的排序，如sorters.put("id","desc")，该参数如果为空表示按默认排序
	 * @param start 查询的起始记录,可为null
	 * @param limit 查询的总记录数, 如果值为-1表示查询到最后,可为null
	 * @return 返回后台用户列表
	 */
	public List<Admins> getAdminsList(Map<String,Object> params, Map<String,String> sorters, Long start, Long limit);
	
	/**
	 * 获取用户总数
	 * @param params 查询参数
	 * @return
	 */
	public long getAdminsCount(Map<String, Object> params);

	/**
	 * 获取后台角色列表
	 * @param roleId 后台角色ID
	 * @return 返回后台用户列表
	 */
	public List<Admins> getAdminsListByRoleId(int roleId);
	
	/**
	 * 删除后台用户
	 * @param userId 后台用户ID
	 */
	public void delAdmins(int userId);
	
	/**
	 * 删除后台用户
	 * @param userIds 后台用户ID列表
	 */
	public void delAdmins(int[] userIds);

	/**
	 * 添加后台用户
	 * @param users 后台用户对象
	 * @return 返回插入的主键ID
	 */
	public int addAdmins(final Admins users);

	/**
	 * 添加后台用户
	 * @param users 后台用户对象
	 * @param syncRoles 是否同步角色
	 * @param syncModules 是否同步模块
	 * @return 返回插入的主键ID
	 */
	public int addAdmins(final Admins users, boolean syncRoles, boolean syncModules);

	/**
	 * 修改后台用户
	 * @param users 后台用户对象
	 * @return 返回受影响的行数
	 */
	public int editAdmins(final Admins users);

	/**
	 * 修改后台用户
	 * @param users 后台用户对象
	 * @param syncRoles 是否同步角色
	 * @param syncModules 是否同步模块
	 * @return 返回受影响的行数
	 */
	public int editAdmins(final Admins users, boolean syncRoles, boolean syncModules);
	
	/**
	 * 获取spring security用户权限资源
	 */
	public Map<String, Collection<ConfigAttribute>> getSecuritySource();
	
	/**
	 * 清空spring security用户权限资源
	 */
	public void clearSecuritySource();
}
