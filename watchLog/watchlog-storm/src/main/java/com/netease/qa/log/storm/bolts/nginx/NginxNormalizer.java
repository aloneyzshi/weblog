package com.netease.qa.log.storm.bolts.nginx;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.storm.bean.Record;
import com.netease.qa.log.storm.service.ConfigDataLoadTask;
import com.netease.qa.log.storm.service.ConfigDataService;
import com.netease.qa.log.storm.service.nginx.NormalizerService;
import com.netease.qa.log.storm.util.Const;
import com.netease.qa.log.storm.util.MybatisUtil;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * normalizer 不做qbs验证（效率上考虑）
 * 
 * @author hzquguoqing
 *
 */
public class NginxNormalizer implements IRichBolt {

	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private int readCount = 0;
	private static AtomicLong count;

	private static final Logger logger = LoggerFactory.getLogger(NginxNormalizer.class);

	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context,
			OutputCollector collector) {
		MybatisUtil.init(stormConf.get(Const.MYBATIS_EVN).toString());
		this.collector = collector;
		ExecutorService POOL = Executors.newFixedThreadPool(1);
		POOL.submit(new ConfigDataLoadTask());
		count = new AtomicLong();
		ScheduledExecutorService POOL1 = Executors.newScheduledThreadPool(1);
		POOL1.scheduleWithFixedDelay(new SumTask(), 1, 1, TimeUnit.SECONDS);
	}

	class SumTask implements Runnable {
		@Override
		public void run() {
			logger.info("NginxNormalizer execute and emit msg: " + count.get());
			count.getAndSet(0);
		}
	}

	public void execute(Tuple input) {
		// normalizer the record
		String hostname = input.getString(0);
		String path = input.getString(1);
		String filePattern = input.getString(2);
		String line = input.getString(3);
		NormalizerService ns = new NormalizerService();
		LogSource logsource = ConfigDataService.getLogSource(hostname, path, filePattern);
		logger.debug("logsource: " + logsource);

		if (logsource == null) {
			logger.warn("logsource in DB is null, logsource: " + hostname + " " + path + " " + filePattern);
			return;
		}
		if (logsource.getLogSourceStatus() == 0) {
			logger.warn("logsource is not  monitored, logsource: " + hostname + " " + path + " " + filePattern);
			return;
		}
		int logsourceId = logsource.getLogSourceId();
		String config = logsource.getLogFormat();
		String[] pros = ConfigDataService.getLogFormatProperties(logsourceId);
		String regex = ConfigDataService.getLogFormatRegex(logsourceId);
		try {
			Record record = ns.normalizerInput(line, config, pros, regex);
			record.setLog_source_id(logsourceId);
			ArrayList<Tuple> a = new ArrayList<Tuple>();
			a.add(input);
			int requestTime = (int) (record.getRequest_time() * 1000);
			int upstream_response_time = (int) (record.getUpstream_response_time() * 1000);
			collector.emit(new Values(record.getLog_source_id(), record.getRemote_addr(), record.getTime_local(),
					record.getRequest(), record.getStatus(), record.getBody_bytes_sent(), requestTime,
					upstream_response_time));
			collector.ack(input);
			count.getAndIncrement();
			// readCount ++;
			// if(readCount > 100){
			// logger.info("NginxNormalizer emit 100 msg");
			// readCount = 0;
			// }
			logger.debug("get nginx log: " + logsource.getLogSourceId() + " " + record.getRequest());
		} catch (Exception e) {
			collector.fail(input);
			logger.info("exception:" + e);
			logger.info("line" + line);
			logger.info("logFormat:" + config);
		}
	}

	public void cleanup() {
		// TODO Auto-generated method stub
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("logSourceId", "Remote_addr", "Time_local", "Request", "Status", "Byte_length",
				"Request_time", "Upstream_response_time"));
	}

	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
