package de.fzi.cep.sepa.sources.mhwirth.main;

import de.fzi.cep.sepa.desc.declarer.EventStreamDeclarer;

public abstract class AbstractMhWirthStream implements EventStreamDeclarer {

	@Override
	public void executeStream() {		
	
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
}
