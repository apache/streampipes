package org.streampipes.pe.sources.samples.hella;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;

public class VisualInspectionProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		DataSourceDescription sep = new DataSourceDescription("source-visual", "Visual Inspection", "Provides streams produced during visual inspection of parts");
		
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		return Arrays.asList(new ScrapDataStream());
	}

}
