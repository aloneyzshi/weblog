package com.netease.qa.log.web.controller;


import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.service.ProjectService;
import com.netease.qa.log.util.Const;
import com.netease.qbs.meta.User;

@Controller
@RequestMapping(value = "/")
public class WlogProjectController {
	
	private static final Logger logger = LoggerFactory.getLogger(WlogProjectController.class);

	@Resource
	private ApiExceptionHandler apiException;
	@Resource
	private ProjectService projectService;
	
	
	@RequestMapping(value = "/projects", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getAllProjects(HttpSession session) {
	
		User user = (User) session.getAttribute("user");
		logger.info("userId:" + user.getId() + ";user email:" + user.getEmail() + ";user name:" + user.getFullname());
		String message = Const.RESPONSE_SUCCESSFUL;
		JSONArray data = projectService.getAllProjectsByQbs(user);
		if(data == null){
			message = Const.RESPONSE_NOTSUCCESSFUL;
		}
		JSONObject result = new JSONObject();
		result.put("message", message);
		result.put("data", data);
		logger.debug("### [route]/projects   [key]data : " +  data.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
}
