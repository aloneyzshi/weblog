package com.me.storm.datatopology.bolt;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;

import org.apache.storm.tuple.Tuple;

import com.me.storm.datatopology.util.GlobalDef;;

public class FilterBolt implements IRichBolt {

	// 是否加载配置标志位
	private static boolean flag_load = false;
	
	private long register = 0;

	// 默认参数~~
	String monitorXml = "Filter.xml";
	// 参数判空标志
	private boolean flag_par = true;
	// 匹配条件间的逻辑关系
	String MatchLogic = "AND";
	// !--匹配类型列表
	String MatchType = "regular::range::routine0";
	// !--匹配字段列表-
	String MatchField = "1::2::5";
	// !--字段值列表-
	String FieldValue = ".*baidu.*::1000,2000::ina";

	private OutputCollector collector;

	public FilterBolt(String MonitorXML) {

		if (MonitorXML == null) {
			flag_par = false;
		} else {
			this.monitorXml = MonitorXML;
		}
	}

	@Override
	public void execute(Tuple arg0) {
		// TODO Auto-generated method stub

	}

	// 正则匹配判断
	private boolean regular(String str, String field, String value) {
		String[] strs = str.split(GlobalDef.FLAG_TABS);

		Pattern p = Pattern.compile(value);
		Matcher m = p.matcher(strs[Integer.parseInt(field) - 1]);
		boolean result = m.matches();

		if (result) {
			return true;
		} else {
			return false;
		}
	}

	// 更改标志位
	public static void isload() {
		flag_load = false;
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

	@Override
	public void prepare(Map arg0, TopologyContext arg1, OutputCollector arg2) {
		// TODO Auto-generated method stub

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
