package de.fzi.cep.sepa.storm.sentiment.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import de.fzi.cep.sepa.storm.topology.SepaTopologyBuilder;

public class Main {

	public static void main(String[] args)
	{
		Config conf = new Config();
		conf.setDebug(true);
		
		StormTopology sentimentDetectionTopology = SepaTopologyBuilder.buildSimpleTopology(new SentimentDetectionBolt("sentiment"), "kalmar39.fzi.de:2181");
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("sentiment-detection", conf, sentimentDetectionTopology);
	}
}
