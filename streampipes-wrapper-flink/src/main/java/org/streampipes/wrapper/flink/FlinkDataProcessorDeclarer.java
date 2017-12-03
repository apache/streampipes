package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public abstract class FlinkDataProcessorDeclarer<B extends EventProcessorBindingParams>
	extends AbstractFlinkDeclarer<DataProcessorDescription, DataProcessorInvocation, FlinkDataProcessorRuntime<B>> implements SemanticEventProcessingAgentDeclarer {
	
	
}
