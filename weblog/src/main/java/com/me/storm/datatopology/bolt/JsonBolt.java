package com.me.storm.datatopology.bolt;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.tuple.Tuple;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonBolt extends BaseRichBolt {

	private OutputCollector collector;

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		String line = input.getString(0);
		line = line.substring(line.indexOf(":"));
		// Map<String, Object> map = (Map<String, Object>)
		// JSONValue.parse(line);
		JSONParser parser = new JSONParser();
		try {
			JSONObject parseObject = (JSONObject) parser.parse(line);
			System.out.println("---->" + parseObject.toJSONString());
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub
		this.collector = collector;

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub

	}

}
