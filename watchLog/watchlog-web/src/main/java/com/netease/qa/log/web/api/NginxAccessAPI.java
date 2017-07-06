package com.netease.qa.log.web.api;

import java.text.ParseException;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.exception.InvalidRequestException;
import com.netease.qa.log.exception.NotFoundRequestException;
import com.netease.qa.log.exception.NullParamException;
import com.netease.qa.log.service.LogSourceService;
import com.netease.qa.log.service.NginxAccessService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.MathUtil;

@Controller
@RequestMapping("/api")
public class NginxAccessAPI {
	private static final Logger logger = LoggerFactory.getLogger(NginxAccessAPI.class);

	@Resource
	private ApiExceptionHandler apiException;
	@Resource
	private LogSourceService logsourceService;
	@Resource
	private NginxAccessService nginxAccessService;

	@RequestMapping(value = "/nginx_charts/real_time/top_list", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getRealTimeTopUrl(
			@RequestParam(value = "log_source_id", required = false) String logsourceId,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "top_n", required = false, defaultValue = "10") String top,
			@RequestParam(value = "sort", required = false, defaultValue = "total_count") String sort, Model model) {
		if (MathUtil.isEmpty(logsourceId, start, end, top, sort)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(logsourceId) || !MathUtil.isInteger(top)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		if (!logsourceService.checkLogSourceExist(Integer.valueOf(logsourceId))) {
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
		// 默认已total_count降序排序，就是tps
		String realSort = sort;
		if (sort.trim().equals("tps") || sort.trim().equals("total_count")) {
			realSort = "total_count";
		} else {
			InvalidRequestException ex = new InvalidRequestException("sort field is wrong!");
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		JSONObject result = nginxAccessService.getTopNUrl(Integer.parseInt(logsourceId), startTime, endTime,
				Integer.parseInt(top), realSort);
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);

	}

	@RequestMapping(value = "/nginx_charts/offline/top_list", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getOfflineTimeTopUrl(
			@RequestParam(value = "log_source_id", required = false) String logsourceId,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "top_n", required = false, defaultValue = "10") String top,
			@RequestParam(value = "sort", required = false, defaultValue = "total_count") String sort, Model model) {
		if (MathUtil.isEmpty(logsourceId, start, end, top, sort)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(logsourceId) || !MathUtil.isInteger(top)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		if (!logsourceService.checkLogSourceExist(Integer.valueOf(logsourceId))) {
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
		// 默认已total_count降序排序，就是tps
		String realSort = sort;
		if (sort.trim().equals("tps") || sort.trim().equals("total_count")) {
			realSort = "total_count";
		} else {
			InvalidRequestException ex = new InvalidRequestException("sort field is wrong!");
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		JSONObject result = nginxAccessService.getTopNUrl(Integer.parseInt(logsourceId), startTime, endTime,
				Integer.parseInt(top), realSort);
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);

	}

	/*
	 * 获取离线统计数据
	 */
	@RequestMapping(value = "/nginx_statistics/offline", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getTopAllData(
			@RequestParam(value = "log_source_id", required = false) String logsourceId,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "top_n", required = false, defaultValue = "10") String top,
			@RequestParam(value = "sort", required = false, defaultValue = "total_count") String sort, Model model) {
		if (MathUtil.isEmpty(logsourceId, start, end)) {
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(logsourceId) || !MathUtil.isInteger(top)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}

		if (!logsourceService.checkLogSourceExist(Integer.valueOf(logsourceId))) {
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
		// 默认已total_count降序排序，就是tps
		String realSort = sort;
		if (sort.trim().equals("tps") || sort.trim().equals("total_count")) {
			realSort = "total_count";
		} else {
			InvalidRequestException ex = new InvalidRequestException("sort field is wrong!");
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		try{
			JSONObject result = nginxAccessService.getTopAllData(Integer.parseInt(logsourceId), startTime, endTime,
					Integer.parseInt(top), realSort);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			return new ResponseEntity<JSONObject>(new JSONObject(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

	@RequestMapping(value = "/nginx_charts/real_time/single", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getRealTimeSingleData(
			@RequestParam(value = "log_source_id", required = false) String logsourceId,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "url", required = false) String url, Model model) {
		if (MathUtil.isEmpty(logsourceId, start, end)) {
			// url 如果为空，则表示总体，否则为单独url
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(logsourceId)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.valueOf(logsourceId))) {
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
		JSONObject result;
		if (MathUtil.isEmpty(url)) {
			result = nginxAccessService.getAllRealSingleData(Integer.parseInt(logsourceId), startTime, endTime);
		} else {
			result = nginxAccessService.getRealSingleData(Integer.parseInt(logsourceId), url, startTime, endTime);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/nginx_charts/offline/single", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getOfflineAllData(
			@RequestParam(value = "log_source_id", required = false) String logsourceId,
			@RequestParam(value = "start", required = false) String start,
			@RequestParam(value = "end", required = false) String end,
			@RequestParam(value = "url", required = false) String url, Model model) {
		if (MathUtil.isEmpty(logsourceId, start, end)) {
			// 如果url为空，返回全部数据（所有tps，error之和）
			NullParamException ne = new NullParamException(Const.NULL_PARAM);
			return new ResponseEntity<JSONObject>(apiException.handleNullParamException(ne), HttpStatus.BAD_REQUEST);
		}
		if (!MathUtil.isInteger(logsourceId)) {
			InvalidRequestException ex = new InvalidRequestException(Const.ID_MUST_BE_NUM);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		if (!logsourceService.checkLogSourceExist(Integer.valueOf(logsourceId))) {
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
		if ((endTime - startTime) / Const.OFFLINE_POINT == 0) {
			InvalidRequestException ex = new InvalidRequestException(Const.TIME_TOO_SHORT);
			return new ResponseEntity<JSONObject>(apiException.handleInvalidRequestError(ex), HttpStatus.BAD_REQUEST);
		}
		JSONObject result;
		if (MathUtil.isEmpty(url)) {
			result = nginxAccessService.getOfflineAllDataWithoutUrl(Integer.parseInt(logsourceId), startTime, endTime);
		} else {
			result = nginxAccessService.getOfflineAllData(Integer.parseInt(logsourceId), url, startTime, endTime);
		}
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

}
