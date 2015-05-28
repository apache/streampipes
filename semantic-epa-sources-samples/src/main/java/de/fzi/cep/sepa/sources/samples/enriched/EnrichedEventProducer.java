package de.fzi.cep.sepa.sources.samples.enriched;

import java.util.ArrayList;
import java.util.List;

import de.fzi.cep.sepa.desc.EventStreamDeclarer;
import de.fzi.cep.sepa.desc.SemanticEventProducerDeclarer;
import de.fzi.cep.sepa.model.impl.Domain;
import de.fzi.cep.sepa.model.impl.EventSource;
import de.fzi.cep.sepa.model.impl.graph.SEP;
import de.fzi.cep.sepa.sources.samples.util.Utils;

public class EnrichedEventProducer implements SemanticEventProducerDeclarer{

	@Override
	public SEP declareModel() {
		SEP sep = new SEP("/enriched", "Enriched Event", "", "", Utils.createDomain(Domain.DOMAIN_PROASENSE), new EventSource());
		return sep;
	}

	@Override
	public List<EventStreamDeclarer> getEventStreams() {
		List<EventStreamDeclarer> eventStreams = new ArrayList<EventStreamDeclarer>();
		
		eventStreams.add(new EnrichedStream());
		return eventStreams;
	}

}
