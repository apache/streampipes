package org.streampipes.pe.sources.hella.sources;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;

public class SapDataProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source-sap", "SAP", "SAP events");
		
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		return Arrays.asList();
	}

}
