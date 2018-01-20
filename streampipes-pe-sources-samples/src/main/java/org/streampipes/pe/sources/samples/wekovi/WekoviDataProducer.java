package org.streampipes.pe.sources.samples.wekovi;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

import java.util.ArrayList;
import java.util.List;

public class WekoviDataProducer implements SemanticEventProducerDeclarer {
	
	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source_random", "Random", "Random Event Producer");
		return sep;
	}

	
	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		
		List<DataStreamDeclarer> streams = new ArrayList<DataStreamDeclarer>();
		
		streams.add(new WekoviStream());

	
		return streams;
	}

}
