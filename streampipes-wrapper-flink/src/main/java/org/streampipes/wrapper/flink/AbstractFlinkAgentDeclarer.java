package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventProcessingAgentDeclarer;
import org.streampipes.model.impl.graph.SepaDescription;
import org.streampipes.model.impl.graph.SepaInvocation;
import org.streampipes.wrapper.BindingParameters;

public abstract class AbstractFlinkAgentDeclarer<B extends BindingParameters> 
	extends AbstractFlinkDeclarer<SepaDescription, SepaInvocation, FlinkSepaRuntime<B>> implements SemanticEventProcessingAgentDeclarer {
	
	
}
