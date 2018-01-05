package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.runtime.EventProcessorRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;

public class StandaloneEventProcessorRuntime extends StandalonePipelineElementRuntime<EventProcessorRuntimeParams<?>> {

	public StandaloneEventProcessorRuntime(StandaloneEventProcessorRuntimeParams<?> params) {
		super(params);
	}

	@Override
	public void discardRuntime() throws SpRuntimeException {
		params.getInputCollectors().forEach(is -> is.unregisterConsumer(instanceId));
		params.discardEngine();
		postDiscard();
	}

	@Override
	public void bindRuntime() throws SpRuntimeException {
		params.bindEngine();
		params.getInputCollectors().forEach(is -> is.registerConsumer(instanceId, params.getEngine()));
		prepareRuntime();
	}

	@Override
	public void prepareRuntime() throws SpRuntimeException {
		for (SpInputCollector spInputCollector : params.getInputCollectors()) {
			spInputCollector.connect();
		}

		params.getOutputCollector().connect();
	}

	@Override
	public void postDiscard() throws SpRuntimeException {
		for(SpInputCollector spInputCollector : params.getInputCollectors()) {
			spInputCollector.disconnect();
		}

		params.getOutputCollector().disconnect();
	}

}
