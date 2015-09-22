package de.fzi.cep.sepa.storm.sentiment.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import de.fzi.cep.sepa.storm.topology.SepaTopologyBuilder;

public class Main {

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {
		String nimbus_host = "ipe-koi05.fzi.de";
		String storm_zookeeper_host_port = "ipe-koi04.fzi.de:2181";

		StormTopology sentimentDetectionTopology = SepaTopologyBuilder
				.buildSimpleTopology(new SentimentDetectionBolt("sentiment"), storm_zookeeper_host_port);

		// Develop settings
//		 Config conf = new Config();
//		 conf.setDebug(true);
//		 conf.put("bootstrap.servers", "ipe-koi04.fzi.de:9092"); //kafka connection
// 		 conf.put("zookeeper.servers", "ipe-koi04.fzi.de:2181");
//		 LocalCluster cluster = new LocalCluster();
//		 cluster.submitTopology("sentiment-detection", conf,
//		 sentimentDetectionTopology);

		// Production settings
		Config productionConfig = new Config();
		productionConfig.put(Config.NIMBUS_HOST, nimbus_host);
		productionConfig.put(Config.NIMBUS_THRIFT_PORT, 6627);
		productionConfig.put("bootstrap.servers", "ipe-koi04.fzi.de:9092"); //kafka connection
		productionConfig.put("zookeeper.servers", "ipe-koi04.fzi.de:2181");
		productionConfig.setDebug(true);
		System.setProperty("storm.jar", "/home/philipp/Downloads/apache-storm-0.9.5/bin/storm");
		StormSubmitter.submitTopology("sentiment-detection", productionConfig, sentimentDetectionTopology);

//		StormSubmitter.s
	}
}
