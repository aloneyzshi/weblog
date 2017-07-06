package com.netease.qa.log.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alibaba.fastjson.JSONObject;


@ControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(InvalidRequestException.class)
	public JSONObject handleInvalidRequestError(InvalidRequestException ex){
		JSONObject json = new JSONObject();
		json.put("message", ex.getMessage());
		return json;
	}
	
	@ExceptionHandler(ConflictRequestException.class)
	public JSONObject handleConflictRequestException(ConflictRequestException cr){
		JSONObject json = new JSONObject();
		json.put("message", cr.getMessage());
		return json;
	}
	
	@ExceptionHandler(NotFoundRequestException.class)
	public JSONObject handleNotFoundRequestException(NotFoundRequestException nr){
		JSONObject json = new JSONObject();
		json.put("message", nr.getMessage());
		return json;
	}
	
	@ExceptionHandler(RuntimeException.class)
	public JSONObject handleUnexception(RuntimeException ex){
		return new JSONObject();
	}
	
	@ExceptionHandler(NullParamException.class)
	public JSONObject handleNullParamException(NullParamException ne){
		JSONObject json = new JSONObject();
		json.put("message", ne.getMessage());
		return json;
	}

}
