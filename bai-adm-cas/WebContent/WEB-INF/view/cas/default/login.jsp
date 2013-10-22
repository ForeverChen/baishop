<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>卡当网管理系统</title>
<link rel="shortcut icon" type="image/ico" href="favicon.ico" />
<script src="/cas/WEB-RES/js/jquery-1.5.2.js" type="text/javascript"></script>
<script src="/cas/WEB-RES/js/jquery.cookie.js" type="text/javascript"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$("#username").val($.cookie("remember_username"));
		
		if($.cookie("remember_username")){
			$("#remember").attr("checked", true);
			$("#password").focus();	
		}else{
			$("#remember").attr("checked", false);
			$("#username").focus();	
		}
		
		$("#username").keydown(function(e){ 
			if(e.keyCode==13){ 
				if(!$("#password").val()){
					$("#password").focus();					
					return false;
				}else{
					return true;
				}
			} 
		});  
		
		$("#fm1").submit(function(){
			if($("#remember").attr("checked")){
				$.cookie("remember_username", $("#username").val(), {expires: 10});
			}else{
				$.cookie("remember_username", null);
			}
		});
	});
</script>
</head>
<link rel="stylesheet" href="/cas/WEB-RES/css/web/erp.css" />
<body class="loginBody">
	<div class="loginMain">
		<div class="fl erp_img erp_logo"></div>
		<fieldset class="fl erp_img erp_login">
			<form:form method="post" id="fm1" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">
				<table class="erp_tableLogin">
					<tr>
						<td class="td1"></td><td>
							<form:errors path="*" id="msg" cssClass="loginEor fz14" />
							&nbsp;
						</td>
					</tr>
					<tr>
						<td class="td1 fz18"><label for="name">用户名：</label></td><td>
							<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
							<form:input cssClass="input_text" id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
						</td>
					</tr>
					<tr>
						<td class="td1 fz18"><label for="pwd">密　码：</label></td><td>
							<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
							<form:password cssClass="input_text" id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />						
						</td>
					</tr>
					<tr>
						<td class="td1"></td>
						<td>
							<label>
								<input type="checkbox" id="remember" tabindex="3" class="fl checkBox"/>
								<span class="checkBoxTips">记住用户名</span>
							</label>
						</td>
					</tr>
					<tr>
						<td class="td1"></td>
						<td>
							<input type="hidden" name="lt" value="${loginTicket}" />
							<input type="hidden" name="execution" value="${flowExecutionKey}" />
							<input type="hidden" name="_eventId" value="submit" />	
	                        <input class="subBtn" name="submit" accesskey="l" value="" tabindex="4" type="submit" />							
						</td>
					</tr>
				</table>
			</form:form>
		</fieldset>
		<div class="fl erp_img erp_rightDiv"></div>
	</div>
</body>
</html>
