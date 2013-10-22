package com.baixc.commons.service.configcenter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.model.configcenter.Enums;
import com.baixc.commons.service.BaseService;
import com.baixc.commons.service.configcenter.EnumsService;

public class EnumsServiceImpl extends BaseService implements EnumsService {

	private static final long serialVersionUID = 6431588222617603394L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientCommons;

	@Override
	public Enums getEnums(int enumsId) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("enumsId", enumsId);
			Enums obj = (Enums)this.sqlMapClientCommons.queryForObject("bai_enums.getEnums", params);
			return obj;
		}catch(Exception e){
			throw new RpcException("查询系统枚举出错", e);
		}
	}

	@Override
	public Enums getEnums(String enumsType, String enumsName) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("enumsType", enumsType);
			params.put("enumsName", enumsName);
			Enums obj = (Enums)this.sqlMapClientCommons.queryForObject("bai_enums.getEnums", params);
			return obj;
		}catch(Exception e){
			throw new RpcException("查询系统枚举出错", e);
		}
	}

	@Override
	public List<Enums> getEnumsList() {
		return this.getEnumsList(new HashMap<String,Object>());
	}

	@Override
	public List<Enums> getEnumsList(String enumsType) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("enumsType", enumsType);

		List<Enums> list = this.getEnumsList(params);		
		return list;
	}
	
	@Override
	public List<Enums> getEnumsList(Map<String, Object> params) {
		try{
			if(params==null)
				params = new HashMap<String,Object>();
			
			@SuppressWarnings("unchecked")
			List<Enums> list = this.sqlMapClientCommons.queryForList("bai_enums.getEnums", params);
			return list;
		}catch(Exception e){
			throw new RpcException("查询系统枚举出错", e);
		}
	}

	@Override
	public void delEnums(int enumsId) {
		this.delEnums(new int[]{enumsId});
	}

	@Override
	public void delEnums(int[] enumsIds) {
		try{
			this.sqlMapClientCommons.delete("bai_enums.delEnums", enumsIds);
		}catch(Exception e){
			throw new RpcException("删除系统枚举出错", e);
		}
	}

	@Override
	public void addEnums(Enums enums) {
		try{
			this.sqlMapClientCommons.insert("bai_enums.addEnums", enums);

        }catch(DuplicateKeyException e){
			throw new RpcException("枚举类型与枚举码重复", e);	
        } catch (Exception e) {
			throw new RpcException("添加系统枚举出错", e);
		}
	}

	@Override
	public void editEnums(Enums enums) {
		try{
			this.sqlMapClientCommons.update("bai_enums.editEnums", enums);

        }catch(DuplicateKeyException e){
			throw new RpcException("枚举类型与枚举码重复", e);	
        } catch (Exception e) {
			throw new RpcException("编辑系统枚举出错", e);
		}
	}

}
