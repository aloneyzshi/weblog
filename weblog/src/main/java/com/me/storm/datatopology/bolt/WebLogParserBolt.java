package com.me.storm.datatopology.bolt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Tuple;

public class WebLogParserBolt implements IRichBolt{
	private Pattern pattern;

	private OutputCollector collector;

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void execute(Tuple input) {
		// TODO Auto-generated method stub
		String webLog = input.getStringByField("str");

		// 解析
		if (webLog != null || !"".equals(webLog)) {

			Matcher matcher = pattern.matcher(webLog);
			if (matcher.find()) {
				// matcher.group(0);
				String ip = matcher.group(1);
				String serverTimeStr = matcher.group(2);

				// 处理时间
				long timestamp = Long.parseLong(serverTimeStr);
				Date date = new Date();
				date.setTime(timestamp);

				DateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
				String dateStr = df.format(date);
				String day = dateStr.substring(0, 8);
				String hour = dateStr.substring(0, 10);
				String minute = dateStr;

				String requestUrl = matcher.group(3);
				String httpRefer = matcher.group(4);
				String userAgent = matcher.group(5);

				// 分流
				// this.collector.emit(IP_COUNT_STREAM, input, new Values(day,
				// hour, minute, ip));
				// this.collector.emit(URL_PARSER_STREAM, input, new Values(day,
				// hour, minute, requestUrl));
				// this.collector.emit(HTTPREFER_PARSER_STREAM, input, new
				// Values(day, hour, minute, httpRefer));
				// this.collector.emit(USERAGENT_PARSER_STREAM, input, new
				// Values(day, hour, minute, userAgent));

			}
		}

		this.collector.ack(input);
	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub
		pattern = Pattern.compile(
				"([^ ]*) [^ ]* [^ ]* \\[([\\d+]*)\\] \\\"[^ ]* ([^ ]*) [^ ]*\\\" \\d{3} \\d+ \\\"([^\"]*)\\\" \\\"([^\"]*)\\\" \\\"[^ ]*\\\"");
		this.collector = collector;

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
