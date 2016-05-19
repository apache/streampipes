package de.fzi.cep.sepa.client.container.init;

import java.util.List;

import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

public abstract class EmbeddedModelSubmitter  {

	protected abstract List<SemanticEventProcessingAgentDeclarer> epaDeclarers();
	
	protected abstract List<SemanticEventProducerDeclarer> sourceDeclarers();
	
	protected abstract List<SemanticEventConsumerDeclarer> consumerDeclarers();
	
	protected abstract int port();
	
	protected abstract String contextPath();
	
	// add init method
}
