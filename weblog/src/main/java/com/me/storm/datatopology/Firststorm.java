package com.me.storm.datatopology;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import com.me.storm.datatopology.spout.ReadLogsSpout;
import com.me.storm.datatopology.bolt.PrintLogBolt;
public class Firststorm { 

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TopologyBuilder builder = new TopologyBuilder();

		
		Config conf = new Config();
		conf.setDebug(false);
		
		builder.setSpout("spout", new ReadLogsSpout());
//		builder.setBolt("bolt", new SenqueceBolt()).shuffleGrouping("spout");

		
		
		 builder.setBolt("print", new PrintLogBolt(),1).shuffleGrouping("spout");

		 
		 
	

		if (args != null && args.length > 0) {
			conf.setNumWorkers(3);
			try {

				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				// TODO: handle exception
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			} catch (AuthorizationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("firststorm", conf, builder.createTopology());
	//		Utils.sleep(30000);
	//		cluster.killTopology("firststorm");
	//		cluster.shutdown();
		 
		 
	
			

		}
	}

}
