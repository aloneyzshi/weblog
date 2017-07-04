package com.netease.qa.log.storm.bolts;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import com.netease.qa.log.meta.Exception;
import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.storm.service.ConfigDataService;
import com.netease.qa.log.storm.service.MonitorDataService;
import com.netease.qa.log.storm.service.MonitorDataWriteTask;
import com.netease.qa.log.storm.util.MybatisUtil;
import com.netease.qa.log.util.Const;
import com.netease.qa.log.util.MD5Utils;

public class LogAnalyser implements IBasicBolt {


	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(LogAnalyser.class);

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		
		String line = input.getString(0);
		LogSource logSource = ConfigDataService.getLogSource(input.getInteger(1));
		Long dsTime = Long.valueOf(input.getString(3));
		 
		int logSourceId = logSource.getLogSourceId();
		ArrayList<String> lineTypeRegexs = logSource.getLineTypeRegexs();

		//TODO 观察性能问题
		HashSet<String> exceptionTypes = new HashSet<String>(); //支持一条日志属于多个type
		for(String lineTypeRegex : lineTypeRegexs){
			try{
				Pattern p = Pattern.compile(lineTypeRegex); 
				Matcher m = p.matcher(line);  
				if(m.find()){
					logger.debug("match! " + m.group() + ", logSource: " + logSource.getLogSourceName());
					exceptionTypes.add(m.group());
				}
			}
			catch(PatternSyntaxException e){
				logger.error("Pattern compile error, regex: " + lineTypeRegex + ", logSource: " + logSource.getLogSourceName() + ", line: " + line, e);
			}
		}
		//日志没有匹配到任何异常类型，设置为unknown类型
		if(exceptionTypes.size() == 0){
			exceptionTypes.add(Const.UNKNOWN_TYPE);
			logger.debug("cant match! set as unknown, logSource: " + logSource.getLogSourceName() + ", line: " + line);
		}
			
		// 查询exception缓存，如果不存在则插入exception表
		for(String exceptionType : exceptionTypes){
			String exceptionTypeMD5 = MD5Utils.getMD5(exceptionType);
			Exception exception = MonitorDataService.getException(logSourceId, exceptionTypeMD5);
			int exceptionId;
			if (exception != null) {
				logger.debug("get exception " + exception);
				exceptionId = exception.getExceptionId();
			}
			else {
				logger.debug("first get! exceptionType: " + exceptionType + ", logSourceId: " + logSourceId);
				exceptionId = MonitorDataService.putException(logSourceId, exceptionTypeMD5, exceptionType, line);
			}
			// unknown类型异常记录原始日志
			if(exceptionType.equals(Const.UNKNOWN_TYPE)){
				if(line.length() >= 500)
					MonitorDataService.putUkExceptionData(logSourceId, dsTime / 1000, line.substring(0, 499));
				else
					MonitorDataService.putUkExceptionData(logSourceId, dsTime / 1000, line);
			}
			
			
			// 记录异常类型和数量
			MonitorDataService.putExceptionData(logSourceId, exceptionId, dsTime / 1000);
		}
	
	}
	
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("line", "logsource", "project", "dsTime"));
	}


	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}


	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext paramTopologyContext) {
		MybatisUtil.init(stormConf.get(com.netease.qa.log.storm.util.Const.MYBATIS_EVN).toString());
		try {
			Thread.sleep(1000);
		}
		catch (InterruptedException e) {
			logger.error("error", e);
		} 
		ExecutorService POOL = Executors.newFixedThreadPool(1);
		POOL.submit(new MonitorDataWriteTask());
	}


	@Override
	public void cleanup() {
		
	}
	
}
