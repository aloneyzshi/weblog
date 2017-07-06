package com.netease.qa.log.service;

import com.alibaba.fastjson.JSONObject;

public interface NginxAccessService {
	
	public JSONObject getTopNUrl(int logSourceId, long start, long end, int topN, String sort);
	
	public JSONObject getTopAllData(int logSourceId, long start, long end, int topN, String sort);
	
	/*
	 * 返回具体url的实时数据
	 */
	public JSONObject getRealSingleData(int logSourceId, String url, long start, long end);
	/*
	 * 返回没有url情况下的全部数据（全部tps,error等）
	 */
	public JSONObject getAllRealSingleData(int logSourceId, long start, long end);
	
	/*
	 * 获取具体url离线曲线数据，自适应300个点，容许一点点误差
	 */
	public JSONObject getOfflineAllData(int logSourceId, String url, long start, long end);
	/*
	 * 获取没有url离线曲线数据，自适应300个点，容许一点点误差
	 */
	public JSONObject getOfflineAllDataWithoutUrl(int logSourceId, long start, long end);
	}
