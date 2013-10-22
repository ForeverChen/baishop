package com.baixc.oauth.handler;

import javax.annotation.Resource;

import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import com.baixc.framework.utils.OauthTokenBuilder;
import com.baixc.framework.utils.OauthTokenBuilder.OauthToken;
import com.baixc.ucenter.model.user.User;
import com.baixc.ucenter.service.user.UserService;


public class UsernamePasswordAuthenticationHandler extends
		AbstractUsernamePasswordAuthenticationHandler {
	
	@Resource
	private UserService userService;

	@Override
    public boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) {
    	try{
    		if(credentials.getUsername().equals("AUTO_USER_TOKEN")){
    			//自动登录
    			OauthToken token = OauthTokenBuilder.get().parseToken(credentials.getPassword());
	    		User user = userService.getUser(token.getUsername(), token.getPassword());
	            if(user!=null) {
	                credentials.setUsername(user.getUsername()); //设置用户名
	                log.debug("User [" + credentials.getUsername() + "] was successfully authenticated.");
	                return true;
	            }
    		}else {
	    		//注册用户登录
	    		User user = userService.getUser(credentials.getUsername(), credentials.getPassword());    		
	            if(user!=null) {
	                credentials.setUsername(user.getUsername()); //如果登录名为邮箱，则设置成用户名
	                log.debug("User [" + credentials.getUsername() + "] was successfully authenticated.");
	                return true;
	            }	            
    		}

            log.debug("User [" + credentials.getUsername() + "] failed authentication");
            return false;
    		
    	}catch(Exception e){
        	log.error("User [" + credentials.getUsername() + "] failed authentication", e);
            return false;
        }
    	
    }
}
