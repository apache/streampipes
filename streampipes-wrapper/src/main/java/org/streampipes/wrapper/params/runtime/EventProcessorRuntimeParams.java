package org.streampipes.wrapper.params.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.function.Supplier;

public abstract class EventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends
				RuntimeParams<B, EventProcessor<B>> { // B - Bind Type


	public EventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier,
																		 B bindingParams) {
		super(supplier, bindingParams);
	}

	public void bindEngine() throws SpRuntimeException {
		engine.bind(bindingParams, getOutputCollector());
	}

	public void discardEngine() {
		engine.discard();
	}

	public abstract SpOutputCollector getOutputCollector()
					throws
					SpRuntimeException;

}
