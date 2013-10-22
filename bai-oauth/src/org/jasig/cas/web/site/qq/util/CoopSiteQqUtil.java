package org.jasig.cas.web.site.qq.util;

import java.util.HashMap;
import java.util.Map;

public class CoopSiteQqUtil {
	
	public static String GATEWAY = "https://graph.qq.com/oauth2.0/";
	public static String RESPONSE_TYPE = "code";
	public static final String CLIENT_ID="100233683";//申请QQ登录成功后，分配给应用的appid
	private static final String CLIENT_SECRET="3f1aa7c6c8c25df533b6e38f7ca11085";//申请QQ登录成功后，分配给网站的appkey
	private static final String GRANT_TYPE="authorization_code";//授权类型
	private static final String STATE="test";//client端的状态值
    private static volatile CoopSiteQqUtil instance;
	
	public static CoopSiteQqUtil get(){
		if(instance == null){
			synchronized (CoopSiteQqUtil.class) {
				if(instance == null){
					instance = new CoopSiteQqUtil();
				}
			}
		}
		return instance;
	}
    
	/**
	 * 生成QQ登录地址
	 * @param return_url
	 * @return String
	 * @throws Exception
	 */
	public String buildRedirectUrl(String return_url){
		String redirectUrl = GATEWAY;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("response_type", RESPONSE_TYPE);
			params.put("client_id", CLIENT_ID);
			params.put("redirect_url", return_url);
			return redirectUrl += "authorize?"+"response_type=" + params.get("response_type") + "&client_id=" + params.get("client_id") +"&redirect_uri="+params.get("redirect_url");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * 生成获取token的地址
	 * @param code 上一步返回的authorization code
	 * @return String
	 */
	public String buildTokenUrl(String code,String return_url) {
		String redirectUrl = GATEWAY;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("grant_type", GRANT_TYPE);
			params.put("client_id", CLIENT_ID);
			params.put("client_secret", CLIENT_SECRET);
			params.put("redirect_url", return_url);
			params.put("state", STATE);
			return redirectUrl += "token?"+"grant_type=" + params.get("grant_type") + "&client_id=" + params.get("client_id") +"&redirect_uri="+params.get("redirect_url")+"&client_secret="+params.get("client_secret")+"&state="+params.get("state")+"&code="+code;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
}
