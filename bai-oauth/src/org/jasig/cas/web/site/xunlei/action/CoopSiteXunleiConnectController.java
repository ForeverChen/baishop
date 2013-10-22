package org.jasig.cas.web.site.xunlei.action;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.web.site.xunlei.util.CoopSiteXunleiUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class CoopSiteXunleiConnectController extends MultiActionController{
	private static final String HTTP_METHOD_GET = "GET";
	private static final String APP_KEY = "EyXYPw0GeOahMSFm2jVi9JC8Y3ir6GWu";
	private static final String OAUTH_VERSION = "1.0";
	private static final String APP_SECRET="YyqXqNHazdLy2F2c63slv1gCeMNoMsaC";
	private String returnUrl;
	private static final String REQUEST_TOKEN_URL="http://api.i.xunlei.com/oauth/request_token?";
	private static final String AUTHORIZE_URL="http://api.i.xunlei.com/oauth/authorize?";
	private static final String ACCESS_TOKEN_URL="http://api.i.xunlei.com/oauth/access_token?";
	private static final String USER_INFO_URL="http://api.i.xunlei.com/oauth/user_info?";
	private static final String SIGN_METHOD="HMAC-SHA1";
	private String targetUrl;
	private String redirectUrl;
	private String tokenUrl;
	private String callbackUrl;
	
	@NotNull
	private String simulateLoginView;
	
	/** 系统基础配置，读取app.conf里的信息 */
	@Resource 	
	protected Properties appConf;
	
	
	public ModelAndView excute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		callbackUrl = request.getParameter("callbackUrl");
		this.buildRedirectUrl(request,response);
		return new ModelAndView("redirect:"+redirectUrl);
	}
	
	/**
	 * 当迅雷登录授权成功后回调的接口
	 * @return String
	 * @throws Exception
	 */
	public ModelAndView callback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		callbackUrl=request.getParameter("callbackUrl");
		//获取参数
		String oauthToken=request.getParameter("oauth_token");
		String oauthVerifier=request.getParameter("oauth_verifier");
		
		LinkedHashMap<String,String> params=new LinkedHashMap<String,String>();
		params.put("oauth_consumer_key", APP_KEY);//应用key
		params.put("oauth_nonce", String.valueOf(new Random().nextInt(100000))+"baixc"+String.valueOf(new Random().nextInt(100000)));//随机串
		params.put("oauth_signature_method", SIGN_METHOD);//签名方式
		params.put("oauth_timestamp",String.valueOf(new Date().getTime()).substring(0,10));//时间戳
		params.put("oauth_token", oauthToken);//token
		params.put("oauth_verifier", oauthVerifier);
		params.put("oauth_version", OAUTH_VERSION);//版本
		
		//获取access_token
		JSONObject jsonObject=new JSONObject();
		jsonObject=getAccessToken(params,request);
		
		String accessToken=jsonObject.get("oauth_token").toString();//获取access_token
		String accessTokenSecret=jsonObject.get("oauth_token_secret").toString();//获取access_token_secret
		
		//获取用户信息
		JSONObject userInfo = getUserInfo(accessToken,accessTokenSecret);
		
		String nickName="";
		String openId=accessToken;
		
		if(userInfo.get("nickname")!=null)
			nickName=CoopSiteXunleiUtil.get().html(new String(userInfo.get("nickname").toString().getBytes("UTF-8"),"UTF-8"));//获取昵称		
		
		ModelAndView simulateView = new ModelAndView(simulateLoginView);
		simulateView.addObject("username", "COOP_USER_XUNLEI");
		simulateView.addObject("password", "XUNLEI&"+openId+"&"+nickName);
		simulateView.addObject("service",callbackUrl);
		return simulateView;
	}
	
	 /*
     * 获取requestToken
     * @return 
     */
	public JSONObject getRequestToken() throws Exception {
		
		JSONObject jsonObject=new JSONObject();
		
		this.buildCallbackUrl();//生成回调地址
		
		//设置url参数
		LinkedHashMap<String,String> params=new LinkedHashMap<String,String>();
		params.put("oauth_callback", returnUrl);//回调地址
		params.put("oauth_consumer_key", APP_KEY);//应用key
		params.put("oauth_nonce", String.valueOf(new Random().nextInt(100000))+"baixc"+String.valueOf(new Random().nextInt(100000)));//随机串
		params.put("oauth_signature_method", SIGN_METHOD);//签名方式
		params.put("oauth_timestamp",String.valueOf(new Date().getTime()).substring(0, 10));//时间戳
		params.put("oauth_version", OAUTH_VERSION);//版本
		
		//生成baseString
		String baseString=CoopSiteXunleiUtil.get().generateBaseString(params, HTTP_METHOD_GET, REQUEST_TOKEN_URL);
		
		//生成签名
		String sign=CoopSiteXunleiUtil.get().createSig(baseString, APP_SECRET+"&");
		
		params.put("oauth_signature", sign);//签名
		
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(CoopSiteXunleiUtil.get().getTokenUrl(params, REQUEST_TOKEN_URL));// 以GET方式发起请求
		try {
			client.executeMethod(getMethod);
			
			String result=null;
			result=getMethod.getResponseBodyAsString();//获取结果字符串
			//转换为json字符串
			String resultJson=null;
			resultJson="{"+result.replace("=", ":'").replace("&", "',")+"'}";
			
			
			//转换为json对象
			jsonObject=JSONObject.fromObject(resultJson);
		
		} 
		catch (Exception e) {
			e.printStackTrace(); 
		} 
		finally {
			getMethod.releaseConnection();// 释放连接
		}
		return jsonObject;
	}
	
	/*
     * 获取accessToken
     * @return 
     */
	public JSONObject getAccessToken(Map<String,String> params,HttpServletRequest request) throws Exception {
		
		JSONObject jsonObject=new JSONObject();
		
		//生成baseString
		String baseString=CoopSiteXunleiUtil.get().generateBaseString(params, HTTP_METHOD_GET, ACCESS_TOKEN_URL);
		String oauthTokenSecret=request.getSession().getAttribute("oauthTokenSecret").toString();
		request.getSession().removeAttribute("oauthTokenSecret");
		
		//生成签名
		String sign=CoopSiteXunleiUtil.get().createSig(baseString, APP_SECRET+"&"+oauthTokenSecret);
		
		params.put("oauth_signature", sign);//签名
		
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(CoopSiteXunleiUtil.get().getTokenUrl(params, ACCESS_TOKEN_URL));// 以GET方式发起请求
		try {
			client.executeMethod(getMethod);
			
			String result=null;
			result=getMethod.getResponseBodyAsString();//获取结果字符串
			//转换为json字符串
			String resultJson=null;
			resultJson="{"+result.replace("=", ":'").replace("&", "',")+"'}";
			
			
			//转换为json对象
			jsonObject=JSONObject.fromObject(resultJson);
		
		} 
		catch (Exception e) {
			e.printStackTrace(); 
		} 
		finally {
			getMethod.releaseConnection();// 释放连接
		}
		return jsonObject;
	}
	
	/*
	 * 获取用户基本信息
	 * @param accessToken获取用户信息的唯一标识
	 * @param accessTokenSecret 密钥
	 */
	public JSONObject getUserInfo(String accessToken,String accessTokenSecret) throws Exception {
		JSONObject jsonObject=new JSONObject();
		
		//设置url参数
		LinkedHashMap<String,String> params=new LinkedHashMap<String,String>();
		params.put("oauth_consumer_key", APP_KEY);//应用key
		params.put("oauth_nonce", String.valueOf(new Random().nextInt(100000))+"baixc"+String.valueOf(new Random().nextInt(100000)));//随机串
		params.put("oauth_signature_method", SIGN_METHOD);//签名方式
		params.put("oauth_timestamp",String.valueOf(new Date().getTime()).substring(0,10));//时间戳
		params.put("oauth_token", accessToken);
		params.put("oauth_version", OAUTH_VERSION);//版本
		
		//生成baseString
		String baseString=CoopSiteXunleiUtil.get().generateBaseString(params, HTTP_METHOD_GET, USER_INFO_URL);
		
		//生成签名
		String sign=CoopSiteXunleiUtil.get().createSig(baseString, APP_SECRET+"&"+accessTokenSecret);
		
		params.put("oauth_signature", sign);//签名
		
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
		GetMethod getMethod = new GetMethod(CoopSiteXunleiUtil.get().getTokenUrl(params, USER_INFO_URL));// 以GET方式发起请求
		try {
			client.executeMethod(getMethod);
			
			String resultJson=null;
			resultJson=getMethod.getResponseBodyAsString();//获取json字符串
				
			//转换为json对象
			jsonObject=JSONObject.fromObject(resultJson);
		
		} 
		catch (Exception e) {
			e.printStackTrace();
			return jsonObject;
		} 
		finally {
			getMethod.releaseConnection();// 释放连接
		}
		return jsonObject;
	}
	
	/**
	 * 生成迅雷授权成功后回调地址
	 * @throws Exception
	 */
	private void buildCallbackUrl(){
		returnUrl =  appConf.getProperty("app.cas.url")
		+ "/xunleiLogin?method=callback&callbackUrl="+callbackUrl;
	}
	
	/**
	 * 生成迅雷登录地址
	 * @throws Exception
	 */
	private void buildRedirectUrl(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
		String oauthToken="";
		String oauthTokenSecret="";
		
		//获取oauthToken
		if(getRequestToken()!=null) {
			JSONObject jsonObject=getRequestToken();
			oauthToken=jsonObject.get("oauth_token").toString();
			oauthTokenSecret=jsonObject.get("oauth_token_secret").toString();
	    }
		
		//拼接url
		if(StringUtils.isNotBlank(oauthToken))
			redirectUrl = AUTHORIZE_URL+"oauth_token="+oauthToken;
		
		//保存oauthTokenSecret
		if(StringUtils.isNotBlank(oauthTokenSecret))
			request.getSession().setAttribute("oauthTokenSecret", oauthTokenSecret);
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokeneUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public String getReturnUrl() {
		return returnUrl;
	}

	public void setReturnUrl(String returnUrl) {
		this.returnUrl = returnUrl;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getSimulateLoginView() {
		return simulateLoginView;
	}

	public void setSimulateLoginView(String simulateLoginView) {
		this.simulateLoginView = simulateLoginView;
	}
	
}
