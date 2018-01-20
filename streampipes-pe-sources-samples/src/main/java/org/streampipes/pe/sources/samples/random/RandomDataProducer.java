package org.streampipes.pe.sources.samples.random;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

import java.util.ArrayList;
import java.util.List;

public class RandomDataProducer implements SemanticEventProducerDeclarer {
	
	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source_random", "Random", "Random Event Producer");
		return sep;
	}

	
	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		
		List<DataStreamDeclarer> streams = new ArrayList<DataStreamDeclarer>();
		
		//streams.add(new RandomTextStream());
		streams.add(new RandomNumberStreamJson());
		streams.add(new RandomNumberStreamList());
		streams.add(new ComplexRandomStream());
		//streams.add(new RandomNumberStreamThrift());
		//streams.add(new NestedListRandomNumberStream());
		//streams.add(new NestedRandomNumberStream());
	
	
		return streams;
	}

}
