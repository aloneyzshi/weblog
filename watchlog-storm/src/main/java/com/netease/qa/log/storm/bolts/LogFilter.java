package com.netease.qa.log.storm.bolts;

import java.util.ArrayList;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import com.netease.qa.log.meta.LogSource;
import com.netease.qa.log.storm.service.ConfigDataService;
import com.netease.qa.log.storm.util.MybatisUtil;
import com.netease.qa.log.util.Const;

public class LogFilter implements IBasicBolt {


	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(LogFilter.class);
	
	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext paramTopologyContext) {
		MybatisUtil.init(stormConf.get(com.netease.qa.log.storm.util.Const.MYBATIS_EVN).toString());
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String line = input.getString(0);

		LogSource logSource = ConfigDataService.getLogSource(input.getInteger(1));
		String keywordStr = logSource.getLineFilterKeyword();
		// 未指定过滤关键字， 不需要过滤
		if (keywordStr.trim().equals(Const.FILTER_KEYWORD_NONE)) {
			collector.emit(new Values(line, input.getValue(1), input.getValue(2), input.getValue(3)));
		}
		// 需要过滤
		else {
			ArrayList<String> keywords = logSource.getLineFilterKeywords();
			String condition = logSource.getLineFilterKeywordsCondition();
			// OR 关键字过滤
			if (condition.equals(Const.FILTER_KEYWORD_OR)) {
				for (String keyword : keywords) {
					if (line.indexOf(keyword.trim()) != -1) {
						collector.emit(new Values(line, input.getValue(1), input.getValue(2), input.getValue(3)));
						logger.info("or get! " + keyword + "logsource: " + logSource.getLogSourceName() + ", " + line);
						break;
					}
				}
			}
			// AND关键字过滤
			else {
				boolean flag = true;
				for (String keyword : keywords) {
					if (line.indexOf(keyword.trim()) == -1) {
						flag = false;
						break;
					}
				}
				if (flag) {
					collector.emit(new Values(line, input.getValue(1), input.getValue(2), input.getValue(3)));
					logger.info("and get! " + "logsource: " + logSource.getLogSourceName() + ", " + line);
				}
			}
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
	public void cleanup() {
		 
	}
	
}
