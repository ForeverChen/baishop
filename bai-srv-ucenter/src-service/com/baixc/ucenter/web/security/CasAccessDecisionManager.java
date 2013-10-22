package com.baixc.ucenter.web.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.model.admin.Roles;

public class CasAccessDecisionManager implements AccessDecisionManager {

	public void decide(Authentication authentication, Object object,
			Collection<ConfigAttribute> configAttributes)
			throws AccessDeniedException, InsufficientAuthenticationException {
		
		if (configAttributes == null) {
			return;
		}
		
		for(ConfigAttribute ca : configAttributes) {
			String needRole = ((SecurityConfig) ca).getAttribute();
			
			if(authentication.getPrincipal() instanceof Admins){			
				List<Roles> roles = ((Admins)authentication.getPrincipal()).getRoles();
				for(Roles role : roles){
					if (needRole.equals(role.getRoleName())) {
						return;
					}
				}
			}
		}
		
		throw new AccessDeniedException("没有权限");
	}

	public boolean supports(ConfigAttribute attribute) {
		return true;
	}

	public boolean supports(Class<?> clazz) {
		return true;
	}
}