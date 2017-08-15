package org.streampipes.wrapper.params.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.routing.EventProcessorInputCollector;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;

import java.util.List;
import java.util.function.Supplier;

public abstract class EventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends
				RuntimeParams<B, EventProcessor<B>> { // B - Bind Type

	private final EventProcessor<B> engine;


	public EventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier,
																		 B bindingParams) {
		super(bindingParams);
		this.engine = supplier.get();
	}

	public EventProcessor<?> getEngine() {
		return engine;
	}


	public void bindEngine() throws SpRuntimeException {
		engine.bind(bindingParams, getOutputCollector());
	}

	public void discardEngine() {
		engine.discard();
	}

	public abstract EventProcessorOutputCollector getOutputCollector()
					throws
					SpRuntimeException;

	public abstract List<EventProcessorInputCollector> getInputCollectors() throws
					SpRuntimeException;

}
