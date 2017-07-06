package com.netease.qa.log.service;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.meta.LogSource;
import com.netease.qbs.meta.Project;
import com.netease.qbs.meta.User;

public interface ProjectService {

	public JSONArray getAllProjectsByQbs(User user);
	
	public boolean checkProjectExsit(int projectId);
	
	public Project findByProjectId(int projectId);
	
	public JSONObject findAllLogSourcesByProjectId(int projectId);
}
