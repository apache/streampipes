package de.fzi.cep.sepa.sources.samples.enriched;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source/enriched", "Enriched Event", "");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		//eventStreams.add(new EnrichedStream());
		eventStreams.add(new EnrichedStreamReplay());
		
		return eventStreams;
	}

}
