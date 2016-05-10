package ${package}.topology;

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
		
		StormTopology myTopology = SepaTopologyBuilder.buildSimpleTopology(new ${classNamePrefix}Bolt("sentiment"), "kalmar39.fzi.de:2181");
		
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("${classNamePrefix}", conf, myTopology);
		
		// Production settings
		//Config productionConfig = new Config();
		
		//StormSubmitter.submitTopology("${classNamePrefix}", productionConfig, sentimentDetectionTopology);
		
	}
}
