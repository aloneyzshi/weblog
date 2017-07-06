package com.netease.qa.log.meta.dao;

import java.util.List;

import com.netease.qa.log.meta.ExceptionData;
import com.netease.qa.log.meta.ExceptionDataRecord;

public interface ExceptionDataDao {
	
	public int insert(ExceptionData exceptionData); 
	
	public int delete(int exceptionDataId);
    
    public ExceptionData findByExceptionDataId(int exceptionDataId);
    
    
    /**
     * 返回起止时间内汇总数据
     * +--------------+-----------------+-------------+
		| exception_id | exception_count | total_count |
		+--------------+-----------------+-------------+
		| 45,46,47     | 3941,6393,2602  |       12936 |
		+--------------+-----------------+-------------+
     */
    public ExceptionDataRecord findSummaryByLogSourceIdAndTime(int logSourceId, long startTime, long endTime);
    
    
    /**
     * 时间维度聚合，返回所有数据,
     * from_unixtime(aaa.sample_time) | sample_time | exception_id | exception_count | total_count |
		-------------------------------+-------------+--------------+-----------------+-------------+
		2015-06-09 21:53:22            |  1433858002 | 45,46,47     | 634,646,283     |        1563 |
		2015-06-09 21:53:17            |  1433857997 | 45,46,47     | 222,787,314     |        1323 |
		2015-06-09 21:53:21            |  1433858001 | 45,46,47     | 215,695,247     |        1157 |
     * @param orderBy: sample_time/ total_count 
     * @param order: desc/asc
     */
    public List<ExceptionDataRecord> findTimeRecordsByLogSourceIdAndTime(int logSourceId, long startTime, long endTime, 
    		String orderBy, String order, int limit, int offset);
    
    /**
     * 项目级别，返回某一个确定日志源的所有数据
     */
    public ExceptionDataRecord findRecordsByLogSourceIdAndTime(int logSourceId, long startTime, long endTime);
    
    /*
     * 返回趋势图的totalcount
     */
    public Integer getTotalCountByLogsourceIdAndTime(int logSourceId, long startTime, long endTime);
    /**
     * 时间维度聚合，返回记录总数，分页用
     */
    public int getTimeRecordsCountByLogSourceIdAndTime(int logSourceId, long startTime, long endTime);
    
    
    
    /**
     * 异常维度聚合，返回异常id和对应数量
	 * @param orderBy: total_count 
     * @param order: desc/asc
     */
    public List<ExceptionData> findErrorRecordsByLogSourceIdAndTime(int logSourceId, long startTime, long endTime, 
    		String orderBy, String order, int limit, int offset);
    
    /**
     * 返回特定类型的异常id和对应数量
     */
    public ExceptionData findUnknownTypeByLogSourceIdAndTime(int logSourceId, long startTime, long endTime, String field);
    
    /**
     * 异常维度聚合，返回记录总数，分页用
     */
    public int getErrorRecordsCountByLogSourceIdAndTime(int logSourceId, long startTime, long endTime);
    
    
    
    /**
     * 聚合分析-异常类型详情"更多"页面 
     * @param orderBy: sample_time/ exception_count 
     * @param order: desc/asc
     */
    public List<ExceptionData> findErrorRecordsByLogSourceIdAndExceptionIdAndTime(int logSourceId, int exceptionId, 
    		long startTime, long endTime, String orderBy, String order, int limit, int offset);

    
    /**
     *  聚合分析-异常类型详情"更多"页面,返回数据总量 
     */
    public int getErrorRecordsCountByLogSourceIdAndExceptionIdAndTime(int logSourceId,  int exceptionId, 
    		long startTime, long endTime);
    
    /**
     * AB平台，按照机器聚合,得到某一个日志源在一段时间内的所有的异常数量
     */
    public Integer getLogSourceExceptionTotalCountByTime(int logSourceId, long startTime, long endTime);
    
    public List<ExceptionData> findErrorRecordsByLogSourceIdAndTimeByAB(int logSourceId, long startTime, long endTime);
    
    //获取所有日志源在所有时间内的所有异常个数total
    public int findErrorTotalByMachineAndTimeByAB(String logSourceIds, long startTime, long endTime);
    
    //获取所有日志源在某一小段时间内的所有异常type和count
    public List<ExceptionData> findErrorRecordsByMachineAndTimeByAB(String logSourceIds, long startTime, long endTime);

}
