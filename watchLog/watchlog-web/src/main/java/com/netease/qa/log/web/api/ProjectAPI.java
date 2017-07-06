package com.netease.qa.log.web.api;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.exception.InvalidRequestException;
import com.netease.qa.log.exception.NotFoundRequestException;
import com.netease.qa.log.exception.NullParamException;
import com.netease.qa.log.service.ProjectService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.MathUtil;

@Controller
@RequestMapping(value = "api/project")
public class ProjectAPI {

	@Resource
	private ProjectService projectService;
	@Resource
	private ApiExceptionHandler apiException;
	
	@RequestMapping(value = "/{project_id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> findAllLogSourceByProjectId(@PathVariable String project_id){
		if (MathUtil.isEmpty(project_id)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(project_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		// 去除api对qbs的依赖
//		if(!projectService.checkProjectExsit(Integer.parseInt(project_id))){
//			NotFoundRequestException nr = new NotFoundRequestException(Const.PROJECT_NOT_EXSIT);
//			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
//		}
		JSONObject result = projectService.findAllLogSourcesByProjectId(Integer.parseInt(project_id));
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
}
