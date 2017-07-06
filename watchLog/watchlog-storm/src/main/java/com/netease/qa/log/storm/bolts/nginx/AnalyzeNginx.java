package com.netease.qa.log.storm.bolts.nginx;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import com.netease.qa.log.storm.service.MonitorDataWriteNginxTask;
import com.netease.qa.log.storm.service.nginx.AnalyzeService;
import com.netease.qa.log.storm.util.MathUtil;
import com.netease.qa.log.storm.util.MybatisUtil;

public class AnalyzeNginx implements IRichBolt {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(AnalyzeNginx.class);
	private static AtomicLong count;
	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		try {
			Thread.sleep(100);
		}
		catch (InterruptedException e) {
			logger.error("error", e);
		} 
		MybatisUtil.init(stormConf.get(com.netease.qa.log.storm.util.Const.MYBATIS_EVN).toString());
		ExecutorService POOL = Executors.newFixedThreadPool(1);
		POOL.submit(new MonitorDataWriteNginxTask());
		count = new AtomicLong();
		ScheduledExecutorService POOL1 = Executors.newScheduledThreadPool(1);
		POOL1.scheduleWithFixedDelay(new SumTask(), 1, 1, TimeUnit.SECONDS);
	}
	
	class SumTask implements Runnable {
		@Override
		public void run() {
			logger.info("AnalyzeNginx execute and emit msg: " + count.get());
			count.getAndSet(0);
		}
	}

	public void execute(Tuple input) {
		int logSourceId = (Integer)input.getValue(0);
		long time = (Long) input.getValue(2);
		String url = (String) input.getValue(3);
		int status = (Integer) input.getValue(4);
		int byteLength = (Integer)input.getValue(5);
		int requestTime = (Integer) input.getValue(6);
		int upstreamResponseTime = (Integer)input.getValue(7);
		long startTime = MathUtil.getStartTime(time);
		logger.debug("get nginx log: :" + logSourceId + ", url:" + url);
		AnalyzeService.putUrlTps(logSourceId, url, startTime);
		AnalyzeService.putTotalRequestTime(logSourceId, url, startTime, requestTime);
		AnalyzeService.putMaxRequestTime(logSourceId, url, startTime, requestTime);
		AnalyzeService.putTotalUpstreamResponseTime(logSourceId, url, startTime, upstreamResponseTime);
		AnalyzeService.putMaxUpstreamResponseTime(logSourceId, url, startTime, upstreamResponseTime);
		AnalyzeService.putOkCount(logSourceId, url, startTime, status);
		AnalyzeService.putError4Count(logSourceId, url, startTime, status);
		AnalyzeService.putError5Count(logSourceId, url, startTime, status);
		AnalyzeService.putTotalByte(logSourceId, url, startTime, byteLength);
		AnalyzeService.putAllRequestTime(logSourceId, url, startTime, requestTime);
		AnalyzeService.putAllUpstreamResponseTime(logSourceId, url, startTime, upstreamResponseTime);
		count.getAndIncrement();
//		collector.ack(input);
	}

	public void cleanup() {
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
