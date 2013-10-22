<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%
//获取配置参数
final Properties appConf = (Properties) WebApplicationContextUtils
		.getWebApplicationContext(request.getServletContext())
		.getBean("appConf");
%>
<!DOCTYPE html>
<html>
<head>
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
<script src="WEB-RES/script/oauth.js" type="text/javascript"></script>
<link href="WEB-RES/css/kd_base.css" rel="stylesheet" />
<link href="WEB-RES/css/web/reg_pg.css" rel="stylesheet" />
<script type="text/javascript">	
	//模拟单点登录
	jQuery(function($){
		var service = encodeURIComponent("${service}");
		loginUrl = service ? "/oauth/login?service="+ service : "";
		oauth.login("${username}", "${password}", loginUrl);
	});
</script>
</head>
<body>
<div class="k_ovh sucess" style="padding:130px 0;">
	<h2 class="k_fl"><img src="/oauth/WEB-RES/images/load.gif"/></h2>
	<p class="welcome k_fl k_p15"><span class="k_f16 k_fwb">登录成功！</span><br/>欢迎加入卡当！  <a id='redirectUrl' href="<%=appConf.getProperty("app.web.url")%>" target="_top" class="k_acblue">无法跳转，请点击这里</a>
</div>
</body>
</html>
