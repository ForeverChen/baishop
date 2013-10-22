<%@ page contentType="text/html; charset=UTF-8" %>
<!-- Wap端 -->
<head>
<title>用户登录 - 卡当网触屏版</title>
<meta name="keywords" content="用户登录" /> 
<meta name="description" content="已注册用户，请使用会员名和密码，登录到卡当网触屏版。" />
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport" />
<meta content="yes" name="apple-mobile-web-app-capable" />
<meta content="black" name="apple-mobile-web-app-status-bar-style" />
<link href="WEB-RES/css/kd_base.css" rel="stylesheet" />
<link href="WEB-RES/css/wap/reg_pg.css" rel="stylesheet" />
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(function($){
		$('#username').keydown(function(e){
			if(e.keyCode==13){
				login();
			}
		});
		$('#password').keydown(function(e){
			if(e.keyCode==13){
				login();
			}
		});
	});
</script>
</head>
<body>
<div class="k_bc res_Content">
	<header class="c_head res_head">
		<h1 class="kdlogo"><a href="<%=appConf.getProperty("app.wap.url")%>"><img src="WEB-RES/images/logo.png" alt="卡当"/></a></h1>
		<ul class="golink">
			<li><a href="<%=appConf.getProperty("app.wap.url")%>/baixc/shopping/order/kuaidi/kuaidiInfoQuery!toWapKuaidi.action"><img src="WEB-RES/images/icon1.png"/></a></li>
			<li><a href="<%=appConf.getProperty("app.wap.url")%>/mobile/mykd/"><img src="WEB-RES/images/icon2.png"/></a></li>
			<li><a href="<%=appConf.getProperty("app.wap.url")%>/baixc/shoppingcar.html"><img src="WEB-RES/images/icon3.png"/></a></li>
		</ul>
	</header>
	<article class="k_bgeee k_pb30">
		<h2 class="k_spes c_goback">用户登录</h2>
		<form:form method="post" id="frmLogin" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">		
			<div class="reg_input">
				<form:errors path="*" id="msg" cssClass="err" element="div" /><br />
				<label for="username">
					<span>请输入会员名或邮箱</span>
					<span class="err" id="UNErr"></span>
					<span>
						<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
						<form:input cssClass="bodyLoginTxt" cssErrorClass="bodyLoginTxt" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
					</span>
				</label>
				<label for="password" class="last">
					<span>密码</span>
					<span class="err" id="PwdErr"></span>
					<span>
	            		<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
						<form:password cssClass="bodyLoginTxt" cssErrorClass="bodyLoginTxt" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
            		</span>
				</label>
            	<input type="hidden" id="it" name="lt" value="${loginTicket}" />
				<input type="hidden" name="execution" value="${flowExecutionKey}" />
				<input type="hidden" name="_eventId" value="submit" />
				<input type="button" class="btn" id="btnSubmit" value="登 录" onclick="login()" />
			</div>
		</form:form>
		<p class="loginTip">还不是会员? <a href="register?service=<%=service%>" class="k_acblue">免费注册</a></p>
	</article>
	<footer class="c_foot res_foot">
		<ul class="k_ovh go">
			<li class="l1"><img src="WEB-RES/images/g1.png"/></li>
			<li class="l2"><img src="WEB-RES/images/g2.png"/></li>
			<li class="l3"><img src="WEB-RES/images/g3.png"/></li>
		</ul>
		<div class="bot">
			<p class="k_bd568 k_posr k_spes k_cd00 tel">400 118 9191<span class="k_spes k_posa s"></span></p>
			<p class="k_f16 k_mt10 write"><a href="<%=appConf.getProperty("app.wap.url")%>">首页</a> | <a href="#">帮助</a> | <a href="#">联系我们</a></p>
			<p class="k_f16 k_mt10 write">网上经营许可证号：<span class="k_pl10">浙B2-20120279</span></p>
		</div>
	</footer>
</div>
</body>