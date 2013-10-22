package com.baixc.framework.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期处理帮助类.
 *
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: 恒生电子股份有限公司</p>
 * @author jinrey
 * @date 2011-8-23 上午10:17:04 
 * @version 1.0
 */
public final class DateUtils {
	public static long ONE_DAY = 24*3600000;
	public static long ONE_MINUTE = 60000;//分
	public static long ONE_HOUR = 3600000;//小时
	/**
	 * yyyyMMdd
	 */
	public static final String DATE_BANK_FORMAT = "yyyyMMdd";
	/**
	 * yyyy-MM-dd
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	/**
	 * yyyy-MM-dd HH:mm:ss
	 */
	public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * yyyy-MM-dd HH:mm
	 */
	public static final String DATETIME1_FORMAT = "yyyy-MM-dd HH:mm";
	/**
	 * yy-MM-dd HH:mm
	 */
	public static final String DATETIME2_FORMAT = "yy-MM-dd HH:mm";
	/**
	 * yyyyMMddHHmmss
	 */
	public static final String DATETIME3_FORMAT = "yyyyMMddHHmmss";
	/**
	 * yyyy年MM月dd日 HH:mm:ss
	 */
	public static final String DATETIME4_FORMAT = "yyyy年MM月dd日 HH:mm:ss";
	
	/**
	 * yy年MM月dd日 HH:mm
	 */
	public static final String DATETIME5_FORMAT = "yy年MM月dd日 HH:mm";
	
	/**
	 * yy年MM月dd日
	 */
	public static final String DATETIME6_FORMAT = "yy年MM月dd日";
	
	/**
	 * 星期信息
	 */
	public static final String WEEK_FORMAT = "EEE";
	
	
	private DateUtils() {
		super();
	}
	
	

	
	/**
	 * 增加年
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addYear(Date date,int i){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR,i);
		return c.getTime();		
	}
	
	/**
	 * 增加月
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addMonth(Date date,int i){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH,i);
		return c.getTime();
	}
	
	/**
	 * 增加天
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addDay(Date date,int i){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE,i);
		return c.getTime();		
	}
	
	/**
	 * 增加小时
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addHour(Date date,int i){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.HOUR,i);
		return c.getTime();		
	}
	
	/**
	 * 增加分钟
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addMinute(Date date,int i){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MINUTE,i);
		return c.getTime();		
	}
	
	/**
	 * 增加秒
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date addSecond(Date date,int i){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.SECOND,i);
		return c.getTime();		
	}
	
	
	/**
	 * 获取年
	 * @param date
	 * @return
	 */
	public static int getYear(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.YEAR);		
	}
	
	/**
	 * 获取月
	 * @param date
	 * @return
	 */
	public static int getMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.MONTH);		
	}

	
	/**
	 * 获取天
	 * @param date
	 * @return
	 */
	public static int getDay(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.get(Calendar.DAY_OF_MONTH);		
	}
	

	/**
	 * 当月的第一天
	 * @param date
	 * @return
	 */
	public static Date firstDayOfMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH,1);
		return c.getTime();		
	}
	
	
	/**
	 * 当月的最后一 天
	 * @param date
	 * @return
	 */
	public static Date lastDayOfMonth(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH,1);
		c.add(Calendar.MONTH,1);
		c.add(Calendar.DATE,-1);
		return c.getTime();		
	}
	
	
  	/**
   	 * 得到当前日期字符串
     * @return
     */
  	public static String getTodayString(){
    	return format(new Date());
  	}
  	
  	
  	/**
  	 * 得到当前日期字符串
  	 * @return
  	 */
  	public static String getTodayString(String dateFormat){
  		return format(new Date(), dateFormat);
  	}


	/**
	 * 将date的实例转换为date型字符串
	 * @param time
	 * @return
	 */
	public static String format(Date date){
		return  format(date,DATETIME_FORMAT);
	}

	
  	/**
  	 * 将日期转换为指定格式的字符串
  	 * @param date
  	 * @param dateFormat
  	 * @return
  	 */
  	public static String format(Date date,String dateFormat){
		if(date!=null){
			SimpleDateFormat format = new SimpleDateFormat(dateFormat);
			String dateString = format.format(date);
			return dateString;
		}
		return null;
  	}

  	
  	/**
  	 * 将日期字符串转换为日期
  	 * @param dateString
  	 * @return
  	 * @throws Exception
  	 */
  	public static Date parse(String dateString) throws Exception{
  		return parse(dateString,DATETIME_FORMAT);
  	}

  	
  	/**
  	 * 将日期字符串按指定格式转换为日期
  	 * @param dateString
  	 * @param dateFormat
  	 * @return
  	 * @throws ParseException 
  	 * @throws Exception
  	 */
  	public static Date parse(String dateString,String dateFormat) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		return format.parse(dateString);
  	}
  	
  	
  	/**
  	 * 获取时间间隔
  	 * @param begin 开始时间
  	 * @param end 结束时间
  	 * @param field 返回类型 Calendar.DATE、Calendar.HOUR、Calendar.MINUTE、Calendar.SECOND、Calendar.MILLISECOND
  	 * @return
  	 */
  	public static long getBetweenTime(Date begin, Date end, int field){
  		long between = (end.getTime() - begin.getTime());  		
  		switch(field){
  		case Calendar.DATE:
  			return between / (24 * 60 * 60 * 1000);
  		case Calendar.HOUR:
  			return between / (60 * 60 * 1000); 
  		case Calendar.MINUTE:
  			return between / (60 * 1000); 
  		case Calendar.SECOND:
  			return between / (1000); 
  		default:
  			return between;
  		}
  	}

  	
	/**
	 * 生成未来几天的日期列表.
	 * @param days 天数
	 * @return 日期的字符串列表.
	 */
	public static List<String> getDateList(int days){
		List<String> dates=new ArrayList<String>();
		Date d=new Date();
		for(int i=0;i<days;i++){
			Date nextDay=addDay(d,i+1);
			dates.add(format(nextDay));
		}
		return dates;
	}

}
