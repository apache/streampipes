package de.fzi.cep.sepa.html;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.SemanticEventProcessingAgentDeclarer;

public class EventProcessingAgentWelcomePage extends WelcomePageGenerator<SemanticEventProcessingAgentDeclarer> {

	public EventProcessingAgentWelcomePage(String baseUri, List<SemanticEventProcessingAgentDeclarer> declarers)
	{
		super(baseUri, declarers);
	}
	
	@Override
	public List<Description> buildUris() {
		for(SemanticEventProcessingAgentDeclarer declarer : declarers)
		{
			AgentDescription producer = new AgentDescription();
			producer.setName(declarer.declareModel().getName());
			producer.setDescription(declarer.declareModel().getDescription());
			if (!declarer.declareModel().getUri().startsWith(baseUri)) producer.setUri(URI.create(baseUri + declarer.declareModel().getUri()));
			else producer.setUri(URI.create(declarer.declareModel().getUri()));
			descriptions.add(producer);
		}
		return descriptions;
	}

}
