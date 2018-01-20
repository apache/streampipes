package org.streampipes.pe.sources.hella.sources;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

public class SapDataProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source-sap", "SAP", "SAP events");
		
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		return Arrays.asList();
	}

}
