package org.streampipes.wrapper.runtime;


import org.apache.commons.lang.RandomStringUtils;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;

public abstract class EventProcessorRuntime extends
				PipelineElementRuntime<EventProcessorRuntimeParams<?>> {
	// routing container

	protected String instanceId;

	public EventProcessorRuntime(EventProcessorRuntimeParams<?> params)
	{
		super(params);
		this.instanceId = RandomStringUtils.randomAlphabetic(8);
	}

	public void discardRuntime() throws SpRuntimeException {
		params.getInputCollectors().forEach(is -> is.unregisterConsumer(instanceId));
		params.discardEngine();
		postDiscard();
	}

	public void bindRuntime() throws SpRuntimeException {
		params.bindEngine();
		params.getInputCollectors().forEach(is -> is.registerConsumer(instanceId, params.getEngine()));
		initRuntime();
	}
	
	public abstract void initRuntime() throws SpRuntimeException;
	
	public abstract void postDiscard() throws SpRuntimeException;
}
