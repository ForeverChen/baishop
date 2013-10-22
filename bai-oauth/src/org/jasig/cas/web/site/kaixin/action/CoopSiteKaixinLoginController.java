package org.jasig.cas.web.site.kaixin.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.jasig.cas.util.CasProtocolSocketFactory;
import org.jasig.cas.web.site.kaixin.util.CoopSiteKaixinUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class CoopSiteKaixinLoginController extends MultiActionController {
	private static final String GRANT_TYPE = "authorization_code";
	private static final String SECRET_KEY = "df3d7c1f8a91433e401bfd6a8b3cd87b";
	private String returnUrl;
	private static final String AUTHORIZE_URL="http://api.kaixin001.com/oauth2/authorize?";
	private static final String ACCESS_TOKEN_URL="https://api.kaixin001.com/oauth2/access_token?";
	private static final String USER_INFO_URL="https://api.kaixin001.com/users/me.json?";
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
		
		//获取单点登录系统登录后回调地址
		callbackUrl = request.getParameter("callbackUrl");
		
		//生成开心 网登录成功后回调地址
		this.buildCallbackUrl();
		
		//生成开心网登录地址
		this.buildRedirectUrl();
		
		return new ModelAndView("redirect:"+redirectUrl);
	}
	
	/**
	 * 生成开心网授权成功后回调地址
	 * @throws UnsupportedEncodingException 
	 * @throws Exception
	 */
	private void buildCallbackUrl() throws UnsupportedEncodingException{
		returnUrl =  appConf.getProperty("app.cas.url")+ 
		URLEncoder.encode("/kaixinLogin?method=callback&callbackUrl="+callbackUrl,"UTF-8");
	}
	
	/**
	 * 生成开心登录地址
	 * @throws Exception
	 */
	private void buildRedirectUrl() throws Exception{
		redirectUrl=CoopSiteKaixinUtil.get().buildRedirectUrl(returnUrl, AUTHORIZE_URL);
	}
	
	/**
	 * 当开心登录授权成功后回调的接口
	 * @return String
	 * @throws Exception
	 */
	public ModelAndView callback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		if(request.getParameter("callbackUrl")!=null)
			callbackUrl=request.getParameter("callbackUrl");
		
		//获取Authorization Code
		String code=request.getParameter("code");
		
		Map<String,String> params=new HashMap<String,String>();
		params.put("grant_type", GRANT_TYPE);
		params.put("code", code);//上面第一步所获得的Authorization Code
		params.put("client_secret", SECRET_KEY);//应用的Secret Key
		params.put("redirect_uri", returnUrl);//回调地址
		params.put("client_id", CoopSiteKaixinUtil.APP_KEY);//应用的API Key
		
		//获取access_token
		JSONObject jsonObject=new JSONObject();
		jsonObject=getAccessToken(params,request);
		
		String accessToken=jsonObject.get("access_token").toString();//获取access_token
		
		//获取用户信息
		JSONObject userInfo = getUserInfo(accessToken);
		
		String nickName="";
		
		String openId="";
		
		if(userInfo.get("name")!=null)
			nickName=userInfo.getString("name");
		
		if(userInfo.get("uid")!=null)
			openId=userInfo.getString("uid");
		
		ModelAndView simulateView = new ModelAndView(simulateLoginView);
		simulateView.addObject("username", "COOP_USER_KAIXIN");
		simulateView.addObject("password", "KAIXIN&"+openId+"&"+nickName);
		simulateView.addObject("service",callbackUrl);
		return simulateView;
	}
	
	/*
     * 获取accessToken
     * @return 
     */
	public JSONObject getAccessToken(Map<String,String> params,HttpServletRequest request) throws Exception {
		
		JSONObject jsonObject=new JSONObject();
		
		//自动加载所有证书
		Protocol myhttps = new Protocol("https", CasProtocolSocketFactory.get(), 443);
		Protocol.registerProtocol("https", myhttps);
		
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(CoopSiteKaixinUtil.get().getUrl(params, ACCESS_TOKEN_URL));// 以GET方式发起请求
		try {
			client.executeMethod(getMethod);
			String resultJson=getMethod.getResponseBodyAsString();//获取结果字符串	
			
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
	 */
	public JSONObject getUserInfo(String accessToken) throws Exception {
		JSONObject jsonObject=new JSONObject();
		
		//设置url参数
		HashMap<String,String> params=new HashMap<String,String>();
		
		params.put("access_token", accessToken);//获取用户信息的唯一标识
		
		//自动加载所有证书
	    Protocol myhttps = new Protocol("https", CasProtocolSocketFactory.get(), 443);
	    Protocol.registerProtocol("https", myhttps);
	    
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "GBK");
		GetMethod getMethod = new GetMethod(CoopSiteKaixinUtil.get().getUrl(params, USER_INFO_URL));// 以GET方式发起请求
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
