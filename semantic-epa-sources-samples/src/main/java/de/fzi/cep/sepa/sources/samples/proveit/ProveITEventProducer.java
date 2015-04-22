package de.fzi.cep.sepa.sources.samples.proveit;

import java.util.List;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class ProveITEventProducer implements SemanticEventProducerDeclarer {

	@Override
	public SEP declareModel() {
		SEP sep = new SEP("/proveit", "ProveIT Logistics", "Several streams produced by a logistics service provider", "", Utils.createDomain(Domain.DOMAIN_PERSONAL_ASSISTANT), new EventSource());
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> streams = new ProveITStreamGenerator().generateStreams();
		return streams;
	}

}
