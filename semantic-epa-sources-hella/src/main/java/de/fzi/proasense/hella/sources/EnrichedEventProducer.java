package de.fzi.proasense.hella.sources;

import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.proasense.hella.streams.HellaEnrichedStream;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		
		SepDescription sep = new SepDescription("source-enriched", "Hella Enriched Event", "Enriched Hella event stream");
		
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		return Arrays.asList(new HellaEnrichedStream());
	}
}
