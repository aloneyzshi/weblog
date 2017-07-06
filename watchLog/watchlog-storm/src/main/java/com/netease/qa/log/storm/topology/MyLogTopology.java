package com.netease.qa.log.storm.topology;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.thrift7.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.DRPCExecutionException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

import com.netease.qa.log.storm.bolts.LogAnalyser;
import com.netease.qa.log.storm.bolts.LogFilter;
import com.netease.qa.log.storm.bolts.LogNormalizer;
import com.netease.qa.log.storm.bolts.ResultWriter;
import com.netease.qa.log.storm.bolts.nginx.AnalyzeNginx;
import com.netease.qa.log.storm.bolts.nginx.FilterUrl;
import com.netease.qa.log.storm.bolts.nginx.NginxNormalizer;
import com.netease.qa.log.storm.spouts.MQConsumer;
import com.netease.qa.log.storm.spouts.NginxReader;
import com.netease.qa.log.storm.util.Const;

public class MyLogTopology {

	private static final Logger logger = LoggerFactory.getLogger(MyLogTopology.class);
	
	// 日志类型
	public static String LOG_TYPE;
	// 数据库环境
	public static String MYBATIS_ENV;
	// MQ地质
	public static String MQ_HOST;
	public static int MQ_PORT;
	public static String MQ_QUEUE;
	// storm通用配置
	public static int TOPOLOGY_WORKERS;
	public static int TOPOLOGY_MAX_SPOUT_PENDING;
	public static int TOPOLOGY_ACKER_EXECUTORS;
	public static String TOPOLOGY_NAME;
	// 异常日志topology的配置
	public static int LOG_SPOUT;
	public static int LOG_NORMALIZER_BOLT;
	public static int LOG_FILTER_BOLT;
	public static int LOG_ANALYSER_BOLT;
	public static int RESULT_WRITER_BOLT;
	// nginx日志topology的配置
	public static int NGINX_READER_SPOUT;
	public static int NGINX_NORMALIZER_BOLT;
	public static int NGINX_FILTER_BOLT;
	public static int NGINX_ANALYSER_BOLT;
	//限流参数
	public static int NGINX_LIMIT_NUM;
	public static int NGINX_SLEEP_TIME;
	public static int EXCEPTION_LIMIT_NUM;
	public static int EXCEPTION_SLEEP_TIME;
	
	
	public static void main(String[] args) throws InterruptedException, AlreadyAliveException,
			InvalidTopologyException, TException, DRPCExecutionException {
		// 第一个参数是配置文件路径
		String fileName = args[0];
		if(fileName.isEmpty()){
			logger.error("lose filename!");
			return;
		}
		//读取配置文件
		readConfig(fileName);
		TopologyBuilder builder = new TopologyBuilder();
		if (LOG_TYPE.equals(Const.EXCEPTION_LOG)) {
			builder.setSpout("mq-consumer", new MQConsumer(), LOG_SPOUT);
			builder.setBolt("log-normalizer", new LogNormalizer(), LOG_NORMALIZER_BOLT).shuffleGrouping("mq-consumer");
			builder.setBolt("log-filter", new LogFilter(), LOG_FILTER_BOLT).shuffleGrouping("log-normalizer");
			builder.setBolt("log-analyser", new LogAnalyser(), LOG_ANALYSER_BOLT).shuffleGrouping("log-filter");
			builder.setBolt("result-writer", new ResultWriter(), RESULT_WRITER_BOLT).shuffleGrouping("log-analyser");
		} 
		else if (LOG_TYPE.equals(Const.NGINX_LOG)) {
			builder.setSpout("nginx-reader", new NginxReader(), NGINX_READER_SPOUT);
			builder.setBolt("nginx-normalizer", new NginxNormalizer(), NGINX_NORMALIZER_BOLT).shuffleGrouping("nginx-reader");
			builder.setBolt("nginx-filter", new FilterUrl(), NGINX_FILTER_BOLT).shuffleGrouping("nginx-normalizer");
			builder.setBolt("nginx-analyze", new AnalyzeNginx(), NGINX_ANALYSER_BOLT).shuffleGrouping("nginx-filter");
		}
		else{
			logger.error("wrong log type");
			return;
		}
		// Configuration
		Config conf = new Config();
		conf.setDebug(false);
		conf.put(Const.MQ_HOST, MQ_HOST);
		conf.put(Const.MQ_PORT, MQ_PORT);
		conf.put(Const.MQ_QUEUE, MQ_QUEUE);
		conf.put(Const.MYBATIS_EVN, MYBATIS_ENV);
		
		conf.put(Config.TOPOLOGY_WORKERS, TOPOLOGY_WORKERS);
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, TOPOLOGY_MAX_SPOUT_PENDING);
		conf.put(Config.TOPOLOGY_ACKER_EXECUTORS, TOPOLOGY_ACKER_EXECUTORS);
		
		conf.put(Const.NGINX_LIMIT_NUM, NGINX_LIMIT_NUM);
		conf.put(Const.NGINX_SLEEP_TIME, NGINX_SLEEP_TIME);
		conf.put(Const.EXCEPTION_LIMIT_NUM, EXCEPTION_LIMIT_NUM);
		conf.put(Const.EXCEPTION_SLEEP_TIME, EXCEPTION_SLEEP_TIME);
		
		StormSubmitter.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());
		
//		LocalCluster cluster = new LocalCluster();
//		cluster.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());
//		Thread.sleep(10000000);
	}

	
	private static void readConfig(String fileName) {
		Properties properties = new Properties();
		InputStream is = null;
		Reader reader = null;
		File file = new File(fileName);
		try {
			is = new FileInputStream(file);
			try {
				reader = new InputStreamReader(is, "UTF-8");
				properties.load(reader);
			} catch (UnsupportedEncodingException e) {
				logger.error("error", e);
			} catch (IOException e) {
				logger.error("error", e);
			}
		} catch (FileNotFoundException e1) {
			logger.error("error", e1);
		}
		LOG_TYPE = properties.getProperty("log.type");
		MYBATIS_ENV = properties.getProperty("mybatis.env");
		MQ_HOST = properties.getProperty("mq.host");
		MQ_PORT = Integer.valueOf(properties.getProperty("mq.port"));
		MQ_QUEUE = properties.getProperty("mq.queue");

		TOPOLOGY_WORKERS = Integer.valueOf(properties.getProperty("topology_workers"));
		TOPOLOGY_MAX_SPOUT_PENDING = Integer.valueOf(properties.getProperty("topology_max_spout_pending"));
		TOPOLOGY_ACKER_EXECUTORS = Integer.valueOf(properties.getProperty("topology_acker_executors"));
		TOPOLOGY_NAME = properties.getProperty("topology.name");

		LOG_SPOUT = Integer.valueOf(properties.getProperty("mq_consumer.spout"));
		LOG_NORMALIZER_BOLT = Integer.valueOf(properties.getProperty("log_normalizer.bolt"));
		LOG_FILTER_BOLT = Integer.valueOf(properties.getProperty("log_filter.bolt"));
		LOG_ANALYSER_BOLT = Integer.valueOf(properties.getProperty("log_analyser.bolt"));
		RESULT_WRITER_BOLT = Integer.valueOf(properties.getProperty("result_writer.bolt"));

		NGINX_READER_SPOUT = Integer.valueOf(properties.getProperty("nginx_reader.spout"));
		NGINX_NORMALIZER_BOLT = Integer.valueOf(properties.getProperty("nginx_normalizer.bolt"));
		NGINX_FILTER_BOLT = Integer.valueOf(properties.getProperty("nginx_filter.bolt"));
		NGINX_ANALYSER_BOLT = Integer.valueOf(properties.getProperty("nginx_analyze.bolt"));
		
		NGINX_LIMIT_NUM = Integer.valueOf(properties.getProperty("nginx_limit_num"));
		NGINX_SLEEP_TIME = Integer.valueOf(properties.getProperty("nginx_sleep_time"));
		EXCEPTION_LIMIT_NUM = Integer.valueOf(properties.getProperty("exception_limit_num"));
		EXCEPTION_SLEEP_TIME = Integer.valueOf(properties.getProperty("exception_sleep_time"));
	}
	
}
