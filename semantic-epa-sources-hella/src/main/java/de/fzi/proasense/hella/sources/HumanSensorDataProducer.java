package de.fzi.proasense.hella.sources;

import java.util.Arrays;
import java.util.List;

import de.fzi.cep.sepa.client.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.client.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;
import de.fzi.proasense.hella.streams.ProductionPlanStream;
import de.fzi.proasense.hella.streams.RawMaterialCertificateStream;
import de.fzi.proasense.hella.streams.RawMaterialChangeStream;

public class HumanSensorDataProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		
		SepDescription sep = new SepDescription("source-human", "Human Sensor", "Provides streams generated manually by humans");
		
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		return Arrays.asList(new RawMaterialCertificateStream(), new RawMaterialChangeStream(), new ProductionPlanStream());
	}

}
