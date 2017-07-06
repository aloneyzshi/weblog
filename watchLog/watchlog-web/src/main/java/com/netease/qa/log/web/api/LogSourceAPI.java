package com.netease.qa.log.web.api;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.exception.ConflictRequestException;
import com.netease.qa.log.exception.InvalidRequestException;
import com.netease.qa.log.exception.NotFoundRequestException;
import com.netease.qa.log.exception.NullParamException;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.service.LogSourceService;
import com.netease.qa.log.service.ProjectService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.MathUtil;

@Controller
@RequestMapping("/api/logsource")
public class LogSourceAPI {

	@Resource
	private LogSourceService logsourceService;
	@Resource
	private ApiExceptionHandler apiException;
	@Resource
	private ProjectService projectService;

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<JSONObject> addLogsource(
			@RequestParam(value = "log_source_name", required = false) String logsourceName,
			@RequestParam(value = "project_id", required = false) String projectid,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "hostname", required = false) String hostname,
			@RequestParam(value = "path", required = false) String path,
			@RequestParam(value = "file_pattern", required = false) String filepattern,
			@RequestParam(value = "line_start", required = false) String linestart,
			@RequestParam(value = "filter_keyword", required = false) String filterkeyword,
			@RequestParam(value = "type_regex", required = false) String typeregex,
			@RequestParam(value = "log_source_format", required = false) String logSourceFormat,
			@RequestParam(value = "creator_id", required = false) String creatorid, Model model) {
		if (MathUtil.isEmpty(logsourceName, projectid, type, hostname, path, filepattern, creatorid)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isName(logsourceName)) {
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_NAME);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(projectid)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(type)) {
			InvalidRequestException ex = new InvalidRequestException(Const.TYPE_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!(Integer.parseInt(type) == 0 || Integer.parseInt(type) == 1)) {
			InvalidRequestException ex = new InvalidRequestException(Const.TYPE_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
//		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
//			NotFoundRequestException nr = new NotFoundRequestException(Const.PROJECT_NOT_EXSIT);
//			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
//		}
		if (logsourceService.checkLogSourceExist(logsourceName)) {
			ConflictRequestException cr = new ConflictRequestException(Const.LOG_NAME_ALREADY_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleConflictRequestException(cr), HttpStatus.CONFLICT);
		}
		if (logsourceService.checkLogSourceExist(hostname, path, filepattern)) {
			ConflictRequestException cr = new ConflictRequestException(Const.LOG_PATH_ALREADY_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleConflictRequestException(cr), HttpStatus.CONFLICT);
		}

		LogSource logSource = new LogSource();
		logSource.setLogSourceName(logsourceName);
		logSource.setProjectId(Integer.parseInt(projectid));
		logSource.setLogType(Integer.parseInt(type));
		logSource.setHostname(hostname);
		logSource.setPath(path);
		logSource.setFilePattern(filepattern);
		logSource.setLogSourceCreatorId(Integer.parseInt(creatorid));
		// 创建异常日志源
		if (Integer.parseInt(type) == 0) {
			if(MathUtil.isEmpty(linestart, filterkeyword, typeregex)){
				NullParamException ne = new NullParamException(Const.NULL_PARAM);
				return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
			}
			logSource.setLineStartRegex(linestart);
			logSource.setLineFilterKeyword(filterkeyword);
			logSource.setLineTypeRegex(typeregex);
			logSource.setLogFormat("");
		} else {
			if(MathUtil.isEmpty(logSourceFormat)){
				NullParamException ne = new NullParamException(Const.NULL_PARAM);
				return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
			}
			logSource.setLineStartRegex("");
			logSource.setLineFilterKeyword("");
			logSource.setLineTypeRegex("");
			logSource.setLogFormat(MathUtil.preProcessLogFormat(logSourceFormat));
		}
		int result = logsourceService.createAllLogSource(logSource);
		if (result == 0) {
			InvalidRequestException ex = new InvalidRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			JSONObject json = new JSONObject();
			json.put("log_source_id", result);
			return new ResponseEntity<JSONObject>(json, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{log_source_id}", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> updateLogsource(@PathVariable String log_source_id,
			@RequestParam(value = "log_source_name", required = false) String logsourcename,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "hostname", required = false) String hostname,
			@RequestParam(value = "path", required = false) String path,
			@RequestParam(value = "file_pattern", required = false) String filepattern,
			@RequestParam(value = "line_start", required = false) String linestart,
			@RequestParam(value = "filter_keyword", required = false) String filterkeyword,
			@RequestParam(value = "type_regex", required = false) String typeregex, 
			@RequestParam(value = "log_source_format", required = false) String logSourceFormat, Model model) {
		if (MathUtil.isEmpty(log_source_id, logsourcename, type, hostname, path, filepattern)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isName(logsourcename)) {
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_NAME);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(type)) {
			InvalidRequestException ex = new InvalidRequestException(Const.TYPE_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!(Integer.parseInt(type) == 0 || Integer.parseInt(type) == 1)) {
			InvalidRequestException ex = new InvalidRequestException(Const.TYPE_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		LogSource logSource = logsourceService.getByLogSourceId(Integer.parseInt(log_source_id));
		if (logSource == null) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}
		//日志源名称改变了，就去检查是否出现日志源名称重复的情况
		if (!logSource.getLogSourceName().trim().equals(logsourcename)) {
			if (logsourceService.checkLogSourceExist(logsourcename)) {
				ConflictRequestException cr = new ConflictRequestException(Const.LOG_NAME_ALREADY_EXSIT);
				return new ResponseEntity<JSONObject>(apiException.handleConflictRequestException(cr),
						HttpStatus.CONFLICT);
			}
		}
		// 日志源路径改变了,才去判断是不是和其他日志源路径重复
		if (!(logSource.getHostname().trim().equals(hostname) && logSource.getPath().trim().equals(path) && logSource
				.getFilePattern().trim().equals(filepattern))) {
			if (logsourceService.checkLogSourceExist(hostname, path, filepattern)) {
				ConflictRequestException cr = new ConflictRequestException(Const.LOG_PATH_ALREADY_EXSIT);
				return new ResponseEntity<JSONObject>(apiException.handleConflictRequestException(cr),
						HttpStatus.CONFLICT);
			}
		}
		logSource.setLogSourceName(logsourcename);
		logSource.setHostname(hostname);
		logSource.setPath(path);
		logSource.setFilePattern(filepattern);
		//修改异常日志源
		if(Integer.parseInt(type) == 0){
			if(MathUtil.isEmpty(linestart, filterkeyword, typeregex)){
				NullParamException ne = new NullParamException(Const.NULL_PARAM);
				return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
			}
			logSource.setLogType(0);
			logSource.setLineStartRegex(linestart);
			logSource.setLineFilterKeyword(filterkeyword);
			logSource.setLineTypeRegex(typeregex);
			logSource.setLogFormat("");
		}else{
			if(MathUtil.isEmpty(logSourceFormat)){
				NullParamException ne = new NullParamException(Const.NULL_PARAM);
				return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
			}
			logSource.setLogType(1);
			logSource.setLineStartRegex("");
			logSource.setLineFilterKeyword("");
			logSource.setLineTypeRegex("");
			logSource.setLogFormat(MathUtil.preProcessLogFormat(logSourceFormat));
		}
		
		int result = logsourceService.updateAllLogSource(logSource);
		if (result == 0) {
			InvalidRequestException ex = new InvalidRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.OK);
		}
	}

	/**
	 * 查询日志源详情
	 * 
	 * @param logsourceid
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/{log_source_id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> findLogSource(@PathVariable String log_source_id, Model model) {
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		JSONObject logSource = logsourceService.getDetailByLogSourceId(Integer.parseInt(log_source_id));
		if (logSource == null) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<JSONObject>(logSource, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/{log_source_id}", method = RequestMethod.DELETE)
	public ResponseEntity<JSONObject> deleteLogSource(@PathVariable String log_source_id, Model model) {
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.parseInt(log_source_id))) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}

		int result = logsourceService.deleteLogSource(Integer.parseInt(log_source_id));
		if (result == 0) {
			InvalidRequestException ex = new InvalidRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/changestatus/{log_source_id}", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> changeStatus(@PathVariable String log_source_id,
			@RequestParam(value = "status", required = false) String status, Model model) {
		if (MathUtil.isEmpty(status)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!status.equals("0") && !status.equals("1")) {
			InvalidRequestException ex = new InvalidRequestException(Const.STATUS_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		LogSource logSource = logsourceService.getByLogSourceId(Integer.parseInt(log_source_id));
		if (logSource == null) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}

		logSource.setLogSourceStatus(Integer.parseInt(status));
		int result = logsourceService.updateLogSource(logSource);
		if (result == 0) {
			InvalidRequestException ex = new InvalidRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex),
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			return new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.OK);
		}
	}

	@RequestMapping(value = "/getstatus/{log_source_id}", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getStatus(@PathVariable String log_source_id) {
		if (MathUtil.isEmpty(log_source_id)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.parseInt(log_source_id))) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}
		JSONObject result = logsourceService.getStatusByLogsourceId(Integer.parseInt(log_source_id));
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

}
