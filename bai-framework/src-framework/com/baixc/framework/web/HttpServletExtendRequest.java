package com.baixc.framework.web;

import java.math.BigDecimal;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartRequest;


public interface HttpServletExtendRequest extends HttpServletRequest,
		MultipartRequest {
	
	/**
	 * 获取原生的HttpServletRequest
	 * @return 返回HttpServletRequest对象
	 */
	public HttpServletRequest getRequest();
	
	/**
	 * 获取参数,并去空格
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public String getParameter(String name);
	
	/**
	 * 获取参数,并去空格
	 * @param name 参数名
	 * @param charsetName 编码，如UTF-8
	 * @return 返回参数值
	 */
	public String getParameter(String name, String charsetName);
	
	/**
	 * 获取参数,并去空格
	 * @param name 参数名
	 * @param charsetName 编码，如UTF-8
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public String getParameter(String name, String charsetName, String defaultValue);

	/**
	 * 获取boolean型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Boolean getBooleanParameter(String name);
	
	/**
	 * 获取boolean型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Boolean getBooleanParameter(String name, Boolean defaultValue);
	
	/**
	 * 获取boolean型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @param equalsValue 比较的值
	 * @return 返回参数值
	 */
	public Boolean getBooleanParameter(String name, Boolean defaultValue, String equalsValue);
	
	/**
	 * 获取Character型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Character getCharParameter(String name);

	/**
	 * 获取Character型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Character getCharParameter(String name, Character defaultValue);
	
	/**
	 * 获取Byte型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Byte getByteParameter(String name);

	/**
	 * 获取Byte型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Byte getByteParameter(String name, Byte defaultValue);

	/**
	 * 获取Short型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Short getShortParameter(String name);

	/**
	 * 获取Short型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Short getShortParameter(String name, Short defaultValue);
	
	/**
	 * 获取Integer型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Integer getIntParameter(String name);
	
	/**
	 * 获取Integer型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Integer getIntParameter(String name, Integer defaultValue);
	
	/**
	 * 获取Long型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Long getLongParameter(String name);
	
	/**
	 * 获取Long型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Long getLongParameter(String name, Long defaultValue);
	
	/**
	 * 获取Float型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Float getFloatParameter(String name);
	
	/**
	 * 获取Float型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Float getFloatParameter(String name, Float defaultValue);
	
	/**
	 * 获取Double型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Double getDoubleParameter(String name);
	
	/**
	 * 获取Double型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Double getDoubleParameter(String name, Double defaultValue);
	
	/**
	 * 获取BigDecimal型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public BigDecimal getBigDecimalParameter(String name);
	
	/**
	 * 获取BigDecimal型参数
	 * @param name 参数名
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public BigDecimal getBigDecimalParameter(String name, BigDecimal defaultValue);
	
	/**
	 * 获取Date型参数
	 * @param name 参数名
	 * @return 返回参数值
	 */
	public Date getDateParameter(String name);

	/**
	 * 获取Date型参数
	 * @param name 参数名
	 * @param pattern 日期格式
	 * @return 返回参数值
	 */
	public Date getDateParameter(String name, String pattern);
	
	/**
	 * 获取Date型参数
	 * @param name 参数名
	 * @param pattern 日期格式
	 * @param defaultValue 默认值
	 * @return 返回参数值
	 */
	public Date getDateParameter(String name, String pattern, Date defaultValue);
	
	
	/**
	 * 获取request绑定后的对象
	 * @param clazz 要绑定的类型
	 * @return 返回绑定后的对象实例
	 */
	<T> T getBindObject(Class<T> clazz);
	
	/**
	 * 获取request绑定后的对象
	 * @param clazz 要绑定的类型
	 * @param objectName 要绑定的参数前缀，如要绑定user.username参数，则传user字符串进去
	 * @return 返回绑定后的对象实例
	 */
	<T> T getBindObject(Class<T> clazz, String objectName);
	
	
}
