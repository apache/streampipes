package org.streampipes.pe.sources.samples.proveit;

import java.util.Arrays;
import java.util.List;

import org.streampipes.container.declarer.EventStreamDeclarer;
import org.streampipes.container.declarer.SemanticEventProducerDeclarer;
import org.streampipes.model.impl.graph.SepDescription;

public class ProveITEventProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("sourc/proveit", "ProveIT Logistics", "Several streams produced by a logistics service provider");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
//		List<EventStreamDeclarer> streams = new ProveITStreamGenerator().generateStreams();
//		return streams;
		return Arrays.asList(new ProveItLocationStream());
	}

}
