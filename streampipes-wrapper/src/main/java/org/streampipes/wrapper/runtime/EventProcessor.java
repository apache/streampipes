package org.streampipes.wrapper.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;

public interface EventProcessor<B extends EventProcessorBindingParams> extends PipelineElement<B> {

	void bind(B parameters, SpOutputCollector collector) throws SpRuntimeException;
}
