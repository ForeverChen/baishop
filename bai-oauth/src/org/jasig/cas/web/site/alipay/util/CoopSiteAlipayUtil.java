package org.jasig.cas.web.site.alipay.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletRequest;

import com.baixc.framework.utils.MD5Encryptor;

public class CoopSiteAlipayUtil {
	public static String GATEWAY = "https://mapi.alipay.com/gateway.do?";
	public static String SERVICE = "alipay.auth.authorize";
	public static String TARGET_SERVICE = "user.auth.quick.login";
	public static String SIGN_TYPE = "MD5";
	public static String INPUT_CHARSET = "UTF-8";
	public static String PARTNER = "2088601177004617";
	public static String KEY="iyzju46ru3cgd8fu9nply1scks6sscil";
	private static volatile CoopSiteAlipayUtil instance;
	
	public static CoopSiteAlipayUtil get(){
		if(instance == null){
			synchronized (CoopSiteAlipayUtil.class) {
				if(instance == null){
					instance = new CoopSiteAlipayUtil();
				}
			}
		}
		return instance;
	}
	/**
	 * 输出对应的参数对应错误：
	 * 1.invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
	 * 2.true 返回正确信息 
	 * 3.false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
	 * */
	public String checkNotify(String urlvalue) {
		String inputLine = "";
		try {
			URL url = new URL(urlvalue);

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();

			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnection.getInputStream()));

			inputLine = in.readLine().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return inputLine;
	}
	
	/**
	 * notify、签名 验证
	 * @param request
	 * @return
	 */
	public boolean check(ServletRequest request){
		// 1. notify验证
		String alipayNotifyURL = "http://notify.alipay.com/trade/notify_query.do?"
			+ "partner=" + PARTNER + "&notify_id="
			+ request.getParameter("notify_id");
		//获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的
		String result = this.checkNotify(alipayNotifyURL);
		if(!"true".equals(result))
			return false;
		return true;
	}
	
	/**
	 * 生成支付宝登录地址
	 * @param return_url
	 * @return
	 * @throws Exception
	 */
	public String buildRedirectUrl(String return_url){
		String redirectUrl = GATEWAY;
		String sign;
		try {
			Map<String, String> params = new HashMap<String, String>();
			params.put("service", SERVICE);
			params.put("target_service", TARGET_SERVICE);
			params.put("partner", PARTNER);
			params.put("return_url", return_url);
			params.put("_input_charset", INPUT_CHARSET);
			
			String key;
			Iterator<String> keys = params.keySet().iterator();
			while(keys.hasNext()){
				key = keys.next();
				redirectUrl += key + "=" + URLEncoder.encode(params.get(key), INPUT_CHARSET) + "&";
			}
			
			sign = MD5Encryptor.encryptHex(this.getSignatureContent(params) + KEY);
			return redirectUrl += "sign=" + sign + "&sign_type=" + SIGN_TYPE;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	// 除 sign 和 sign_type 外，其它各参数按KEY排序输出字符串
	public String getSignatureContent(Map<String, String> params) {
		StringBuffer content = new StringBuffer();
		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			if (key == null || key.equalsIgnoreCase("sign")
					|| key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			String value = params.get(key);
			content.append((i == 0 ? "" : "&") + key + "=" + value);
		}
		return content.toString();
	}
}
