package de.fzi.cep.sepa.desc;

import java.util.List;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;
import de.fzi.cep.sepa.endpoint.Server;

public class ModelSubmitter {
			
	public static boolean submitProducer(List<SemanticEventProducerDeclarer> producers, int port)
	{
		List<RestletConfig> restletConfigurations = new RestletGenerator(port)
		.addSepRestlets(producers)
		.getRestletConfigurations();	
		
		return start(port, restletConfigurations);
	}
	
	public static boolean submitProducer(
			List<SemanticEventProducerDeclarer> producers) throws Exception {
	
		return submitProducer(producers, Configuration.getInstance().SOURCES_PORT);
	}
	
	public static boolean submitAgent(List<SemanticEventProcessingAgentDeclarer> declarers, int port)
	{
		List<RestletConfig> restletConfigurations = new RestletGenerator(port)
		.addSepaRestlets(declarers)
		.getRestletConfigurations();	

		return start(port, restletConfigurations);
	}

	public static boolean submitAgent(List<SemanticEventProcessingAgentDeclarer> declarers) throws Exception {
		return submitAgent(declarers, Configuration.getInstance().ESPER_PORT);
	}

	public static boolean submitConsumer(List<SemanticEventConsumerDeclarer> declarers, int port) {
		List<RestletConfig> restletConfigurations = new RestletGenerator(Configuration.getInstance().ACTION_PORT)
		.addSecRestlets(declarers)
		.getRestletConfigurations();	
		
		return start(Configuration.getInstance().ACTION_PORT, restletConfigurations);
	}
	
	public static boolean submitConsumer(
			List<SemanticEventConsumerDeclarer> declarers) throws Exception {
		
		return submitConsumer(declarers, Configuration.getInstance().ACTION_PORT);
	}	
	
	private static boolean start(int port, List<RestletConfig> configs)
	{
		return Server.INSTANCE.create(port, configs);
	}

}
