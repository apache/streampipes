package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.wrapper.runtime.EventProcessorRuntime;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;

public class StandaloneEventProcessorRuntime extends EventProcessorRuntime {


	public StandaloneEventProcessorRuntime(StandaloneEventProcessorRuntimeParams<?> params) {
		super(params);
	}



	@Override
	public void initRuntime() {
		params.getInputCollectors().forEach(is -> is.getConsumer().connect());
	}

	@Override
	public void preDiscard() {

	}

	@Override
	public void postDiscard() {

	}

}
