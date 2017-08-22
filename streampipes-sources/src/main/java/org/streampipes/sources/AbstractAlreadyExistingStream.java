package org.streampipes.sources;


import org.streampipes.container.declarer.EventStreamDeclarer;

public abstract class AbstractAlreadyExistingStream implements EventStreamDeclarer {

	@Override
	public void executeStream() {		
	
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
}
