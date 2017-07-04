package com.netease.qa.log.storm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MathUtil {

	// 标准化时间，获取long型时间戳
	public static Long parse2Long(String time) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
		Date date = format.parse(time);
		return date.getTime() / 1000;
	}

	// 获取string时间
	public static String parse2String(long time) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy:HH:mm:ss");
		return format.format(time);
	}

	public static int Month2int(String month) {
		int m = -1;
		String monthstr = month.toLowerCase();
		if (monthstr.startsWith("jan")) {
			m = 1;
		} else if (monthstr.startsWith("feb")) {
			m = 2;
		} else if (monthstr.startsWith("mar")) {
			m = 3;
		} else if (monthstr.startsWith("apr")) {
			m = 4;
		} else if (monthstr.startsWith("may")) {
			m = 5;
		} else if (monthstr.startsWith("jun")) {
			m = 6;
		} else if (monthstr.startsWith("jul")) {
			m = 7;
		} else if (monthstr.startsWith("aug")) {
			m = 8;
		} else if (monthstr.startsWith("sep")) {
			m = 9;
		} else if (monthstr.startsWith("oct")) {
			m = 10;
		} else if (monthstr.startsWith("nov")) {
			m = 11;
		} else if (monthstr.startsWith("dec")) {
			m = 12;
		}
		return m;
	}

	public static long getStartTime(long time) {
		long firstTime = 0L;
		firstTime = 1436238525L;//time=07/7/2015:11:08:45
		long div = time - firstTime;
		int time_interval = Const.STORM_TIME_INTERVAL;
		long startTime = firstTime + (div / time_interval) * time_interval;
		return startTime;
	}

	public static String getBeanField(String str) {
		String temp = str.toLowerCase();
		if (temp.contains("remote") && temp.contains("addr")) {
			return Const.REMOTE_ADDR;
		} else if (temp.contains("user")) {
			return Const.REMOTE_USER;
		} else if (temp.contains("time")) {
			return Const.TIME_LOCAL;
		} else if (temp.contains("request")) {
			return Const.REQUEST;
		} else if (temp.contains("status")) {
			return Const.STATUS;
		} else if (temp.contains("body_bytes_sent")) {
			return Const.BODY_BYTES_SENT;
		} else if (temp.contains("http_referer")) {
			return Const.HTTP_REFERER;
		} else if (temp.contains("http_user_agent")) {
			return Const.HTTP_SUER_AGENT;
		} else if (temp.contains("http") && temp.contains("forwarded")) {
			return Const.HTTP_X_FORWARDED_FOR;
		} else if (temp.contains("request_time")) {
			return Const.REQUEST_TIME;
		} else if (temp.contains("host")) {
			return Const.HOST;
		} else if (temp.contains("upstream_addr")) {
			return Const.UPSTREAM_ADDR;
		} else if (temp.contains("upstream_status")) {
			return Const.UPSTREAM_STATUS;
		} else if (temp.contains("upstream_response_time")) {
			return Const.UPSTREAM_RESPONSE_TIME;
		} else {
			return null;
		}
	}

}
