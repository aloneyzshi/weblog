package com.netease.qa.log.web.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.debugger.Debugger;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.exception.InvalidRequestException;
import com.netease.qa.log.exception.NotFoundRequestException;
import com.netease.qa.log.exception.NullParamException;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.service.LogSourceService;
import com.netease.qa.log.service.ProjectService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.ConstCN;
import com.netease.qa.log.util.MathUtil;

@Controller
@RequestMapping(value = "/logsrc")
public class WlogSrcController {

	private static final Logger logger = LoggerFactory.getLogger(WlogRTController.class);

	@Resource
	private ApiExceptionHandler apiException;
	@Resource
	private ProjectService projectService;
	@Resource
	private LogSourceService logSourceService;

	@RequestMapping(value = "manage/logtable", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> findLogSourceSortedByProjectid(
			@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "sort", required = false, defaultValue = "update_time") String sort,
			@RequestParam(value = "order", required = false, defaultValue = "desc") String order,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset) {
		if (MathUtil.isEmpty(projectid, limit, offset)) {
			NullParamException ne = new NullParamException(ConstCN.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(projectid)) {
			InvalidRequestException ex = new InvalidRequestException(ConstCN.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			InvalidRequestException ex = new InvalidRequestException(ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			NotFoundRequestException nr = new NotFoundRequestException(ConstCN.PROJECT_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}
		String field = MathUtil.getSortField(sort);
		int recordsTotal = logSourceService.getTotalCountByProjectId(Integer.parseInt(projectid));
		String message = ConstCN.RESPONSE_SUCCESSFUL;
		JSONArray data = logSourceService.getLogSourcesListSortedByProjectid(Integer.parseInt(projectid), field, order,
				Integer.parseInt(limit), Integer.parseInt(offset));
		JSONObject result = new JSONObject();
		if (data == null)
			message = ConstCN.RESPONSE_NOTSUCCESSFUL;
		result.put("message", message);
		result.put("total", recordsTotal);
		result.put("rows", data);
		logger.debug("### [route]manage/logtable  [key]rows : " + data.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/destroy", method = RequestMethod.POST)
	public String deleteLogSources(@RequestParam(value = "ids") String ids,
			@RequestParam(value = "proj") String projectid, RedirectAttributes model) {
		// 成功和失败的重定向url
		String ret = "redirect:/logsrc/manage?proj=" + projectid;
		if (MathUtil.isEmpty(ids)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret;
		}
		// 选中删除，所以日志必存在，不需要进行日志检查
		int[] logsource_ids = MathUtil.parse2IntArray(ids);
		int result = logSourceService.deleteLogSources(logsource_ids);
		if (result == 0) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INNER_ERROR);
			return ret;
		}
		model.addFlashAttribute("status", 0);
		model.addFlashAttribute("message", ConstCN.RESPONSE_SUCCESSFUL);
		return ret;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public String getDetailLogsource(@PathVariable(value = "id") String id, Model model) {
		LogSource logSource = logSourceService.getByLogSourceId(Integer.parseInt(id));
		if (logSource == null) {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "show");
			model.addAttribute("id", 0);
			model.addAttribute("logsrc_name", "NONE");
			model.addAttribute("host_name", "NONE");
			model.addAttribute("logsrc_path", "NONE");
			model.addAttribute("logsrc_file", "NONE");
			model.addAttribute("start_regex", "NONE");
			model.addAttribute("filter_keyword", "NONE");
			model.addAttribute("reg_regex", "NONE");
		} else {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "show");
			model.addAttribute("id", logSource.getLogSourceId());
			model.addAttribute("logsrc_name", logSource.getLogSourceName());
			model.addAttribute("host_name", logSource.getHostname());
			model.addAttribute("logsrc_path", logSource.getPath());
			model.addAttribute("logsrc_file", logSource.getFilePattern());
			model.addAttribute("start_regex", logSource.getLineStartRegex());
			model.addAttribute("filter_keyword", logSource.getLineFilterKeyword());
			model.addAttribute("reg_regex", logSource.getLineTypeRegex());
		}
		return "logsrc/show";
	}

	@RequestMapping(value = "/new", method = RequestMethod.GET)
	public String newLogsrc(Locale locale, Model model) {
		model.addAttribute("controller", "WlogManage");
		model.addAttribute("action", "new");
		return "logsrc/new";
	}

	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public String addLogsource(@RequestParam(value = "logsrc_name", required = false) String logsourceName,
			@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "host_name", required = false) String hostname,
			@RequestParam(value = "logsrc_path", required = false) String path,
			@RequestParam(value = "logsrc_file", required = false) String filepattern,
			@RequestParam(value = "start_regex", required = false) String linestart,
			@RequestParam(value = "filter_keyword_arr[]", required = false) String[] filterkeywords,
			@RequestParam(value = "reg_regex_arr[]", required = false) String[] typeregexs,
			@RequestParam(value = "filter_keyword_con", required = false) String filter_keyword_con,
			@RequestParam(value = "reg_regex_con", required = false) String reg_regex_con, RedirectAttributes model) {
		String ret = "redirect:/logsrc/manage?proj=" + projectid;
		String ret_new = "redirect:/logsrc/new?proj=" + projectid;
		if (MathUtil.isEmpty(logsourceName, projectid, hostname, path, filepattern, linestart, filter_keyword_con,
				reg_regex_con)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_new;
		}
		if (filterkeywords == null || typeregexs == null) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_new;
		}
		if (!MathUtil.isName(logsourceName)) {
			model.addFlashAttribute("message", ConstCN.INVALID_NAME);
			return ret_new;
		}
		if (!MathUtil.isInteger(projectid)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.ID_MUST_BE_NUM);
			return ret_new;
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.PROJECT_NOT_EXSIT);
			return ret_new;
		}
		if (logSourceService.checkLogSourceExist(logsourceName)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.LOG_NAME_ALREADY_EXSIT);
			return ret_new;
		}
		if (logSourceService.checkLogSourceExist(hostname, path, filepattern)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.LOG_PATH_ALREADY_EXSIT);
			return ret_new;
		}
		LogSource logSource = new LogSource();
		logSource.setLogSourceName(logsourceName);
		logSource.setProjectId(Integer.parseInt(projectid));
		logSource.setHostname(hostname);
		logSource.setPath(path);
		logSource.setFilePattern(filepattern);
		logSource.setLineStartRegex(linestart);
		logSource.setLineFilterKeyword(MathUtil.parse2Str(filterkeywords, filter_keyword_con));
		logSource.setLineTypeRegex(MathUtil.parse2Str(typeregexs, Const.FILITER_TYPE));
		int result = logSourceService.createLogSource(logSource);
		if (result == 0) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INNER_ERROR);
			return ret_new;
		} else {
			model.addFlashAttribute("status", 0);
			model.addFlashAttribute("message", ConstCN.RESPONSE_SUCCESSFUL);
			return ret;
		}
	}

	@RequestMapping(value = "/repeat", method = RequestMethod.POST)
	public String repeatLogsource(@RequestParam(value = "logsrc_name", required = false) String logsourceName,
			@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "id", required = false) String logsourceid,
			@RequestParam(value = "host_name", required = false) String hostname,
			@RequestParam(value = "logsrc_path", required = false) String path,
			@RequestParam(value = "logsrc_file", required = false) String filepattern,
			@RequestParam(value = "start_regex", required = false) String linestart,
			@RequestParam(value = "filter_keyword_arr[]", required = false) String[] filterkeywords,
			@RequestParam(value = "reg_regex_arr[]", required = false) String[] typeregexs,
			@RequestParam(value = "filter_keyword_con", required = false) String filter_keyword_con,
			@RequestParam(value = "reg_regex_con", required = false) String reg_regex_con, RedirectAttributes model) {
		String ret = "redirect:/logsrc/manage?proj=" + projectid;
		String ret_fail = "redirect:/logsrc/" + logsourceid + "/copy?proj=" + projectid;
		if (MathUtil.isEmpty(logsourceName, projectid, hostname, path, filepattern, linestart, filter_keyword_con,
				reg_regex_con)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_fail;
		}
		if (filterkeywords == null || typeregexs == null) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_fail;
		}
		if (!MathUtil.isName(logsourceName)) {
			model.addFlashAttribute("message", ConstCN.INVALID_NAME);
			return ret_fail;
		}
		if (!MathUtil.isInteger(projectid)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.ID_MUST_BE_NUM);
			return ret_fail;
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.PROJECT_NOT_EXSIT);
			return ret_fail;
		}
		if (logSourceService.checkLogSourceExist(logsourceName)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.LOG_NAME_ALREADY_EXSIT);
			return ret_fail;
		}
		if (logSourceService.checkLogSourceExist(hostname, path, filepattern)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.LOG_PATH_ALREADY_EXSIT);
			return ret_fail;
		}
		LogSource logSource = new LogSource();
		logSource.setLogSourceName(logsourceName);
		logSource.setProjectId(Integer.parseInt(projectid));
		logSource.setHostname(hostname);
		logSource.setPath(path);
		logSource.setFilePattern(filepattern);
		logSource.setLineStartRegex(linestart);
		logSource.setLineFilterKeyword(MathUtil.parse2Str(filterkeywords, filter_keyword_con));
		logSource.setLineTypeRegex(MathUtil.parse2Str(typeregexs, Const.FILITER_TYPE));
		int result = logSourceService.createLogSource(logSource);
		if (result == 0) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INNER_ERROR);
			return ret_fail;
		} else {
			model.addFlashAttribute("status", 0);
			model.addFlashAttribute("message", ConstCN.RESPONSE_SUCCESSFUL);
			return ret;
		}
	}

	@RequestMapping(value = "/start_monitor", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> startMonitorStatus(@RequestParam(value = "ids", required = false) String ids) {
		if (MathUtil.isEmpty(ids)) {
			NullParamException ne = new NullParamException(ConstCN.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		// 选中修改，所以日志必存在，不需要进行日志检查
		int[] logsource_ids = MathUtil.parse2IntArray(ids);
		int result = logSourceService.changeMonitorStatus(logsource_ids, 1);
		JSONObject resultJson = new JSONObject();
		if (result == -1) {
			resultJson.put("status", -1);
			resultJson.put("message", ConstCN.INNER_ERROR);
			return new ResponseEntity<JSONObject>(resultJson, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			resultJson.put("status", 0);
			resultJson.put("message", ConstCN.RESPONSE_SUCCESSFUL);
			return new ResponseEntity<JSONObject>(resultJson, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/stop_monitor", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> stopMonitorStatus(@RequestParam(value = "ids", required = false) String ids) {
		if (MathUtil.isEmpty(ids)) {
			NullParamException ne = new NullParamException(ConstCN.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		// 选中修改，所以日志必存在，不需要进行日志检查
		int[] logsource_ids = MathUtil.parse2IntArray(ids);
		int result = logSourceService.changeMonitorStatus(logsource_ids, 0);
		JSONObject resultJson = new JSONObject();
		if (result == -1) {
			resultJson.put("status", -1);
			resultJson.put("message", ConstCN.INNER_ERROR);
			return new ResponseEntity<JSONObject>(resultJson, HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			resultJson.put("status", 0);
			resultJson.put("message", ConstCN.RESPONSE_SUCCESSFUL);
			return new ResponseEntity<JSONObject>(resultJson, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{id}/edit", method = RequestMethod.GET)
	public String editLogSource(@PathVariable(value = "id") String logsourceId,
			@RequestParam(value = "proj", required = false) String projectid, Model model) {
		if (MathUtil.isEmpty(logsourceId, projectid) || !MathUtil.isInteger(logsourceId)) {
			return "redirect:/logsrc/manage?proj=" + projectid;
		}
		LogSource logSource = logSourceService.getByLogSourceId(Integer.parseInt(logsourceId));
		if (logSource == null) {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "edit");
			model.addAttribute("id", "0");
			model.addAttribute("logsrc_name", "NONE");
			model.addAttribute("host_name", "NONE");
			model.addAttribute("logsrc_path", "NONE");
			model.addAttribute("logsrc_file", "NONE");
			model.addAttribute("start_regex", "NONE");
			model.addAttribute("filter_keyword", "NONE");
			model.addAttribute("reg_regex", "NONE");
		} else {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "edit");
			model.addAttribute("id", logSource.getLogSourceId());
			model.addAttribute("logsrc_name", logSource.getLogSourceName());
			model.addAttribute("host_name", logSource.getHostname());
			model.addAttribute("logsrc_path", logSource.getPath());
			model.addAttribute("logsrc_file", logSource.getFilePattern());
			model.addAttribute("start_regex", logSource.getLineStartRegex());
			model.addAttribute("filter_keyword", logSource.getLineFilterKeyword());
			model.addAttribute("reg_regex", logSource.getLineTypeRegex());
		}
		return "logsrc/edit";
	}

	@RequestMapping(value = "/{id}/copy", method = RequestMethod.GET)
	public String copyLogSource(@PathVariable(value = "id") String logsourceId,
			@RequestParam(value = "proj", required = false) String projectid, Model model) {
		if (MathUtil.isEmpty(logsourceId, projectid) || !MathUtil.isInteger(logsourceId)) {
			return "redirect:/logsrc/manage?proj=" + projectid;
		}
		LogSource logSource = logSourceService.getByLogSourceId(Integer.parseInt(logsourceId));
		if (logSource == null) {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "copy");
			model.addAttribute("id", "0");
			model.addAttribute("logsrc_name", "NONE");
			model.addAttribute("host_name", "NONE");
			model.addAttribute("logsrc_path", "NONE");
			model.addAttribute("logsrc_file", "NONE");
			model.addAttribute("start_regex", "NONE");
			model.addAttribute("filter_keyword", "NONE");
			model.addAttribute("reg_regex", "NONE");
		} else {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "copy");
			model.addAttribute("id", logSource.getLogSourceId());
			model.addAttribute("logsrc_name", logSource.getLogSourceName());
			model.addAttribute("host_name", logSource.getHostname());
			model.addAttribute("logsrc_path", logSource.getPath());
			model.addAttribute("logsrc_file", logSource.getFilePattern());
			model.addAttribute("start_regex", logSource.getLineStartRegex());
			model.addAttribute("filter_keyword", logSource.getLineFilterKeyword());
			model.addAttribute("reg_regex", logSource.getLineTypeRegex());
		}
		return "logsrc/copy";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String commitEditLogSource(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "id", required = false) String logsourceid,
			@RequestParam(value = "logsrc_name", required = false) String logsourceName,
			@RequestParam(value = "host_name", required = false) String hostname,
			@RequestParam(value = "logsrc_path", required = false) String path,
			@RequestParam(value = "logsrc_file", required = false) String filepattern,
			@RequestParam(value = "start_regex", required = false) String linestart,
			@RequestParam(value = "filter_keyword_arr[]", required = false) String[] filterkeywords,
			@RequestParam(value = "reg_regex_arr[]", required = false) String[] typeregexs,
			@RequestParam(value = "filter_keyword_con", required = false) String filter_keyword_con,
			@RequestParam(value = "reg_regex_con", required = false) String reg_regex_con,
			@RequestParam(value = "logsourcecreatorname", required = false, defaultValue = "none") String creatorname,
			@RequestParam(value = "pre_page", required = false) String prepage,
			RedirectAttributes model) {
		String ret_fail = "redirect:/logsrc/" + logsourceid + "/edit?proj=" + projectid;
		if (MathUtil.isEmpty(logsourceid, logsourceName, projectid, hostname, path, filepattern, linestart,
				filter_keyword_con, reg_regex_con, creatorname)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_fail;
		}

		if (filterkeywords == null || typeregexs == null) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_fail;
		}
		if (!MathUtil.isName(logsourceName)) {
			model.addFlashAttribute("message", ConstCN.INVALID_NAME);
			return ret_fail;
		}
		if (!MathUtil.isInteger(projectid) || !MathUtil.isInteger(logsourceid)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.ID_MUST_BE_NUM);
			return ret_fail;
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.PROJECT_NOT_EXSIT);
			return ret_fail;
		}
		// 日志源名称改了，才能check
		LogSource logsource = logSourceService.getByLogSourceId(Integer.parseInt(logsourceid));
		if (!logsourceName.trim().equals(logsource.getLogSourceName())) {
			if (logSourceService.checkLogSourceExist(logsourceName)) {
				model.addFlashAttribute("status", -1);
				model.addFlashAttribute("message", ConstCN.LOG_NAME_ALREADY_EXSIT);
				return ret_fail;
			}
		}
		// path修改
		if (!(hostname.trim().equals(logsource.getHostname()) && path.trim().equals(logsource.getPath()) && filepattern
				.trim().equals(logsource.getFilePattern()))) {
			if (logSourceService.checkLogSourceExist(hostname, path, filepattern)) {
				model.addFlashAttribute("message", ConstCN.LOG_PATH_ALREADY_EXSIT);
				return ret_fail;
			}
		}
		LogSource logSource = logsource;
		logSource.setLogSourceName(logsourceName);
		logSource.setProjectId(Integer.parseInt(projectid));
		logSource.setHostname(hostname);
		logSource.setPath(path);
		logSource.setFilePattern(filepattern);
		logSource.setLineStartRegex(linestart);
		logSource.setLineFilterKeyword(MathUtil.parse2Str(filterkeywords, filter_keyword_con));
		logSource.setLineTypeRegex(MathUtil.parse2Str(typeregexs, Const.FILITER_TYPE));
		int result = logSourceService.updateLogSource(logSource);
		if (result == 0) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INNER_ERROR);
			return ret_fail;
		} else {
			// 如果上一个页面是调试页面，并且logsourceid不为空，设置成功修改后，跳转到页面为：调试页面
			String ret_succ ;
			if(prepage.equals("debug")){
				 model.addFlashAttribute("status", 0);
				 ret_succ = "redirect:/logsrc/"+logsourceid+"/debug?proj=" + projectid;
			}else{
				model.addFlashAttribute("status", 0);
				model.addFlashAttribute("message", ConstCN.RESPONSE_SUCCESSFUL);				
				ret_succ = "redirect:/logsrc/manage?proj=" + projectid;
			}
			return ret_succ;
		}
	}

	@RequestMapping(value = "/{id}/debug", method = RequestMethod.GET)
	public String debugLogSource(@PathVariable(value = "id") String logsourceId,
			@RequestParam(value = "proj", required = false) String projectid, Model model) {
		if (MathUtil.isEmpty(logsourceId, projectid) || !MathUtil.isInteger(logsourceId)) {
			return "redirect:/logsrc/manage?proj=" + projectid;
		}
		LogSource logSource = logSourceService.getByLogSourceId(Integer.parseInt(logsourceId));
		if (logSource == null) {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "debug");
			model.addAttribute("id", "0");
			model.addAttribute("logsrc_name", "NONE");
			model.addAttribute("host_name", "NONE");
			model.addAttribute("logsrc_path", "NONE");
			model.addAttribute("logsrc_file", "NONE");
			model.addAttribute("start_regex", "NONE");
			model.addAttribute("filter_keyword", "NONE");
			model.addAttribute("reg_regex", "NONE");
		} else {
			model.addAttribute("controller", "WlogManage");
			model.addAttribute("action", "debug");
			model.addAttribute("id", logSource.getLogSourceId());
			model.addAttribute("logsrc_name", logSource.getLogSourceName());
			model.addAttribute("host_name", logSource.getHostname());
			model.addAttribute("logsrc_path", logSource.getPath());
			model.addAttribute("logsrc_file", logSource.getFilePattern());
			model.addAttribute("start_regex", logSource.getLineStartRegex());
			model.addAttribute("filter_keyword", logSource.getLineFilterKeyword());
			model.addAttribute("reg_regex", logSource.getLineTypeRegex());
		}
		return "logsrc/debug";
	}

	@RequestMapping(value = "/debugvalidate", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> debugValidate(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "log_id") String logsourceId, @RequestParam(value = "debug_info") String debuginfo)
			throws InterruptedException {
	    LogSource logSource = logSourceService.getByLogSourceId(Integer.parseInt(logsourceId));
		FileOutputStream fop = null;
		File file = null;
		String content = debuginfo;
		String filename = "";
		try {
			filename = logSource.getLogSourceName() + "_" + System.currentTimeMillis();
			file = new File(filename);
			fop = new FileOutputStream(file);
			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}
			// get the content in bytes
			byte[] contentInBytes = content.getBytes();
			fop.write(contentInBytes);
			fop.flush();
			fop.close();
		} catch (IOException e) { 
			logger.error("find exception when create debug file", e);
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				logger.error("find exception", e);
			}
		}
		Debugger d = new Debugger(logSource, filename);
		d.doDebug();
		file.delete();//删除调试文件
		HashMap<String, Integer> exceptionCountCache = d.getExceptionCountMap();
		ArrayList<String> unknownCache = d.getUnknownList();
		// message
		String message = Const.RESPONSE_SUCCESSFUL;
		// 表1第1行
		JSONArray error_tc = new JSONArray();
		JSONObject tc1 = new JSONObject();
		for (Entry<String, Integer> e1 : exceptionCountCache.entrySet()) {
			tc1 = new JSONObject();
			tc1.put("type", e1.getKey());
			tc1.put("count", e1.getValue());
			error_tc.add(tc1);
		}
		// 表2所有行 unknow_list 数组
		JSONArray unknow_list = new JSONArray();
		for (String error : unknownCache) {
			unknow_list.add(error);
		}
		// 所有返回结果
		JSONObject result = new JSONObject();
		result.put("status", 0);
		result.put("message", message);
		result.put("error_tc", error_tc);
		result.put("unknow_list", unknow_list);
		// 打印调试信息
		logger.debug("### [route]/logsrc/debugvalidate   [key]error_tc : " + error_tc.toJSONString());
		logger.debug("### [route]/logsrc/debugvalidate   [key]unknow_list : " + unknow_list.toJSONString());
		
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
}
