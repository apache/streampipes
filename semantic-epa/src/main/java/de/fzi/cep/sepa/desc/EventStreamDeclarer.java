package de.fzi.cep.sepa.desc;

import de.fzi.cep.sepa.model.impl.graph.SepDescription;


public interface EventStreamDeclarer {

	public de.fzi.cep.sepa.model.impl.EventStream declareModel(SepDescription sep);
	
	public void executeStream();
	
	public boolean isExecutable();
}
