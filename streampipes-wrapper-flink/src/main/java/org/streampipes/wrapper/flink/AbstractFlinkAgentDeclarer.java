package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;

public abstract class AbstractFlinkAgentDeclarer<B extends EventProcessorBindingParams>
	extends AbstractFlinkDeclarer<SepaDescription, SepaInvocation, FlinkSepaRuntime<B>> implements SemanticEventProcessingAgentDeclarer {
	
	
}
