package de.fzi.cep.sepa.desc.declarer;

import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;


public interface EventStreamDeclarer {

	public EventStream declareModel(SepDescription sep);
	
	public void executeStream();
	
	public boolean isExecutable();
}
