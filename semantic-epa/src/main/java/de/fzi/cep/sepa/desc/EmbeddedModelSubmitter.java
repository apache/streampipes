package de.fzi.cep.sepa.desc;

import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.endpoint.RestletConfig;

public abstract class EmbeddedModelSubmitter extends Application {

	protected abstract List<SemanticEventProcessingAgentDeclarer> epaDeclarers();
	
	protected abstract List<SemanticEventProducerDeclarer> sourceDeclarers();
	
	protected abstract List<SemanticEventConsumerDeclarer> consumerDeclarers();
	
	protected abstract int port();
	
	protected abstract String contextPath();
	
	@Override
	public synchronized Restlet createInboundRoot() {
		
		Router router = new Router(getContext());
		
		generateSepaRestlets(epaDeclarers(), sourceDeclarers(), consumerDeclarers(), port(), contextPath()).forEach(c -> router.attach(c.getUri(), c.getRestlet()));
		return router;
	}
	
	private List<RestletConfig> generateSepaRestlets(List<SemanticEventProcessingAgentDeclarer> sepaDeclarers, List<SemanticEventProducerDeclarer> sourceDeclarers, List<SemanticEventConsumerDeclarer> consumerDeclarers, int port, String contextPath)
	{
		return new RestletGenerator(port, contextPath, false).addSepaRestlets(sepaDeclarers).addSepRestlets(sourceDeclarers).addSecRestlets(consumerDeclarers).getRestletConfigurations();
	}
}
