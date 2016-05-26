package de.fzi.proasense.hella.sources;

import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.proasense.hella.streams.ScrapDataStream;

public class VisualInspectionProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-visual", "Visual Inspection", "Provides streams produced during visual inspection of parts");
		
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		return Arrays.asList(new ScrapDataStream());
	}

}
