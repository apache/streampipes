package org.streampipes.pe.sources.samples.enriched;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer{

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source_enriched", "Enriched Event", "");
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
