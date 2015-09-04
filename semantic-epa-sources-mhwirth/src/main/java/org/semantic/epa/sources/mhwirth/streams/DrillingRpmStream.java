package org.semantic.epa.sources.mhwirth.streams;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;
import de.fzi.cep.sepa.model.impl.EventStream;
import de.fzi.cep.sepa.model.impl.graph.SepDescription;

public class DrillingRpmStream implements EventStreamDeclarer {

	@Override
	public EventStream declareModel(SepDescription sep) {

		EventStream stream = new EventStream();
	
		return stream;
	}

	@Override
	public void executeStream() {
		
	}


	@Override
	public boolean isExecutable() {
		return false;
	}

}
