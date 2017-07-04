package com.me.storm.datatopology.bolt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.apache.storm.tuple.Fields;

public class PrintLogBolt extends BaseBasicBolt {

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		try {
			String mesg = input.getString(0);
			if (mesg != null) // 打印数据
				System.out.println("Tuple: " + mesg);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * String line = input.getString(0); line =
		 * line.substring(line.indexOf(":") + 1); JSONParser parser = new
		 * JSONParser();
		 * 
		 * try { JSONObject parseObject = (JSONObject) parser.parse(line);
		 * 
		 * String[] strs = ((String) parseObject.get("req")).split(" "); String
		 * value = "/etc/passwd"; Pattern p = Pattern.compile(value); Matcher m
		 * = p.matcher(strs[1]); if (m.find()) { parseObject.put("abnormal",
		 * "1"); System.out.println("Tuple: " + parseObject.toJSONString());
		 * 
		 * }
		 * 
		 * } catch (ParseException e) { e.printStackTrace(); }
		 */
	}

	private boolean regular(String str, String field, String value) {
		String[] strs = str.split("");

		Pattern p = Pattern.compile(value);
		Matcher m = p.matcher(strs[Integer.parseInt(field) - 1]);
		boolean result = m.matches();

		if (result) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("mesg"));
	}

}
