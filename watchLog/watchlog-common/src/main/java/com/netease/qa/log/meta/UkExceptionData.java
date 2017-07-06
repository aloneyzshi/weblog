package com.netease.qa.log.meta;


/**
 * 无法判断异常类型的异常日志
 * @author hzzhangweijie
 *
 */
public class UkExceptionData {
	
	private int ukExceptionDataId;
	private int logSourceId;
	private Long originLogTime;
	private String originLog;
	
	public int getUkExceptionDataId() {
		return ukExceptionDataId;
	}
	
	public void setUkExceptionDataId(int ukExceptionDataId) {
		this.ukExceptionDataId = ukExceptionDataId;
	}
	
	public int getLogSourceId() {
		return logSourceId;
	}
	
	public void setLogSourceId(int logSourceId) {
		this.logSourceId = logSourceId;
	}
	
	public Long getOriginLogTime() {
		return originLogTime;
	}
	
	public void setOriginLogTime(Long originLogTime) {
		this.originLogTime = originLogTime;
	}
	
	public String getOriginLog() {
		return originLog;
	}
	
	public void setOriginLog(String originLog) {
		this.originLog = originLog;
	}

	

}
