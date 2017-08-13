package org.streampipes.wrapper.params.runtime;

import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.function.Supplier;

public abstract class EventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends
				RuntimeParams<B, EventProcessor> { // B - Bind Type

	private final Supplier<EventProcessor<B>> supplier;

	public EventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier,
																		 B bindingParams) {
		super(bindingParams);
		this.supplier = supplier;
	}

	public EventProcessor<B> getPreparedEngine() {
		EventProcessor<B> engine = supplier.get();
		engine.bind(bindingParams);
		return engine;
	}

}
