package de.fzi.cep.sepa.storm.sentiment.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import de.fzi.cep.sepa.storm.topology.SepaTopologyBuilder;

public class Main {

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException
	{
		Config conf = new Config();
		conf.setDebug(true);
		
		StormTopology sentimentDetectionTopology = SepaTopologyBuilder.buildSimpleTopology(new SentimentDetectionBolt("sentiment"), "ipe-koi04.fzi.de:2181");
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("sentiment-detection", conf, sentimentDetectionTopology);
		
		// Production settings
		//Config productionConfig = new Config();
		
		//StormSubmitter.submitTopology("sentiment-detection", productionConfig, sentimentDetectionTopology);
		
	}
}
