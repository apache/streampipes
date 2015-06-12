package de.fzi.cep.sepa.html;

import java.net.URI;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.SemanticEventConsumerDeclarer;

public class EventConsumerWelcomePage extends WelcomePage<SemanticEventConsumerDeclarer>{

	public EventConsumerWelcomePage(String baseUri, List<SemanticEventConsumerDeclarer> declarers)
	{
		super();
		buildUris(baseUri, declarers);
	}
	
	@Override
	protected void buildUris(String baseUri,
			List<SemanticEventConsumerDeclarer> declarers) {
		for(SemanticEventConsumerDeclarer declarer : declarers)
		{
			Description producer = new AgentDescription();
			producer.setName(declarer.declareModel().getName());
			producer.setUri(URI.create(baseUri + declarer.declareModel().getUri()));
			producers.add(producer);
		}
		
	}

}
