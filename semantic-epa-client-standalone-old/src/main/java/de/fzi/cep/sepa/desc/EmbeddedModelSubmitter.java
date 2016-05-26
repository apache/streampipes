package de.fzi.cep.sepa.desc;

import java.util.List;

import org.restlet.Application;

import de.fzi.cep.sepa.client.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;

public abstract class EmbeddedModelSubmitter extends Application {

	protected abstract List<SemanticEventProcessingAgentDeclarer> epaDeclarers();
	
	protected abstract List<SemanticEventProducerDeclarer> sourceDeclarers();
	
	protected abstract List<SemanticEventConsumerDeclarer> consumerDeclarers();
	
	protected abstract int port();
	
	protected abstract String contextPath();
	
//	@Override
//    public synchronized Restlet createInboundRoot() {
//		Server.INSTANCE.createEmbedded(
//				generateSepaRestlets(epaDeclarers(), sourceDeclarers(), consumerDeclarers(), port(), contextPath()));
//		
//		return Server.INSTANCE.getRouter();
//    }
		
	private List<RestletConfig> generateSepaRestlets(List<SemanticEventProcessingAgentDeclarer> sepaDeclarers, List<SemanticEventProducerDeclarer> sourceDeclarers, List<SemanticEventConsumerDeclarer> consumerDeclarers, int port, String contextPath)
	{
		return new RestletGenerator(port, contextPath, false)
			.addSepaRestlets(sepaDeclarers)
			.addSepRestlets(sourceDeclarers)
			.addSecRestlets(consumerDeclarers)
			.getRestletConfigurations(false);
	}
}
