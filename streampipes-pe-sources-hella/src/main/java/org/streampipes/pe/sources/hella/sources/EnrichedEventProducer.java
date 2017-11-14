package org.streampipes.pe.sources.hella.sources;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.hella.streams.HellaEnrichedStream;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		
		DataSourceDescription sep = new DataSourceDescription("source-enriched", "Hella Enriched Event", "Enriched Hella event stream");
		
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		return Arrays.asList(new HellaEnrichedStream());
	}
}
