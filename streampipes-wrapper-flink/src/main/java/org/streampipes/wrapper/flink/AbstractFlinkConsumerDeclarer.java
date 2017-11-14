package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;

public abstract class AbstractFlinkConsumerDeclarer 
	extends AbstractFlinkDeclarer<DataSinkDescription, DataSinkInvocation, FlinkSecRuntime> implements SemanticEventConsumerDeclarer {

}
