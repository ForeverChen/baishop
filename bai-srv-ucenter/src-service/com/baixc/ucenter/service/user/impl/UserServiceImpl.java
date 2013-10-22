package com.baixc.ucenter.service.user.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.service.BaseService;
import com.baixc.ucenter.model.user.User;
import com.baixc.ucenter.service.user.UserService;


/**
 * 后台用户服务类
 * @author Linpn
 */
public class UserServiceImpl extends BaseService implements UserService, UserDetailsService {

	private static final long serialVersionUID = 7365701042609186465L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientUser;

	private Md5PasswordEncoder md5 = new Md5PasswordEncoder();

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
    	// 获取用户
    	User user = this.getUser(username);
//    	if(user!=null){
//	    	user.setLastLoginIp(lastLoginIp);
//	    	user.setLastLoginTime(new Date());
//	    	user.setVisitCount(user.getVisitCount()+1);
//	    	this.editUser(user, false);
//    	}
    	
    	return  user;
	}

	@Override
	public User getUser(int userId) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userId", userId);
			User admin = (User)this.sqlMapClientUser.queryForObject("bai_user.getUser", params);
			return admin;
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}

	@Override
	public User getUser(String username) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("username", username);
			User admin = (User)this.sqlMapClientUser.queryForObject("bai_user.getUser", params);
			return admin;
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}

	@Override
	public User getUser(String username, String password) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("username", username);
			params.put("password", md5.encodePassword(password, username));
			User admin = (User)this.sqlMapClientUser.queryForObject("bai_user.getUser", params);
			return admin;
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}

	@Override
	public List<User> getUserList(Map<String, Object> params, Map<String, String> sorters, Long start, Long limit) {
		try{
			if(params==null)
				params = new HashMap<String,Object>();

			params.put("sort", this.getDbSort(sorters));
			params.put("start", start);
			params.put("limit", limit);
			
			@SuppressWarnings("unchecked")
			List<User> list = this.sqlMapClientUser.queryForList("bai_user.getUser", params);
			return list;
			
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}
	
	@Override
	public long getUserCount(Map<String, Object> params) {
		try{
			long count = (Long)this.sqlMapClientUser.queryForObject("bai_user.getUserCount", params);			
			return count;		
		}catch(Exception e){
			throw new RpcException("查询用户出错", e);
		}
	}
	

	@Override
	@Transactional
	public void delUser(int userId) {
		this.delUser(new int[]{userId});
	}	

	@Override
	@Transactional
	public void delUser(int[] userIds) {
		try{
			if(userIds==null || userIds.length<=0)
				throw new RpcException("userIds不能为空");
			
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userIds", userIds);
			
			this.sqlMapClientUser.delete("bai_user.delUser", params);
			
		}catch(Exception e){
			throw new RpcException("删除用户出错", e);
		}
	}
	
	@Override
	@Transactional
	public long addUser(final User user) {
		try{
			// 添加用户记录
			long userId = (Long)this.sqlMapClientUser.insert("bai_user.addUser", user);		
			user.setUserId(userId);	

			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userIds", new long[]{user.getUserId()});
			
			return userId;

        }catch(DuplicateKeyException e){
			throw new RpcException("用户名或邮箱重复", e);	
        } catch (Exception e) {
			throw new RpcException("添加用户出错", e);
		}
	}

	@Override
	@Transactional
	public long editUser(final User user) {
		try{
			// 编辑用户记录
			long count = this.sqlMapClientUser.update("bai_user.editUser", user);

			Map<String,Object> params = new HashMap<String,Object>();
			params.put("userIds", new long[]{user.getUserId()});			
			
			return count;

        }catch(DuplicateKeyException e){
			throw new RpcException("用户名或邮箱重复", e);	
        } catch (Exception e) {
			throw new RpcException("编辑用户出错", e);
		}
	}
	
}
