package de.fzi.proasense.monitoring.sources;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.proasense.monitoring.streams.DecisionMakingStream;
import de.fzi.proasense.monitoring.streams.PredictionStream;

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
