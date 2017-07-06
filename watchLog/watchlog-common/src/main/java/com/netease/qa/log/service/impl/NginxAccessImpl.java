package com.netease.qa.log.service.impl;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.meta.NginxAccess;
import com.netease.qa.log.meta.dao.LogSourceDao;
import com.netease.qa.log.meta.dao.NginxAccessDao;
import com.netease.qa.log.service.NginxAccessService;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.MathUtil;

@Service
public class NginxAccessImpl implements NginxAccessService {

	private static final Logger logger = LoggerFactory.getLogger(NginxAccessImpl.class);
	@Resource
	private NginxAccessDao nginxAccessDao;
	@Resource
	private LogSourceDao logSourceDao;
	

	@Override
	public JSONObject getTopNUrl(int logSourceId, long start, long end, int topN, String sort) {
		LogSource logSource = logSourceDao.findByLogSourceId(logSourceId);
		List<String> urls = nginxAccessDao.getTopUrl(logSourceId, start, end, topN, sort);
		JSONObject results = new JSONObject();
		JSONObject result = new JSONObject();
		results.put("project_id", logSource.getProjectId());
		results.put("log_source_id", logSource.getLogSourceId());
		results.put("log_source_name", logSource.getLogSourceName());
		results.put("data", urls);
		result.put("code", 200);
		result.put("results", results);
		return result;
	}

	@Override
	public JSONObject getTopAllData(int logSourceId, long start, long end, int topN, String sort) {
		List<NginxAccess> nginxAccesses = nginxAccessDao.getTopAllData(logSourceId, start, end, topN, sort);
		JSONObject result = new JSONObject();
		JSONObject results = new JSONObject();
		LogSource logSource = logSourceDao.findByLogSourceId(logSourceId);
		results.put("project_id", logSource.getProjectId());
		results.put("log_source_id", logSource.getLogSourceId());
		results.put("log_source_name", logSource.getLogSourceName());
		JSONObject data;
		JSONArray datas = new JSONArray();
		long totalTime = end - start;
		for (NginxAccess nginxAccess : nginxAccesses) {
			data = new JSONObject();
			data.put("url", nginxAccess.getUrl());
			long totalCount = nginxAccess.getTotalCount();
			data.put("total", totalCount);
			// tps = 总数量/总时间
			BigDecimal tb1 = new BigDecimal(String.valueOf(totalCount));
			BigDecimal tb2 = new BigDecimal(String.valueOf(totalTime));
			double tps = tb1.divide(tb2, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put("tps", tps);
			// error
			long okCount = nginxAccess.getOkCount();
			data.put("error", nginxAccess.getError4Count() + nginxAccess.getError5Count());
			// ok_rate
			BigDecimal okSum = new BigDecimal(String.valueOf(okCount));
			BigDecimal totalSum = new BigDecimal(String.valueOf(totalCount));
			double ok_rate = okSum.divide(totalSum, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put("rate_ok", ok_rate);
			// 4**_rate
			BigDecimal error4Sum = new BigDecimal(String.valueOf(nginxAccess.getError4Count()));
			double error4_rate = error4Sum.divide(totalSum, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put("rate_client_error", error4_rate);
			// 5**_rate
			BigDecimal error5Sum = new BigDecimal(String.valueOf(nginxAccess.getError5Count()));
			double error5_rate = error5Sum.divide(totalSum, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put("rate_server_error", error5_rate);
			// msize
			BigDecimal bytesSum = new BigDecimal(String.valueOf(nginxAccess.getByteTotal()));
			double msize = bytesSum.divide(totalSum, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put("msize", msize);
			// avg_rt
			BigDecimal requestTimeSum = new BigDecimal(String.valueOf(nginxAccess.getRequestTimeTotal()));
			double avg_rt = requestTimeSum.divide(totalSum, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
			data.put("rt_avg", avg_rt);
			// max_rt
			data.put("rt_max", nginxAccess.getRequestTimeMax());
			// 90_rt
			String url = nginxAccess.getUrl();
			int count = nginxAccessDao.getTotalNumByUrl(logSourceId, url, start, end);
			int count90 = count * 90 / 100;
			if(count90 == 0){
				data.put("rt_ninety", 0);
			}else{
				int value90 = nginxAccessDao.getValue("request_time_90", logSourceId, url, start, end, count90 - 1);
				data.put("rt_ninety", value90);
			}
			// 99_rt
			int count99 = count * 99 / 100;
			if(count99 == 0){
				data.put("rt_ninety_nine", 0);
			}else{
				int value99 = nginxAccessDao.getValue("request_time_99", logSourceId, url, start, end, count99 - 1);
				data.put("rt_ninety_nine", value99);
			}
			datas.add(data);
		}
		results.put("data", datas);
		result.put("code", 200);
		result.put("results", results);
		return result;
	}

	@Override
	public JSONObject getRealSingleData(int logSourceId, String url, long start, long end) {
		NginxAccess nginxAccess = nginxAccessDao.getRealTimeData(logSourceId, url, start, end);
		LogSource logSource = logSourceDao.findByLogSourceId(logSourceId);
		JSONObject results = new JSONObject();
		results.put("project_id", logSource.getProjectId());
		results.put("log_source_id", logSource.getLogSourceId());
		results.put("log_source_name", logSource.getLogSourceName());
		results.put("url", url);
		// end - start 这段时间没有，数据赋0
		if (nginxAccess == null) {
			JSONObject result = new JSONObject();
			JSONObject data = new JSONObject();
			long avgTime = (end + start) / 2;
			avgTime = avgTime * 1000;
			// tps
			JSONObject tps = new JSONObject();
			tps.put("time", avgTime);
			tps.put("value", 0);
			data.put("tps", tps);
			// error
			JSONObject error = new JSONObject();
			error.put("time", avgTime);
			error.put("value", 0);
			data.put("error", error);
			// avg_rt
			JSONObject avg_rt = new JSONObject();
			avg_rt.put("time", avgTime);
			avg_rt.put("value", 0);
			data.put("avg_rt", avg_rt);
			// max_rt
			JSONObject max_rt = new JSONObject();
			max_rt.put("time", avgTime);
			max_rt.put("value", 0);
			data.put("max_rt", max_rt);

			results.put("data", data);
			result.put("code", 200);
			result.put("results", results);
			return result;
		}
		JSONObject data = new JSONObject();
		JSONObject tps = new JSONObject();
		long time = nginxAccess.getStartTimeStamp();
		// tps
		tps.put("time", time);
		BigDecimal totalCount = new BigDecimal(String.valueOf(nginxAccess.getTotalCount()));
		BigDecimal totalTime = new BigDecimal(String.valueOf(end - start));
		double tpsValue = totalCount.divide(totalTime, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
		tps.put("value", tpsValue);
		data.put("tps", tps);
		// error
		JSONObject error = new JSONObject();
		error.put("time", time);
		error.put("value", nginxAccess.getError4Count() + nginxAccess.getError5Count());
		data.put("error", error);
		// avg_rt
		JSONObject avg_rt = new JSONObject();
		avg_rt.put("time", time);
		BigDecimal totalRequestTime = new BigDecimal(String.valueOf(nginxAccess.getRequestTimeTotal()));
		double avg_rtValue = totalRequestTime.divide(totalCount, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
		avg_rt.put("value", avg_rtValue);
		data.put("avg_rt", avg_rt);
		// max_rt
		JSONObject max_rt = new JSONObject();
		max_rt.put("time", time);
		max_rt.put("value", nginxAccess.getRequestTimeMax());
		data.put("max_rt", max_rt);

		results.put("data", data);
		JSONObject result = new JSONObject();
		result.put("code", 200);
		result.put("results", results);
		return result;
	}
	
	/*
	 * 返回没有url情况下的全部数据（全部tps,error等）
	 */
	@Override
	public JSONObject getAllRealSingleData(int logSourceId, long start, long end) {
		NginxAccess nginxAccess = nginxAccessDao.getAllRealTimeData(logSourceId, start, end);
		LogSource logSource = logSourceDao.findByLogSourceId(logSourceId);
		JSONObject results = new JSONObject();
		results.put("project_id", logSource.getProjectId());
		results.put("log_source_id", logSource.getLogSourceId());
		results.put("log_source_name", logSource.getLogSourceName());
		// end - start 这段时间没有，数据赋0
		if (nginxAccess == null) {
			JSONObject result = new JSONObject();
			JSONObject data = new JSONObject();
			long avgTime = (end + start) / 2;
			avgTime = avgTime * 1000;
			// tps
			JSONObject tps = new JSONObject();
			tps.put("time", avgTime);
			tps.put("value", 0);
			data.put("tps", tps);
			// error
			JSONObject error = new JSONObject();
			error.put("time", avgTime);
			error.put("value", 0);
			data.put("error", error);
			// avg_rt
			JSONObject avg_rt = new JSONObject();
			avg_rt.put("time", avgTime);
			avg_rt.put("value", 0);
			data.put("avg_rt", avg_rt);
			// max_rt
			JSONObject max_rt = new JSONObject();
			max_rt.put("time", avgTime);
			max_rt.put("value", 0);
			data.put("max_rt", max_rt);

			results.put("data", data);
			result.put("code", 200);
			result.put("results", results);
			return result;
		}
		JSONObject data = new JSONObject();
		JSONObject tps = new JSONObject();
		long time = nginxAccess.getStartTimeStamp();
		// tps
		tps.put("time", time);
		BigDecimal totalCount = new BigDecimal(String.valueOf(nginxAccess.getTotalCount()));
		BigDecimal totalTime = new BigDecimal(String.valueOf(end - start));
		double tpsValue = totalCount.divide(totalTime, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
		tps.put("value", tpsValue);
		data.put("tps", tps);
		// error
		JSONObject error = new JSONObject();
		error.put("time", time);
		error.put("value", nginxAccess.getError4Count() + nginxAccess.getError5Count());
		data.put("error", error);
		// avg_rt
		JSONObject avg_rt = new JSONObject();
		avg_rt.put("time", time);
		BigDecimal totalRequestTime = new BigDecimal(String.valueOf(nginxAccess.getRequestTimeTotal()));
		double avg_rtValue = totalRequestTime.divide(totalCount, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
		avg_rt.put("value", avg_rtValue);
		data.put("avg_rt", avg_rt);
		// max_rt
		JSONObject max_rt = new JSONObject();
		max_rt.put("time", time);
		max_rt.put("value", nginxAccess.getRequestTimeMax());
		data.put("max_rt", max_rt);

		results.put("data", data);
		JSONObject result = new JSONObject();
		result.put("code", 200);
		result.put("results", results);
		return result;
	}

	@Override
	public JSONObject getOfflineAllData(int logSourceId, String url, long start, long end) {
		int pointNum = Const.OFFLINE_POINT;
		int timeRange = MathUtil.getTimeRangeByPoint(start, end, pointNum);
		long startTime, endTime;
		LogSource logSource = logSourceDao.findByLogSourceId(logSourceId);
		JSONObject results = new JSONObject();
		JSONObject data = new JSONObject();
		results.put("project_id", logSource.getProjectId());
		results.put("log_source_id", logSource.getLogSourceId());
		results.put("log_source_name", logSource.getLogSourceName());
		results.put("url", url);
		JSONArray tpses = new JSONArray();
		JSONArray errors = new JSONArray();
		JSONArray avg_rts = new JSONArray();
		JSONArray max_rts = new JSONArray();
		NginxAccess nginxAccess;
		JSONObject tps, error, avg_rt, max_rt;
		for (int i = 0; i < pointNum; i++) {
			startTime = start + i * timeRange;
			endTime = startTime + timeRange;
			nginxAccess = nginxAccessDao.getRealTimeData(logSourceId, url, startTime, endTime);
			tps = new JSONObject();
			error = new JSONObject();
			avg_rt = new JSONObject();
			max_rt = new JSONObject();
			if (nginxAccess == null) {
				long avgTime = (endTime + startTime) / 2;
				avgTime = avgTime * 1000;
				// tps
				tps.put("time", avgTime);
				tps.put("value", 0);
				tpses.add(tps);
				// error
				error.put("time", avgTime);
				error.put("value", 0);
				errors.add(error);
				// avg_rt
				avg_rt.put("time", avgTime);
				avg_rt.put("value", 0);
				avg_rts.add(avg_rt);
				// max_rt
				max_rt.put("time", avgTime);
				max_rt.put("value", 0);
				max_rts.add(max_rt);
			} else {
				long time = nginxAccess.getStartTimeStamp();
				// tps
				tps.put("time", time);
				BigDecimal totalCount = new BigDecimal(String.valueOf(nginxAccess.getTotalCount()));
				BigDecimal totalTime = new BigDecimal(String.valueOf(end - start));
				double tpsValue = totalCount.divide(totalTime, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
				tps.put("value", tpsValue);
				tpses.add(tps);
				// error
				error.put("time", time);
				error.put("value", nginxAccess.getError4Count() + nginxAccess.getError5Count());
				errors.add(error);
				// avg_rt
				avg_rt.put("time", time);
				BigDecimal totalRequestTime = new BigDecimal(String.valueOf(nginxAccess.getRequestTimeTotal()));
				double avg_rtValue = totalRequestTime.divide(totalCount, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
				avg_rt.put("value", avg_rtValue);
				avg_rts.add(avg_rt);
				// max_rt
				max_rt.put("time", time);
				max_rt.put("value", nginxAccess.getRequestTimeMax());
				max_rts.add(max_rt);
			}
		}
		data.put("tps", tpses);
		data.put("error", errors);
		data.put("avg_rt", avg_rts);
		data.put("max_rt", max_rts);
		results.put("data", data);
		JSONObject result = new JSONObject();
		result.put("code", 200);
		result.put("results", results);
		return result;
	}

	@Override
	public JSONObject getOfflineAllDataWithoutUrl(int logSourceId, long start, long end) {
		int pointNum = Const.OFFLINE_POINT;
		int timeRange = MathUtil.getTimeRangeByPoint(start, end, pointNum);
		long startTime, endTime;
		LogSource logSource = logSourceDao.findByLogSourceId(logSourceId);
		JSONObject results = new JSONObject();
		JSONObject data = new JSONObject();
		results.put("project_id", logSource.getProjectId());
		results.put("log_source_id", logSource.getLogSourceId());
		results.put("log_source_name", logSource.getLogSourceName());
		JSONArray tpses = new JSONArray();
		JSONArray errors = new JSONArray();
		JSONArray avg_rts = new JSONArray();
		JSONArray max_rts = new JSONArray();
		NginxAccess nginxAccess;
		JSONObject tps, error, avg_rt, max_rt;
		long TestStart = System.currentTimeMillis();
		for (int i = 0; i < pointNum; i++) {
			startTime = start + i * timeRange;
			endTime = startTime + timeRange;
			long TestInterStart = System.currentTimeMillis();
			nginxAccess = nginxAccessDao.getAllRealTimeData(logSourceId, startTime, endTime);
			long TestInterEnd = System.currentTimeMillis();
			logger.debug("第" + i + "个点");
			logger.debug("内部返回的总时间：" + (TestInterEnd - TestInterStart));
//			System.out.println("内部返回的总时间：" + (TestInterEnd - TestInterStart));
			tps = new JSONObject();
			error = new JSONObject();
			avg_rt = new JSONObject();
			max_rt = new JSONObject();
			if (nginxAccess == null) {
				long avgTime = (endTime + startTime) / 2;
				avgTime = avgTime * 1000;
				// tps
				tps.put("time", avgTime);
				tps.put("value", 0);
				tpses.add(tps);
				// error
				error.put("time", avgTime);
				error.put("value", 0);
				errors.add(error);
				// avg_rt
				avg_rt.put("time", avgTime);
				avg_rt.put("value", 0);
				avg_rts.add(avg_rt);
				// max_rt
				max_rt.put("time", avgTime);
				max_rt.put("value", 0);
				max_rts.add(max_rt);
			} else {
				long time = nginxAccess.getStartTimeStamp();
				// tps
				tps.put("time", time);
				BigDecimal totalCount = new BigDecimal(String.valueOf(nginxAccess.getTotalCount()));
				BigDecimal totalTime = new BigDecimal(String.valueOf(end - start));
				double tpsValue = totalCount.divide(totalTime, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
				tps.put("value", tpsValue);
				tpses.add(tps);
				// error
				error.put("time", time);
				error.put("value", nginxAccess.getError4Count() + nginxAccess.getError5Count());
				errors.add(error);
				// avg_rt
				avg_rt.put("time", time);
				BigDecimal totalRequestTime = new BigDecimal(String.valueOf(nginxAccess.getRequestTimeTotal()));
				double avg_rtValue = totalRequestTime.divide(totalCount, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
				avg_rt.put("value", avg_rtValue);
				avg_rts.add(avg_rt);
				// max_rt
				max_rt.put("time", time);
				max_rt.put("value", nginxAccess.getRequestTimeMax());
				max_rts.add(max_rt);
			}
		}
		long TestEnd = System.currentTimeMillis();
		logger.debug("返回的总时间：" + (TestEnd - TestStart));
//		System.out.println("返回的总时间：" + (TestEnd - TestStart));
		data.put("tps", tpses);
		data.put("error", errors);
		data.put("avg_rt", avg_rts);
		data.put("max_rt", max_rts);
		results.put("data", data);
		JSONObject result = new JSONObject();
		result.put("code", 200);
		result.put("results", results);
		return result;
	}

}
