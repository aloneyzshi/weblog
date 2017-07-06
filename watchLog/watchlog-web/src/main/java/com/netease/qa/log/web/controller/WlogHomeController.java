package com.netease.qa.log.web.controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller
public class WlogHomeController {
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home( Model model) {
		model.addAttribute("controller", "WlogHome" );	
		model.addAttribute("action", "home" );			
		return "home";
	}
	
}
