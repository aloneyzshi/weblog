package com.netease.qa.log.meta;


public class ExceptionDataRecord {
	
	private Long sampleTime;
	private String exceptionIds;
	private String exceptionCounts;
	private int totalCount;

	
	public Long getSampleTime() {
		return sampleTime;
	}
	
	public void setSampleTime(Long sampleTime) {
		this.sampleTime = sampleTime;
	}

	public String getExceptionIds() {
		return exceptionIds;
	}
	
	public void setExceptionIds(String exceptionIds) {
		this.exceptionIds = exceptionIds;
	}

	public String getExceptionCounts() {
		return exceptionCounts;
	}

	public void setExceptionCounts(String exceptionCounts) {
		this.exceptionCounts = exceptionCounts;
	}
	
	public int getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public String toString(){
		return "sampleTime: " + sampleTime  + ", exceptionIds: " + exceptionIds + 
				", exceptionCounts: " + exceptionCounts + ", totalCount: " + totalCount;
	}
	
}
