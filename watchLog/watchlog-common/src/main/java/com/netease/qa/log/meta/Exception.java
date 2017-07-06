package com.netease.qa.log.meta;


public class Exception {
	
	private int exceptionId;
	private int logSourceId;
	private String exceptionType;
	private String exceptionTypeMD5;
	private String exceptionDemo;
	
	
	public int getExceptionId() {
		return exceptionId;
	}

	public void setExceptionId(int exceptionId) {
		this.exceptionId = exceptionId;
	}

	public int getLogSourceId() {
		return logSourceId;
	}
	
	public void setLogSourceId(int logSourceId) {
		this.logSourceId = logSourceId;
	}
	
	public String getExceptionType() {
		return exceptionType;
	}
	
	public void setExceptionType(String exceptionType) {
		this.exceptionType = exceptionType;
	}
	
	public String getExceptionTypeMD5() {
		return exceptionTypeMD5;
	}
	
	public void setExceptionTypeMD5(String exceptionTypeMD5) {
		this.exceptionTypeMD5 = exceptionTypeMD5;
	}
	
	public String getExceptionDemo() {
		return exceptionDemo;
	}
	
	public void setExceptionDemo(String exceptionDemo) {
		this.exceptionDemo = exceptionDemo;
	}

}
