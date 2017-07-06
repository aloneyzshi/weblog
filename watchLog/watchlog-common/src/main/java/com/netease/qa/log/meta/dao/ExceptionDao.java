package com.netease.qa.log.meta.dao;

import com.netease.qa.log.meta.Exception;

public interface ExceptionDao {

	public int insert(Exception exception); 
	
	public int delete(int exceptionId);
    
    public Exception findByExceptionId(int exceptionId);
    
    public Exception findByTwoKey(int logSourceId, String exceptionTypeMD5);

}
