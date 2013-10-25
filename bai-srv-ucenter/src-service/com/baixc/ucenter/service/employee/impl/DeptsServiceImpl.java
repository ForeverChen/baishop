package com.baixc.ucenter.service.employee.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.orm.ibatis.SqlMapClientTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.rpc.RpcException;
import com.baixc.commons.service.BaseService;
import com.baixc.ucenter.model.employee.Depts;
import com.baixc.ucenter.service.employee.DeptsService;

public class DeptsServiceImpl extends BaseService implements DeptsService {

	private static final long serialVersionUID = -7713295501664179256L;

	@Resource
	protected SqlMapClientTemplate sqlMapClientAss;
	
	@Override
	public Depts getDepts(int deptId) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("deptId", deptId);
			Depts dept = (Depts)this.sqlMapClientAss.queryForObject("Depts.getDepts", params);
			return dept;
		}catch(Exception e){
			throw new RpcException("查询部门出错", e);
		}
	}

	@Override
	public Depts getDepts(String deptCode) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("deptCode", deptCode);
			Depts dept = (Depts)this.sqlMapClientAss.queryForObject("Depts.getDepts", params);
			return dept;
		}catch(Exception e){
			throw new RpcException("查询部门出错", e);
		}
	}

	@Override
	public List<Depts> getDeptsList() {
		return this.getDeptsList(null);
	}	

	@Override
	public List<Depts> getDeptsList(Map<String,Object> params) {
		try{
			@SuppressWarnings("unchecked")
			List<Depts> list = this.sqlMapClientAss.queryForList("Depts.getDepts", params);
			return list;			
		}catch(Exception e){
			throw new RpcException("查询部门出错", e);
		}
	}

	@Override
	public List<Depts> getDeptsListByPid(int deptPid) {
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("deptPid", deptPid);
		List<Depts> list = this.getDeptsList(params);
		return list;
	}


	@Override
	@Transactional
	public void delDepts(int deptId) {		
		Depts dept = this.getDepts(deptId);
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put("deptParentCode",dept.getDeptCode());
		
		List<Depts>	list = this.getDeptsList(params);		
		int[] deptIds = new int[list.size()+1];
		
		int i;
		for(i=0;i<list.size();i++){
			deptIds[i] = list.get(i).getDeptId();
		}
		deptIds[i] = deptId;		
		
		this.delDepts(deptIds);
	}
	
	@Override
	@Transactional
	public void delDepts(int[] deptIds) {
		try{
			Map<String,Object> params = new HashMap<String,Object>();
			params.put("deptIds", deptIds);
			
			this.sqlMapClientAss.delete("Depts.delDepts", deptIds);
			this.sqlMapClientAss.delete("AdminsDepts.delAdminsDepts", params);
		}catch(Exception e){
			throw new RpcException("删除部门出错", e);
		}
	}


	@Override
	@Transactional
	public void addDepts(Depts dept) {
		try{
			this.sqlMapClientAss.insert("Depts.addDepts", dept);

        }catch(DuplicateKeyException e){
			throw new RpcException("部门编号重复");
        } catch (Exception e) {
			throw new RpcException("添加部门出错", e);
		}
	}

	@Override
	@Transactional
	public void editDepts(Depts dept) {
		try{
			this.sqlMapClientAss.update("Depts.editDepts", dept);

        }catch(DuplicateKeyException e){
			throw new RpcException("部门编号重复");
        } catch (Exception e) {
			throw new RpcException("编辑部门出错", e);
		}
	}
	
	
	
	@Override
	public JSONObject getTreeDeptOfJSON() {
		final JSONObject json = new JSONObject();
		
//		try {
//			json.put("id", 0);
//			json.put("text", "组织架构");
//			json.put("deptId", 0);
//			json.put("deptName", "组织架构");
//			json.put("iconCls", "icon-dept");
//			json.put("children", new JSONArray());
//			json.put("cbbDept", new JSONArray());
//			
//			final List<Depts> list = this.getDeptsList();			
//			
//			//递归加载
//			TreeRecursiveHandle<Depts> treeRecursiveHandle = new TreeRecursiveHandle<Depts>(){
//				public void recursive(JSONObject treeNode) throws JSONException{
//					for(Depts dept : list){
//						if(dept.getDeptPid().equals(treeNode.getInt("id"))){					
//							
//							JSONObject node = JSONObject.fromObject(dept);
//							
//							node.put("id", dept.getDeptId());
//							node.put("text", dept.getDeptName());
//							node.put("expanded", true);
//							node.put("leaf", true);
//							node.put("iconCls", "icon-dept");
//							
//							//递归
//							this.recursive(node);
//							
//							//添加到树中
//							JSONArray children;
//							try {
//								children = treeNode.getJSONArray("children");
//							} catch (JSONException e) {
//								treeNode.put("children", new JSONArray());
//								children = treeNode.getJSONArray("children");
//							}							
//							children.add(node);
//							treeNode.put("leaf", false);						
//							
//							//添加节点到列表中
//							json.getJSONArray("cbbDept").add(JSONArray.fromObject(new Object[]{dept.getDeptId(), dept.getDeptName()}));
//						}
//					}
//				}
//			};
//			
//			treeRecursiveHandle.recursive(json);
//			
//		} catch (Exception e) {
//			throw new RpcException("查询部门出错", e);
//		}
		
		return json;
	}

}
