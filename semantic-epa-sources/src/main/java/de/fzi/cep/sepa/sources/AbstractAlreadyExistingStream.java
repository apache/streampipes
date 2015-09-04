package de.fzi.cep.sepa.sources;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;

public abstract class AbstractAlreadyExistingStream implements EventStreamDeclarer {

	@Override
	public void executeStream() {		
	
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
}
