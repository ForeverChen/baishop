package org.jasig.cas.web.site.sohu.action;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.jasig.cas.web.site.sohu.util.SohuConstants;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.baixc.framework.utils.MD5Encryptor;
import com.sohu.open.sdk.OpenParameter;
import com.sohu.open.sdk.oauth2.OAuth;
import com.sohu.open.sdk.utils.HttpConnection;

public class CoopSiteSohuConnectController extends MultiActionController {
	
	/** 系统基础配置，读取app.conf里的信息 */
	@Resource 	
	protected Properties appConf;
	
	// Alipay 登录地址
	private String redirectUrl;
	
	private String targetUrl;
	
	@NotNull
	private String simulateLoginView;
	
	private String callbackUrl;

	public ModelAndView passport(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		callbackUrl = request.getParameter("callbackUrl");
		
		// 重定向到用户认证页面
		redirectUrl = HttpConnection.getInstance().getUri(SohuConstants.AUTHORIZE_URI, 
			      new OpenParameter[] { 
			      new OpenParameter("client_id", SohuConstants.CLIENT_ID), 
			      new OpenParameter("redirect_uri", appConf.getProperty("app.cas.url")
							+ URLEncoder.encode("/sohuLogin?method=callback&callbackUrl="+callbackUrl,"UTF-8")),
			      new OpenParameter("response_type", "code"),
			      new OpenParameter("state", SohuConstants.STATE),
			      new OpenParameter("display", SohuConstants.DISPLAY)});
		return new ModelAndView("redirect:"+redirectUrl);
	}

	/**
	 * 当搜狐登录成功时回调的接口
	 * 
	 * @return
	 * @throws Exception
	 */
	public ModelAndView callback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		callbackUrl=request.getParameter("callbackUrl");
		try {
			JSONObject token = this.getAccessToken(request.getParameter("code"));
			if (token != null
					&& token.get(OpenParameter.ACCESS_TOKEN) != null
					&& StringUtils.isNotBlank(token.get(OpenParameter.ACCESS_TOKEN).toString())) {
				String accessToken = (String)token.get(OpenParameter.ACCESS_TOKEN);
				String refreshToken = (String)token.get(OpenParameter.REFRESH_TOKEN);
				//判断token是否过期，如果过期，则refreshToken，补救重新获取
				if(token.get("error") != null){
					String invalidToken = (String)token.get("error");
					if(StringUtils.isNotBlank(invalidToken) && invalidToken.contains("invalid_token")){
						JSONObject refreshTokenObject = OAuth.tokenRefresh(refreshToken);
						if(refreshTokenObject != null){
							token = refreshTokenObject;
							accessToken = (String)token.get(OpenParameter.ACCESS_TOKEN);
							refreshToken = (String)token.get(OpenParameter.REFRESH_TOKEN);
						}
					}
				}
				if(token != null){
					request.getSession().setAttribute(OpenParameter.ACCESS_TOKEN, accessToken);
					request.getSession().setAttribute(OpenParameter.REFRESH_TOKEN, refreshToken);
					String sohuId = token.get("open_id").toString();
					JSONObject result = this.getNickNameJson(accessToken);
					String nickName = sohuId;
					if(result != null && result.get("data") != null){
						Object uniqnameObject = ((JSONObject)result.get("data")).get("uniqname");
						if(uniqnameObject != null && StringUtils.isNotBlank(uniqnameObject.toString()))
							nickName = uniqnameObject.toString();
					}
					if(StringUtils.isNotBlank(sohuId)){
						ModelAndView simulateView = new ModelAndView(simulateLoginView);
						simulateView.addObject("username", "COOP_USER_SOHU");
						simulateView.addObject("password", "SOHU&"+sohuId+"&"+nickName);
						simulateView.addObject("service",callbackUrl);
						return simulateView;
					}
				}
			}
		} catch (Exception e) {
		}
		return null;
	}
	
	private JSONObject getAccessToken(String code) throws UnsupportedEncodingException{
		if(StringUtils.isBlank(code))
			return null;
		return (JSONObject)JSONValue.parse(HttpConnection.getInstance().doPost(SohuConstants.TOKEN_URI, 
	      new OpenParameter[] { 
	      new OpenParameter("client_id", SohuConstants.CLIENT_ID), 
	      new OpenParameter("redirect_uri", appConf.getProperty("app.cas.url")
					+ URLEncoder.encode("/sohuLogin?method=callback&callbackUrl="+callbackUrl,"UTF-8")), 
	      new OpenParameter("grant_type", "authorization_code"), 
	      new OpenParameter("code", code.trim()), 
	      new OpenParameter("client_secret", SohuConstants.CLIENT_SECRET) }));
	}
	
	private JSONObject getNickNameJson(String accessToken) {
		String sign = MD5Encryptor.encryptHex("access_token=" + accessToken + SohuConstants.CLIENT_SECRET, "utf-8");
		return (JSONObject) JSONValue.parse(HttpConnection.getInstance()
				.doPost(SohuConstants.GET_USERINFO_URI,
						new OpenParameter[] { new OpenParameter("access_token",
								accessToken), new OpenParameter("sig", sign) }));
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public String getTargetUrl() {
		return targetUrl;
	}

	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}

	public String getSimulateLoginView() {
		return simulateLoginView;
	}

	public void setSimulateLoginView(String simulateLoginView) {
		this.simulateLoginView = simulateLoginView;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}
	
}
