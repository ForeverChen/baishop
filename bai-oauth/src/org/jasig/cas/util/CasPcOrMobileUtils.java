package org.jasig.cas.util;

import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class CasPcOrMobileUtils {

	/**
	 * 判断是否是手机访问
	 * @return 如果是则返回true，否则返回false
	 */
	public static boolean isMobile(HttpServletRequest request) {
//		boolean isMobile = PcOrMobileUtils.isMobile(request);
		boolean isMobile = false;
		
		try{
			Pattern pattern0 = Pattern.compile(".*http://.*/mobile/.*");
			Pattern pattern1 = Pattern.compile(".*http://m.baixc.com.*");
			Pattern pattern2 = Pattern.compile(".*http://m.test.baixc.com.*");
	 
			String referer = request.getHeader("Referer");
			if(referer!=null){
				isMobile = isMobile || pattern0.matcher(referer).matches() || pattern1.matcher(referer).matches() || pattern2.matcher(referer).matches();
			}
			String service = request.getParameter("service");
			if(service!=null){
				isMobile = isMobile || pattern0.matcher(service).matches() || pattern1.matcher(service).matches() || pattern2.matcher(service).matches();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return isMobile;
	}
	
	
	/**
	 * 是否需要推送登录
	 * @param pushUrl 推送的URL
	 * @param redirectUrl 回跳的URL
	 * @return
	 */
	public static boolean isPush(String pushUrl, String redirectUrl){
		//pushUrl为空，则不推送
		if(StringUtils.isBlank(pushUrl) || StringUtils.isBlank(redirectUrl))
			return false;
		
		//两个URL是同一个系统时，则不推送
		if(redirectUrl.startsWith(pushUrl.replaceAll("(.*)(oauth_check)", "$1")))
			return false;
		
		return true;
	}
}
