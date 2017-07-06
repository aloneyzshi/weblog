package com.netease.qa.log.meta.dao;

import java.util.List;

import com.netease.qa.log.meta.UkExceptionData;


public interface UkExceptionDataDao {
	
	public int insert(UkExceptionData ukExceptionData); 
	
	public int delete(int ukExceptionDataId);
    
    public UkExceptionData findByUkExceptionDataId(int ukExceptionDataId);
    
    public List<UkExceptionData> findByLogSourceIdAndTime(int logSourceId, long startTime, long endTime, int limit, int offset);
    
    public int getTotalCount(int logsourceId, long startTime, long endTime);
    
}
