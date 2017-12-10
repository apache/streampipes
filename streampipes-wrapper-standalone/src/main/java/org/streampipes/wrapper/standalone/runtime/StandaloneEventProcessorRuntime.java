package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.EventProcessorRuntime;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;

public class StandaloneEventProcessorRuntime extends EventProcessorRuntime {


	public StandaloneEventProcessorRuntime(StandaloneEventProcessorRuntimeParams<?> params) {
		super(params);
	}


	@Override
	public void initRuntime() throws SpRuntimeException {
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
