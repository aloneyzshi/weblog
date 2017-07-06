package com.netease.qa.log.storm.service;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.meta.Exception;
import com.netease.qa.log.meta.ExceptionData;
import com.netease.qa.log.meta.NginxAccess;
import com.netease.qa.log.meta.UkExceptionData;
import com.netease.qa.log.meta.dao.ExceptionDao;
import com.netease.qa.log.meta.dao.ExceptionDataDao;
import com.netease.qa.log.meta.dao.NginxAccessDao;
import com.netease.qa.log.meta.dao.UkExceptionDataDao;
import com.netease.qa.log.storm.service.nginx.AnalyzeService;
import com.netease.qa.log.storm.util.MybatisUtil;

/**
 * 监控数据
 * 
 * @author hzzhangweijie
 *
 */

public class MonitorDataService {

	private static final Logger logger = LoggerFactory.getLogger(MonitorDataService.class);

	private static ConcurrentHashMap<String, Exception> exceptionCache;
	private static ConcurrentHashMap<Integer, Exception> exceptionIdCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>> exceptionCountCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Long>> exceptionTimeCache;

	static {
		exceptionCache = new ConcurrentHashMap<String, Exception>();
		exceptionIdCache = new ConcurrentHashMap<Integer, Exception>();
		exceptionCountCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Integer>>();
		exceptionTimeCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, Long>>();
	}

	public static Exception getException(int logSourceId, String exceptionTypeMD5) {
		String key = logSourceId + "_" + exceptionTypeMD5;
		if (exceptionCache.containsKey(key)) {
			return exceptionCache.get(key);
		} else {
			SqlSession sqlSession = MybatisUtil.getSqlSessionFactory().openSession(true);
			try {
				ExceptionDao exceptionDao = sqlSession.getMapper(ExceptionDao.class);
				Exception exception = exceptionDao.findByTwoKey(logSourceId, exceptionTypeMD5);
				if (exception != null) {
					exceptionCache.put(key, exception);
					exceptionIdCache.put(exception.getExceptionId(), exception);
				}
				return exception;
			} finally {
				sqlSession.close();
			}
		}
	}

	public static Exception getException(int exceptionId) {
		if (exceptionIdCache.containsKey(exceptionId)) {
			return exceptionIdCache.get(exceptionId);
		} else {
			SqlSession sqlSession = MybatisUtil.getSqlSessionFactory().openSession(true);
			try {
				ExceptionDao exceptionDao = sqlSession.getMapper(ExceptionDao.class);
				Exception exception = exceptionDao.findByExceptionId(exceptionId);
				if (exception != null) {
					exceptionIdCache.put(exception.getExceptionId(), exception);
				}
				return exception;
			} finally {
				sqlSession.close();
			}
		}
	}

	public static int putException(int logSourceId, String exceptionTypeMD5, String exceptionType, String exceptionDemo) {
		Exception exception = new Exception();
		exception.setLogSourceId(logSourceId);
		exception.setExceptionTypeMD5(exceptionTypeMD5);
		exception.setExceptionType(exceptionType);
		exception.setExceptionDemo(exceptionDemo);

		SqlSession sqlSession = MybatisUtil.getSqlSessionFactory().openSession(true);
		try {
			ExceptionDao exceptionDao = sqlSession.getMapper(ExceptionDao.class);
			exceptionDao.insert(exception);
			exceptionCache.put(logSourceId + "_" + exceptionTypeMD5, exception);
			return exception.getExceptionId();
		} finally {
			sqlSession.close();
		}
	}

	public static void putUkExceptionData(int logSourceId, Long originLogTime, String originLog) {
		UkExceptionData ukExceptionData = new UkExceptionData();
		ukExceptionData.setLogSourceId(logSourceId);
		ukExceptionData.setOriginLogTime(originLogTime);
		ukExceptionData.setOriginLog(originLog);

		SqlSession sqlSession = MybatisUtil.getSqlSessionFactory().openSession(true);
		try {
			UkExceptionDataDao ukExceptionDataDao = sqlSession.getMapper(UkExceptionDataDao.class);
			ukExceptionDataDao.insert(ukExceptionData);
		} finally {
			sqlSession.close();
		}
	}

	/**
	 * 监控数据添加至内存缓存
	 */
	public static void putExceptionData(int logSourceId, int exceptionId, Long dsTime) {
		// 添加logSourceId:exceptionId:count缓存。 count为该exceptionId对应的日志数量
		if (!exceptionCountCache.containsKey(logSourceId)) {
			ConcurrentHashMap<Integer, Integer> tmp = new ConcurrentHashMap<Integer, Integer>();
			tmp.put(exceptionId, 1);
			exceptionCountCache.put(logSourceId, tmp);
		} else {
			try {
				ConcurrentHashMap<Integer, Integer> tmp = exceptionCountCache.get(logSourceId);
				if (!tmp.containsKey(exceptionId)) {
					tmp.put(exceptionId, 1);
				} else {
					tmp.put(exceptionId, tmp.get(exceptionId) + 1);
				}
			} catch (NullPointerException e) {
				logger.error("putExceptionData1:", e);
			}

		}
		// 添加logSourceId:exceptionId:time缓存.
		// time为这个监控周期内，该exceptionId对应的日志的ds_time(flume-agent接收到原始日志的时间)之和。
		if (!exceptionTimeCache.containsKey(logSourceId)) {
			ConcurrentHashMap<Integer, Long> tmp = new ConcurrentHashMap<Integer, Long>();
			tmp.put(exceptionId, dsTime);
			exceptionTimeCache.put(logSourceId, tmp);
		} else {
			try {
				ConcurrentHashMap<Integer, Long> tmp = exceptionTimeCache.get(logSourceId);
				if (!tmp.containsKey(exceptionId)) {
					tmp.put(exceptionId, dsTime);
				} else {
					tmp.put(exceptionId, tmp.get(exceptionId) + dsTime);
					int count = exceptionCountCache.get(logSourceId).get(exceptionId);
					logger.debug("logSourceId: " + logSourceId + ", exceptionid: " + exceptionId + ", count: " + count
							+ ", dsTime: " + tmp.get(exceptionId) / count);
				}
			} catch (NullPointerException e) {
				logger.error("putExceptionData2:", e);
			}
		}
	}

	/**
	 * 写入数据库 exception_data表
	 */
	public static void writeExceptionData(Long sampleTime) {
		logger.info("---write exception data into DB---");
		SqlSession sqlSession = MybatisUtil.getSqlSessionFactory().openSession(true);
		try {
			ExceptionDataDao exceptionDataDao = sqlSession.getMapper(ExceptionDataDao.class);
			for (Entry<Integer, ConcurrentHashMap<Integer, Integer>> e1 : exceptionCountCache.entrySet()) {
				int logSourceId = e1.getKey();
				ConcurrentHashMap<Integer, Integer> tmp = e1.getValue();
				ConcurrentHashMap<Integer, Long> exception_time = exceptionTimeCache.get(logSourceId);

				for (Entry<Integer, Integer> e2 : tmp.entrySet()) {
					int exceptionId = e2.getKey();
					int count = e2.getValue();
					Long dsTime = exception_time.get(exceptionId) / count;

					ExceptionData exceptionData = new ExceptionData();
					exceptionData.setLogSourceId(logSourceId);
					exceptionData.setExceptionId(exceptionId);
					exceptionData.setSampleTime(dsTime); // 改成dsTime的平均值
					exceptionData.setExceptionCount(count);
					exceptionDataDao.insert(exceptionData);
					logger.debug(exceptionData.toString());
				}
			}
			exceptionCountCache.clear();
			exceptionTimeCache.clear();
		} finally {
			sqlSession.close();
		}
	}

	public static void writeNginxAccessData() {
		logger.info("---write NginxAccess data into DB---");
		SqlSession sqlSession = MybatisUtil.getSqlSessionFactory().openSession(true);

		try {
			NginxAccessDao nad = sqlSession.getMapper(NginxAccessDao.class);
			List<NginxAccess> NginxAccesses = AnalyzeService.writeResult();
			for (NginxAccess nginxAccess : NginxAccesses) {
				nad.insert(nginxAccess);
			}
		} finally {
			sqlSession.close();
		}
	}

}
