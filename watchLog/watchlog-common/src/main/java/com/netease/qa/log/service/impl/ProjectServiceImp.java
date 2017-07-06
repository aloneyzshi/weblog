package com.netease.qa.log.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.meta.dao.LogSourceDao;
import com.netease.qa.log.service.ProjectService;
import com.netease.qa.log.util.MathUtil;
import com.netease.qbs.QbsService;
import com.netease.qbs.meta.Project;
import com.netease.qbs.meta.User;

@Service
public class ProjectServiceImp implements ProjectService{

	@Autowired
    private QbsService qbsService;
	@Resource
	private LogSourceDao logSourceDao;
	
	@Override
	public JSONArray getAllProjectsByQbs(User user) {
		List<Project> projects = qbsService.getProjects(user.getId());
		if(projects.size() == 0){
			return new JSONArray();
		}
		JSONArray records = new JSONArray();
		Project project = null;
		JSONObject record;
		int i = 0;
		while(i < projects.size()){
			project = projects.get(i);
			record = new JSONObject();
			record.put("id", project.getId());
			record.put("name", project.getName());
			records.add(record);
			i++;
		}
		return records;
	}

	@Override
	public boolean checkProjectExsit(int projectId) {
		Project project = qbsService.getProjectById(projectId);
		if(project != null){
			return true;
		}
		return false;
	}

	@Override
	public Project findByProjectId(int projectId) {
		Project project = qbsService.getProjectById(projectId);
		return project;
	}

	@Override
	public JSONObject findAllLogSourcesByProjectId(int projectId) {
		List<LogSource> logSources = logSourceDao.selectAllByProjectId(projectId);
		//Project project = qbsService.getProjectById(projectId);
		JSONObject result = new JSONObject();
		JSONArray logsources = new JSONArray();
		JSONObject logsource = new JSONObject();
		if(logSources != null && logSources.size() > 0){
			for(LogSource logSource : logSources){
				logsource = new JSONObject();
				logsource.put("log_source_id", logSource.getLogSourceId());
				logsource.put("log_source_name", logSource.getLogSourceName());
				logsource.put("modify_time", MathUtil.parse2Str(logSource.getModifyTime()));
				logsource.put("hostname", logSource.getHostname());
				logsource.put("path", logSource.getPath());
				logsource.put("file_pattern", logSource.getFilePattern());
				logsource.put("line_start", logSource.getLineStartRegex());
				logsource.put("filter_keyword", logSource.getLineFilterKeyword());
				logsource.put("type_regex", logSource.getLineTypeRegex());
				logsource.put("creator_id", logSource.getLogSourceCreatorId());
				logsource.put("status", logSource.getLogSourceStatus());
				logsources.add(logsource);
			}
		}else{
			logsources.add(logsource);
		}
		
		result.put("project_id", projectId);
		//result.put("name", project.getName());
		result.put("log_sources", logsources);
		return result;
	}

}
