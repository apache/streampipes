package de.fzi.cep.sepa.client.declarer;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;


public interface EventStreamDeclarer {

	EventStream declareModel(SepDescription sep);
	
	void executeStream();
	
	boolean isExecutable();
}
