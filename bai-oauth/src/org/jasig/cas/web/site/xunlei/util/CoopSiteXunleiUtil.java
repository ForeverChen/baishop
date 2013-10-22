package org.jasig.cas.web.site.xunlei.util;

import java.net.URLEncoder;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.StringUtils;

import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class CoopSiteXunleiUtil {
	private static final String MAC_NAME = "HmacSHA1";    
    private static final String ENCODING = "UTF-8";
	
	private static volatile CoopSiteXunleiUtil instance;
	public static CoopSiteXunleiUtil get(){
		if(instance == null){
			synchronized (CoopSiteXunleiUtil.class) {
				if(instance == null){
					instance = new CoopSiteXunleiUtil();
				}
			}
		}
		return instance;
	}
	
	/**  
     * 使用 HMAC-SHA1 签名方法对encryptText进行签名  
     * @param encryptText 被签名的字符串  
     * @param encryptKey  密钥  
     * @return  签名
     * @throws Exception  
     */    
	public byte[] HmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception     
    {           
        byte[] data=encryptKey.getBytes(ENCODING);  
        //根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称  
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);   
        //生成一个指定 Mac 算法 的 Mac 对象  
        Mac mac = Mac.getInstance(MAC_NAME);   
        //用给定密钥初始化 Mac 对象  
        mac.init(secretKey);    
          
        byte[] text = encryptText.getBytes(ENCODING);    
        //完成 Mac 操作   
        return mac.doFinal(text);    
    }
	
	/**
	 * 创建baseString
	 * @param params url所带的参数
	 * @param httpMethod http方法名
	 * @param url 请求url
	 * @return 
	 */
	@SuppressWarnings("deprecation")
	public String generateBaseString(Map<String,String> params,String httpMethod,String url) {
		String baseString=httpMethod+"&"+URLEncoder.encode(url.replace("?", ""));
		int i=0;
		for(Map.Entry<String, String> entry:params.entrySet()) {
			if(i>0)
				baseString+="%26"+URLEncoder.encode(URLEncoder.encode(entry.getKey()))+"%3D"+URLEncoder.encode(URLEncoder.encode(entry.getValue()));
			else
				baseString+="&"+URLEncoder.encode(URLEncoder.encode(entry.getKey()))+"%3D"+URLEncoder.encode(URLEncoder.encode(entry.getValue()));
			i++;
		}
		return baseString;
	}
	
	/**
	 * 创建签名
	 * @param baseString 基础串
	 * @param key 签名key
	 * @return String 签名
	 */
	public String createSig(String baseString,String key) throws Exception {
		BASE64Encoder enc=new BASE64Encoder();
		String sig=enc.encode(HmacSHA1Encrypt(baseString,key));
		return sig;
	}
	
	/**
	 * 生成获取requesttoken的Url
	 * @param params url参数
	 * @param url 请求url
	 * @return url
	 */
	@SuppressWarnings("deprecation")
	public String getTokenUrl(Map<String,String> params,String url) {
		String tokenUrl=url;
		int i=0;
		for(Map.Entry<String, String> entry:params.entrySet()) {
			if(i>0)
				tokenUrl+="&"+URLEncoder.encode(entry.getKey())+"="+URLEncoder.encode(entry.getValue());
			else
				tokenUrl+=URLEncoder.encode(entry.getKey())+"="+URLEncoder.encode(entry.getValue());
			i++;
		}
		return tokenUrl;
	}
	
	/**
	 * html字符转义
	 * @param content 转义前的字符串
	 * @return 转义后的字符串
	 */
	public String html(String content) {
		if(content==null) return "";        
		    String html = content;
		html = StringUtils.replace(html, "'", "&apos;");
		html = StringUtils.replace(html, "\"", "&quot;");
		html = StringUtils.replace(html, "\t", "&nbsp;&nbsp;");// 替换跳格
		html = StringUtils.replace(html, "<", "&lt;");
		html = StringUtils.replace(html, ">", "&gt;");
		return html;
	}
}
