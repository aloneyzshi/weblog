package com.me.storm.datatopology.bolt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		/*
		 * try { String mesg = input.getString(0); if (mesg != null) // 打印数据
		 * System.out.println("Tuple: " + mesg); } catch (Exception e) {
		 * e.printStackTrace(); }
		 * 
		 */

		String line = input.getString(0);

		System.out.println("Tuple: " + line);

		String reg = "([^ ]*) ([^ ]*) ([^ ]*) (\\[.*\\]) (\".*?\") (-|[0-9]*) (-|[0-9]*) (\".*?\") (\".*?\")";
		Pattern pattern = Pattern.compile(reg);
		// Matcher matcher2 = pattern2.matcher(line);
		Matcher matcher = pattern.matcher(line);

		while (matcher.find()) {
			 
			// matcher.group(0);
			String ip = matcher.group(1);
			String serverTimeStr = matcher.group(2);
			/*
			 * // 处理时间 long timestamp = Long.parseLong(serverTimeStr); Date date
			 * = new Date(); date.setTime(timestamp);
			 * 
			 * DateFormat df = new SimpleDateFormat("yyyyMMddHHmm"); String
			 * dateStr = df.format(date); String day = dateStr.substring(0, 8);
			 * String hour = dateStr.substring(0, 10); String minute = dateStr;
			 */
			String requestUrl = matcher.group(3);
			String httpRefer = matcher.group(4);
			String userAgent = matcher.group(5);

			// System.out.print(dateStr);
			for (int i = 0; i <= matcher.groupCount(); i++) {
				System.out.println(i+" : "+matcher.group(i));
			}

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
