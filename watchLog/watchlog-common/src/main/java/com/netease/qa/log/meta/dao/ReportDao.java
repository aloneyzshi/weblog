package com.netease.qa.log.meta.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.netease.qa.log.meta.Report;

/*
 * report has not update
 */
public interface ReportDao {

    public int insert(Report report);

	public int delete(int reportId);
	
	public Report findByReportId(int reportId);
	
	public int getTotalCountByProjectId(int projectId);
	
	public List<Report> getSortedByProjectId(int projectId, String field, String order, int limit, int offset);
	
	public Report findByTime(int logsrcid, Timestamp start, Timestamp end);
}
