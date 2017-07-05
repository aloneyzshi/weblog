package com.me.storm.datatopology.spout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Values;

import org.apache.storm.tuple.Fields;
 

 

public class ReadLogsSpout implements IRichSpout {

	private SpoutOutputCollector collector;
	FileInputStream fis;
	InputStreamReader isr;
	BufferedReader br;

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		// TODO Auto-generated method stub

		this.collector = collector;

	//	String file = "D://haproxy_overwatch_10_160_81_13.log-20170514-1494714303";
		String file = "D://2.txt";
		try {

			this.fis = new FileInputStream(file);
			this.isr = new InputStreamReader(fis, "UTF-8");
			this.br = new BufferedReader(isr);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub

	}

	@Override
	public void nextTuple() {
		// TODO Auto-generated method stub
		String str = "";
		try {
			while ((str = this.br.readLine()) != null) {
				this.collector.emit(new Values(str));
				Thread.sleep(100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void ack(Object msgId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fail(Object msgId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("str"));

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

}
