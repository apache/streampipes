package de.fzi.cep.sepa.sources.samples.hella;

import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

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
