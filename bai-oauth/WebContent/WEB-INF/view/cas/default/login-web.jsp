<%@ page contentType="text/html; charset=UTF-8" %>
<!-- Web端 -->
<head>
<title>卡当网.个性礼品定制 一件起订,全国配送!</title>
<meta name="keywords" content="用户登录" /> 
<meta name="description" content="已注册用户，请使用会员名和密码，登录到卡当网" />
<link href="WEB-RES/css/kd_base.css" rel="stylesheet" />
<link href="WEB-RES/css/web/reg_pg.css" rel="stylesheet" />
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
<script type="text/javascript">
	jQuery(function($){
		$('#username').focus(function(){
			if($(this).val()=='输入会员名或邮箱'){
				$(this).val('');
			}
		});
		$('#username').blur(function(){
			if($(this).val()==''){
				$(this).val('输入会员名或邮箱');
				return;
			}
			var username = $('#username').val().replace(/(^\s+)|(\s+$)/g, "");
			var isUsername = /^\w{5,20}$/g.test(username) || /^(\w)+(\.\w+)*@(\w||\-)+((\.\w+)+)$/g.test(username);
			if(!isUsername){
				$('#UNErr').addClass('k_cb00').text('会员名应为5-20个字符或邮箱');
				flag = false;
			}else{
				$('#UNErr').removeClass('k_cb00').text('');
			}
		});
		$('#password').blur(function(){
			var password = $('#password').val().replace(/(^\s+)|(\s+$)/g, "");
			var isPassword = /^.{6,20}$/.test(password);
			if(!isPassword){
				$('#PwdErr').addClass('k_cb00').text('密码应为6-20个字符');
				flag = false;
			}else{
				$('#PwdErr').removeClass('k_cb00').text('');
			}
		});
		

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
		
		var str = location.search;
		$('#OuathRegister').attr('href','/oauth/register'+str);
	});
</script>
</head>
<body>
	<%
	String isDialog = request.getParameter("dialog")==null?"":request.getParameter("dialog");
	if(!isDialog.equals("true")){
	%>
		<div class="c_head">
			<div class="c_headSBg k_posr">
				<a title="选择生日礼物,个性礼品定制,就在卡当!" href="http://www.baixc.com/" class="k_ovh">
					<img alt="个性礼品定制，就在卡当网" src="WEB-RES/images/web_logo.png" />
				</a>
				<a title="卡当首页" href="http://www.baixc.com/" class="k_acblue backHome">返回首页</a>
				<img alt="客服热线400 118 9191（免长途费）" src="WEB-RES/images/callkefu.png" class="c_headSHot"/>
			</div>
		</div>
		<div class="c_content k_ovh">
			<div class="c_contentLeft">
	<%}%>
	<form:form method="post" id="frmLogin" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">		
		<div class="c_userLogin">
			<h3>已经是会员，请登录！</h3>
			<form:errors path="*" id="msg" cssClass="k_pl5 myertip" element="div" />
			<label class="c_formLabel" for="username">
				<span class="k_pr10 k_fl myn">账&nbsp;&nbsp;号</span>
				<form:input cssClass="k_fl inp" cssErrorClass="k_fl inp" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" value="输入会员名或邮箱" />
				<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
				<span class="k_fl k_pl5" id="UNErr"></span>
			</label>
			<label class="c_formLabel" for="password">
				<span class="k_fl k_pr10 myn">密&nbsp;&nbsp;码</span>
				<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
				<form:password cssClass="k_fl inp" cssErrorClass="k_fl inp" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
				<span id="PwdErr" class="k_fl k_pl5"></span>
			</label>
			<p class="k_ovh mybtn"><label class="c_btn k_spes"><input type="button" id="btnSubmit" value="登 录" class="btn" onclick="login()"/></label></p>
		</div>
		<input type="hidden" id="it" name="lt" value="${loginTicket}" />
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="_eventId" value="submit" />
	</form:form>
	<p class="otherLogin k_mt20">使用合作站账号登录卡当：</p>
	<p class="otherLogin k_mt5">
		<%if(isDialog.equals("true")){%>
			<a onclick="window.open('qqLogin?method=excute&callbackUrl=<%=service%>','newwindow0','height=400,width=700,top=50,left=50,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')" href="javascript:{}" class="k_acblue"><i class="qqicon">&nbsp;</i>&nbsp;QQ</a> |
			<a onclick="window.open('alipayLogin?method=excute&callbackUrl=<%=service%>','newwindow1','height=500,width=600,top=50,left=50,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')" href="javascript:{}" class="k_acblue k_pl5 k_pr5"><i class="zficon">&nbsp;</i>&nbsp;支付宝</a> |
			<a onclick="window.open('xunleiLogin?method=excute&callbackUrl=<%=service%>','newwindow2','height=500,width=700,top=50,left=50,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')" href="javascript:{}" class="k_acblue k_pl5 k_pr5"><i class="xlicon">&nbsp;</i>&nbsp;迅雷</a> |
			<a onclick="window.open('kaixinLogin?method=excute&callbackUrl=<%=service%>','newwindow2','height=500,width=700,top=50,left=50,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')" href="javascript:{}" class="k_acblue k_pl5 k_pr5"><i class="kxicon">&nbsp;</i>&nbsp;开心网</a> |
			<a onclick="window.open('sohuLogin?method=passport&callbackUrl=<%=service%>','newwindow2','height=500,width=700,top=50,left=50,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no')" href="javascript:{}" class="k_acblue k_pl5 k_pr5"><i class="shicon">&nbsp;</i>&nbsp;搜狐</a>				
		<%}else{%>
			<a href="qqLogin?method=excute&callbackUrl=<%=service%>" class="k_acblue"><i class="qqicon">&nbsp;</i>&nbsp;QQ</a> |
			<a href="alipayLogin?method=excute&callbackUrl=<%=service%>" class="k_acblue k_pl5 k_pr5"><i class="zficon">&nbsp;</i>&nbsp;支付宝</a> |
			<a href="xunleiLogin?method=excute&callbackUrl=<%=service%>" class="k_acblue k_pl5 k_pr5"><i class="xlicon">&nbsp;</i>&nbsp;迅雷</a> |
			<a href="kaixinLogin?method=excute&callbackUrl=<%=service%>" class="k_acblue k_pl5 k_pr5"><i class="kxicon">&nbsp;</i>&nbsp;开心网</a> |
			<a href="sohuLogin?method=passport&callbackUrl=<%=service%>" class="k_acblue k_pl5 k_pr5"><i class="shicon">&nbsp;</i>&nbsp;搜狐</a>
		<%}%>
	</p>	
	<%if(!isDialog.equals("true")){%>
			<p class="noReg">
				<span>还不是会员？</span>
				<a title="免费注册会员" href="/oauth/register" id="OuathRegister" class="k_acblue">免费注册</a>
			</p>
			<p class="k_pl30 k_pt10">曾经通过卡当合作渠道下过单<a href="" class="k_acblue k_pl5">绑定账号送金钻</a></p>
		</div>
		<div class="c_contentRight">
			<p class="b1"></p>
			<p class="p1"><a href="http://bbs.baixc.com/thread-353500-1-1.html" target="_blank" class="k_acblue">支持全国1200个城市货到付款</a></p>
			<p class="b2"></p>
			<p class="p2">支付宝特约商家，100%好评</p>
			<p class="b3"></p>
			<p>质量问题30日免费退换，<a href="http://bbs.baixc.com/thread-352353-1-1.html" target="_blank" class="k_acblue">退换货规则</a></p>
		</div>
	</div>
		<div class="c_footS">
			<span>Copyright &copy; 2006-2013 baixc.com</span> <span>网上经营许可证号：<a href="http://www.miibeian.gov.cn/" target="_blank" rel="nofollow">浙B2-20120279</a></span><br/>
			<span>杭州卡当<a href="<kd:domain key='home'/>/">礼品</a>有限公司</span> <span>杭州市萧山区金城路1038号国际创业中心13楼</span>
		</div>
	<%}%>
</body>