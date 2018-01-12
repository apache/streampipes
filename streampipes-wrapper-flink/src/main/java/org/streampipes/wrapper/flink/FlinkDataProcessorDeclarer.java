package org.streampipes.wrapper.flink;

import org.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public abstract class FlinkDataProcessorDeclarer<B extends EventProcessorBindingParams>
	extends EventProcessorDeclarer<B, FlinkDataProcessorRuntime<B>> {
	
	
}
