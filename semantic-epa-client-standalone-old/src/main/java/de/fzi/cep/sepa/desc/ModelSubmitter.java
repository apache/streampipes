package de.fzi.cep.sepa.desc;

import java.util.List;

import de.fzi.cep.sepa.commons.config.ClientConfiguration;
import de.fzi.cep.sepa.declarer.InvocableDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;
import de.fzi.cep.sepa.endpoint.Server;

public class ModelSubmitter {
			
	public static boolean submitProducer(List<SemanticEventProducerDeclarer> producers, int port)
	{
		List<RestletConfig> restletConfigurations = new RestletGenerator(port)
		.addSepRestlets(producers)
		.getRestletConfigurations(true);	
		
		return start(port, restletConfigurations);
	}
	
	public static boolean submitProducer(
			List<SemanticEventProducerDeclarer> producers) throws Exception {
	
		return submitProducer(producers, ClientConfiguration.INSTANCE.getSourcesPort());
	}
	
	public static boolean submitAgent(List<SemanticEventProcessingAgentDeclarer> declarers, int port)
	{
		List<RestletConfig> restletConfigurations = new RestletGenerator(port)
		.addSepaRestlets(declarers)
		.getRestletConfigurations(true);	

		return start(port, restletConfigurations);
	}

	public static boolean submitAgent(List<SemanticEventProcessingAgentDeclarer> declarers) throws Exception {
		return submitAgent(declarers, ClientConfiguration.INSTANCE.getEsperPort());
	}

	public static boolean submitConsumer(List<SemanticEventConsumerDeclarer> declarers, int port) {
		List<RestletConfig> restletConfigurations = new RestletGenerator(port)
		.addSecRestlets(declarers)
		.getRestletConfigurations(true);	
		
		return start(port, restletConfigurations);
	}
	
	public static boolean submitMixed(List<InvocableDeclarer<?, ?>> declarers, int port) {
		List<RestletConfig> restletConfigurations = new RestletGenerator(port)
		.addRestlets(declarers)
		.getRestletConfigurations(true);	
		
		return start(port, restletConfigurations);
		
	}
	
	public static boolean submitConsumer(
			List<SemanticEventConsumerDeclarer> declarers) throws Exception {
		
		return submitConsumer(declarers, ClientConfiguration.INSTANCE.getActionPort());
	}	
	
	private static boolean start(int port, List<RestletConfig> configs)
	{
		return Server.INSTANCE.create(port, configs);
	}

}
