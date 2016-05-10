package de.fzi.cep.sepa.storm.topology;

import java.util.UUID;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringKeyValueScheme;
import storm.kafka.ZkHosts;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.TopologyBuilder;

public class SepaTopologyBuilder {

	public static StormTopology buildSimpleTopology(IRichBolt bolt, String zookeeperUrl)
	{
		  TopologyBuilder builder = new TopologyBuilder();
		  
		  
		  String topicName = "SEPA.SEP.Twitter.Sample";
		  BrokerHosts hosts = new ZkHosts("ipe-koi04.fzi.de:2181");
		  SpoutConfig spoutConfig = new SpoutConfig(hosts, topicName, "/", UUID.randomUUID().toString());
		  spoutConfig.scheme = new SchemeAsMultiScheme(new StringKeyValueScheme());
		  KafkaSpout kafkaSpout = new KafkaSpout(spoutConfig);
		  
		  
		  
		  
		  
		  
//		  SepaSpout sepaSpout = new SepaSpout("sepaspout", zookeeperUrl);
//		  SinkSepaBolt<? extends BindingParameters> sinkSepaBolt = new SinkSepaBolt<>("sinkbolt");
//		  
//		  builder.setSpout(sepaSpout.getId(), sepaSpout);
//		  builder.setBolt("sentiment", bolt)
//		  	.shuffleGrouping(sepaSpout.getId(), SepaSpout.SEPA_DATA_STREAM);
////		  	.shuffleGrouping(sepaSpout.getId());
//		  builder.setBolt(sinkSepaBolt.getId(), sinkSepaBolt)
//		  	.shuffleGrouping("sentiment", SepaSpout.SEPA_DATA_STREAM);
		  
		  return builder.createTopology();		  
	}

}
