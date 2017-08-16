package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.routing.EventProcessorInputCollector;
import org.streampipes.wrapper.runtime.EventProcessorRuntime;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;

public class StandaloneEventProcessorRuntime extends EventProcessorRuntime {


	public StandaloneEventProcessorRuntime(StandaloneEventProcessorRuntimeParams<?> params) {
		super(params);
	}


	@Override
	public void initRuntime() throws SpRuntimeException {
		for (EventProcessorInputCollector eventProcessorInputCollector : params.getInputCollectors()) {
			eventProcessorInputCollector.connect();
		}

		params.getOutputCollector().connect();
	}

	@Override
	public void postDiscard() throws SpRuntimeException {
		for(EventProcessorInputCollector eventProcessorInputCollector : params.getInputCollectors()) {
			eventProcessorInputCollector.disconnect();
		}

		params.getOutputCollector().disconnect();
	}

}
