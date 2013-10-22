<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%
//获取参数
final Properties appConf = (Properties) WebApplicationContextUtils
		.getWebApplicationContext(request.getServletContext())
		.getBean("appConf");
%>
<!DOCTYPE html>
<html>
<head>
<title>卡当网登出</title>
<meta charset="utf-8" />
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
<script src="<%=appConf.getProperty("app.baixc.sso")%>/baixc/ssoproxy/logout?target=/baixc/web/images/px.gif" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(function($){
		var url = '${service}';
		if(url){
			window.location.replace(url);
		}else{
			window.location.replace("login");
		}
	});
</script>
</head>
<body>
</body>
</html>
