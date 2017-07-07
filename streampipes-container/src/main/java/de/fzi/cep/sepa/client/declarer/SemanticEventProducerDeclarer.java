package de.fzi.cep.sepa.client.declarer;

import java.util.List;

import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public interface SemanticEventProducerDeclarer extends Declarer<SepDescription> {
	List<EventStreamDeclarer> getEventStreams();
}
