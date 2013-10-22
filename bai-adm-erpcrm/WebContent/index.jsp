<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="java.util.Properties" %>
<%@ page import="org.springframework.web.context.support.WebApplicationContextUtils" %>
<%
	final Properties appConf = (Properties) WebApplicationContextUtils.getWebApplicationContext(request.getServletContext()).getBean("appConf");
	String url = appConf.getProperty("app.ass.url") +"?sysname=erpcrm&sysurl=" + URLEncoder.encode(appConf.getProperty("app.url"),"UTF-8");
	response.sendRedirect(url);
%>