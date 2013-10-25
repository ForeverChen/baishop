package com.baixc.commons.service.configcenter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.baixc.commons.model.configcenter.Enums;

/**
 * 枚举服务接口
 * @author Linpn
 */
public interface EnumsService extends Serializable {
	
	/**
	 * 获取通用业务群的 单元素枚举(K-V)
	 * @param key 枚举键
	 * @return 返回 单元素枚举(K-V)
	 */
	public Enums getEnums(String key);
	
	/**
	 * 获取通用业务群的 多元素枚举(组)中的元素
	 * @param group 枚举分组; 为空表示单元素枚举(K-V)，不为空表示多元素枚举(组)
	 * @param key 枚举键
	 * @return 返回 多元素枚举(组)中的元素
	 */
	public Enums getEnums(String group, String key);

	/**
	 * 获取指定业务群的 单元素枚举(K-V)
	 * @param biz 系统业务群; 为空表示默认(通用)业务群
	 * @param key 枚举键
	 * @return 返回 单元素枚举(K-V)
	 */
	public Enums getEnumsByBiz(String biz, String key);
	
	/**
	 * 获取指定业务群的 多元素枚举(组)中的元素
	 * @param biz 系统业务群; 为空表示默认(通用)业务群
	 * @param group 枚举分组; 为空表示单元素枚举(K-V)，不为空表示多元素枚举(组)
	 * @param key 枚举键
	 * @return 返回 多元素枚举(组)中的元素
	 */
	public Enums getEnumsByBiz(String biz, String group, String key);
	
	
	/**
	 * 获取通用业务群的 多元素枚举(组)
	 * @param group 枚举分组; 为空表示单元素枚举(K-V)，不为空表示多元素枚举(组)
	 * @return 返回 多元素枚举(组)
	 */
	public List<Enums> getEnumsGroup(String group);

	/**
	 * 获取指定业务群的 多元素枚举(组)
	 * @param biz 系统业务群; 为空表示默认(通用)业务群
	 * @param group 枚举分组; 为空表示单元素枚举(K-V)，不为空表示多元素枚举(组)
	 * @return 返回 多元素枚举(组)
	 */
	public List<Enums> getEnumsGroupByBiz(String biz, String group);
	
	
	
	
	/**
	 * 获取枚举
	 * @param id 枚举ID
	 * @return 返回枚举对象
	 */
	public Enums getEnums(int id);
	
	/**
	 * 获取枚举列表
	 * @param params 查询参数
	 * @return 返回枚举列表
	 */
	public List<Enums> getEnumsList(Map<String,Object> params);
	
	
	/**
	 * 删除枚举
	 * @param id 枚举ID
	 */
	public void delEnums(int id);
	
	/**
	 * 删除枚举
	 * @param ids 参数ID列表
	 */
	public void delEnums(int[] ids);
	
	/**
	 * 添加枚举
	 * @param enums 枚举对象
	 */
	public void addEnums(Enums enums);	
	
	/**
	 * 修改枚举
	 * @param enums 枚举对象
	 */
	public void editEnums(Enums enums);	

}
