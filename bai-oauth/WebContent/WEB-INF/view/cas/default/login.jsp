<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.jasig.cas.util.*" %>
<%@ page import="org.apache.commons.lang.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%
//获取参数
final Properties appConf = (Properties) WebApplicationContextUtils
		.getWebApplicationContext(request.getServletContext())
		.getBean("appConf");
String service = request.getParameter("service")==null?appConf.getProperty("app.web.url"):request.getParameter("service");
String auto = request.getParameter("auto")==null?"":request.getParameter("auto");
String token = request.getParameter("token")==null?"":request.getParameter("token");
%>
<!DOCTYPE html>
<html>
<%
//是否自动登录
if(StringUtils.isNotBlank(auto)){
%>
	<%@ include file="login-auto.jsp"%>
<%
}else{
	//判断是WEB还是WAP
	if(!CasPcOrMobileUtils.isMobile(request)){
%>
	<%@ include file="login-web.jsp"%>
<%	
	}else{
%>
	<%@ include file="login-wap.jsp"%>
<%
	}
}
%>
<script src="WEB-RES/script/oauth.js" type="text/javascript"></script>
<script type="text/javascript">
//老卡当登录后再新的单点登录
function login(){
	if($('#btnSubmit').attr('logining')=='yes'){
		$('#msg').text('正在登录，请勿重复提交登录!');
		return;
	}
	
	var username = $('#username').val().replace(/(^\s+)|(\s+$)/g, "");
	var password = $('#password').val().replace(/(^\s+)|(\s+$)/g, "");
	
	if("<%=auto%>"){
		//注册用户自动登录
		$('#btnSubmit').attr('logining','yes');
		$('#msg').text('正在登录，请稍后...');
		var url = "<%=appConf.getProperty("app.baixc.sso")%>/baixc/ssoproxy/login?ajax=true&token=" + encodeURIComponent(password);
		oauth.loadScript(
			url, 
			function(){
		    	$("#frmLogin").submit();
				$('#btnSubmit').attr('logining','no');		
				$('#msg').text('');	
			}
		);
		
	}else{
		//注册用户普通登录
		var flag = true;
		var isUsername = /^\w{5,20}$/g.test(username) || /^(\w)+(\.\w+)*@(\w||\-)+((\.\w+)+)$/g.test(username);
		var isPassword = /^.{6,20}$/.test(password);
		
		if(!username){
			$('#UNErr').addClass('k_cb00').text('会员名或邮箱不能为空');
			flag = false;
		}else {
			if(!isUsername){
				$('#UNErr').addClass('k_cb00').text('会员名应为5-20个字符或邮箱');
				flag = false;
			}else{
				$('#UNErr').removeClass('k_cb00').text('');
			}
		}
		if(!password){
			$('#PwdErr').addClass('k_cb00').text('密码不能为空');
			flag = false;
		}else{
			if(!isPassword){
				$('#PwdErr').addClass('k_cb00').text('密码应为6-20个字符');
				flag = false;
			}else{
				$('#PwdErr').removeClass('k_cb00').text('');
			}
		}
		
		if(flag){
			//注册用户登录
			$('#btnSubmit').attr('logining','yes');
			$('#msg').text('正在登录，请稍后...');
			var url = "<%=appConf.getProperty("app.baixc.sso")%>/baixc/ssoproxy/login?ajax=true&ssoUser.name=" + username +"&ssoUser.password=" + password;
			oauth.loadScript(
				url, 
				function(){
			    	$("#frmLogin").submit();
					$('#btnSubmit').attr('logining','no');		
					$('#msg').text('');		
				}
			);
		}
	}
}

//自动登录
if("<%=auto%>" && !$('#msg').text()){
	$("#username").val('AUTO_USER_TOKEN');
	$("#password").val('<%=token%>');
	$("#btnSubmit").click();
}
</script>
</html>
