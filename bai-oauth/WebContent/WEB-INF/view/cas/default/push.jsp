<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Properties" %>
<%@ page import="java.net.*" %>
<%@ page import="org.jasig.cas.util.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%
//获取配置参数
final Properties appConf = (Properties) WebApplicationContextUtils
		.getWebApplicationContext(request.getServletContext())
		.getBean("appConf");
String platform = request.getParameter("platform");
String url = request.getParameter("url");
String reg = "(.+)(spring-security-redirect=)(.*)&(ticket=.*)";
if(url.matches(reg)){
	url = url.replaceAll(reg, "$1$4&$2") + URLEncoder.encode(url.replaceAll(reg, "$3"), "UTF-8");;
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
	function onload(){
		var url = "<%=url==null?"":url%>";
		if(url){
			window.location.replace(url);
		}else{
			window.location.replace($("#redirectUrl").attr("href"));
		}
	}
</script>
<%
//判断是WEB还是WAP
if(!"wap".equals(platform)){
%>
<!-- Web端 -->
<link href="WEB-RES/css/web/reg_pg.css" rel="stylesheet" />
</head>
<body onload="onload();">
<div class="k_ovh sucess" style="padding:130px 0;">
	<h2 class="k_fl"><img src="/oauth/WEB-RES/images/load.png"/></h2>
	<p class="welcome k_fl k_p15"><span class="k_f18 k_fwb">登录成功！</span><br/>欢迎加入卡当！  <a id='redirectUrl' href="<%=appConf.getProperty("app.web.url")%>" target="_top" class="k_acblue">无法跳转，请点击这里</a>
</div>
<!-- 主动推送登录 -->
<%if(CasPcOrMobileUtils.isPush(appConf.getProperty("app.check.kdweb"), url)){%><iframe src='<%=appConf.getProperty("app.check.kdweb")%>' style='position:absolute;left:-9999px;'></iframe><%}%>
<%	
}else{
%>
<!-- Wap端 -->
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport" />
<meta content="yes" name="apple-mobile-web-app-capable" />
<meta content="black" name="apple-mobile-web-app-status-bar-style" />
<link href="WEB-RES/css/wap/reg_pg.css" rel="stylesheet" />
</head>
<body onload="onload();">
<div class="res_Content" style="padding-top:100px;">
	<article class="k_pb30 k_pt30">
		<div class="k_ovh sucess">
			<h2 class="k_fl"><img src="/oauth/WEB-RES/images/load.gif"/></h2>
			<p class="welcome k_fl k_p15"><span class="k_f16 k_fwb">登录成功！</span><br/>欢迎加入卡当！  <a id='redirectUrl' href="<%=appConf.getProperty("app.wap.url")%>" target="_top" class="k_acblue">无法跳转，请点击这里</a>
		</div>
	</article>
</div>
<!-- 主动推送登录 -->
<%if(CasPcOrMobileUtils.isPush(appConf.getProperty("app.check.mobile"), url)){%><iframe src='<%=appConf.getProperty("app.check.mobile")%>' style='position:absolute;left:-9999px;'></iframe><%}%>
<%	
}
%>
</body>
</html>
