package de.fzi.cep.sepa.desc;

import java.util.List;

import de.fzi.cep.sepa.model.impl.SEP;

public interface SemanticEventProducerDeclarer {

	public SEP declareModel();
	
	public List<EventStreamDeclarer> getEventStreams();
}
