package com.netease.qa.log.web.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WlogManageController {
	
	@RequestMapping(value = "logsrc/manage", method = RequestMethod.GET)
	public String manage(Locale locale, Model model) {
		model.addAttribute("controller", "WlogManage" );		
		model.addAttribute("action", "manage" );				
		return "logsrc/manage";
	}
	
	
}
