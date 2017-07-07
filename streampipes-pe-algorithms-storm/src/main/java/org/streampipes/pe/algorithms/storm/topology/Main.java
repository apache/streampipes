package org.streampipes.pe.algorithms.storm.topology;

import java.net.URI;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import org.streampipes.model.impl.EventStream;
import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.model.impl.staticproperty.MappingPropertyUnary;
import org.streampipes.runtime.BindingParameters;
import de.fzi.cep.sepa.storm.topology.SepaSpout;
import de.fzi.cep.sepa.storm.topology.SinkSepaBolt;
import de.fzi.cep.sepa.storm.utils.Utils;

public class Main {

	
	private static String SINK_BOLT_ID = "sinkBolt";
	public static  String SPOUT_ID = "SepaSpout";
	
	private static String SENTIMENT_BOLD_ID = "sentimentBolt";

	public static void main(String[] args) throws AlreadyAliveException, InvalidTopologyException {

		SepaInvocation invocation = Utils.getSepaInvocation(args[0]);

		StormTopology sentimentDetectionTopology = buildTopology(invocation);
		
		Config conf = getConfig(invocation);
		
		// use this method to test the topology during development
//		testDeploy(conf, sentimentDetectionTopology);

		// use this method to deploy the topology to a production cluster
		productionDeploy(conf, sentimentDetectionTopology);

	}
	
	private static StormTopology buildTopology(SepaInvocation invocation) {
		// TODO make inputGrounding work with multiple input streams
		EventStream inputEventStream = invocation.getInputStreams().get(0);
		EventStream outputEventStream = invocation.getOutputStream();
		
		TopologyBuilder builder = new TopologyBuilder();
		SepaSpout sepaSpout = new SepaSpout(SPOUT_ID, inputEventStream);
		SinkSepaBolt<? extends BindingParameters> sinkSepaBolt = new SinkSepaBolt<>(SINK_BOLT_ID, outputEventStream);
		SentimentDetectionBolt sentimentBolt = new SentimentDetectionBolt(SENTIMENT_BOLD_ID, inputEventStream);
		
		
		  builder.setSpout(sepaSpout.getId(), sepaSpout);
		  builder.setBolt(SENTIMENT_BOLD_ID, sentimentBolt)
		  	.shuffleGrouping(sepaSpout.getId(), Utils.SEPA_DATA_STREAM);
		  builder.setBolt(sinkSepaBolt.getId(), sinkSepaBolt)
		  	.shuffleGrouping(SENTIMENT_BOLD_ID, Utils.SEPA_DATA_STREAM);
		
		
		return builder.createTopology();
	}
	
	private static Config getConfig(SepaInvocation invocation) {
		Config conf = new Config();
		EventStream inputEventStream = invocation.getInputStreams().get(0);
		
		//Get the static parameter sentimentMapsTo from the invocation graph
		URI mapsTo = ((MappingPropertyUnary) invocation.getStaticProperties().get(0)).getMapsTo();
		
		EventProperty ep = Utils.getEventPropertyById(mapsTo, inputEventStream);
		conf.put("sentiment.param1", ep.getRuntimeName());
		
		return conf;
	}
	
	private static void testDeploy(Config conf, StormTopology topology) {
		 conf.setDebug(true);
		 LocalCluster cluster = new LocalCluster();
		 cluster.submitTopology(Name.getTopologyName(), conf, topology);
	}

	private static void productionDeploy(Config productionConfig, StormTopology topology) throws AlreadyAliveException, InvalidTopologyException {
		productionConfig.put(Config.NIMBUS_HOST, Utils.NIMBUS_HOST);
		productionConfig.put(Config.NIMBUS_THRIFT_PORT, Utils.NIMBUS_THRIFT_PORT);
		productionConfig.setDebug(true);
		StormSubmitter.submitTopology(Name.getTopologyName(), productionConfig, topology);
	}
	
	
	
}
