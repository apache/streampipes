package org.streampipes.sources;


import org.streampipes.container.declarer.DataStreamDeclarer;

public abstract class AbstractAlreadyExistingStream implements DataStreamDeclarer {

	@Override
	public void executeStream() {		
	
	}

	@Override
	public boolean isExecutable() {
		return false;
	}
}
