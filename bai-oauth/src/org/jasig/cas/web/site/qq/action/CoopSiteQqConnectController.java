package org.jasig.cas.web.site.qq.action;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.apache.commons.lang.StringUtils;
import org.jasig.cas.util.CasProtocolSocketFactory;
import org.jasig.cas.web.site.qq.util.CoopSiteQqUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class CoopSiteQqConnectController extends MultiActionController {
	
	/** 系统基础配置，读取app.conf里的信息 */
	@Resource 	
	protected Properties appConf;
	
	@NotNull
	private String simulateLoginView;
	
	private String redirectUrl;
	private static final String GATEWAY="https://graph.qq.com/oauth2.0/me?";
	private static final String GETUSERINFO="https://graph.qq.com/user/get_user_info?";
	private String targetUrl;
	private String callbackUrl;
	
	/**
	 * QQ登录地址生成
	 */
	public ModelAndView excute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		callbackUrl = request.getParameter("callbackUrl");
		this.buildRedirectUrl();
		return new ModelAndView("redirect:"+redirectUrl);
	}
	
	/**
	 * 当qq登录成功时回调的接口
	 * @throws Exception                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
	 */
	public ModelAndView callback(HttpServletRequest request,HttpServletResponse response) throws Exception {
		
		//获取Authorization Code
		callbackUrl=request.getParameter("callbackUrl");
		String code=request.getParameter("code");
		String token =null;
		String openId=null;
		String nickName=null;
		this.buildTokenUrl(code);
		Protocol myhttps = new Protocol("https", CasProtocolSocketFactory.get(), 443);
		Protocol.registerProtocol("https", myhttps);
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(redirectUrl);
		try {
			client.executeMethod(getMethod);
			token=getMethod.getResponseBodyAsString();// 获取token
			openId=getOpenId(token);// 获取openid
			nickName=getUserInfo(openId, token, CoopSiteQqUtil.CLIENT_ID).get("nickname").toString().replace(" ", "&nbsp;");//获取用户昵称
			
		} catch (Exception e) {
		} finally {
			getMethod.releaseConnection();// 释放连接
		}
		ModelAndView simulateView = new ModelAndView(simulateLoginView);
		simulateView.addObject("username", "COOP_USER_QQ");
		simulateView.addObject("password", "QQ&"+openId+"&"+nickName);
		simulateView.addObject("service",callbackUrl);
		return simulateView;
	}
	
	/**
	 * 生成QQ登录地址
	 * @throws Exception
	 */
	@SuppressWarnings("deprecation")
	private void buildRedirectUrl() throws Exception{
		String return_url =  appConf.getProperty("app.cas.url")
				+ URLEncoder.encode("/qqLogin?method=callback&callbackUrl="+callbackUrl);
		redirectUrl = CoopSiteQqUtil.get().buildRedirectUrl(return_url);
	}
	
	/**
	 * 生成获取token地址
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private void buildTokenUrl(String code) throws Exception{
		String return_url =  appConf.getProperty("app.cas.url") 
				+ URLEncoder.encode("/qqLogin?method=callback");
		redirectUrl = CoopSiteQqUtil.get().buildTokenUrl(code,return_url);
	}
	
	public String getRedirectUrl() {
		return redirectUrl;
	}
	
	 /*
     * 获取OpenId
     * @param token
     * @return String
     */
	public String getOpenId(String token) {
		String value = null;
		String resulJson=null;
		
		//自动加载所有证书
		Protocol myhttps = new Protocol("https", CasProtocolSocketFactory.get(), 443);
		Protocol.registerProtocol("https", myhttps);
		
		HttpClient client = new HttpClient();
		GetMethod getMethod = new GetMethod(GATEWAY+token);// 以GET方式发起请求
		try {
			client.executeMethod(getMethod);
			resulJson=getMethod.getResponseBodyAsString();//获取json字符串
			String a = resulJson.substring(resulJson.indexOf('{') + 1);  
            String b = a.substring(0, a.indexOf('}')).replace("\"", ""); 
            value = b.split(",")[1].replace("openid:", "");
		} catch (Exception e) {
		} finally {
			getMethod.releaseConnection();// 释放连接
		}
		if(StringUtils.isNotBlank(value))//对于CUID默认反解码处理
			try {value = URLDecoder.decode(value, "UTF-8");
			} catch (UnsupportedEncodingException e) { }
		return value;
	}
	
	/*
	 * 获取用户基本信息
	 * @param openId
	 * @param token
	 * @param appId
	 */
	@SuppressWarnings("rawtypes")
	public Map getUserInfo(String openId,String token,String appId) {
		String resultJson=null;
		Map<String,Object> params=new HashMap<String,Object>();
		Protocol myhttps = new Protocol("https", CasProtocolSocketFactory.get(), 443);
		Protocol.registerProtocol("https", myhttps);
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
		JSONObject jsonObject=new JSONObject();
		GetMethod getMethod = new GetMethod(GETUSERINFO+token+"&oauth_consumer_key="+appId+"&openid="+openId);// 以GET方式发起请求
		try {
			client.executeMethod(getMethod);
			resultJson=getMethod.getResponseBodyAsString();//获取json字符串
			jsonObject=JSONObject.fromObject(resultJson);
			params.put("nickname", jsonObject.get("nickname"));
		} catch (Exception e) {
		} finally {
			getMethod.releaseConnection();// 释放连接
		}
		return params;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public void setSimulateLoginView(final String simulateLoginView) {
		this.simulateLoginView = simulateLoginView;
	}

}
