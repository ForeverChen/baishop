package org.jasig.cas.web.site.alipay.action;

import java.util.Properties;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

import org.jasig.cas.web.site.alipay.util.CoopSiteAlipayUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class CoopSiteAlipayConnectController extends MultiActionController{
	
	private String redirectUrl;
	private String targetUrl;
	private String callbackUrl;
	
	@NotNull
	private String simulateLoginView;
	
	/** 系统基础配置，读取app.conf里的信息 */
	@Resource
	protected Properties appConf;
	
	/**
	 * 支付宝登录地址生成
	 */
	public ModelAndView excute(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		callbackUrl=request.getParameter("callbackUrl");
		this.buildRedirectUrl();
		return new ModelAndView("redirect:"+redirectUrl);
	}
	
	/**
	 * 当支付宝登录成功时回调的接口
	 * @return
	 * @throws Exception
	 */
	public ModelAndView callback(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		callbackUrl=request.getParameter("callbackUrl");
		redirectUrl = "/";
		ModelAndView simulateView = new ModelAndView(simulateLoginView);
		// 1. notify & 签名验证；2、获取用户ID值
		if(CoopSiteAlipayUtil.get().check(request)){
			String userId=request.getParameter("user_id");
			String realName=request.getParameter("real_name");   			  
			byte b[] =realName.getBytes("ISO-8859-1");  
			realName = new String(b);  
			simulateView.addObject("username", "COOP_USER_ALIPAY");
			simulateView.addObject("password", "ALIPAY&"+userId+"&"+realName);
			simulateView.addObject("service",callbackUrl);	
		}
		return simulateView;
	}
	
	/**
	 * 生成支付宝登录地址
	 * @throws Exception
	 */
	private void buildRedirectUrl() throws Exception{
		String return_url = appConf.getProperty("app.cas.url") 
			+ "/alipayLogin?method=callback&callbackUrl="+callbackUrl;
		redirectUrl = CoopSiteAlipayUtil.get().buildRedirectUrl(return_url);
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

	public void setSimulateLoginView(String simulateLoginView) {
		this.simulateLoginView = simulateLoginView;
	}
	
}
