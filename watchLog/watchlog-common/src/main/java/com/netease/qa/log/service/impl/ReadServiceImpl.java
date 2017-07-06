package com.netease.qa.log.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.meta.ExceptionData;
import com.netease.qa.log.meta.ExceptionDataRecord;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.meta.UkExceptionData;
import com.netease.qa.log.meta.dao.ExceptionDao;
import com.netease.qa.log.meta.dao.ExceptionDataDao;
import com.netease.qa.log.meta.dao.LogSourceDao;
import com.netease.qa.log.meta.dao.UkExceptionDataDao;
import com.netease.qa.log.service.ReadService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.ConstCN;
import com.netease.qa.log.util.MathUtil;

@Service
public class ReadServiceImpl implements ReadService {

	private static final Logger logger = LoggerFactory.getLogger(ReadServiceImpl.class);

	@Resource
	private ExceptionDao exceptionDao;
	@Resource
	private ExceptionDataDao exceptionDataDao;
	@Resource
	private UkExceptionDataDao ukExceptionDataDao;
	@Resource
	private LogSourceDao logSourceDao;

	@Override
	public int getTimeCountByLogSourceIdAndTime(int logSourceId, long startTime, long endTime) {
		return exceptionDataDao.getTimeRecordsCountByLogSourceIdAndTime(logSourceId, startTime, endTime);
	}

	@Override
	public int getErrorTypeCountByLogSourceId(int logSourceId, long startTime, long endTime) {
		return exceptionDataDao.getErrorRecordsCountByLogSourceIdAndTime(logSourceId, startTime, endTime);
	}

	@Override
	public int getErrorRecordsCountByLogSourceIdAndExceptionIdAndTime(int logSourceId, int exceptionId, long startTime,
			long endTime) {
		return exceptionDataDao.getErrorRecordsCountByLogSourceIdAndExceptionIdAndTime(logSourceId, exceptionId,
				startTime, endTime);
	}

	@Override
	public JSONObject queryLatestTimeRecords(int logSourceId, long currentTime) {
		// 时间精度取整： xx:xx:00 、xx:xx:30两种精度
		Long formatCurrentTime = currentTime / Const.RT_SHOW_TIME * Const.RT_SHOW_TIME;
		JSONObject result = new JSONObject();
		result.put("projectid", logSourceDao.findByLogSourceId(logSourceId).getProjectId());
		result.put("logsourceid", logSourceId);
		JSONArray records = new JSONArray();
		for (int i = 0; i < Const.RT_SHOW_NUM; i++) {
			long endTime = formatCurrentTime - Const.RT_SHOW_TIME * i;
			long startTime = endTime - Const.RT_SHOW_TIME + 1; // mysql
			// between包含前后区间值，此处取前开后闭，以防止重复数据。
			try {
				ExceptionDataRecord exceptionDataRecord = exceptionDataDao.findSummaryByLogSourceIdAndTime(logSourceId,
						startTime, endTime);
				JSONArray details = new JSONArray();
				if (exceptionDataRecord != null && exceptionDataRecord.getTotalCount() > 0) {
					String[] eids = exceptionDataRecord.getExceptionIds().split(",");
					String[] ecounts = exceptionDataRecord.getExceptionCounts().split(",");
					for (int j = 0; j < eids.length; j++) {
						int exceptionId = Integer.valueOf(eids[j].trim());
						String type = exceptionDao.findByExceptionId(exceptionId).getExceptionType();

						JSONObject detail = new JSONObject();
						detail.put("type", type);
						detail.put("count", ecounts[j]);
						details.add(detail);
					}
				}
				JSONObject record = new JSONObject();
				record.put("date_time", MathUtil.parse2Str(endTime));
				record.put(
						"total_count",
						exceptionDataRecord != null && exceptionDataRecord.getTotalCount() > 0 ? exceptionDataRecord
								.getTotalCount() : 0);
				record.put("error_tc", details);
				records.add(record);
			} catch (Exception e) {
				logger.error("error", e);
				return null;
			}
		}
		result.put("record", records);
		return result;
	}

	@Override
	public JSONArray queryRecordsByTime(int projectid, long start_time, long end_time) {
		// 时间精度取整： xx:xx:00 、xx:xx:30两种精度
		// 获取时间区间，和次数
		int timeRange = MathUtil.getTimeRange(start_time, end_time);
		int num = (int) ((end_time - start_time) / timeRange);
		Long formatStartTime = start_time / timeRange * timeRange;
		JSONArray results = new JSONArray();
		List<LogSource> logSources = logSourceDao.getSortedByProjectId(projectid, "modify_time", "desc", 100, 0);
		for (int i = 0; i < logSources.size(); i++) {
			JSONObject result = new JSONObject();
			LogSource logSource = logSources.get(i);
			JSONArray datas = new JSONArray();
			for (int j = 0; j < num; j++) {
				long startTime = formatStartTime + timeRange * j + 1;
				long endTime = startTime + timeRange;
				int totalCount = 0;
				// between包含前后区间值，此处取前开后闭，以防止重复数据。
				try {
					int logSourceId = logSource.getLogSourceId();
					Integer total = exceptionDataDao.getTotalCountByLogsourceIdAndTime(logSourceId, startTime, endTime);
					if (total != null) {
						totalCount = total;
					} else {
						totalCount = 0;
					}
					JSONObject data = new JSONObject();
					data.put("x", endTime * 1000);
					data.put("y", totalCount);
					datas.add(data);
				} catch (Exception e) {
					logger.error("error", e);
					return null;
				}
			}
			result.put("logsrc_name", logSource.getLogSourceName());
			result.put("data", datas);
			results.add(result);
		}
		return results;
	}

	@Override
	public JSONObject queryTimeRecords(int logSourceId, long startTime, long endTime, String orderBy, String order,
			int limit, int offset) {
		List<ExceptionDataRecord> exceptionDataRecords = null;
		try {
			if (orderBy.equals("sample_time"))
				orderBy = "aaa.sample_time";
			exceptionDataRecords = exceptionDataDao.findTimeRecordsByLogSourceIdAndTime(logSourceId, startTime,
					endTime, orderBy, order, limit, offset);
		} catch (Exception e) {
			logger.error("error", e);
			return null;
		}
		// 组装数据
		JSONObject result = new JSONObject();
		result.put("project_id", logSourceDao.findByLogSourceId(logSourceId).getProjectId());
		result.put("log_source_id", logSourceId);

		JSONArray records = new JSONArray();
		for (ExceptionDataRecord eRecord : exceptionDataRecords) {
			JSONObject record = new JSONObject();
			JSONArray details = new JSONArray();
			record.put("date_time", MathUtil.parse2Str(eRecord.getSampleTime()));
			record.put("total_count", eRecord.getTotalCount());

			String[] eids = eRecord.getExceptionIds().split(",");
			String[] ecounts = eRecord.getExceptionCounts().split(",");
			for (int i = 0; i < eids.length; i++) {
				int exceptionId = Integer.valueOf(eids[i].trim());
				String type = exceptionDao.findByExceptionId(exceptionId).getExceptionType();

				JSONObject detail = new JSONObject();
				detail.put("type", type);
				detail.put("count", ecounts[i]);
				details.add(detail);
			}
			record.put("error_tc", details);
			records.add(record);
		}
		result.put("record", records);
		return result;
	}

	@Override
	public JSONObject queryErrorRecords(int logSourceId, long startTime, long endTime, String orderBy, String order,
			int limit, int offset) {
		List<ExceptionData> exceptionDatas = null;
		ExceptionData unknownexception = exceptionDataDao.findUnknownTypeByLogSourceIdAndTime(logSourceId, startTime,
				endTime, Const.UNKNOWN_TYPE);
		try {
			exceptionDatas = exceptionDataDao.findErrorRecordsByLogSourceIdAndTime(logSourceId, startTime, endTime,
					orderBy, order, limit, offset);
		} catch (Exception e) {
			logger.error("error", e);
			return null;
		}
		// 组装数据
		JSONObject result = new JSONObject();
		result.put("projectid", logSourceDao.findByLogSourceId(logSourceId).getProjectId());
		result.put("logsourceid", logSourceId);
		JSONObject error = new JSONObject();
		JSONArray errors = new JSONArray();
		if (unknownexception != null) {
			com.netease.qa.log.meta.Exception ukexception = exceptionDao.findByExceptionId(unknownexception
					.getExceptionId());
			error.put("exp_id", ukexception.getExceptionId());
			error.put("error_type", Const.UNKNOWN_TYPE);
			error.put("error_example", Const.UNKNOWN_TYPE);
			error.put("total_count", unknownexception.getExceptionCount());
			errors.add(error);
		}
		for (ExceptionData exceptionData : exceptionDatas) {
			com.netease.qa.log.meta.Exception exception = exceptionDao
					.findByExceptionId(exceptionData.getExceptionId());
			error = new JSONObject();
			if (!exception.getExceptionType().equals(Const.UNKNOWN_TYPE)) {
				error.put("exp_id", exception.getExceptionId());
				error.put("error_type", exception.getExceptionType());
				error.put("error_example", exception.getExceptionDemo());
				error.put("total_count", exceptionData.getExceptionCount());
				errors.add(error);
			}
		}
		result.put("error", errors);
		return result;
	}

	@Override
	public JSONObject queryErrorRecordsWithTimeDetail(int logSourceId, long startTime, long endTime, String orderBy,
			String order, int limit, int offset) {
		List<ExceptionData> exceptionDatas = null;
		ExceptionData unknownexception = exceptionDataDao.findUnknownTypeByLogSourceIdAndTime(logSourceId, startTime,
				endTime, Const.UNKNOWN_TYPE);
		try {
			exceptionDatas = exceptionDataDao.findErrorRecordsByLogSourceIdAndTime(logSourceId, startTime, endTime,
					orderBy, order, limit, offset);
		} catch (Exception e) {
			logger.error("error", e);
			return null;
		}
		// 组装数据
		JSONObject result = new JSONObject();
		result.put("project_id", logSourceDao.findByLogSourceId(logSourceId).getProjectId());
		result.put("log_source_id", logSourceId);
		JSONObject error = new JSONObject();
		JSONArray errors = new JSONArray();
		// 当unknown不为空，且第一页，才把error装入array中
		if (unknownexception != null && offset == 0) {
			com.netease.qa.log.meta.Exception ukexception = exceptionDao.findByExceptionId(unknownexception
					.getExceptionId());
			error.put("exp_id", ukexception.getExceptionId());
			error.put("error_type", Const.UNKNOWN_TYPE);
			error.put("error_example", Const.UNKNOWN_TYPE);
			error.put("total_count", unknownexception.getExceptionCount());
			error.put("detail", new JSONArray());
			errors.add(error);
		}
		for (ExceptionData exceptionData : exceptionDatas) {
			error = new JSONObject();
			com.netease.qa.log.meta.Exception exception = exceptionDao
					.findByExceptionId(exceptionData.getExceptionId());
			if (!exception.getExceptionType().equals(Const.UNKNOWN_TYPE)) {
				error.put("exp_id", exception.getExceptionId());
				error.put("error_type", exception.getExceptionType());
				error.put("error_example", exception.getExceptionDemo());
				error.put("total_count", exceptionData.getExceptionCount());
				/**
				 * detail
				 * jsonarray在web端，没有用（因为detail被写在另外一个接口queryDetailByErrorType中
				 * ），只是在api中才有用
				 */
				JSONArray details = new JSONArray();
				List<ExceptionData> tmp = exceptionDataDao.findErrorRecordsByLogSourceIdAndExceptionIdAndTime(
						logSourceId, exceptionData.getExceptionId(), startTime, endTime, "sample_time", "desc", 99999,
						0);
				for (ExceptionData e : tmp) {
					JSONObject detail = new JSONObject();
					detail.put(MathUtil.parse2Str(e.getSampleTime()), e.getExceptionCount());
					details.add(detail);
				}
				error.put("detail", details);
				errors.add(error);
			}
		}
		result.put("error", errors);
		return result;
	}

	@Override
	public JSONObject queryUnknownExceptions(int logSourceId, long startTime, long endTime, int limit, int offset) {
		List<UkExceptionData> ukExceptionDatas = null;
		try {
			ukExceptionDatas = ukExceptionDataDao.findByLogSourceIdAndTime(logSourceId, startTime, endTime, limit,
					offset);
		} catch (Exception e) {
			logger.error("error", e);
			return null;
		}
		// 组装数据
		JSONObject result = new JSONObject();
		result.put("project_id", this.logSourceDao.findByLogSourceId(logSourceId).getProjectId());
		result.put("log_source_id", logSourceId);
		// 查不到数据，unknown部分为空
		if (ukExceptionDatas.size() == 0) {
			result.put("unknowns", new JSONArray());
			return result;
		}
		JSONArray unknowns = new JSONArray();
		for (UkExceptionData uk : ukExceptionDatas) {
			JSONObject unknown = new JSONObject();
			unknown.put(MathUtil.parse2Str(uk.getOriginLogTime()), uk.getOriginLog());
			unknowns.add(unknown);
		}
		result.put("unknowns", unknowns);
		return result;
	}

	@Override
	public JSONObject queryDetailByErrorType(int logSourceId, int exceptionId, long startTime, long endTime,
			String sort, String order, int limit, int offset) {
		JSONObject result = new JSONObject();
		JSONArray details = new JSONArray();
		List<ExceptionData> tmp = exceptionDataDao.findErrorRecordsByLogSourceIdAndExceptionIdAndTime(logSourceId,
				exceptionId, startTime, endTime, sort, order, limit, offset);
		for (ExceptionData e : tmp) {
			JSONObject detail = new JSONObject();
			detail.put("date_time", MathUtil.parse2Str(e.getSampleTime()));
			detail.put("total_count", e.getExceptionCount());
			details.add(detail);
		}
		result.put("details", details);
		return result;
	}

	@Override
	public JSONObject queryExceptionByLogSourceIdAndTime(int logSourceId, long startTime, long endTime) {
		ExceptionDataRecord exceptionDataRecord = null;
		try {
			exceptionDataRecord = exceptionDataDao.findRecordsByLogSourceIdAndTime(logSourceId, startTime, endTime);
		} catch (Exception e) {
			logger.error("error", e);
			return null;
		}
		// 组装数据
		JSONObject result = new JSONObject();
		JSONArray details = new JSONArray();
		if (exceptionDataRecord != null) {
			String[] eids = exceptionDataRecord.getExceptionIds().split(",");
			String[] ecounts = exceptionDataRecord.getExceptionCounts().split(",");
			for (int i = 0; i < eids.length; i++) {
				int exceptionId = Integer.valueOf(eids[i].trim());
				String type = exceptionDao.findByExceptionId(exceptionId).getExceptionType();
				JSONObject detail = new JSONObject();
				detail.put("type", type);
				detail.put("count", ecounts[i]);
				details.add(detail);
			}
			result.put("error_tc", details);
			result.put("total_count", exceptionDataRecord.getTotalCount());
			return result;
		} else {
			result.put("error_tc", details);
			result.put("total_count", 0);
			return result;
		}
	}

	/**
	 * 根据日志源列表，按照机器聚合，AB平台
	 */
	@Override
	public JSONObject queryErrorRecordsByLogSourceIds(List<Integer> logSourceIds, long start_time, long end_time) {
		JSONObject result = new JSONObject();
		//map用来临时
		Map<String, JSONArray> temp = new HashMap<String, JSONArray>();
		for (int i = 0; i < logSourceIds.size(); i++) {
			JSONObject detail = new JSONObject(); // detail : 一条日志源的基本信息和error_tc数组
			int logsourceId = logSourceIds.get(i);
			LogSource logSource = logSourceDao.findByLogSourceId(logsourceId);
			detail.put("log_source_id", logSource.getLogSourceId());
			detail.put("log_source_name", logSource.getLogSourceName());
			String serverName = logSource.getHostname().trim();
			Integer total_count = exceptionDataDao.getLogSourceExceptionTotalCountByTime(logsourceId, start_time,
					end_time);
			if (total_count == null) {
				total_count = 0;
			}
			detail.put("total_count", total_count);
			// 获取error_tc数组
			JSONArray error_tcs = new JSONArray();
			JSONObject error_tc;
			List<ExceptionData> exceptionDatas = exceptionDataDao.findErrorRecordsByLogSourceIdAndTimeByAB(logsourceId,
					start_time, end_time);
			if (exceptionDatas != null && exceptionDatas.size() > 0) {
				for (ExceptionData exceptionData : exceptionDatas) {
					error_tc = new JSONObject();
					error_tc.put("type", exceptionData.getExceptionType());
					error_tc.put("count", exceptionData.getExceptionCount());
					error_tcs.add(error_tc);
				}
			}
			detail.put("error_tc", error_tcs);
			JSONArray details = null;
			if (!temp.containsKey(serverName)) {
				details = new JSONArray();
				details.add(detail);
			} else {
				details = temp.get(serverName);
				details.add(detail);
			}
			temp.put(serverName, details);
		}
		// 把hashmap中的数据封装起来
		JSONArray records = new JSONArray();
		JSONObject record = null;
		@SuppressWarnings("rawtypes")
		Iterator iter = temp.entrySet().iterator();
		while (iter.hasNext()) {
			record = new JSONObject();
			@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry) iter.next();
			String key = (String) entry.getKey();
			JSONArray val = (JSONArray) entry.getValue();
			record.put("hostname", key);
			record.put("detail", val);
			records.add(record);
		}
		result.put("record", records);
		return result;
	}
	/**
	 * 4.4 批量获取日志源异常统计信息，按照机器聚合 区分日志源 （ AB 平台）
	 */
	@Override
	public JSONObject queryErrorRecordsByHostname(String hostname, long start_time, long end_time) {
		JSONObject result = new JSONObject();
		JSONObject record = new JSONObject();
		record.put("hostname", hostname);
		JSONArray details = new JSONArray();
		List<LogSource> logSources = logSourceDao.getLogSourcesByHostname(hostname);
		if(logSources == null || logSources.isEmpty()){
			record.put("detail", details);
			result.put("record", record);
			return result;
		}
		for(LogSource logSource : logSources){
			int logsourceId = logSource.getLogSourceId();
			JSONObject detail = new JSONObject();
			detail.put("log_source_id", logSource.getLogSourceId());
			detail.put("log_source_name", logSource.getLogSourceName());
			Integer total_count = exceptionDataDao.getLogSourceExceptionTotalCountByTime(logsourceId, start_time,
					end_time);
			if (total_count == null) {
				total_count = 0;
			}
			detail.put("total_count", total_count);
			// 获取error_tc数组
			JSONArray error_tcs = new JSONArray();
			JSONObject error_tc;
			List<ExceptionData> exceptionDatas = exceptionDataDao.findErrorRecordsByLogSourceIdAndTimeByAB(logsourceId,
					start_time, end_time);
			if (exceptionDatas != null && exceptionDatas.size() > 0) {
				for (ExceptionData exceptionData : exceptionDatas) {
					error_tc = new JSONObject();
					error_tc.put("type", exceptionData.getExceptionType());
					error_tc.put("count", exceptionData.getExceptionCount());
					error_tcs.add(error_tc);
				}
			}
			detail.put("error_tc", error_tcs);
			details.add(detail);
		}
		record.put("detail", details);
		result.put("record", record);
		return result;
	}
	
	/*
	 * AB平台 按机器聚合所有日志源的异常信息-曲线数据
	 */
	@Override
	public JSONObject queryErrorRecordsGraphByMachine(String host_name,long start, long end, int log_type) {
		logger.debug(" --- queryErrorRecordsGraphByMachine ---" );
		// 自适应时间间隔
		int pointNum = Const.MACHINE_ERROR_POINT;
		int timeRange = MathUtil.getTimeRangeByPoint(start, end, pointNum);
		JSONArray data = new JSONArray();
		// 汇总机器的所有日志源，如果机器上没有日志源，则不需要分析了
		// select group_concat(log_source_id) from log_source  where hostname="app-52.photo.163.org" and log_type=0;
		String log_source_ids_str = logSourceDao.findIdsByHostname(host_name, log_type);
		logger.debug(" --- log_source_ids_str---" + log_source_ids_str);
		if (log_source_ids_str != null) {
			// 对每一小段时间进行分析，对应data内部一个元素
			long startTime, endTime;
			for (int i = 0; i < pointNum; i++) {
				startTime = start + i * timeRange;
				endTime = startTime + timeRange;
				logger.debug(" --- startTime---" + startTime);
				logger.debug(" --- endTime---" + endTime);
				// total: 获取所有日志源在某一小段时间内的所有total个数
				// select IFNULL( sum(exception_count), 0 ) as total  from exception_data where log_source_id in ( 40,41,37,21,30,32,33,35,36,39 ) and sample_time between 1433833990 and 1433834014; 
				int total = exceptionDataDao.findErrorTotalByMachineAndTimeByAB(log_source_ids_str,startTime, endTime);
				logger.debug(" --- total---" + total);
				// 如果不存在异常，则不需要查询具体信息了
				if (total > 0) {
					JSONObject dataObj = new JSONObject();
					dataObj.put("total", total);
					 // time: 聚合时间，开始和结束时间的中间时间
					long avgTime = (endTime + startTime) / 2;
					dataObj.put("time", avgTime * 1000);
					 // error_tc : 获取所有日志源在某一小段时间内的异常具体type和count
					JSONArray error_tc = new JSONArray();
					List<ExceptionData> exceptionDatas = exceptionDataDao.findErrorRecordsByMachineAndTimeByAB(log_source_ids_str, startTime, endTime);
					for (ExceptionData exceptionData : exceptionDatas) {
						JSONObject errobj = new JSONObject();
						errobj.put("type", exceptionData.getExceptionType());
						errobj.put("count", exceptionData.getExceptionCount());
						error_tc.add(errobj);
					}
					dataObj.put("error_tc", error_tc);
					data.add(dataObj);
				}else{
					JSONObject dataObj = new JSONObject();
					dataObj.put("total", 0);
					// time: 聚合时间，开始和结束时间的中间时间
					long avgTime = (endTime + startTime) / 2;
					dataObj.put("time", avgTime * 1000);
					JSONArray error_tc = new JSONArray();
					dataObj.put("error_tc", error_tc);
					data.add(dataObj);
				}
			}
		}
		// 封装results结果
		JSONObject results = new JSONObject();
		results.put("host_name", host_name);
		results.put("data", data);
		// 封装最后结果
		JSONObject record = new JSONObject();
		record.put("code", 200);
		record.put("results", results);
		return record;
	}
	
	/*
	 * 按机器聚合所有日志源的异常信息-表格数据
	 */
	@Override
	public JSONObject queryErrorRecordsTableByMachine(String host_name, long start, long end , int log_type){
		logger.debug(" --- queryErrorRecordsTableByMachine ---" );
		JSONArray error_tc = new JSONArray();
		int total = 0; 
		// 汇总机器的所有日志源，如果机器上没有日志源，则不需要分析了
		// select group_concat(log_source_id) from log_source  where hostname="app-52.photo.163.org" and log_type=0;
		String log_source_ids_str = logSourceDao.findIdsByHostname(host_name, log_type);
		logger.debug(" --- log_source_ids_str---" + log_source_ids_str);
		if (log_source_ids_str != null) {
			// 对整个时间范围内进行查询
				total = exceptionDataDao.findErrorTotalByMachineAndTimeByAB(log_source_ids_str,start, end);
				logger.debug(" --- total---" + total);
				// 如果不存在异常，则不需要查询具体信息了
				if (total > 0) {
					 // error_tc : 获取所有日志源在某一小段时间内的异常具体type和count
					List<ExceptionData> exceptionDatas = exceptionDataDao.findErrorRecordsByMachineAndTimeByAB(log_source_ids_str, start, end);
					for (ExceptionData exceptionData : exceptionDatas) {
						JSONObject errobj = new JSONObject();
						errobj.put("type", exceptionData.getExceptionType());
						errobj.put("count", exceptionData.getExceptionCount());
						errobj.put("sample", exceptionData.getExceptionDemo());
						error_tc.add(errobj);
					}
			}
		}
		// 封装results结果
		JSONObject results = new JSONObject();
		results.put("host_name", host_name);
		results.put("total", total);
		results.put("error_tc", error_tc);
		
		// 封装最后结果
		JSONObject record = new JSONObject();
		record.put("code", 200);
		record.put("results", results);
		return record;
		
	}

}
