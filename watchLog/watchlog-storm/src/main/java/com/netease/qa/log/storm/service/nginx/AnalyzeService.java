package com.netease.qa.log.storm.service.nginx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.meta.NginxAccess;
 

/**
 * 分析记录，得到tps,requestTimeTotal,requestTimeMax,upstreamResponseTimeTotal,
 * upstreamResponseTimeMax等
 * 
 * @author hzquguoqing
 *
 */
public class AnalyzeService {
	private static final Logger logger = LoggerFactory.getLogger(AnalyzeService.class);

	// <log_source_id,<url,<time,count>>>
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> urlTpsCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> requestTimeTotalCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> requestTimeMaxCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> upstreamResponseTimeTotalCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> upstreamResponseTimeMaxCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> okCountCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> error4CountCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> error5CountCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> byteTotalCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>> allRequestTimeCache;
	private static ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>> allUpstreamResponseTimeCache;

	static {
		urlTpsCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		requestTimeTotalCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		requestTimeMaxCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		upstreamResponseTimeTotalCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		upstreamResponseTimeMaxCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		okCountCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		error4CountCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		error5CountCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		byteTotalCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>>();
		allRequestTimeCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>>();
		allUpstreamResponseTimeCache = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>>();
	}

	public static void putUrlTps(int logSourceId, String url, long startTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// urlTpsCache的第二层
		ConcurrentHashMap<Long, Integer> time_count;// urlTpsCache的第三层
		if (!urlTpsCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, 1);
			url_count.put(url, time_count);
			urlTpsCache.put(logSourceId, url_count);
		} else {
			url_count = urlTpsCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, 1);
				url_count.put(url, time_count);
				urlTpsCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, 1);
					url_count.put(url, time_count);
					urlTpsCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, time_count.get(startTime) + 1);
						url_count.put(url, time_count);
						urlTpsCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putUrlTps:", e);
					}
				}
			}
		}
	}

	public static void putTotalRequestTime(int logSourceId, String url, long startTime, int requestTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// 第二层
		ConcurrentHashMap<Long, Integer> time_count;// 第三层
		if (!requestTimeTotalCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, requestTime);
			url_count.put(url, time_count);
			requestTimeTotalCache.put(logSourceId, url_count);
		} else {
			url_count = requestTimeTotalCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, requestTime);
				url_count.put(url, time_count);
				requestTimeTotalCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, requestTime);
					url_count.put(url, time_count);
					requestTimeTotalCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, time_count.get(startTime) + requestTime);
						url_count.put(url, time_count);
						requestTimeTotalCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putTotalRequestTime:", e);
					}
				}
			}
		}
	}

	public static void putMaxRequestTime(int logSourceId, String url, long startTime, int requestTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// 第二层
		ConcurrentHashMap<Long, Integer> time_count;// 第三层
		if (!requestTimeMaxCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, requestTime);
			url_count.put(url, time_count);
			requestTimeMaxCache.put(logSourceId, url_count);
		} else {
			url_count = requestTimeMaxCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, requestTime);
				url_count.put(url, time_count);
				requestTimeMaxCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, requestTime);
					url_count.put(url, time_count);
					requestTimeMaxCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, Math.max(time_count.get(startTime), requestTime));
						url_count.put(url, time_count);
						requestTimeMaxCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putMaxRequestTime:", e);
					}
				}
			}
		}
	}

	public static void putTotalUpstreamResponseTime(int logSourceId, String url, long startTime,
			int upstreamResponseTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// 第二层
		ConcurrentHashMap<Long, Integer> time_count;// 第三层
		if (!upstreamResponseTimeTotalCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, upstreamResponseTime);
			url_count.put(url, time_count);
			upstreamResponseTimeTotalCache.put(logSourceId, url_count);
		} else {
			url_count = upstreamResponseTimeTotalCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, upstreamResponseTime);
				url_count.put(url, time_count);
				upstreamResponseTimeTotalCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, upstreamResponseTime);
					url_count.put(url, time_count);
					upstreamResponseTimeTotalCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, time_count.get(startTime) + upstreamResponseTime);
						url_count.put(url, time_count);
						upstreamResponseTimeTotalCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putTotalUpstreamResponseTime:", e);
					}
				}
			}
		}
	}

	public static void putMaxUpstreamResponseTime(int logSourceId, String url, long startTime, int upstreamResponseTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// 第二层
		ConcurrentHashMap<Long, Integer> time_count;// 第三层
		if (!upstreamResponseTimeMaxCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, upstreamResponseTime);
			url_count.put(url, time_count);
			upstreamResponseTimeMaxCache.put(logSourceId, url_count);
		} else {
			url_count = upstreamResponseTimeMaxCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, upstreamResponseTime);
				url_count.put(url, time_count);
				upstreamResponseTimeMaxCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, upstreamResponseTime);
					url_count.put(url, time_count);
					upstreamResponseTimeMaxCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, Math.max(time_count.get(startTime), upstreamResponseTime));
						url_count.put(url, time_count);
						upstreamResponseTimeMaxCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putMaxUpstreamResponseTime:", e);
					}
				}
			}
		}
	}

	public static void putOkCount(int logSourceId, String url, long startTime, int status) {
		int flag = status / 100;
		if (flag == 4 || flag == 5) {
			// 如果是4**或5**直接返回
			return;
		}
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// urlTpsCache的第二层
		ConcurrentHashMap<Long, Integer> time_count;// urlTpsCache的第三层
		if (!okCountCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, 1);
			url_count.put(url, time_count);
			okCountCache.put(logSourceId, url_count);
		} else {
			url_count = okCountCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, 1);
				url_count.put(url, time_count);
				okCountCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, 1);
					url_count.put(url, time_count);
					okCountCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, time_count.get(startTime) + 1);
						url_count.put(url, time_count);
						okCountCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putOkCount:", e);
					}
				}
			}
		}
	}

	public static void putError4Count(int logSourceId, String url, long startTime, int status) {
		int flag = status / 100;
		if (flag != 4) {
			// 如果不是4**直接返回
			return;
		}
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// urlTpsCache的第二层
		ConcurrentHashMap<Long, Integer> time_count;// urlTpsCache的第三层
		if (!error4CountCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, 1);
			url_count.put(url, time_count);
			error4CountCache.put(logSourceId, url_count);
		} else {
			url_count = error4CountCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, 1);
				url_count.put(url, time_count);
				error4CountCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, 1);
					url_count.put(url, time_count);
					error4CountCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						time_count.put(startTime, time_count.get(startTime) + 1);
						url_count.put(url, time_count);
						error4CountCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putError4Count:", e);
					}
				}
			}
		}
	}

	public static void putError5Count(int logSourceId, String url, long startTime, int status) {
		int flag = status / 100;
		if (flag != 5) {
			// 如果不是5**直接返回
			return;
		}
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// urlTpsCache的第二层
		ConcurrentHashMap<Long, Integer> time_count;// urlTpsCache的第三层
		if (!error5CountCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, 1);
			url_count.put(url, time_count);
			error5CountCache.put(logSourceId, url_count);
		} else {
			url_count = error5CountCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, 1);
				url_count.put(url, time_count);
				error5CountCache.put(logSourceId, url_count);
			} else {
				// 如果time_count被clear，url_count.get(url)不会是null，而是size()=0
				time_count = url_count.get(url);
				/*
				 * 下面的if-else语句应该是个原子操作，它在操作cache时，是不能被打断去执行clear操作的。
				 * 如果cpu被写线程抢占，执行了writeDB操作，那么这条记录就会无端损失掉，导致数据库查询时少1(被丢弃掉)
				 */
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, 1);
					url_count.put(url, time_count);
					error5CountCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						// time_count被clear后，本身不会是null，time_count.get()是null
						time_count.put(startTime, time_count.get(startTime) + 1);
						url_count.put(url, time_count);
						error5CountCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putError5Count", e);
					}
				}
			}
		}
	}

	public static void putTotalByte(int logSourceId, String url, long startTime, int byteLength) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count;// 第二层
		ConcurrentHashMap<Long, Integer> time_count;// 第三层
		if (!byteTotalCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>();
			time_count = new ConcurrentHashMap<Long, Integer>();
			time_count.put(startTime, byteLength);
			url_count.put(url, time_count);
			byteTotalCache.put(logSourceId, url_count);
		} else {
			url_count = byteTotalCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, Integer>();
				time_count.put(startTime, byteLength);
				url_count.put(url, time_count);
				byteTotalCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					time_count.put(startTime, byteLength);
					url_count.put(url, time_count);
					byteTotalCache.put(logSourceId, url_count);
				} else {
					// startTime 在第三层
					try {
						time_count.put(startTime, time_count.get(startTime) + byteLength);
						url_count.put(url, time_count);
						byteTotalCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putTotalByte:", e);
					}
				}
			}
		}
	}

	public static void putAllRequestTime(int logSourceId, String url, long startTime, int requestTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>> url_count;// 第二层
		ConcurrentHashMap<Long, List<Integer>> time_count;// 第三层
		List<Integer> requestTimeArray;
		if (!allRequestTimeCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>();
			time_count = new ConcurrentHashMap<Long, List<Integer>>();
			requestTimeArray = new ArrayList<Integer>();
			requestTimeArray.add(requestTime);
			time_count.put(startTime, requestTimeArray);
			url_count.put(url, time_count);
			allRequestTimeCache.put(logSourceId, url_count);
		} else {
			url_count = allRequestTimeCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, List<Integer>>();
				requestTimeArray = new ArrayList<Integer>();
				requestTimeArray.add(requestTime);
				time_count.put(startTime, requestTimeArray);
				url_count.put(url, time_count);
				allRequestTimeCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					requestTimeArray = new ArrayList<Integer>();
					requestTimeArray.add(requestTime);
					time_count.put(startTime, requestTimeArray);
					url_count.put(url, time_count);
					allRequestTimeCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						requestTimeArray = time_count.get(startTime);
						requestTimeArray.add(requestTime);
						time_count.put(startTime, requestTimeArray);
						url_count.put(url, time_count);
						allRequestTimeCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putAllRequestTime:", e);
					}
				}
			}
		}
	}

	public static void putAllUpstreamResponseTime(int logSourceId, String url, long startTime, int upstreamResponseTime) {
		ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>> url_count;// 第二层
		ConcurrentHashMap<Long, List<Integer>> time_count;// 第三层
		List<Integer> upstreamResponseTimeArray;
		if (!allUpstreamResponseTimeCache.containsKey(logSourceId)) {
			url_count = new ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>();
			time_count = new ConcurrentHashMap<Long, List<Integer>>();
			upstreamResponseTimeArray = new ArrayList<Integer>();
			upstreamResponseTimeArray.add(upstreamResponseTime);
			time_count.put(startTime, upstreamResponseTimeArray);
			url_count.put(url, time_count);
			allUpstreamResponseTimeCache.put(logSourceId, url_count);
		} else {
			url_count = allUpstreamResponseTimeCache.get(logSourceId);
			if (!url_count.containsKey(url)) {
				// url不在第二层
				time_count = new ConcurrentHashMap<Long, List<Integer>>();
				upstreamResponseTimeArray = new ArrayList<Integer>();
				upstreamResponseTimeArray.add(upstreamResponseTime);
				time_count.put(startTime, upstreamResponseTimeArray);
				url_count.put(url, time_count);
				allUpstreamResponseTimeCache.put(logSourceId, url_count);
			} else {
				time_count = url_count.get(url);
				if (!time_count.containsKey(startTime)) {
					// startTime 不在第三层
					upstreamResponseTimeArray = new ArrayList<Integer>();
					upstreamResponseTimeArray.add(upstreamResponseTime);
					time_count.put(startTime, upstreamResponseTimeArray);
					url_count.put(url, time_count);
					allUpstreamResponseTimeCache.put(logSourceId, url_count);
				} else {
					try {
						// startTime 在第三层
						upstreamResponseTimeArray = time_count.get(startTime);
						upstreamResponseTimeArray.add(upstreamResponseTime);
						time_count.put(startTime, upstreamResponseTimeArray);
						url_count.put(url, time_count);
						allUpstreamResponseTimeCache.put(logSourceId, url_count);
					} catch (Exception e) {
						logger.error("putAllUpstreamResponseTime:", e);
					}
				}
			}
		}
	}

	public static List<NginxAccess> writeResult() {
		List<NginxAccess> NginxAccesses = new ArrayList<NginxAccess>();
		NginxAccess NginxAccess;
		// hashMap <logSourceId_url_startTime, NginxAccess>
		Map<String, NginxAccess> results = new HashMap<String, NginxAccess>();

		// 插入tps的值
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : urlTpsCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int tpsCount = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setTotalCount(tpsCount);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可,time_count.size()=0;
				time_count.clear();
			}
		}
		// 插入totalRequestTime
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : requestTimeTotalCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int totalRequestTime = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setRequestTimeTotal(totalRequestTime);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入MaxRequestTime
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : requestTimeMaxCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int maxRequestTime = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setRequestTimeMax(maxRequestTime);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入upstream_response_time_total
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : upstreamResponseTimeTotalCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int totalUpstreamResponseTime = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setUpstreamResponseTimeTotal(totalUpstreamResponseTime);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入upstream_response_time_max
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : upstreamResponseTimeMaxCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int maxUpstreamResponseTime = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setUpstreamResponseTimeMax(maxUpstreamResponseTime);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入okCount
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : okCountCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int okCount = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setOkCount(okCount);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入error4Count
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : error4CountCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int error4Count = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setError4Count(error4Count);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入error5Count
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : error5CountCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int error5Count = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setError5Count(error5Count);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入byteTotal
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>>> entry : byteTotalCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, Integer>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, Integer>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, Integer> time_count = interEntry.getValue();
				for (Map.Entry<Long, Integer> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					int byteTotal = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setStartTime(startTime);
					NginxAccess.setByteTotal(byteTotal);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}

		// 插入requestTime的90,99值
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>> entry : allRequestTimeCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, List<Integer>>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, List<Integer>> time_count = interEntry.getValue();
				for (Map.Entry<Long, List<Integer>> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					List<Integer> allRequestTime = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					Collections.sort(allRequestTime);
					int size = allRequestTime.size();
					int value90 = allRequestTime.get((int) (size * 90 / 100));
					int value99 = allRequestTime.get((int) (size * 99 / 100));
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setRequestTime90(value90);
					NginxAccess.setRequestTime99(value99);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 插入upstreamResponseTime的90,99值
		for (Map.Entry<Integer, ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>>> entry : allUpstreamResponseTimeCache
				.entrySet()) {
			int logSourceId = entry.getKey();
			ConcurrentHashMap<String, ConcurrentHashMap<Long, List<Integer>>> url_count = entry.getValue();
			for (Map.Entry<String, ConcurrentHashMap<Long, List<Integer>>> interEntry : url_count.entrySet()) {
				String url = interEntry.getKey();
				ConcurrentHashMap<Long, List<Integer>> time_count = interEntry.getValue();
				for (Map.Entry<Long, List<Integer>> inter3Entry : time_count.entrySet()) {
					long startTime = inter3Entry.getKey();
					List<Integer> allUpstreamResponseTime = inter3Entry.getValue();
					String key = logSourceId + "_" + url + "_" + startTime;
					NginxAccess = results.get(key);
					if (NginxAccess == null) {
						NginxAccess = new NginxAccess();
					}
					Collections.sort(allUpstreamResponseTime);
					int size = allUpstreamResponseTime.size();
					int value90 = allUpstreamResponseTime.get((int) (size * 90 / 100));
					int value99 = allUpstreamResponseTime.get((int) (size * 99 / 100));
					NginxAccess.setLogSourceId(logSourceId);
					NginxAccess.setUrl(url);
					NginxAccess.setUpstreamResponseTime90(value90);
					NginxAccess.setUpstreamResponseTime99(value99);
					results.put(key, NginxAccess);
				}
				// 只要clear 第三层嵌套即可
				time_count.clear();
			}
		}
		// 把hashmap中的NginxAccess加入list中
		for (Map.Entry<String, NginxAccess> entry : results.entrySet()) {
			NginxAccesses.add(entry.getValue());
		}
		return NginxAccesses;
	}

}
