package com.baixc.ass.web;

import net.sf.json.JSONObject;

import org.springframework.web.servlet.ModelAndView;

import com.baixc.framework.web.HttpServletExtendRequest;
import com.baixc.framework.web.HttpServletExtendResponse;
import com.baixc.ucenter.model.admin.Admins;
import com.baixc.ucenter.web.controller.PageAdmController;

public class Index extends PageAdmController {

	@Override
	public void execute(HttpServletExtendRequest request,
			HttpServletExtendResponse response, ModelAndView modeview) {
		
		Admins user =this.getCurrUser();		
		if(user!=null){
			JSONObject systemModules = this.getSystemModulesOfJSON(user, 
														request.getParameter("sysname"), 
														request.getParameter("sysurl"));			
			modeview.addObject("casass", systemModules.getJSONArray("casass"));
			modeview.addObject("shopmng", systemModules.getJSONArray("shopmng"));
			modeview.addObject("erpcrm", systemModules.getJSONArray("erpcrm"));
			modeview.addObject("search", systemModules.getJSONArray("search"));
		}
	}

}
