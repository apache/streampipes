package org.streampipes.pe.sources.samples.random;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;

import java.util.ArrayList;
import java.util.List;

public class RandomDataProducer implements SemanticEventProducerDeclarer {
	
	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("source_random", "Random", "Random Event Producer");		
		return sep;
	}

	
	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		
		List<EventStreamDeclarer> streams = new ArrayList<EventStreamDeclarer>();
		
		//streams.add(new RandomTextStream());
		streams.add(new RandomNumberStreamJson());
		streams.add(new RandomNumberStreamList());
		//streams.add(new RandomNumberStreamThrift());
		//streams.add(new NestedListRandomNumberStream());
		//streams.add(new NestedRandomNumberStream());
	
	
		return streams;
	}

}
