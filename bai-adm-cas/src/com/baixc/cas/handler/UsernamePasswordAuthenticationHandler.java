package com.baixc.cas.handler;

import javax.annotation.Resource;

import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;

import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.service.admin.AdminsService;


/**
 * 自定义用户名密码认证
 * @author Linpn
 */
public class UsernamePasswordAuthenticationHandler extends
		AbstractUsernamePasswordAuthenticationHandler {

	@Resource
	protected AdminsService adminsService;

    public boolean authenticateUsernamePasswordInternal(final UsernamePasswordCredentials credentials) {
    	try{
    		Admins user = adminsService.getAdmins(credentials.getUsername(), credentials.getPassword());
        	
        	if(user!=null){
                credentials.setUsername(user.getUsername());
                log.debug("User [" + credentials.getUsername() + "] was successfully authenticated.");
                return true;
        	}

            log.debug("User [" + credentials.getUsername() + "] failed authentication");
            return false;
    		
    	}catch(Exception e){
        	e.printStackTrace();
        	log.debug("User [" + credentials.getUsername() + "] failed authentication");
            return false;
        }
    }
    


}
