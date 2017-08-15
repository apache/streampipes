package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.EventProcessorOutputCollector;

public interface EventProcessor<B extends EventProcessorBindingParams> extends PipelineElement<B> {

	void bind(B parameters, EventProcessorOutputCollector collector);
}
