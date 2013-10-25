package com.baixc.commons.service.configcenter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.model.configcenter.Enums;
import com.baixc.commons.service.BaseService;
import com.baixc.commons.service.configcenter.EnumsService;

public class EnumsServiceImpl extends BaseService implements EnumsService {

	private static final long serialVersionUID = 6431588222617603394L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientAss;

	@Override
	public Enums getEnums(String key) {
		return this.getEnums("", key);
	}

	@Override
	public Enums getEnums(String group, String key) {
		return this.getEnumsByBiz("", group, key);
	}

	@Override
	public Enums getEnumsByBiz(String biz, String key) {
		return this.getEnumsByBiz(biz, "", key);
	}

	@Override
	public Enums getEnumsByBiz(String biz, String group, String key) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("biz", biz);
		params.put("group", group);
		params.put("key", key);
		
		List<Enums> list = this.getEnumsList(params);
		if(list.size()>0)
			return list.get(0);
		else
			return null;
		
	}

	@Override
	public List<Enums> getEnumsGroup(String group) {
		return this.getEnumsGroupByBiz("", group);
	}

	@Override
	public List<Enums> getEnumsGroupByBiz(String biz, String group) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("biz", biz);
		params.put("group", group);
		
		List<Enums> list = this.getEnumsList(params);
		return list;
	}
	
	

	@Override
	public Enums getEnums(int id) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("id", id);
			Enums obj = (Enums)this.sqlMapClientAss.queryForObject("bai_enums.getEnums", params);
			return obj;
		}catch(Exception e){
			throw new RpcException("查询枚举出错", e);
		}
	}
	
	@Override
	public List<Enums> getEnumsList(Map<String, Object> params) {
		try{
			if(params==null)
				params = new HashMap<String,Object>();
			
			@SuppressWarnings("unchecked")
			List<Enums> list = this.sqlMapClientAss.queryForList("bai_enums.getEnums", params);
			return list;
		}catch(Exception e){
			throw new RpcException("查询枚举出错", e);
		}
	}

	@Override
	public void delEnums(int id) {
		this.delEnums(new int[]{id});
	}

	@Override
	public void delEnums(int[] ids) {
		try{
			this.sqlMapClientAss.delete("bai_enums.delEnums", ids);
		}catch(Exception e){
			throw new RpcException("删除枚举出错", e);
		}
	}

	@Override
	public void addEnums(Enums enums) {
		try{
			this.sqlMapClientAss.insert("bai_enums.addEnums", enums);

        } catch (Exception e) {
			throw new RpcException("添加枚举出错", e);
		}
	}

	@Override
	public void editEnums(Enums enums) {
		try{
			this.sqlMapClientAss.update("bai_enums.editEnums", enums);

        } catch (Exception e) {
			throw new RpcException("编辑枚举出错", e);
		}
	}

}
