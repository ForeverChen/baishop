package com.baixc.commons.service.configcenter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.model.configcenter.Params;
import com.baixc.commons.service.BaseService;
import com.baixc.commons.service.configcenter.ParamsService;

public class ParamsServiceImpl extends BaseService implements ParamsService {

	private static final long serialVersionUID = 2378024773454144950L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientCommons;

	@Override
	public Params getParams(int paramsId) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("paramsId", paramsId);
			Params obj = (Params)this.sqlMapClientCommons.queryForObject("bai_params.getParams", params);
			return obj;
		}catch(Exception e){
			throw new RpcException("查询系统参数出错", e);
		}
	}
	

	@Override
	public Params getParams(String paramsName) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("paramsName", paramsName);
			Params obj = (Params)this.sqlMapClientCommons.queryForObject("bai_params.getParams", params);
			return obj;
		}catch(Exception e){
			throw new RpcException("查询系统参数出错", e);
		}
	}


	@Override
	public List<Params> getParamsList() {
		return this.getParamsList(null);
	}

	@Override
	public List<Params> getParamsList(Map<String, Object> map) {
		try{
			if(map==null)
				map = new HashMap<String,Object>();
			
			@SuppressWarnings("unchecked")
			List<Params> list = this.sqlMapClientCommons.queryForList("bai_params.getParams", map);		
			return list;
		}catch(Exception e){
			throw new RpcException("查询系统参数出错", e);
		}
	}
	

	@Override
	public void delParams(int paramsId) {
		this.delParams(new int[]{paramsId});
	}


	@Override
	public void delParams(int[] paramsIds) {
		try{
			this.sqlMapClientCommons.delete("bai_params.delParams",paramsIds);		
		}catch(Exception e){
			throw new RpcException("删除系统参数出错", e);
		}
	}

	
	@Override
	public void addParams(Params params) {
		try{
			this.sqlMapClientCommons.insert("bai_params.addParams",params);		

        }catch(DuplicateKeyException e){
			throw new RpcException("参数名重复", e);	
        } catch (Exception e) {
			throw new RpcException("添加系统参数出错", e);
		}
	}
	

	@Override
	public void editParams(Params params) {
		try{
			this.sqlMapClientCommons.update("bai_params.editParams",params);		

        }catch(DuplicateKeyException e){
			throw new RpcException("参数名重复", e);	
        } catch (Exception e) {
			throw new RpcException("编辑系统参数出错", e);
		}
	}


}
