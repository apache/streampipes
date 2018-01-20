package org.streampipes.pe.sources.hella.sources;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.DataStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.graph.DataSourceDescription;
import org.streampipes.pe.sources.hella.streams.ProductionPlanStream;
import org.streampipes.pe.sources.hella.streams.RawMaterialCertificateStream;
import org.streampipes.pe.sources.hella.streams.RawMaterialChangeStream;

public class HumanSensorDataProducer implements SemanticEventProducerDeclarer {

	@Override
	public DataSourceDescription declareModel() {
		
		DataSourceDescription sep = new DataSourceDescription("source-human", "Human Sensor", "Provides streams generated manually by humans");
		
		return sep;
	}

	@Override
	public List<DataStreamDeclarer> getEventStreams() {
		return Arrays.asList(new RawMaterialCertificateStream(), new RawMaterialChangeStream(), new ProductionPlanStream());
	}

}
