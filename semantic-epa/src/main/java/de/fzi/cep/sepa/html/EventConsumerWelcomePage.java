package de.fzi.cep.sepa.html;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;

public class EventConsumerWelcomePage extends WelcomePageGenerator<SemanticEventConsumerDeclarer>{

	
	public EventConsumerWelcomePage(String baseUri, List<SemanticEventConsumerDeclarer> declarers)
	{
		super(baseUri, declarers);
	}
	
	@Override
	public List<Description> buildUris() {
		for(SemanticEventConsumerDeclarer declarer : declarers)
		{
			Description producer = new AgentDescription();
			producer.setName(declarer.declareModel().getName());
			producer.setDescription(declarer.declareModel().getDescription());
			producer.setUri(URI.create(baseUri + declarer.declareModel().getUri()));
			descriptions.add(producer);
		}
		return descriptions;	
	}
}
