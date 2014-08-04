package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.SEP;


public interface EventStreamDeclarer {

	public de.fzi.cep.sepa.model.impl.EventStream declareStream(SEP sep);
	
	public void executeStream();
}
