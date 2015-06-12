package de.fzi.cep.sepa.html;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;

public class EventProducerWelcomePage extends WelcomePage<SemanticEventProducerDeclarer> {

	public EventProducerWelcomePage(String baseUri, List<SemanticEventProducerDeclarer> declarers)
	{
		super();
		buildUris(baseUri, declarers);
	}
	
	@Override
	protected void buildUris(String baseUri, List<SemanticEventProducerDeclarer> declarers)
	{
		for(SemanticEventProducerDeclarer declarer : declarers)
		{
			List<AgentDescription> streams = new ArrayList<AgentDescription>();
			StreamDescription description = new StreamDescription();
			description.setName(declarer.declareModel().getName());
			description.setDescription(declarer.declareModel().getDescription());
			description.setUri(URI.create(baseUri + declarer.declareModel().getUri()));
			for(EventStreamDeclarer streamDeclarer : declarer.getEventStreams())
			{
				AgentDescription ad = new AgentDescription();
				ad.setDescription(streamDeclarer.declareModel(declarer.declareModel()).getDescription());
				ad.setUri(URI.create(baseUri + streamDeclarer.declareModel(declarer.declareModel()).getUri()));
				ad.setName(streamDeclarer.declareModel(declarer.declareModel()).getName());
				streams.add(ad);
			}
			description.setStreams(streams);
			producers.add(description);
		}
	}
	
}
