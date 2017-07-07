package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.impl.graph.SecDescription;
import org.streampipes.model.impl.graph.SecInvocation;

public abstract class AbstractFlinkConsumerDeclarer 
	extends AbstractFlinkDeclarer<SecDescription, SecInvocation, FlinkSecRuntime> implements SemanticEventConsumerDeclarer {

}
