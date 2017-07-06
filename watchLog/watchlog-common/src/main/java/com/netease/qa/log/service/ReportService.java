package com.netease.qa.log.service;

import com.alibaba.fastjson.JSONArray;
import com.netease.qa.log.meta.Report;;

public interface ReportService {
	
	public int createReport(Report report);
	
	public int deleteReport(int reportid);
	
 	public int getTotalCountByProjectId(int projectid);
 	
 	public JSONArray getReportListSortedByProjectid(int project, String field, String order, int limit, int offset);
 
 	public Report getReportById(int reportid);

}
