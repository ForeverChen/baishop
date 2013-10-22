<%@ page contentType="text/html; charset=UTF-8" %>
<head>
<script src="WEB-RES/script/jquery1.8.3.js" type="text/javascript"></script>
</head>
<body>
<form:form method="post" id="frmLogin" cssClass="fm-v clearfix" commandName="${commandName}" htmlEscape="true">		
	<form:errors path="*" id="msg" element="div" /><br />
	<div style="display: none">
		<label for="username">
			<spring:message code="screen.welcome.label.netid.accesskey" var="userNameAccessKey" />
			<form:input id="username" size="25" tabindex="1" accesskey="${userNameAccessKey}" path="username" autocomplete="false" htmlEscape="true" />
		</label>
		<label for="password" class="last">
			<spring:message code="screen.welcome.label.password.accesskey" var="passwordAccessKey" />
			<form:password id="password" size="25" tabindex="2" path="password"  accesskey="${passwordAccessKey}" htmlEscape="true" autocomplete="off" />
		</label>
		<input type="hidden" id="it" name="lt" value="${loginTicket}" />
		<input type="hidden" name="execution" value="${flowExecutionKey}" />
		<input type="hidden" name="_eventId" value="submit" />
		<input type="button" class="btn" id="btnSubmit" value="提交" onclick="login()" />
	</div>
</form:form>
</body>