package com.netease.qa.log.service.impl;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.meta.Report;
import com.netease.qa.log.meta.dao.LogSourceDao;
import com.netease.qa.log.meta.dao.ReportDao;
import com.netease.qa.log.service.ReportService;
import com.netease.qa.log.util.MathUtil;

@Service
public class ReportServiceImpl implements ReportService{

	private static final Logger logger = LoggerFactory.getLogger(LogsourceServiceImpl.class);
	@Resource
	private ReportDao reportDao;
	@Resource
	private LogSourceDao logSourceDao;

	@Override
	public int createReport(Report report) {
		try {
			reportDao.insert(report);
			return report.getLogSourceId();
		} catch (Exception e) {
			logger.error("error", e);
			return 0;
		}
	}
	
	@Override
	public int deleteReport(int reportid) {
		try {
			reportDao.delete(reportid);
			return 1;
		} catch (Exception e) {
			logger.error("error", e);
			return 0;
		}
	}

	@Override
	public int getTotalCountByProjectId(int projectid) {
		int count = reportDao.getTotalCountByProjectId(projectid);
		return count;
	}

	@Override
	public JSONArray getReportListSortedByProjectid(int project, String field, String order, int limit, int offset) {
		
		List<Report> reports = null;
		try {
			reports = reportDao.getSortedByProjectId(project, field, order, limit, offset);
		} catch (Exception e) {
			System.out.println("database is error");
			logger.error("error", e);
			return null;
		}
		JSONArray result = new JSONArray();
		if(reports.size() == 0){
		  result.add(new JSONObject());
		  return result;
		}
		JSONObject record = null;
		JSONArray records = new JSONArray();
		Report report = new Report();
		int i = 0;
		while(i < reports.size()){
			record = new JSONObject();
			report = reports.get(i);
			record.put("report_id", report.getReportId());
			record.put("title", report.getTitle());
			record.put("start_time", MathUtil.parse2Str(report.getStartTime()));
			record.put("end_time", MathUtil.parse2Str(report.getEndTime()));
			record.put("creator", report.getCreatorId());
			record.put("create_time", MathUtil.parse2Str(report.getCreatTime()));
			records.add(record);
			i++;
		}
		return records;
	}

	@Override
	public Report getReportById(int reportid) {
		Report report = reportDao.findByReportId(reportid);
		return report;
	}
	
}
