package org.streampipes.pe.proasense.monitoring.sources;

import java.util.ArrayList;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;
import org.streampipes.pe.proasense.monitoring.streams.DecisionMakingStream;
import org.streampipes.pe.proasense.monitoring.streams.PredictionStream;

public class MonitoringProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		
		SepDescription sep = new SepDescription("source-monitoring", "Monitoring", "Provides streams to monitor the ProaSense system");
		
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		streams.add(new DecisionMakingStream());
		streams.add(new PredictionStream());
		
		return streams;
	}
}
