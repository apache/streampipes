package de.fzi.cep.sepa.desc;

import java.util.List;

import de.fzi.cep.sepa.commons.config.Configuration;
import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;
import de.fzi.cep.sepa.endpoint.Server;

public class ModelSubmitter {
			
	public static boolean submitProducer(
			List<SemanticEventProducerDeclarer> producers) throws Exception {
		
		List<RestletConfig> restletConfigurations = new RestletGenerator(Configuration.getInstance().SOURCES_PORT)
		.addSepRestlets(producers, false)
		.getRestletConfigurations();	
		
		return start(Configuration.getInstance().SOURCES_PORT, restletConfigurations);
	}

	public static boolean submitAgent(List<SemanticEventProcessingAgentDeclarer> declarers) throws Exception {
		List<RestletConfig> restletConfigurations = new RestletGenerator(Configuration.getInstance().ESPER_PORT)
			.addSepaRestlets(declarers, false)
			.getRestletConfigurations();	

		return start(Configuration.getInstance().ESPER_PORT, restletConfigurations);
	}

	public static boolean submitConsumer(
			List<SemanticEventConsumerDeclarer> declarers) throws Exception {
		
		List<RestletConfig> restletConfigurations = new RestletGenerator(Configuration.getInstance().ACTION_PORT)
		.addSecRestlets(declarers)
		.getRestletConfigurations();	
		
		return start(Configuration.getInstance().ACTION_PORT, restletConfigurations);
	}	
	
	private static boolean start(int port, List<RestletConfig> configs)
	{
		return Server.INSTANCE.create(port, configs);
	}

}
