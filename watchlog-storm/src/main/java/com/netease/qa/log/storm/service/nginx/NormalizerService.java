package com.netease.qa.log.storm.service.nginx;

import java.text.ParseException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.storm.bean.Record;
import com.netease.qa.log.storm.util.Const;
import com.netease.qa.log.storm.util.MathUtil;
import com.netease.qa.log.storm.util.Regex;

public class NormalizerService {
	
	private static final Logger logger = LoggerFactory.getLogger(NormalizerService.class);
	
	// 根据key-value的hashmap自适应生成record
	public Record normalizerInput(String input, String configuration, String[] pros, String regex) {
//		String[] pros = Regex.getProperty(configuration);
//		String regex = Regex.produceRegex(pros);
		String[] groups = Regex.split(input, regex, configuration);
		Map<String, String> recordMap = Regex.produceRecord(pros, groups);
		Record record = new Record();
		for (Map.Entry<String, String> entry : recordMap.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			setRecordField(record, key, value);
		}
		return record;
	}

	// 根据字段包含的属性，调用具体的set方法
	// 有个坑，有公共前缀的属性，需要把长的放在前面，以免被短的先匹配。
	// 例如：request_time和request,contains("request_time")一定要放在contains("request")前面
	public Record setRecordField(Record record, String str, String value) {
		String temp = str.toLowerCase();
		if (temp.contains("remote") && temp.contains("addr")) {
			record.setRemote_addr(value);
			return record;
		} else if (temp.contains("http_user_agent")) {
			record.setHttp_user_agent(value);
			return record;
		} else if (temp.contains("user")) {
			record.setRemote_user(value);
			return record;
		} else if (temp.contains("time_local")) {
			String[] timeTemp = value.trim().split(" ");
			String[] timeStr = timeTemp[0].trim().split("/");
			String time = timeStr[0] + "/" + MathUtil.Month2int(timeStr[1]) + "/" + timeStr[2];
			Long time1 = 0L;
			try {
				time1 = MathUtil.parse2Long(time);
			} catch (ParseException e) {
				logger.error("ParseException", e);
			}
			record.setTime_local(time1);
			return record;
		} else if (temp.contains("request_time")) {
			try{
				record.setRequest_time(Double.parseDouble(value));
			}catch(Exception e){
				logger.error("ParseException", e);
				record.setRequest_time(Double.parseDouble("0"));
			}
			return record;
		} else if (temp.contains("request")) {
			record.setRequest(value);
			return record;
		} else if (temp.contains("upstream_status")) {
			if (value.trim().equals(Const.REPLACE_MESSAGE))
				value = "0";
			String[] statuses = value.trim().split(",");
			if(statuses.length > 1){
				value = statuses[statuses.length - 1].trim();
			}
			try{
				record.setUpstream_status(Integer.parseInt(value));
			}catch(Exception e){
				logger.error("ParseException", e);
				record.setUpstream_status(Integer.parseInt("200"));
			}
			return record;
		} else if (temp.contains("status")) {
			try{
				record.setStatus(Integer.parseInt(value));
			}catch(Exception e){
				logger.error("ParseException", e);
				record.setStatus(Integer.parseInt("0"));
			}
			return record;
		} else if (temp.contains("body_bytes_sent")) {
			try{
				record.setBody_bytes_sent(Integer.parseInt(value));
			}catch(Exception e){
				logger.error("ParseException", e);
				record.setBody_bytes_sent(Integer.parseInt("0"));
			}
			return record;
		} else if (temp.contains("http_referer")) {
			record.setHttp_referer(value);
			return record;
		} else if (temp.contains("http") && temp.contains("forwarded")) {
			record.setHttp_x_forwarded_for(value);
			return record;
		} else if (temp.contains("host")) {
			record.setHost(value);
			return record;
		} else if (temp.contains("upstream_addr")) {
			record.setUpstream_addr(value);
			return record;
		} else if (temp.contains("upstream_response_time")) {
			if (value.trim().equals(Const.REPLACE_MESSAGE)) {
				value = "0";// 有特殊情况
			}
			String[] times = value.trim().split(",");
			if(times.length > 1){
				int tempTime = 0;
				double tmp = 0;
				for(int i=0;i<times.length;i++){
					try{
						tmp = Double.parseDouble(times[i]);
					}catch(Exception e){
						logger.error("upstream_response_time:" + value);
						logger.error("NumberFormatException", e);
						tmp = 0;
					}
					tempTime += (tmp * 1000);
				}
				double n1 = (double)(Math.round(tempTime)/1000.0);
				value = String.valueOf(n1);
			}
			try{
				record.setUpstream_response_time(Double.parseDouble(value));
			}catch(Exception e){
				logger.error("ParseException", e);
				record.setUpstream_response_time(0);
			}
			return record;
		} else if (temp.contains("server_name")) {
			record.setServer_name(value);
			return record;
		} else {
			return record;
		}
	}
}
