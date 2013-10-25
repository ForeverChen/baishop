package com.baixc.commons.service.configcenter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.baixc.commons.model.configcenter.City;

/**
 * 区域管理服务接口
 * @author Linpn
 */
public interface CityService extends Serializable {

	/**
	 * 获取城市
	 * @param id 城市ID
	 * @return 返回城市对象
	 */
	public City getCity(int id);

	/**
	 * 获取城市
	 * @param code 城市编号
	 * @return 返回城市对象
	 */
	public City getCity(String code);
	
	/**
	 * 获取城市列表
	 * @return 返回城市列表
	 */
	public List<City> getCityList();
	
	/**
	 * 获取城市列表
	 * @param params 查询参数
	 * @return 返回城市列表
	 */
	public List<City> getCityList(Map<String,Object> params);
	
	/**
	 * 通过父节点获取城市列表
	 * @param cityPid 爷节点ID
	 * @return 返回城市列表
	 */
	public List<City> getCityListByPid(int cityPid);
	
	/**
	 * 删除城市,及该城市下的子节点
	 * @param id 城市ID
	 */
	public void delCity(int id);
	

	/**
	 * 删除城市，只删除ids所列的城市，不包括子城市
	 * @param ids 城市ID列表
	 */
	public void delCity(int[] ids);
	
	/**
	 * 添加城市
	 * @param city 城市对象
	 */
	public void addCity(City city);	
	
	/**
	 * 修改城市
	 * @param city 城市对象
	 */
	public void editCity(City city);	
	
}
