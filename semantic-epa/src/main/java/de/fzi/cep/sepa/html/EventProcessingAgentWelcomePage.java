package de.fzi.cep.sepa.html;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;

public class EventProcessingAgentWelcomePage extends WelcomePage<SemanticEventProcessingAgentDeclarer> {

	public EventProcessingAgentWelcomePage(String baseUri, List<SemanticEventProcessingAgentDeclarer> declarers)
	{
		super();
		buildUris(baseUri, declarers);
	}
	
	@Override
	protected void buildUris(String baseUri,
			List<SemanticEventProcessingAgentDeclarer> declarers) {
		for(SemanticEventProcessingAgentDeclarer declarer : declarers)
		{
			AgentDescription producer = new AgentDescription();
			producer.setName(declarer.declareModel().getName());
			producer.setDescription(declarer.declareModel().getDescription());
			producer.setUri(URI.create(baseUri + declarer.declareModel().getUri()));
			producers.add(producer);
		}
	}

}
