package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.Map;

public interface EventProcessor<B extends EventProcessorBindingParams> extends PipelineElement<B> {

	void onEvent(Map<String, Object> event, String sourceInfo, SpCollector outputCollector);

}
