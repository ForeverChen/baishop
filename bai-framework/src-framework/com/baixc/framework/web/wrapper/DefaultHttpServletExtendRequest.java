package com.baixc.framework.web.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.baixc.framework.web.HttpServletExtendRequest;


public class DefaultHttpServletExtendRequest implements HttpServletExtendRequest {

	protected final Log logger = LogFactory.getLog(getClass());
	
	private HttpServletRequest request;
	
	public DefaultHttpServletExtendRequest(HttpServletRequest request){
		this.request = request;
	}
	
	
	@Override
	public HttpServletRequest getRequest() {
		return request;
	}


	@Override
	public String getParameter(String name) {
		String value = request.getParameter(name);
		if(value!=null)
			value = value.trim();
		return value;
	}

	@Override
	public String getParameter(String name, String charsetName) {
		return this.getParameter(name, charsetName, null);
	}

	@Override
	public String getParameter(String name, String charsetName, String defaultValue) {
		try {
			String value = this.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			if(StringUtils.isBlank(charsetName))
				charsetName = "UTF-8";
			
			return new String(value.getBytes("ISO8859-1"), charsetName);
			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e); 
		}
	}
	
	@Override
	public Boolean getBooleanParameter(String name) {
		return this.getBooleanParameter(name, null);
	}

	@Override
	public Boolean getBooleanParameter(String name, Boolean defaultValue) {
		return this.getBooleanParameter(name, defaultValue, null);
	}

	@Override
	public Boolean getBooleanParameter(String name, Boolean defaultValue, String equalsValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			if(StringUtils.isBlank(equalsValue))
				return Boolean.valueOf(value.trim());
			else
				return value.trim().equals(equalsValue);			
			
		}catch(Exception e){
			return defaultValue;
		}
	}

	@Override
	public Character getCharParameter(String name) {
		return this.getCharParameter(name, null);
	}

	@Override
	public Character getCharParameter(String name, Character defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Character.valueOf(value.trim().charAt(0));
			
		}catch(Exception e){
			return defaultValue;
		}
	}

	@Override
	public Byte getByteParameter(String name) {
		return this.getByteParameter(name, null);
	}

	@Override
	public Byte getByteParameter(String name, Byte defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Byte.valueOf(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}

	@Override
	public Short getShortParameter(String name) {
		return this.getShortParameter(name, null);
	}

	@Override
	public Short getShortParameter(String name, Short defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Short.valueOf(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}

	@Override
	public Integer getIntParameter(String name) {
		return this.getIntParameter(name, null);
	}

	@Override
	public Integer getIntParameter(String name, Integer defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Integer.valueOf(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}

	@Override
	public Long getLongParameter(String name) {
		return this.getLongParameter(name, null);
	}

	@Override
	public Long getLongParameter(String name, Long defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Long.valueOf(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	@Override
	public Float getFloatParameter(String name) {
		return this.getFloatParameter(name, null);
	}

	@Override
	public Float getFloatParameter(String name, Float defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Float.valueOf(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	@Override
	public Double getDoubleParameter(String name) {
		return this.getDoubleParameter(name, null);
	}

	@Override
	public Double getDoubleParameter(String name, Double defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return Double.valueOf(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	@Override
	public BigDecimal getBigDecimalParameter(String name) {
		return this.getBigDecimalParameter(name, null);
	}
	
	@Override
	public BigDecimal getBigDecimalParameter(String name, BigDecimal defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			return new BigDecimal(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}
	
	@Override
	public Date getDateParameter(String name) {
		return this.getDateParameter(name, null);
	}

	@Override
	public Date getDateParameter(String name, String pattern){
		return this.getDateParameter(name, pattern, null);
	}	

	@Override
	public Date getDateParameter(String name, String pattern, Date defaultValue) {
		try{
			String value = request.getParameter(name);
			
			if(StringUtils.isBlank(value))
				return defaultValue;
			
			SimpleDateFormat sdf;
			if(StringUtils.isBlank(pattern))
				sdf = new SimpleDateFormat("yyyy-MM-dd");
			else
				sdf = new SimpleDateFormat(pattern);
			
			return sdf.parse(value.trim());
			
		}catch(Exception e){
			return defaultValue;
		}
	}	

	@Override
	public <T> T getBindObject(Class<T> clazz) {
		return this.getBindObject(clazz, null);
	}

	@Override
	public <T> T getBindObject(Class<T> clazz, String objectName) {
		try {
			PropertyValue[] propertyValues = new ServletRequestParameterPropertyValues(request).getPropertyValues();
			T obj = clazz.newInstance();
			objectName = StringUtils.isBlank(objectName)?"":objectName+".";
			
			for(PropertyValue propertyValue : propertyValues){
				try {
					String name = propertyValue.getName();
					
					//反射赋值
					if(name.startsWith(objectName)){
						Field field = clazz.getDeclaredField(name.substring(objectName.length()));
						Method method = clazz.getMethod("set" + field.getName().substring(0, 1).toUpperCase(Locale.ENGLISH) + field.getName().substring(1), field.getType());
						
						//String
						if(field.getType()==String.class){
							method.invoke(obj, this.getParameter(name));
						}else
						//Boolean
						if(field.getType()==Boolean.class){
							method.invoke(obj, this.getBooleanParameter(name));
						}else
						//Character
						if(field.getType()==Character.class){
							method.invoke(obj, this.getCharParameter(name));
						}else
						//Byte
						if(field.getType()==Byte.class){
							method.invoke(obj, this.getByteParameter(name));
						}else
						//Short
						if(field.getType()==Short.class){
							method.invoke(obj, this.getShortParameter(name));
						}else
						//Integer
						if(field.getType()==Integer.class){
							method.invoke(obj, this.getIntParameter(name));
						}else
						//Long
						if(field.getType()==Long.class){
							method.invoke(obj, this.getLongParameter(name));
						}else
						//Float
						if(field.getType()==Float.class){
							method.invoke(obj, this.getFloatParameter(name));
						}else
						//Double
						if(field.getType()==Double.class){
							method.invoke(obj, this.getDoubleParameter(name));
						}else
						//BigDecimal
						if(field.getType()==BigDecimal.class){
							method.invoke(obj, this.getBigDecimalParameter(name));
						}else
						//Date
						if(field.getType()==Date.class){
							method.invoke(obj, this.getDateParameter(name));
						}
						//其它
						else{
							method.invoke(obj, propertyValue.getValue());
						}
					}
				} catch (Exception e) {
				}
			}
						
			return obj;
			
		} catch (Exception e) {
			logger.error("绑定对象出错", e);
		}
		
		return null;
	}
	
	@Override
	public MultipartFile getFile(String name) {		
		if (request instanceof MultipartRequest) {
			MultipartRequest multipartRequest = (MultipartRequest) request;
			return multipartRequest.getFile(name);
		}
		
		return null;
	}

	@Override
	public List<MultipartFile> getFiles(String name) {
		if (request instanceof MultipartRequest) {
			MultipartRequest multipartRequest = (MultipartRequest) request;
			return multipartRequest.getFiles(name);
		}
		
		return null;
	}

	@Override
	public Iterator<String> getFileNames() {		
		if (request instanceof MultipartRequest) {
			MultipartRequest multipartRequest = (MultipartRequest) request;
			return multipartRequest.getFileNames();
		}
		
		return null;
	}

	@Override
	public Map<String, MultipartFile> getFileMap() {
		if (request instanceof MultipartRequest) {
			MultipartRequest multipartRequest = (MultipartRequest) request;
			return multipartRequest.getFileMap();
		}
		
		return null;
	}

	@Override
	public MultiValueMap<String, MultipartFile> getMultiFileMap() {
		if (request instanceof MultipartRequest) {
			MultipartRequest multipartRequest = (MultipartRequest) request;
			return multipartRequest.getMultiFileMap();
		}
		
		return null;
	}
	
	
	@Override
	public String getAuthType() {
		return request.getAuthType();
	}

	@Override
	public Cookie[] getCookies() {
		return request.getCookies();
	}

	@Override
	public long getDateHeader(String name) {
		return request.getDateHeader(name);
	}

	@Override
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	@Override
	public Enumeration<String> getHeaders(String name) {
		return request.getHeaders(name);
	}

	@Override
	public Enumeration<String> getHeaderNames() {
		return request.getHeaderNames();
	}

	@Override
	public int getIntHeader(String name) {
		return request.getIntHeader(name);
	}

	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public String getPathInfo() {
		return request.getPathInfo();
	}

	@Override
	public String getPathTranslated() {
		return request.getPathTranslated();
	}

	@Override
	public String getContextPath() {
		return request.getContextPath();
	}

	@Override
	public String getQueryString() {
		return request.getQueryString();
	}

	@Override
	public String getRemoteUser() {
		return request.getRemoteUser();
	}

	@Override
	public boolean isUserInRole(String role) {
		return request.isUserInRole(role);
	}

	@Override
	public Principal getUserPrincipal() {
		return request.getUserPrincipal();
	}

	@Override
	public String getRequestedSessionId() {
		return request.getRequestedSessionId();
	}

	@Override
	public String getRequestURI() {
		return request.getRequestURI();
	}

	@Override
	public StringBuffer getRequestURL() {
		return request.getRequestURL();
	}

	@Override
	public String getServletPath() {
		return request.getServletPath();
	}

	@Override
	public HttpSession getSession(boolean create) {
		return request.getSession(create);
	}

	@Override
	public HttpSession getSession() {
		return request.getSession();
	}

	@Override
	public boolean isRequestedSessionIdValid() {
		return request.isRequestedSessionIdValid();
	}

	@Override
	public boolean isRequestedSessionIdFromCookie() {
		return request.isRequestedSessionIdFromCookie();
	}

	@Override
	public boolean isRequestedSessionIdFromURL() {
		return request.isRequestedSessionIdFromURL();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isRequestedSessionIdFromUrl() {
		return request.isRequestedSessionIdFromUrl();
	}

	@Override
	public boolean authenticate(HttpServletResponse response)
			throws IOException, ServletException {
		return request.authenticate(response);
	}

	@Override
	public void login(String username, String password) throws ServletException {
		request.login(username, password);
	}

	@Override
	public void logout() throws ServletException {
		request.logout();
	}

	@Override
	public Collection<Part> getParts() throws IOException,
			IllegalStateException, ServletException {
		return request.getParts();
	}

	@Override
	public Part getPart(String name) throws IOException, IllegalStateException,
			ServletException {
		return request.getPart(name);
	}

	@Override
	public Object getAttribute(String name) {
		return request.getAttribute(name);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return request.getAttributeNames();
	}

	@Override
	public String getCharacterEncoding() {
		return request.getCharacterEncoding();
	}

	@Override
	public void setCharacterEncoding(String env)
			throws UnsupportedEncodingException {
		request.setCharacterEncoding(env);
	}

	@Override
	public int getContentLength() {
		return request.getContentLength();
	}

	@Override
	public String getContentType() {
		return request.getContentType();
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		return request.getInputStream();
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return request.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return request.getParameterValues(name);
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return request.getParameterMap();
	}

	@Override
	public String getProtocol() {
		return request.getProtocol();
	}

	@Override
	public String getScheme() {
		return request.getScheme();
	}

	@Override
	public String getServerName() {
		return request.getServerName();
	}

	@Override
	public int getServerPort() {
		return request.getServerPort();
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return request.getReader();
	}

	@Override
	public String getRemoteAddr() {
		return request.getRemoteAddr();
	}

	@Override
	public String getRemoteHost() {
		return request.getRemoteHost();
	}

	@Override
	public void setAttribute(String name, Object o) {
		request.setAttribute(name, o);
	}

	@Override
	public void removeAttribute(String name) {
		request.removeAttribute(name);
	}

	@Override
	public Locale getLocale() {
		return request.getLocale();
	}

	@Override
	public Enumeration<Locale> getLocales() {
		return request.getLocales();
	}

	@Override
	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String path) {
		return request.getRequestDispatcher(path);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String getRealPath(String path) {
		return request.getRealPath(path);
	}

	@Override
	public int getRemotePort() {
		return request.getRemotePort();
	}

	@Override
	public String getLocalName() {
		return request.getLocalName();
	}

	@Override
	public String getLocalAddr() {
		return request.getLocalAddr();
	}

	@Override
	public int getLocalPort() {
		return request.getLocalPort();
	}

	@Override
	public ServletContext getServletContext() {
		return request.getServletContext();
	}

	@Override
	public AsyncContext startAsync() {
		return request.startAsync();
	}

	@Override
	public AsyncContext startAsync(ServletRequest servletRequest,
			ServletResponse servletResponse) {
		return request.startAsync(servletRequest, servletResponse);
	}

	@Override
	public boolean isAsyncStarted() {
		return request.isAsyncStarted();
	}

	@Override
	public boolean isAsyncSupported() {
		return request.isAsyncSupported();
	}

	@Override
	public AsyncContext getAsyncContext() {
		return request.getAsyncContext();
	}

	@Override
	public DispatcherType getDispatcherType() {
		return request.getDispatcherType();
	}
	
}

