package com.netease.qa.log.web.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.exception.InvalidRequestException;
import com.netease.qa.log.exception.NotFoundRequestException;
import com.netease.qa.log.exception.NullParamException;
import com.netease.qa.log.service.LogSourceService;
import com.netease.qa.log.service.ReadService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.MathUtil;

@Controller
@RequestMapping("/api/report")
public class ReadServiceAPI {

	private static final Logger logger = LoggerFactory.getLogger(ReadServiceAPI.class);

	@Resource
	private ReadService readService;

	@Resource
	private LogSourceService logsourceService;

	@Resource
	private ApiExceptionHandler apiException;

	/**
	 * 按时间聚合
	 */
	@RequestMapping(value = "/time/{log_source_id}", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> readByTime(@PathVariable String log_source_id,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		if (MathUtil.isEmpty(start, end, limit, offset)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			InvalidRequestException ex = new InvalidRequestException(Const.LIMIT_AND_OFFSET_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.valueOf(log_source_id))) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}
		Long startTime = null;
		Long endTime = null;
		try {
			startTime = MathUtil.parse2Long(start);
			endTime = MathUtil.parse2Long(end);
		} catch (ParseException e) {
			logger.error("error", e);
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_TIME_FORMAT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		JSONObject jsonObject = readService.queryTimeRecords(Integer.parseInt(log_source_id), startTime, endTime, "sample_time",
				"desc", Integer.parseInt(limit), Integer.parseInt(offset));
		if (jsonObject == null) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}

	/**
	 * 按异常类型聚合
	 */
	@RequestMapping(value = "/error/{log_source_id}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> readByError(@PathVariable String log_source_id,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		if (MathUtil.isEmpty(start, end, limit, offset)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			InvalidRequestException ex = new InvalidRequestException(Const.LIMIT_AND_OFFSET_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.valueOf(log_source_id))) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}
		Long startTime = null;
		Long endTime = null;
		try {
			startTime = MathUtil.parse2Long(start);
			endTime = MathUtil.parse2Long(end);
		} catch (ParseException e) {
			logger.error("error", e);
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_TIME_FORMAT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		JSONObject jsonObject = readService.queryErrorRecordsWithTimeDetail(Integer.parseInt(log_source_id), startTime, endTime,
				"exception_count", "desc", Integer.parseInt(limit), Integer.parseInt(offset));
		if (jsonObject == null) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}

	/**
	 * 获取unknown类型异常
	 */
	@RequestMapping(value = "/unknown/{log_source_id}", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> readByUnknow(@PathVariable String log_source_id,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		if (MathUtil.isEmpty(start, end, limit, offset)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(log_source_id)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			InvalidRequestException ex = new InvalidRequestException(Const.LIMIT_AND_OFFSET_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.valueOf(log_source_id))) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.LOG_NOT_EXSIT);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr), HttpStatus.NOT_FOUND);
		}
		Long startTime = null;
		Long endTime = null;
		try {
			startTime = MathUtil.parse2Long(start);
			endTime = MathUtil.parse2Long(end);
		} catch (ParseException e) {
			logger.error("error", e);
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_TIME_FORMAT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		JSONObject jsonObject = readService.queryUnknownExceptions(Integer.parseInt(log_source_id), startTime, endTime,
				Integer.parseInt(limit), Integer.parseInt(offset));
		if (jsonObject == null) {
			NotFoundRequestException nr = new NotFoundRequestException(Const.INNER_ERROR);
			return new ResponseEntity<JSONObject>(apiException.handleNotFoundRequestException(nr),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.OK);
	}
	/**
	 * 4.4 批量获取日志源异常统计信息，按照机器聚合区分日志源（AB平台）
	 * @param logSourceIds
	 * @param start
	 * @param end
	 * @return
	 */
	@RequestMapping(value = "/batch", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<JSONObject> findErrorByHostname(
			@RequestParam(value = "hostname", required = false) String hostname,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end) {
		if(MathUtil.isEmpty(hostname, start, end)){
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		Long startTime = null;
		Long endTime = null;
		try {
			startTime = MathUtil.parse2Long(start);
			endTime = MathUtil.parse2Long(end);
		} catch (ParseException e) {
			logger.error("error", e);
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_TIME_FORMAT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		JSONObject result = readService.queryErrorRecordsByHostname(hostname, startTime, endTime);
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
	
	/*
	 * 4.5 获取机器异常日志曲线：按机器聚合
	 */
	@RequestMapping(value = "/machine_graph", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getMachineErrorGraph(
			@RequestParam(value = "host_name", required = false) String hostname,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "log_type", required = false, defaultValue="0") int logtype
			){
		logger.debug("--- /api/report/machine_graph ---");
		// 判断参数是否缺失
		if (MathUtil.isEmpty(hostname, start, end)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		logger.debug("---params--- ");
		logger.debug("start: " + start + "; end: " + end + "; logtype: " + logtype);
		//   目前只支持exception日志类型
		if(logtype !=0 ){
			String message = "log_type must be exception file!";
			InvalidRequestException ie = new InvalidRequestException(message);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ie), HttpStatus.BAD_REQUEST);				
		}
		// 开始和结束时间转换为长整型
		Long startTime = null;
		Long endTime = null;
		try {
			startTime = MathUtil.parse2Long(start);
			endTime = MathUtil.parse2Long(end);
		} catch (ParseException e) {
			logger.error("error", e);
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_TIME_FORMAT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		// 时间差合法性，起始时间大小和长度
		if(endTime <= startTime){
			String message = "end time must greater than start time!";
			InvalidRequestException ie = new InvalidRequestException(message);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ie), HttpStatus.BAD_REQUEST);			
		}
		if((endTime - startTime)/Const.MACHINE_ERROR_POINT == 0){
			InvalidRequestException ex = new InvalidRequestException(Const.TIME_TOO_SHORT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		JSONObject result = readService.queryErrorRecordsGraphByMachine(hostname,startTime,endTime,logtype);
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}	
	
	/*
	 * 4.6 获取机器异常日志表格：按机器聚合
	 */
	@RequestMapping(value = "/machine_table", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getMachineErrorTable(
			@RequestParam(value = "host_name", required = false) String hostname,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "log_type", required = false, defaultValue="0") int logtype
			){
		logger.debug("--- /api/report/machine_table ---");
		// 判断参数是否缺失
		if (MathUtil.isEmpty(hostname, start, end)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		logger.debug("---params--- ");
		logger.debug("hostname" + hostname+ ";start: " + start + "; end: " + end + "; logtype: " + logtype);
		//   目前只支持exception日志类型
		if(logtype !=0 ){
			String message = "log_type must be exception file!";
			InvalidRequestException ie = new InvalidRequestException(message);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ie), HttpStatus.BAD_REQUEST);				
		}
		// 开始和结束时间转换为长整型
		Long startTime = null;
		Long endTime = null;
		try {
			startTime = MathUtil.parse2Long(start);
			endTime = MathUtil.parse2Long(end);
		} catch (ParseException e) {
			logger.error("error", e);
			InvalidRequestException ex = new InvalidRequestException(Const.INVALID_TIME_FORMAT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		// 时间差合法性，起始时间大小和长度
		if(endTime <= startTime){
			String message = "end time must greater than start time!";
			InvalidRequestException ie = new InvalidRequestException(message);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ie), HttpStatus.BAD_REQUEST);			
		}
		if((endTime - startTime)/Const.MACHINE_ERROR_POINT == 0){
			InvalidRequestException ex = new InvalidRequestException(Const.TIME_TOO_SHORT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		JSONObject result = readService.queryErrorRecordsTableByMachine(hostname,startTime,endTime,logtype);
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}		

}
