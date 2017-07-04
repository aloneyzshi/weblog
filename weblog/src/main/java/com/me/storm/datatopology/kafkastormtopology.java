package com.me.storm.datatopology;

import java.util.Arrays;

import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;

import com.me.storm.datatopology.bolt.*;

import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;

import org.apache.storm.LocalCluster;

public class kafkastormtopology {

	public static void main(String[] args)
			throws InterruptedException, AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		// TODO Auto-generated method stub
		/*
		 * TopologyBuilder builder = new TopologyBuilder();
		 * builder.setSpout("kafka-reader", new KafkaSpout(spoutConfig), 1);
		 * builder.setBolt("filter-bolt", new FilterBolt(),
		 * 1).shuffleGrouping("kafka-reader"); builder.setBolt("input-line", new
		 * TransferBolt(), 1).shuffleGrouping("reader-input"); Config config =
		 * new Config(); String name = KafkaTopology.class.getSimpleName();
		 * config.setNumWorkers(PropertiesUtil.getInt(KafkaProperties.
		 * NUM_WORKERS)); StormSubmitter.submitTopologyWithProgressBar(name,
		 * config, builder.createTopology());
		 */
		String zks = "10.82.57.12:2182";
		String topic = "storm-data-topic";
		String zkPath = "/kafka/brokers/topics";
		String id = "storm-spout";

		BrokerHosts brokerHosts = new ZkHosts(zks, zkPath);
		SpoutConfig spoutConf = new SpoutConfig(brokerHosts, topic, "/" + topic, id);
		spoutConf.scheme = new SchemeAsMultiScheme(new StringScheme());
		spoutConf.zkServers = Arrays.asList(new String[] { zks });
		spoutConf.zkPort = 2182;

		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("kafkaspout", new KafkaSpout(spoutConf));
		builder.setBolt("PrintLogBolt", new PrintLogBolt(), 1).shuffleGrouping("kafkaspout");

		Config config = new Config();

		if (args != null && args.length > 0) {
			config.setNumWorkers(1);
			StormSubmitter.submitTopologyWithProgressBar(args[0], config, builder.createTopology());
		} else {
			// 这里是本地模式下运行的启动代码。
			config.setMaxTaskParallelism(1);
			LocalCluster cluster = new LocalCluster();
			StormSubmitter.submitTopologyWithProgressBar("kafkatopology", config, builder.createTopology());
		}
	}

}
