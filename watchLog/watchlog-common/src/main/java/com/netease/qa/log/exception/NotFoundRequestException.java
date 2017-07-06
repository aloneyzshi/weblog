package com.netease.qa.log.exception;

public class NotFoundRequestException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotFoundRequestException(String message){
		super(message);
	}
}
