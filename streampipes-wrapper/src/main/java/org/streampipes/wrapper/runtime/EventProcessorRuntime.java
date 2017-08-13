package org.streampipes.wrapper.runtime;


import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;

public abstract class EventProcessorRuntime { //
// routing
// container

	protected EventProcessor<?> engine;

	public EventProcessorRuntime(EventProcessorRuntimeParams<?> params)
	{
		this.engine = params.getPreparedEngine();
	}

	public void discardRuntime() {
		preDiscard();
		engine.discard();
		postDiscard();
	}
	
	public abstract void initRuntime();
	
	public abstract void preDiscard();
	
	public abstract void postDiscard();
}
