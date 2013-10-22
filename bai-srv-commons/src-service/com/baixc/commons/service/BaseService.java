package com.baixc.commons.service;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.baixc.framework.utils.ConvertUtils;

/**
 * service基类，主要编写service公共方法
 * @author Linpn
 */
public abstract class BaseService implements Serializable {

	private static final long serialVersionUID = -2489358585140025260L;

	/**
	 * log4j对象
	 */
	protected final Log logger = LogFactory.getLog(getClass());
	
	
	//-------------------------------------- service 公共方法 -------------------------------------------//


	/**
	 * 将排序的map参数转成sql中order by所需要的格式
	 * @param sorters service方法中的排序参数
	 * @return 返回sql语句中order by所需要的格式
	 */
	public String getDbSort(Map<String, String> sorters){
		if(sorters==null)
			return "";
		
		StringBuilder sbSort = new StringBuilder();	
		for(Map.Entry<String, String> entry : sorters.entrySet()){
			if(sbSort.length()>0)
				sbSort.append(",");
			
			String sort = ConvertUtils.toDatabaseField(entry.getKey());
			String dir = entry.getValue();
			
			if(!StringUtils.isBlank(sort)){
				sbSort.append(sort);			
				if(!StringUtils.isBlank(dir)){	
					sbSort.append(" ");
					sbSort.append(entry.getValue());
				}
			}else{
				return "";
			}
		}
		
		return sbSort.toString();
	}
	
	
	/**
	 * 将排序的map参数转成sql中order by所需要的格式
	 * @param sorters service方法中的排序参数
	 * @return 返回sql语句中order by所需要的格式
	 */
	public String getDbSortNoConver(Map<String, String> sorters){
		if(sorters==null)
			return "";
		
		StringBuilder sbSort = new StringBuilder();	
		for(Map.Entry<String, String> entry : sorters.entrySet()){
			if(sbSort.length()>0)
				sbSort.append(",");
			
			String sort = entry.getKey();
			String dir = entry.getValue();
			
			if(!StringUtils.isBlank(sort)){
				sbSort.append(sort);			
				if(!StringUtils.isBlank(dir)){	
					sbSort.append(" ");
					sbSort.append(entry.getValue());
				}
			}else{
				return "";
			}
		}	
		return sbSort.toString();
	}
	
}
