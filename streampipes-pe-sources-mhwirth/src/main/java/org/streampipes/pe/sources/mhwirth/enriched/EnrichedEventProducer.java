package org.streampipes.pe.sources.mhwirth.enriched;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.mhwirth.friction.GearboxFrictionCoefficientStream;
import org.streampipes.pe.sources.mhwirth.friction.SwivelFrictionCoefficientStream;

import java.util.ArrayList;
import java.util.List;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer{

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source-enriched", "Enriched Event", "");
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
