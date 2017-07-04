package com.netease.qa.log.storm.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netease.qa.log.storm.bolts.nginx.NginxNormalizer;

/*
 * 思路：对format格式，进行空格分割，生成响应的属性数组。循环属性数组中每一个属性，不包含$值，表示该属性无效。
 * 包含$值的属性，计算出$的位置。如果$的位置不在第一位，表示$前面有其他字符(并且默认这些其他字符是成对出现，即[$remote_addr]),
 * 去掉该属性的首尾。produceRegex根据属性数组，生成每一个属性的匹配表达式，并拼接。到此根据format，自适应生成的正则表达式完成。
 */
public class Regex {
	private static final Logger logger = LoggerFactory.getLogger(Regex.class);
	
	public static void main(String[] avgs) {
		// sample1，xiaowu
		String pro = "$remote_addr - $remote_user [$time_local] \"$request\" "
				+ "$status $body_bytes_sent \"$http_referer\" "
				+ "\"$http_user_agent\" \"$http_x_forwarded_for\" $request_time "
				+ "\"$host\" \"$upstream_addr\" \"$upstream_status\" \"$upstream_response_time\"";
		String str = "10.110.10.119:27971 - - [04/Aug/2015:12:30:12 +0800] \"GET /services/checkUsername?username=b05031025@163.com&product=urs HTTP/1.1\" 200 34 \"-\" "
				+ "\"curl/7.19.7 (x86_64-redhat-linux-gnu) libcurl/7.19.7 NSS/3.14.0.0 zlib/1.2.3 libidn/1.18 libssh2/1.4.2\" \"-\" 0.003 \"reg.163.com\" \"10.110.10.49:36901\" "
				+ "\"200\" \"0.003\"";
		// sample2,邮件上
		String pro2 = "$remote_addr$remote_port  -  $remote_user   \"[-$name-]\" [$time_local]  \"$request\"  "
				+ "$status  $body_bytes_sent  \"$http_referer\"  \"X\"  "
				+ "$server_name  \"$http_user_agent\"  \"$request_time\"  \"-X-\"  \"$upstream_addr\"  \"$upstream_response_time\"";
		String str2 = "123.58.164.244:8080  -  - \"[-quguoqing-]\"  [02/Sep/2015:00:14:28 +0800]  \"GET /services/checkMobToken?userip=223.252.222.243 HTTP/1.0\" "
				+ " 200  126  \"-\"  \"X\"  reg.163.com \"Python-urllib/1.17\" \"0.037\" \"-X-\" \"10.110.10.49:36901\" \"0.004\"";
		
		String[] pros = Regex.getProperty(pro2);
		for (int i = 0; i < pros.length; i++) {
			System.out.println("pros[" + i + "]:" + pros[i]);
		}
		String regexStr = Regex.produceRegex(pros);
		System.out.println("regexStr:" + regexStr);
		String[] groups = Regex.split(str2, regexStr, pro2);
		for (int i = 0; i < groups.length; i++) {
			System.out.println("groups[" + i + "]:" + groups[i]);
		}
		Map<String, String> record = Regex.produceRecord(pros, groups);
		if(record == null){
			System.out.println("log_format is error, please input right log_format");
		}
		for (Map.Entry<String, String> entry : record.entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}

	public static String[] getProperty(String properties) {
		//使用空格(一个或多个)进行分割
		String[] pros = properties.split("\\s+");
		return pros;
	}

	// 动态生成，匹配表达式
	public static String produceRegex(String[] strs) {
		String regex = "";
		String start;
		String end;
		for (int i = 0; i < strs.length; i++) {

			if (strs[i].contains("$")) {
				if (i == 0) {
					end = endMark(strs[i]);
					regex = "(.*?\\s*)";
				} else {
					start = startMark(strs[i]);
					end = endMark(strs[i]);
					if (start.equals("\"")) {
						regex = regex + "(\\s+)" + "(\"[^\"]*\")";
					} else {
						regex = regex + "(\\s+)" + "(" + start + ".*?" + end + ")";
					}
				}
			} else {
				regex = regex + "(\\s+)" + "(" + strs[i] + ")";
			}
		}
		return regex;
	}

	public static String startMark(String str) {
		String startMark = "";
		if (!str.startsWith("$")) {
			startMark = str.substring(0, 1);
		}
		if (startMark.equals("[")) {
			startMark = "\\[";
		}
		return startMark;
	}

	public static String endMark(String str) {
		String endMark = "\\s*";
		if (!str.startsWith("$")) {
			// 一般$前后都是成对出现
			endMark = str.substring(str.length() - 1, str.length());
		}
		if (endMark.equals("]")) {
			endMark = "\\]";
		}
		return endMark;
	}

	public static String[] split(String input, String regex, String config) {
		Pattern pattern = Pattern.compile(regex);
		Matcher m = pattern.matcher(input);
		int num = m.groupCount();
		Double length = (double) num / 2.0;
		String[] groups = new String[(int) Math.ceil(length)];
		String temp = "";
		if (m.find()) {
			int j = 0;
			for (int i = 1; i <= num; i++) {
				temp = m.group(i);
				if (!temp.trim().equals("")) {
					groups[j] = temp;
					j++;
				}
			}
		}
		else{
			logger.info("cant match!!!!!!!!!!!!!!!");
			logger.info("log_format:" + config);
			logger.info("input: " + input);
			logger.info("regex: " + regex);
		}
		return groups;
	}

	// 生成一个key-value的record记录，key是属性名称，value是该属性的值
	public static Map<String, String> produceRecord(String[] properties, String[] groups) {
		Map<String, String> record = new HashMap<String, String>();
		if (properties.length != groups.length) {
			return null;
		}
		for (int i = 0; i < properties.length; i++) {
			String keyTmp = properties[i];
			String valueTmp = groups[i];
			// 属性没有$，则表示该属性无效，跳过
			if (keyTmp.contains("$")) {
				// 返回$的位置
				int index = keyTmp.indexOf("$");
				String key = keyTmp.substring(index + 1, keyTmp.length() - index);
				String value = valueTmp.substring(index, valueTmp.length() - index);
				record.put(key, value);
			}
		}
		return record;
	}
	
	public static String initMQinput(String input){
		String str = input.substring(1, input.length() - 1) + "";
		//匹配反斜杠，替换为空串
		String realStr = str.replaceAll("\\\\", "");
		return realStr;
	} 
}
