package de.fzi.cep.sepa.sources.mhwirth.enriched;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.cep.sepa.sources.mhwirth.friction.GearboxFrictionCoefficientStream;
import de.fzi.cep.sepa.sources.mhwirth.friction.SwivelFrictionCoefficientStream;

import java.util.ArrayList;
import java.util.List;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer{

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-enriched", "Enriched Event", "");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		eventStreams.add(new EnrichedStream());
		eventStreams.add(new GearboxFrictionCoefficientStream());
		eventStreams.add(new SwivelFrictionCoefficientStream());
		
		return eventStreams;
	}

}
