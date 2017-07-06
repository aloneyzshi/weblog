package com.netease.qa.log.service;

import java.util.List;

import com.netease.qa.log.meta.UkExceptionData;

public interface UnknowService {

	public int getTotalCount(int logSourceId, long startTime, long endTime);
	
	public List<UkExceptionData> findByLogSourceIdAndTime(int logSourceId, long startTime, long endTime, int limit, int offset);
}
