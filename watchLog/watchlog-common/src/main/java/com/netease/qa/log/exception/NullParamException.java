package com.netease.qa.log.exception;

public class NullParamException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NullParamException(String message){
		super(message);
	}
}