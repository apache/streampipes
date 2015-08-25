package de.fzi.cep.sepa.storm.topology;

import de.fzi.cep.sepa.runtime.param.BindingParameters;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;

public class SepaTopologyBuilder {

	public static StormTopology buildSimpleTopology(FunctionalSepaBolt<? extends BindingParameters> bolt, String zookeeperUrl)
	{
		  TopologyBuilder builder = new TopologyBuilder();
		  
		  SepaSpout sepaSpout = new SepaSpout("sepaspout", zookeeperUrl);
		  SinkSepaBolt<? extends BindingParameters> sinkSepaBolt = new SinkSepaBolt<>("sinkbolt");
		  
		  builder.setSpout(sepaSpout.getId(), sepaSpout);
		  builder.setBolt(bolt.getId(), bolt)
		  	.shuffleGrouping(sepaSpout.getId(), SepaSpout.SEPA_DATA_STREAM)
		  	.shuffleGrouping(sepaSpout.getId(), SepaSpout.SEPA_CONFIG_STREAM);
		  builder.setBolt(sinkSepaBolt.getId(), sinkSepaBolt)
		  	.shuffleGrouping(bolt.getId(), SepaSpout.SEPA_DATA_STREAM)
		  	.shuffleGrouping(bolt.getId(), SepaSpout.SEPA_CONFIG_STREAM);
		  
		  return builder.createTopology();		  
	}
}
