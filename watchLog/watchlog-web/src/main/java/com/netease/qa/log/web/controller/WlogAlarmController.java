package com.netease.qa.log.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class WlogAlarmController {
	@RequestMapping(value = "/logsrc/alarm", method = RequestMethod.GET)
	public String home( Model model) {
		model.addAttribute("controller", "WlogAlarm" );	
		model.addAttribute("action", "alarm" );			
		return "/logsrc/alarm";
	}
	

}
