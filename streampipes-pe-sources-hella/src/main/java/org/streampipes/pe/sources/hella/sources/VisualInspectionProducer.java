package org.streampipes.pe.sources.hella.sources;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.sources.hella.streams.ScrapDataStream;

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
