<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.jasig.cas.util.*" %>
<%@ page import="org.springframework.web.context.support.*" %>
<%@ page import="org.jasig.cas.authentication.principal.Response" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
//获取参数
final Properties appConf = (Properties) WebApplicationContextUtils
		.getWebApplicationContext(request.getServletContext())
		.getBean("appConf");
%>
<!DOCTYPE html>
<html>
<head>
<meta content="text/html; charset=utf-8" http-equiv="Content-Type" />
<meta charset="utf-8" />
<link href="WEB-RES/css/kd_base.css" rel="stylesheet" />
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
<script src="WEB-RES/script/oauth.js" type="text/javascript"></script>
<script type="text/javascript">
	//ajax注册，注册完后再登录
	function register(){
		if($('#Submits').attr('loading')=='yes'){
			$('#msg').show().text('正在注册，请勿重复提交注册!');
			return;
		}
		var username = $('#username').val().replace(/(^\s+)|(\s+$)/g, "");
		var password = $('#password').val().replace(/(^\s+)|(\s+$)/g, "");
		var repassword = $('#repassword').val().replace(/(^\s+)|(\s+$)/g, "");
		var email = $("#email").val().replace(/(^\s+)|(\s+$)/g, "");

		var flag = true;
		var isUsername = /^\w{5,20}$/g.test(username);
		var isPassword = /^.{6,20}$/.test(password);
		var isEmail = /^(\w)+(\.\w+)*@(\w||\-)+((\.\w+)+)$/.test(email);
		
		if(!username){
			$('#UNErr').addClass('k_cb00').text('会员名不能为空');
			flag = false;
		}else {
			if(!isUsername) {
				$('#UNErr').addClass('k_cb00').text('5-20个字符，只能为字母、数字、下划线！');
				flag = false;
			}else{
				if($('#username').attr('sameName') == 'false'){
					$('#UNErr').addClass('k_cb00').text('已存在相同的用户名！');
					flag = false;
				}else{
					$('#UNErr').removeClass('k_cb00').text('');
				}
			}
		}
		if(!password || !isPassword){
			$('#PwdErr').addClass('k_cb00').text('密码应为6-20个字符');
			flag = false;
		}else {
			$('#PwdErr').removeClass('k_cb00').text('');
			if(password != repassword) {
				$('#RPwdErr').addClass('k_cb00').text('密码不一致，请重输');
				flag = false;
			}else {
				$('#RPwdErr').removeClass('k_cb00').text('');
			}
		}
		if(!email) {
			$('#EMErr').addClass('k_cb00').text('邮箱地址不能为空');
			flag = false;
		}else{
			if(!isEmail){
				$('#EMErr').addClass('k_cb00').text('邮箱格式有误');
				flag = false;
			}else{
				if($("#email").attr('sameName') == 'false'){
					$('#EMErr').addClass('k_cb00').text('已存在相同的邮箱！');
					flag = false;
				}else{
					$('#EMErr').removeClass('k_cb00').text('');
				}
			}
		}		
		//注册
		if(flag) {
			$('#Submits').attr('loading','yes');
			$('#msg').show().text('正在注册，请稍后...');
			$.ajax({
				type: "post",
				url: "register?action=register",
				data: {
					username: $("#username").val(),
					password: $("#password").val(),
					repassword: $("#repassword").val(),
					email: $("#email").val(),
					captcha: $("#captcha").val()
				},
				success: function(data, textStatus){
					//注册完成，登录
					var result = eval('('+data+')');
					if(result.success){
						//模拟单点登录
						var service = $("#service").val() || "";
						var loginUrl = service ? "/oauth/login?service="+ service : "";
						oauth.login($("#username").val(), $("#password").val(), loginUrl);
					}else{
						$('#reg_captacha_img').attr('src', 'kaptcha.jpgx?'+Date.parse(new Date()));
						$('#msg').show().text(result.msg);
						$('#Submits').attr('loading','no');
					}
				},
				error: function(XMLHttpRequest, textStatus, errorThrown){
					$('#msg').show().text('网络连接异常或浏览器安全级别太高!');
					$('#Submits').attr('loading','no');
				}
			});
		}else{
			$('#Submits').attr('loading','no');
		}
	}
</script>
<%
//判断是WEB还是WAP
if(!CasPcOrMobileUtils.isMobile(request)){
%>
<!-- Web端 -->
<title>卡当网.个性礼品定制 一件起订,全国配送!</title>
<meta name="keywords" content="用户注册" /> 
<meta name="description" content="免费注册卡当网会员，享受个性礼物定制乐趣。" />
<script type="text/javascript">
	jQuery(function($){
		$('#username').blur(function(){
			var username = $('#username').val().replace(/(^\s+)|(\s+$)/g, "");
			if(!username){
				$('#UNErr').removeClass('unright').addClass('k_cb00 unerr').text('会员名不能为空。');
				$('#UNErrr').remove();
				flag = false;
				return;
			}
			var isUsername = /^\w{5,20}$/g.test(username);
			if(!isUsername){
				$('#UNErr').removeClass('unright').addClass('k_cb00 unerr').text('5-20个字符，只能为字母、数字、下划线！');
				$('#UNErrr').remove();
				flag = false;
			}else{
				$.get('register?action=check&username='+username,function(data){
					var dataObj = eval('('+data+')');
					if(dataObj.success == false){
						$('#UNErr').removeClass('unright').addClass('k_cb00 unerr').text(dataObj.msg);
						$('#UNErrr').remove();
						$('#username').attr('sameName',false);
						flag = false;
					}else{
						$('#UNErr').removeClass('k_cb00 unerr').addClass('unright').text('');
						$('#username').attr('sameName',true);
						$('#UNErrr').remove();
					}
				})
			}
		});
		$('#password').blur(function(){
			var password = $('#password').val().replace(/(^\s+)|(\s+$)/g, "");
			var isPassword = /^.{6,20}$/.test(password);
			if(!isPassword){
				$('#PwdErr').removeClass('unright').addClass('k_cb00 unerr').text('密码应为6-20个字符');
				$('#PwdErrr').remove();
				flag = false;
			}else{
				$('#PwdErr').removeClass('k_cb00 unerr').addClass('unright').text('');
				$('#PwdErrr').remove();
			}
		});		
		$('#repassword').blur(function(){
			var password = $('#password').val().replace(/(^\s+)|(\s+$)/g, "");
			var repassword = $('#repassword').val().replace(/(^\s+)|(\s+$)/g, "");
			if(password != repassword) {
				$('#RPwdErr').removeClass('unright').addClass('k_cb00 unerr').text('密码不一致，请重输');
				flag = false;
			}else {
				$('#RPwdErr').removeClass('k_cb00 unerr').addClass('unright').text('');
			}
		});		
		$('#email').blur(function(){
			var email = $("#email").val().replace(/(^\s+)|(\s+$)/g, "");
			var isEmail = /^(\w)+(\.\w+)*@(\w||\-)+((\.\w+)+)$/.test(email);
			if(!email) {
				$('#EMErr').removeClass('unright').addClass('k_cb00 unerr').text('邮箱地址不能为空');
				flag = false;
			}else{
				if(!isEmail){
					$('#EMErr').removeClass('unright').addClass('k_cb00 unerr').text('邮箱格式有误');
					flag = false;
				}else{
					$.get('register?action=check&email='+email,function(data){
						var dataObj = eval('('+data+')');
						if(dataObj.success == false){
							$('#EMErr').removeClass('unright').addClass('k_cb00 unerr').text(dataObj.msg);
							$('#email').attr('sameName',false);
							flag = false;
						}else{
							$('#EMErr').removeClass('k_cb00').addClass('unright').text('');
							$('#email').attr('sameName',true);
						}
					})
				}
			}	
		});
		
		$('#captcha').blur(function(e){
			$('#msg').hide();
		});
		
		$('#username').keydown(function(e){
			if(e.keyCode==13){
				$('#password').focus();
			}
		});
		$('#password').keydown(function(e){
			if(e.keyCode==13){
				$('#repassword').focus();
			}
		});
		$('#repassword').keydown(function(e){
			if(e.keyCode==13){
				$('#email').focus();
			}
		});
		$('#email').keydown(function(e){
			if(e.keyCode==13){
				$('#captcha').focus();
			}
		});	
		
		//register?action=check&captcha=    验证码
		$('#captcha').keydown(function(e){
			if(e.keyCode==13){
				if($("#checkB").attr("checked")=='checked'){
					register();
	   			}else{
	   				alert("您还未选择用户服务协议!")
	   			}
			}
		});		
		$('#Submits').click(function(){
			if($("#checkB").attr("checked")=='checked'){
				register();
				}else{
					alert("您还未选择用户服务协议!")
				}
		});
		
		var str = location.search;
		$('#OuathLogin').attr('href','/oauth/login'+str);
	});
</script>
<link href="WEB-RES/css/web/reg_pg.css" rel="stylesheet" />
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
			<div class="c_contentRegLeft">
	<%}%>
	<div class="c_userLogin">
		<label class="c_formLabel" for="username">
			<span class="k_pr10 k_fl myn">会员名</span>
			<input class="k_fl inp" id="username" type="text" />
			<span class="k_fl k_pl5" id="UNErr"></span>
			<span class="k_cl k_db mytip" id="UNErrr">5-20个字符，只能为字母、数字、下划线。</span>
		</label>
		<label class="c_formLabel" for="password">
			<span class="k_fl k_pr10 myn">密&nbsp;&nbsp;&nbsp;码</span>
			<input class="k_fl inp" id="password" type="password"/>
			<span class="k_fl k_pl5" id="PwdErr"></span>
			<span class="k_cl k_db mytip" id="PwdErrr">6-20个字符，请使用字母、数字或其组合。</span>
		</label>
		<label class="c_formLabel" for="repassword">
			<span class="k_fl k_pr10 myn">确认密码</span>
			<input class="k_fl inp" id="repassword" type="password"/>
			<span class="k_fl k_pl5" id="RPwdErr"></span>
		</label>
		<label class="c_formLabel" for="email">
			<span class="k_fl k_pr10 myn">电子邮箱</span>
			<input class="k_fl inp" id="email" type="text"/>
			<span class="k_fl k_pl5" id="EMErr"></span>
		</label>
		<label class="c_formLabel k_fl" for="captcha" style="width:190px;margin-bottom:0;">
			<span class="k_fl k_pr10 myn">验证码</span>
			<input type="text" id="captcha" name="captcha" class="inp" style="width:100px;"/>
		</label>
		<p class="k_fl k_db k_ovh">
			<img id="reg_captacha_img" style="width:72px;height:32px" src="kaptcha.jpgx" class="k_fl" onclick="$('#reg_captacha_img').attr('src', 'kaptcha.jpgx?'+Date.parse(new Date()));" style="cursor: pointer;"/>
           	<span class="k_fl k_pt15 k_pl5" onclick="$('#reg_captacha_img').click();" style="cursor: pointer;color:#00b4ff;">看不清楚？换一张</span>
		</p>
		<p class="k_cl tip_p">
			<span class="k_db">卡当和您的主要联系方式。没有邮箱？推荐使用 <a href="http://mail.qq.com" target="_blank" class="k_acblue">QQ邮箱</a> <a href="http://mail.sohu.com" target="_blank" class="k_acblue">搜狐邮箱</a></span>
			<span class="k_db"><input type="checkbox" id="checkB" checked="checked"/><span class="k_pl5">我已阅读并同意卡当<a href="http://bbs.baixc.com/thread-352063-1-1.html" target="_blank" class="k_acblue">“用户服务协议”</a></span></span>
		</p>
		<p class="k_pl5 k_cb00 k_pt5 k_pb5 webmsg" id="msg"></p>
		<p class="k_ovh mybtn">
			<label class="c_btn k_spes">			
			<input type="hidden" id="service" name="service" value="<%=request.getParameter("service")==null?"":request.getParameter("service")%>" />
			<input type="button" id="Submits" value="提交信息" class="btn" />
			</label>
		</p>
	</div>
	<%if(!isDialog.equals("true")){%>
		</div>
		<ul class="c_contentRegRight">
			<li class="regRightLi k_mt30">已有账号？<a href="/oauth/login" id="OuathLogin" class="k_acblue">登录</a></li>
			<li class="regRightLi">曾经通过卡当合作渠道下过单<br><a href="http://www.baixc.com/baixc/coop/user/bind/userBind.action" class="k_acblue">绑定账号送金钻</a></li>
		</ul>
	</div>
	<div class="c_footS">
		<span>Copyright &copy; 2006-2013 baixc.com</span> <span>网上经营许可证号：<a href="http://www.miibeian.gov.cn/" target="_blank" rel="nofollow">浙B2-20120279</a></span><br/>
		<span>杭州卡当<a href="<kd:domain key='home'/>/">礼品</a>有限公司</span> <span>杭州市萧山区金城路1038号国际创业中心13楼</span>
	</div>
	<%}%>
</body>
<%	
}else{
%>
<!-- Wap端 -->
<title>用户注册 - 卡当网触屏版</title>
<meta name="keywords" content="用户注册" /> 
<meta name="description" content="免费注册卡当网会员，享受个性礼物定制乐趣。" />
<meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" name="viewport" />
<meta content="yes" name="apple-mobile-web-app-capable" />
<meta content="black" name="apple-mobile-web-app-status-bar-style" />
<link href="WEB-RES/css/wap/reg_pg.css" rel="stylesheet" />
<script type="text/javascript">
	jQuery(function($){
		$('#username').keydown(function(e){
			if(e.keyCode==13){
				$('#password').focus();
			}
		});
		$('#password').keydown(function(e){
			if(e.keyCode==13){
				$('#repassword').focus();
			}
		});
		$('#repassword').keydown(function(e){
			if(e.keyCode==13){
				$('#email').focus();
			}
		});
		$('#email').keydown(function(e){
			if(e.keyCode==13){
				register();
			}
		});
	});
</script>
</head>
<body>
<div class="k_bc res_Content">
	<header class="c_head res_head">
		<h1 class="kdlogo"><a href="<%=appConf.getProperty("app.wap.url")%>"><img src="WEB-RES/images/logo.png" alt="卡当"></a></h1>
		<ul class="golink">
			<li><a href="<%=appConf.getProperty("app.wap.url")%>/baixc/shopping/order/kuaidi/kuaidiInfoQuery!toWapKuaidi.action"><img src="WEB-RES/images/icon1.png"></a></li>
			<li><a href="<%=appConf.getProperty("app.wap.url")%>/mobile/mykd/"><img src="WEB-RES/images/icon2.png"></a></li>
			<li><a href="<%=appConf.getProperty("app.wap.url")%>/baixc/shoppingcar.html"><img src="WEB-RES/images/icon3.png"></a></li>
		</ul>
	</header>
	<article class="k_bgeee k_pb30">
		<h2 class="k_spes c_goback">用户注册</h2>
		<form action="#" method="get">
			<div class="reg_input">
				<label for="username">
					<span>会员名</span>
					<span class="err" id="UNErr"></span>
					<input id="username" name="username" type="text"/>
					<span class="tip">5-20个字符，只能为字母、数字、下划线</span>
				</label>
				<label for="password">
					<span>密码</span>
					<span class="err" id="PwdErr"></span>
					<input id="password" name="password" type="password"/>
					<span class="tip">6-20个字符，请使用字母、数字或其组合</span>
				</label>
				<label for="repassword">
					<span>确认密码</span>
					<span class="err" id="RPwdErr"></span>
					<input id="repassword" name="repassword" type="password"/>
				</label>
				<label for="email" class="last">
					<span>电子邮箱</span>
					<span class="err" id="EMErr"></span>
					<input id="email" name="email" type="text"/>
					<span class="tip">接收订单信息和找回密码</span>
				</label>
				<p id="msg" style="font-size: 18px;color: #D00;text-align: center;" class="k_pb15"></p>
				<input type="hidden" id="service" name="service" value="<%=request.getParameter("service")==null?"":request.getParameter("service")%>" />
				<input type="button" class="btn" onclick="register()" id="Submits" value="免费注册"/>
			</div>
		</form>
		<p class="loginTip">已有账号? <a href="login?service=<%=request.getParameter("service")%>" class="k_acblue">马上登录</a></p>
	</article>
	<footer class="c_foot res_foot">
		<ul class="k_ovh go">
			<li class="l1"><img src="WEB-RES/images/g1.png"/></li>
			<li class="l2"><img src="WEB-RES/images/g2.png"/></li>
			<li class="l3"><img src="WEB-RES/images/g3.png"/></li>
		</ul>
		<div class="bot">
			<p class="k_bd568 k_posr k_spes k_cd00 tel">400 118 9191<span class="k_spes k_posa s"></span></p>
			<p class="k_f16 k_mt10 write"><a href="<%=appConf.getProperty("app.wap.url")%>/">首页</a> | <a href="#">帮助</a> | <a href="#">联系我们</a></p>
			<p class="k_f16 k_mt10 write">网上经营许可证号：<span class="k_pl10">浙B2-20120279</span></p>
		</div>
	</footer>
</div>
</body>
<%
}
%>
</html>
