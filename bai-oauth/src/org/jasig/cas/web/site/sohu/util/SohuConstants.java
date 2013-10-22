package org.jasig.cas.web.site.sohu.util;

public interface SohuConstants {
	//用户签名KEY
	String SIGN_KEY = "baixc&sohu";
	//PASSPORT接口
	String INF_GET_USER_ID = "http://passport.sohu.com/oauth/spi/get_userid.action";
	//CONSUMER KEY
	String CONSUMER_KEY = "BAIXC_COM";
	//CONSUMER SECRET
	String CONSUMER_SECRET = "55p40220cEjd54Q2H072D824X1xJ310l";
	//接口URL
	String BASE = "http://passport.sohu.com/oauth/";

	String CLIENT_ID = "607f4b0acb8444fc830a2253d79d9611";
	
	String CLIENT_SECRET = "8b52f15bd17365252194a90a5f54d563";
	//授权认证url
	String AUTHORIZE_URI = "https://api.sohu.com/oauth2/authorize";
	//获取 token
	String TOKEN_URI = "https://api.sohu.com/oauth2/token";
	//获取用户昵称
	String GET_USERINFO_URI = "https://api.sohu.com/rest/pp/prv/1/user/get_info";

	//other
	//任意值，会原样返回，用于防止伪造跨站请求
	String STATE = "5e7ebb5b307ed7de3a2b08f46f13b6e4";
	//默认page
	String DISPLAY = "page";
}
