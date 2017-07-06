package com.netease.qa.log.meta.dao;

import java.util.List;

import com.netease.qa.log.meta.LogSource;


public interface LogSourceDao {
	
	public int insert(LogSource logSource);
	
	public int insertAll(LogSource logSource);
	
	public int update(LogSource logSource);
	
	public int updateAll(LogSource logSource);

	public int delete(int logSourceId);
    
    public LogSource findByLogSourceId(int logSourceId);
    
    public LogSource findByLocation(String hostname, String path, String filePattern);
    
    public LogSource findByLogSourceName(String logSourceName);
    
    public List<LogSource> selectAllByProjectId(int projectId);
    
    public List<LogSource> selectAllByProjectIdOrderByName(int projectId);
    
    public int getTotalCountByProjectId(int projectId);
    
    public List<LogSource> findByProjectId(int projectId, int limit, int offset);
    
    public List<LogSource> getSortedByProjectId(int projectId, String field, String order, int limit, int offset);
    
    /**
     * 获取hostname下所有的日志源
     * @param hostname
     * @return
     */
    public List<LogSource> getLogSourcesByHostname(String hostname);
    
    // 获取机器所有的日志源ids，不包含nginx日志源
    public String findIdsByHostname(String hostname, int log_type);
    
}
