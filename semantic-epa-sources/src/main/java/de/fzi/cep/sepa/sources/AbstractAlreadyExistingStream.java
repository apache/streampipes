package de.fzi.cep.sepa.sources;

import de.fzi.cep.sepa.declarer.EventStreamDeclarer;

public abstract class AbstractAlreadyExistingStream implements EventStreamDeclarer {

	@Override
	public void executeStream() {		
	
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
}
