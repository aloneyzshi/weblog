package com.netease.qa.log.meta.dao;

import java.util.List;

import com.netease.qa.log.meta.NginxAccess;


public interface NginxAccessDao {
	/*
	 * 插入数据
	 */
	public int insert(NginxAccess nginxAccess);
	/*
	 * 获取实时和离线top N数据
	 */
	public List<String> getTopUrl(int logSourceId, long start, long end, int topN, String sort);
	/*
	 * 获取Top N的统计数据
	 */
	public List<NginxAccess> getTopAllData(int logSourceId, long start, long end, int topN, String sort);
	/*
	 * 获取统计数据具体url的总数
	 */
	public int getTotalNumByUrl(int logSourceId, String url, long start, long end);
	/*
	 * 获取统计数据具体url的90值或99值
	 * field 表示求90或99值，offset是第多少个，即90值的位置
	 */
	public int getValue(String field, int logSourceId, String url, long start, long end, int offset);
	/*
	 * 获取实时曲线数据，用户每30s发一次请求，只会返回一条数据,时间区间比较大，可以返回多条记录，
	 * 多记录之和
	 */
	public NginxAccess getRealTimeData(int logSourceId, String url, long start, long end);
	/*
	 * 获取没有url情况下的实时曲线数据，用户每30s发一次请求，只会返回一条数据,没有url返回的是所有该时间段内所有url的tps等之和
	 */
	public NginxAccess getAllRealTimeData(int logSourceId, long start, long end);
	
}
