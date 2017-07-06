package com.netease.qa.log.storm.bolts;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.IBasicBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class ResultWriter implements IBasicBolt {


	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(ResultWriter.class);

	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {}


	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String line = input.getString(0);
		logger.debug(line);
	}


	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}


	@Override
	public void cleanup() {
		
	}


	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map paramMap, TopologyContext paramTopologyContext) {
		
	}
	
}
