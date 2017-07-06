package com.netease.qa.log.web.controller;

import java.sql.Timestamp;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.exception.ApiExceptionHandler;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.meta.Report;
import com.netease.qa.log.meta.UkExceptionData;
import com.netease.qa.log.service.LogSourceService;
import com.netease.qa.log.service.ProjectService;
import com.netease.qa.log.service.ReadService;
import com.netease.qa.log.service.ReportService;
import com.netease.qa.log.service.UnknowService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.ConstCN;
import com.netease.qa.log.util.MathUtil;

@Controller
public class WlogPMController {

	private static final Logger logger = LoggerFactory.getLogger(WlogPMController.class);

	@Resource
	private LogSourceService logSourceService;
	@Resource
	private ReadService readService;
	@Resource
	private ApiExceptionHandler apiException;
	@Resource
	private ProjectService projectService;
	@Resource
	private ReportService reportService;
	@Resource
	private UnknowService unknowService;

	// 日志聚合分析，选择日志源内容
	@RequestMapping(value = "logsrc/pm_analyse", method = RequestMethod.GET)
	public String pm_analyse(@RequestParam(value = "proj", required = false) String projectid, Model model) {
		model.addAttribute("controller", "WlogPM");
		model.addAttribute("action", "pm_analyse");
		return "logsrc/pm_analyse";
	}

	// 历史报告表格内容
	@RequestMapping(value = "logsrc/pm_analyse/pmtable", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getSortedReports(
			@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "sort", required = false, defaultValue = "create_time") String sort,
			@RequestParam(value = "order", required = false, defaultValue = "desc") String order,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		String message = "";
		int recordsTotal = 0;
		JSONObject result = new JSONObject();
		JSONArray data = new JSONArray();
		if (MathUtil.isEmpty(projectid, limit, offset)) {
			message = ConstCN.NULL_PARAM;
			result.put("message", message);
			result.put("total", recordsTotal);
			result.put("rows", data);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(projectid)) {
			message = ConstCN.ID_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", recordsTotal);
			result.put("rows", data);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			message = ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", recordsTotal);
			result.put("rows", data);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			message = ConstCN.PROJECT_NOT_EXSIT;
			result.put("message", message);
			result.put("total", recordsTotal);
			result.put("rows", data);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		String field = MathUtil.getReportField(sort);
		recordsTotal = reportService.getTotalCountByProjectId(Integer.parseInt(projectid));
		message = ConstCN.RESPONSE_SUCCESSFUL;
		data = reportService.getReportListSortedByProjectid(Integer.parseInt(projectid), field, order,
				Integer.parseInt(limit), Integer.parseInt(offset));
		if (data == null)
			message = ConstCN.RESPONSE_NOTSUCCESSFUL;
		result.put("message", message);
		result.put("total", recordsTotal);
		result.put("rows", data);
		logger.debug("### [route]logsrc/pm_analyse/pmtable  [key]rows : " + data.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	// 删除历史报告
	@RequestMapping(value = "/logsrc/pm_analyse/destroy", method = RequestMethod.POST)
	public String deleteReport(@RequestParam(value = "proj") String projectid,
			@RequestParam(value = "report_id") String reportid, RedirectAttributes model) {
		// 成功和失败的重定向url
		String ret = "redirect:/logsrc/pm_analyse?proj=" + projectid;
		if (MathUtil.isEmpty(reportid)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret;
		}
		// 选中删除，所以日志必存在，不需要进行日志检查
		int result = reportService.deleteReport(Integer.parseInt(reportid));
		if (result == 0) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INNER_ERROR);
			return ret;
		}
		model.addFlashAttribute("status", 0);
		model.addFlashAttribute("message", ConstCN.RESPONSE_SUCCESSFUL);
		return ret;
	}

	@RequestMapping(value = "/logsrc/pm_analyse/store", method = RequestMethod.POST)
	public String addReport(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "log_id", required = false) String logsrcId,
			@RequestParam(value = "start_time", required = false) String startTime,
			@RequestParam(value = "end_time", required = false) String endTime,
			@RequestParam(value = "title", required = false) String title, RedirectAttributes model) {
		String ret_succ = "redirect:/logsrc/pm_analyse?proj=" + projectid;
		String ret_fail = "redirect:/logsrc/pm_projlevel_unsave?proj=" + projectid + "&start_time=" + startTime
				+ "&end_time=" + endTime;
		if (MathUtil.isEmpty(projectid, startTime, endTime)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.NULL_PARAM);
			return ret_fail;
		}
		if (!MathUtil.isInteger(projectid)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.ID_MUST_BE_NUM);
			return ret_fail;
		}
		if (!MathUtil.isTitle(title)) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INVALID_NAME);
			return ret_fail;
		}
		Report report = new Report();
		report.setProjectId(Integer.parseInt(projectid));
		report.setLogSourceId(-1);
		report.setStartTime(MathUtil.parse2Time(startTime));
		report.setEndTime(MathUtil.parse2Time(endTime));
		report.setCreatorId(1);
		report.setTitle(title);
		report.setComment(Const.DEFAULT);
		int result = reportService.createReport(report);
		if (result == 0) {
			model.addFlashAttribute("status", -1);
			model.addFlashAttribute("message", ConstCN.INNER_ERROR);
			return ret_fail;
		} else {
			model.addFlashAttribute("status", 0);
			model.addFlashAttribute("message", ConstCN.RESPONSE_SUCCESSFUL);
			return ret_succ;
		}
	}

	@RequestMapping(value = "/logsrc/pm_analyse_unsave", method = RequestMethod.GET)
	public String viewReport(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "log_id", required = false) String logsrcId,
			@RequestParam(value = "start_time", required = false) String startTime,
			@RequestParam(value = "end_time", required = false) String endTime, Model model) {
		model.addAttribute("controller", "WlogPM");
		model.addAttribute("action", "pm_analyse_unsave");
		if (MathUtil.isEmpty(projectid, logsrcId)) {
			model.addAttribute("status", -1);
			model.addAttribute("message", ConstCN.NULL_PARAM);
			return "logsrc/pm_analyse_unsave";
		}
		
		if (!MathUtil.isInteger(projectid)) {
			model.addAttribute("status", -1);
			model.addAttribute("message", ConstCN.ID_MUST_BE_NUM);
			return "logsrc/pm_analyse_unsave";
		}
		LogSource logSource = logSourceService.getByLogSourceId(Integer.parseInt(logsrcId));
		model.addAttribute("status", 0);
		model.addAttribute("log_id", Integer.parseInt(logsrcId));
		model.addAttribute("logsrc_name", logSource.getLogSourceName());
		model.addAttribute("host_name", logSource.getHostname());
		model.addAttribute("logsrc_path", logSource.getPath());
		model.addAttribute("logsrc_file", logSource.getFilePattern());
		model.addAttribute("start_regex", logSource.getLineStartRegex());
		model.addAttribute("filter_keyword", logSource.getLineFilterKeyword());
		model.addAttribute("reg_regex", logSource.getLineTypeRegex());
		Long start = null;
		Long end = null;
		if (Integer.parseInt(reportid) == 0) {
			try {
				model.addAttribute("start_time", startTime);
				model.addAttribute("end_time", endTime);				
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			try {
				model.addAttribute("start_time", MathUtil.parse2Str(report.getStartTime()));
				model.addAttribute("end_time", MathUtil.parse2Str(report.getEndTime()));				
				start = MathUtil.parse2Long(MathUtil.parse2Str(report.getStartTime()));
				end = MathUtil.parse2Long(MathUtil.parse2Str(report.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		JSONObject resultByTime = readService.queryTimeRecords(Integer.parseInt(logsrcId), start, end,
				Const.ORDER_FIELD_SAMPLE_TIME, Const.ORDER_DESC, 10, 0);
		model.addAttribute("pm_error_dist_table", resultByTime.getJSONArray("record"));
		logger.debug("### [route]/logsrc/pm_analyse_unsave  [key]pm_error_dist_table : " + resultByTime.toJSONString());
		JSONObject resultByError = readService.queryErrorRecords(Integer.parseInt(logsrcId), start, end,
				Const.ORDER_FIELD_SAMPLE_TIME, Const.ORDER_DESC, 5, 0);
		model.addAttribute("pm_error_type_table", resultByError.getJSONArray("error"));
		logger.debug("### [route]/logsrc/pm_analyse_unsave  [key]pm_error_type_table : " + resultByError.toJSONString());
		return "logsrc/pm_analyse_unsave";
	}


	@RequestMapping(value = "/logsrc/pm_analyse/error_dist_more", method = RequestMethod.GET)
	public String moreDist(Model model) {
		return "logsrc/pm_analyse_error_dist_more";
	}

	@RequestMapping(value = "/logsrc/pm_analyse/error_dist_table", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> distTable(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "log_id", required = false) String logsourceid,
			@RequestParam(value = "start_time", required = false) String starttime,
			@RequestParam(value = "end_time", required = false) String endtime,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "sort", required = false, defaultValue = "date_time") String sort,
			@RequestParam(value = "order", required = false, defaultValue = "desc") String order, Model model) {
		String message = "";
		JSONObject result = new JSONObject();
		JSONArray rows = new JSONArray();
		int total = 0;
		if (MathUtil.isEmpty(projectid, reportid, limit, offset, sort, order)) {
			message = ConstCN.NULL_PARAM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(projectid) || !MathUtil.isInteger(reportid)) {
			message = ConstCN.ID_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			message = ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			message = ConstCN.PROJECT_NOT_EXSIT;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		String field = Const.ORDER_FIELD_SAMPLETIME;
		if (sort.equals("total_count"))
			field = Const.ORDER_FIELD_TOTALCOUNT;
		// 未保存的report，的更多页面
		if (Integer.parseInt(reportid) == 0) {
			String start_time = starttime.replaceAll("%20", " ");
			String end_time = endtime.replaceAll("%20", " ");
			Long start = null;
			Long end = null;
			try {
				start = MathUtil.parse2Long(start_time);
				end = MathUtil.parse2Long(end_time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			total = readService.getTimeCountByLogSourceIdAndTime(Integer.parseInt(logsourceid), start, end);
			JSONObject resultByTime = readService.queryTimeRecords(Integer.parseInt(logsourceid), start, end, field,
					order, Integer.parseInt(limit), Integer.parseInt(offset));
			rows = resultByTime.getJSONArray("record");
		} else {
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			String startTime = MathUtil.parse2Str(report.getStartTime());
			String endTime = MathUtil.parse2Str(report.getEndTime());
			Long start = null;
			Long end = null;
			try {
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			total = readService.getTimeCountByLogSourceIdAndTime(report.getLogSourceId(), start, end);
			JSONObject resultByTime = readService.queryTimeRecords(report.getLogSourceId(), start, end, field, order,
					Integer.parseInt(limit), Integer.parseInt(offset));
			rows = resultByTime.getJSONArray("record");
		}
		result.put("message", ConstCN.RESPONSE_SUCCESSFUL);
		result.put("total", total);
		result.put("rows", rows);
		logger.debug("### [route]/logsrc/pm_analyse/error_dist_table  [key]rows : " + rows.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/logsrc/pm_analyse/error_type_more", method = RequestMethod.GET)
	public String moreErrorType(Model model) {
		return "logsrc/pm_analyse_error_type_more";
	}

	@RequestMapping(value = "/logsrc/pm_analyse/error_type_table", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> typeTable(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "log_id", required = false) String logsourceid,
			@RequestParam(value = "start_time", required = false) String starttime,
			@RequestParam(value = "end_time", required = false) String endtime,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset,
			@RequestParam(value = "sort", required = false, defaultValue = "exceptionCount") String sort,
			@RequestParam(value = "order", required = false, defaultValue = "desc") String order, Model model) {
		String message = "";
		JSONObject result = new JSONObject();
		JSONArray rows = new JSONArray();
		int total = 0;
		if (MathUtil.isEmpty(projectid, limit, offset, sort, order)) {
			message = ConstCN.NULL_PARAM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(projectid)) {
			message = ConstCN.ID_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			message = ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!projectService.checkProjectExsit(Integer.parseInt(projectid))) {
			message = ConstCN.PROJECT_NOT_EXSIT;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		String field = Const.ORDER_FIELD_EXCEPTIONCOUNT;
		// 未保存的report，查看更多异常类型
		if (Integer.parseInt(reportid) == 0) {
			String start_time = starttime.replaceAll("%20", " ");
			String end_time = endtime.replaceAll("%20", " ");
			Long start = null;
			Long end = null;
			try {
				start = MathUtil.parse2Long(start_time);
				end = MathUtil.parse2Long(end_time);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			total = readService.getErrorTypeCountByLogSourceId(Integer.parseInt(logsourceid), start, end);
			JSONObject resultByType = readService.queryErrorRecordsWithTimeDetail(Integer.parseInt(logsourceid), start,
					end, field, order, Integer.parseInt(limit), Integer.parseInt(offset));
			rows = resultByType.getJSONArray("error");
		} else {
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			String startTime = MathUtil.parse2Str(report.getStartTime());
			String endTime = MathUtil.parse2Str(report.getEndTime());
			Long start = null;
			Long end = null;
			try {
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			total = readService.getErrorTypeCountByLogSourceId(report.getLogSourceId(), start, end);
			JSONObject resultByType = readService.queryErrorRecordsWithTimeDetail(report.getLogSourceId(), start, end,
					field, order, Integer.parseInt(limit), Integer.parseInt(offset));
			rows = resultByType.getJSONArray("error");
		}
		result.put("message", ConstCN.RESPONSE_SUCCESSFUL);
		result.put("total", total);
		result.put("rows", rows);
		logger.debug("### [route]/logsrc/pm_analyse/error_type_table  [key]rows : " + rows.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/logsrc/pm_analyse/error_type_total_table", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getSortedSample(
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "log_id", required = false) String logsourceid,
			@RequestParam(value = "start_time", required = false) String startTime,
			@RequestParam(value = "end_time", required = false) String endTime,
			@RequestParam(value = "exp_id", required = false) String exceptionid,
			@RequestParam(value = "sort", required = false, defaultValue = "date_time") String sort,
			@RequestParam(value = "order", required = false, defaultValue = "desc") String order,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		String message = "";
		JSONObject result = new JSONObject();
		JSONObject detail = new JSONObject();
		JSONArray rows = new JSONArray();
		int total = 0;
		if (MathUtil.isEmpty(exceptionid, sort, order, limit, offset)) {
			message = ConstCN.NULL_PARAM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(exceptionid)) {
			message = ConstCN.ID_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			message = ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		String field = Const.ORDER_FIELD_SAMPLETIME;
		if (sort.equals("total_count"))
			field = Const.ORDER_FIELD_EXCEPTIONCOUNT;
		// 是未保存的report
		if (Integer.parseInt(reportid) == 0) {
			Long start = null;
			Long end = null;
			try {
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			total = readService.getErrorRecordsCountByLogSourceIdAndExceptionIdAndTime(Integer.parseInt(logsourceid),
					Integer.parseInt(exceptionid), start, end);
			detail = readService.queryDetailByErrorType(Integer.parseInt(logsourceid), Integer.parseInt(exceptionid),
					start, end, field, order, Integer.parseInt(limit), Integer.parseInt(offset));
		} else {
			// 已保存的report
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			Timestamp start_time = report.getStartTime();
			Timestamp end_time = report.getEndTime();
			Long start = null;
			Long end = null;
			try {
				start = MathUtil.parse2Long(MathUtil.parse2Str(start_time));
				end = MathUtil.parse2Long(MathUtil.parse2Str(end_time));
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			total = readService.getErrorRecordsCountByLogSourceIdAndExceptionIdAndTime(report.getLogSourceId(),
					Integer.parseInt(exceptionid), start, end);
			detail = readService.queryDetailByErrorType(report.getLogSourceId(), Integer.parseInt(exceptionid), start,
					end, field, order, Integer.parseInt(limit), Integer.parseInt(offset));
		}
		result.put("message", ConstCN.RESPONSE_SUCCESSFUL);
		result.put("total", total);
		result.put("rows", detail.getJSONArray("details"));
		logger.debug("### [route]/logsrc/pm_analyse/error_type_total_table  [key]rows : " + rows.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/logsrc/pm_analyse/unknown", method = RequestMethod.GET)
	public String unknowPage(Model model) {
		return "logsrc/pm_analyse_unkown";
	}

	@RequestMapping(value = "/logsrc/pm_analyse/unknown_table", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getUnknownDatas(
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "log_id", required = false) String logsourceid,
			@RequestParam(value = "start_time", required = false) String startTime,
			@RequestParam(value = "end_time", required = false) String endTime,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		String message = "";
		JSONObject result = new JSONObject();
		JSONObject unknow = new JSONObject();
		JSONArray rows = new JSONArray();
		int total = 0;
		if (MathUtil.isEmpty(limit, offset)) {
			message = ConstCN.NULL_PARAM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			message = ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		Long start = null;
		Long end = null;
		int logsourceId = 0;
		if (Integer.parseInt(reportid) == 0) {
			logsourceId = Integer.parseInt(logsourceid);
			try {
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			logsourceId = report.getLogSourceId();
			try {
				start = MathUtil.parse2Long(MathUtil.parse2Str(report.getStartTime()));
				end = MathUtil.parse2Long(MathUtil.parse2Str(report.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		total = unknowService.getTotalCount(logsourceId, start, end);
		List<UkExceptionData> unknowsDatas = new ArrayList<UkExceptionData>();
		unknowsDatas = unknowService.findByLogSourceIdAndTime(logsourceId, start, end, Integer.parseInt(limit),
				Integer.parseInt(offset));
		for (UkExceptionData unknowdata : unknowsDatas) {
			unknow = new JSONObject();
			unknow.put("uknow_id", unknowdata.getUkExceptionDataId());
			unknow.put("sample", unknowdata.getOriginLog());
			rows.add(unknow);
		}
		message = ConstCN.RESPONSE_SUCCESSFUL;
		result.put("message", message);
		result.put("total", total);
		result.put("rows", rows);
		logger.debug("### [route]/logsrc/pm_analyse/unknown_table  [key]rows : " + rows.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	// 项目级聚合报告-未保存页面
	@RequestMapping(value = "/logsrc/pm_projlevel_unsave", method = RequestMethod.GET)
	public String pm_projlevel_unsave(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "start_time", required = false) String starttime,
			@RequestParam(value = "end_time", required = false) String endtime, Model model) {
		model.addAttribute("controller", "WlogPM");
		model.addAttribute("action", "pm_projlevel_unsave");
		model.addAttribute("proj", projectid);
		model.addAttribute("start_time", starttime);
		model.addAttribute("end_time", endtime);
		return "logsrc/pm_projlevel_unsave";
	}

	// 项目级聚合报告-保存页面
	@RequestMapping(value = "/logsrc/pm_projlevel_save", method = RequestMethod.GET)
	public String pm_projlevel_save(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "report_id", required = false) String reportid, Model model) {
		Report report = reportService.getReportById(Integer.parseInt(reportid));
		model.addAttribute("controller", "WlogPM");
		model.addAttribute("action", "pm_projlevel_save");
		model.addAttribute("proj", projectid);
		model.addAttribute("report_id", Integer.parseInt(reportid));
		String startTime = MathUtil.parse2Str(report.getStartTime());
		String endTime = MathUtil.parse2Str(report.getEndTime());
		model.addAttribute("start_time", startTime);
		model.addAttribute("end_time", endTime);
		model.addAttribute("title", report.getTitle());
		return "logsrc/pm_projlevel_saved";
	}

	@RequestMapping(value = "/logsrc/pm_projlevel_etc_table", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getExceptionsByProject(
			@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "start_time", required = false) String startTime,
			@RequestParam(value = "end_time", required = false) String endTime,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		String message = "";
		JSONArray rows = new JSONArray();
		int total = 0;
		JSONObject result = new JSONObject();
		// 判断参数limit,offset是否为空
		if (MathUtil.isEmpty(limit, offset)) {
			message = ConstCN.NULL_PARAM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		// 判断limit,offset是否为整形
		if (!MathUtil.isInteger(limit) || !MathUtil.isInteger(offset)) {
			message = ConstCN.LIMIT_AND_OFFSET_MUST_BE_NUM;
			result.put("message", message);
			result.put("total", total);
			result.put("rows", rows);
			return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
		}
		Long start = null;
		Long end = null;
		int projectId = 0;
		// 没传report_id,根据startTime和endTime处理业务
		if (Integer.parseInt(reportid) == 0) {
			projectId = Integer.parseInt(projectid);
			try {
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// 有report_id，根据report_id处理业务
		else {
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			projectId = report.getProjectId();
			try {
				start = MathUtil.parse2Long(MathUtil.parse2Str(report.getStartTime()));
				end = MathUtil.parse2Long(MathUtil.parse2Str(report.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// 获取日志源总数
		total = logSourceService.getTotalCountByProjectId(projectId);
		// 获取日志源列表，默认按照日志源名称升序排列
		List<LogSource> logSources = logSourceService.getLogsourcesByProjectIdOrderByName(projectId, Integer.parseInt(limit),Integer.parseInt(offset));
		// 封装返回数据
		JSONObject row = new JSONObject();
		JSONObject temp = null;
		for (LogSource logsource : logSources) {
			temp = readService.queryExceptionByLogSourceIdAndTime(logsource.getLogSourceId(), start, end);
			row = new JSONObject();
			row.put("log_id", logsource.getLogSourceId());
			row.put("report_id", Integer.parseInt(reportid));
			row.put("logsrc_name", logsource.getLogSourceName());
			row.put("error_tc", temp.getJSONArray("error_tc"));
			row.put("total_count", temp.get("total_count"));
			rows.add(row);
		}
		result.put("message", Const.RESPONSE_SUCCESSFUL);
		result.put("total", total);
		result.put("rows", rows);
		logger.debug("### [route]/logsrc/pm_projlevel_etc_table  [key]rows : " + rows.toJSONString());
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}

	@RequestMapping(value = "/logsrc/pm_projlevel_etc_charts", method = RequestMethod.GET)
	public ResponseEntity<JSONObject> getDataByGraph(@RequestParam(value = "proj", required = false) String projectid,
			@RequestParam(value = "report_id", required = false, defaultValue = "0") String reportid,
			@RequestParam(value = "start_time", required = false) String startTime,
			@RequestParam(value = "end_time", required = false) String endTime,
			@RequestParam(value = "limit", required = false) String limit,
			@RequestParam(value = "offset", required = false) String offset, Model model) {
		int status = -1;
		JSONObject result = new JSONObject();
		Long start = null;
		Long end = null;
		int projectId = 0;
		if (Integer.parseInt(reportid) == 0) {
			projectId = Integer.parseInt(projectid);
			try {
				start = MathUtil.parse2Long(startTime);
				end = MathUtil.parse2Long(endTime);
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			Report report = reportService.getReportById(Integer.parseInt(reportid));
			projectId = report.getProjectId();
			try {
				start = MathUtil.parse2Long(MathUtil.parse2Str(report.getStartTime()));
				end = MathUtil.parse2Long(MathUtil.parse2Str(report.getEndTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		long temp = end - start;
		if (temp > 7 * 24 * 3600) {
			status = -1;
			result.put("message", Const.RESPONSE_NOTSUCCESSFUL);
			result.put("status", status);
			result.put("results", new JSONArray());
		} else {
			JSONArray results = readService.queryRecordsByTime(projectId, start, end);
			status = 0;
			result.put("message", Const.RESPONSE_SUCCESSFUL);
			result.put("status", status);
			result.put("results", results);
		}
		logger.debug("### [route]/logsrc/pm_projlevel_etc_charts  [key]results : " + status);
		return new ResponseEntity<JSONObject>(result, HttpStatus.OK);
	}
}
