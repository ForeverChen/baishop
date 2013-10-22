<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.jasig.cas.util.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ page import="org.jasig.cas.authentication.principal.Response" %>
<%
//当不是登录页过来时，直接跳转
String referer = request.getHeader("Referer");
if(referer==null || !referer.matches("https://.*"+ request.getContextPath() +"/login.*")){
	Object _response = request.getAttribute("response");
	if(_response!=null){
		String url = ((Response)_response).getUrl();
	 	String reg = "(.+)(spring-security-redirect=.*)&(ticket=.*)";
	 	if(url.matches(reg))
	 		url = url.replaceAll(reg, "$1$3&$2");
		response.sendRedirect(url);
		return;
	}
}

//获取配置参数
final Properties appConf = (Properties) WebApplicationContextUtils
		.getWebApplicationContext(request.getServletContext())
		.getBean("appConf");

//去掉https的cas地址
String httpCasUrl = appConf.getProperty("app.cas.url").replaceFirst("^https", "http");

//判断是WEB还是WAP
String platform;
if(!CasPcOrMobileUtils.isMobile(request)){
	platform = "web";
}else{
	platform = "wap";
}
%>
<!DOCTYPE html>
<html>
<head>
<title>亲爱的用户，欢迎您登录卡当网</title>
<meta charset="utf-8" />
<link href="WEB-RES/css/kd_base.css" rel="stylesheet" />
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(function($){
		//以http的方式跳转到推送页面
		var url = '<%=httpCasUrl%>/push?url='+ encodeURIComponent('${requestScope.response.url}') +'&platform=<%=platform%>';
		window.location.replace(url);
	});
</script>
</head>
<body>
</body>
</html>
