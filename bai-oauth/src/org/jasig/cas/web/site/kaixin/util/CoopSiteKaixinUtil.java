package org.jasig.cas.web.site.kaixin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class CoopSiteKaixinUtil {
	
	public static String RESPONSE_TYPE = "code";
	public static final String APP_KEY = "1634594364713a58fedcb729d335198a";//申请开心网时获得的APIkey
    private static volatile CoopSiteKaixinUtil instance;
	
	public static CoopSiteKaixinUtil get(){
		if(instance == null){
			synchronized (CoopSiteKaixinUtil.class) {
				if(instance == null){
					instance = new CoopSiteKaixinUtil();
				}
			}
		}
		return instance;
	}
		
	/**
	 * 生成开心登录地址
	 * @param return_url
	 * @return String
	 * @throws Exception
	 */
	public String buildRedirectUrl(String return_url,String gateway){
		String redirectUrl = gateway;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("response_type", RESPONSE_TYPE);
			params.put("client_id", APP_KEY);
			params.put("redirect_url", return_url);
			return redirectUrl +="response_type=" + params.get("response_type") + "&client_id=" + params.get("client_id") +"&redirect_uri="+params.get("redirect_url");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * 生成Url
	 * @param params url参数
	 * @param url 请求url
	 * @return 生成的url
	 * @throws UnsupportedEncodingException 
	 */
	public String getUrl(Map<String,String> params,String url) throws UnsupportedEncodingException {
		int i=0;
		for(Map.Entry<String, String> entry:params.entrySet()) {
			if(i>0)
				url+="&"+URLEncoder.encode(entry.getKey(),"UTF-8")+"="+URLEncoder.encode(entry.getValue(),"UTF-8");
			else
				url+=URLEncoder.encode(entry.getKey(),"UTF-8")+"="+URLEncoder.encode(entry.getValue(),"UTF-8");
			i++;
		}
		return url;
	}
}
