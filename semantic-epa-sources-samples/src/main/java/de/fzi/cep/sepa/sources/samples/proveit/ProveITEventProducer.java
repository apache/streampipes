package de.fzi.cep.sepa.sources.samples.proveit;

import java.util.List;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.declarer.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class ProveITEventProducer implements SemanticEventProducerDeclarer {

	@Override
	public SepDescription declareModel() {
		SepDescription sep = new SepDescription("sourc/proveit", "ProveIT Logistics", "Several streams produced by a logistics service provider");
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ProveITStreamGenerator().generateStreams();
		return streams;
	}

}
