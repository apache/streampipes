package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.SEP;

public interface SemanticEventProducerDeclarer {

	public SEP declareSemanticEventProducer();
	
	public int declarePort();
	
	public String declareURIPath();
}
