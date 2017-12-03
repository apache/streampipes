package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;

public abstract class FlinkDataSinkDeclarer
	extends AbstractFlinkDeclarer<DataSinkDescription, DataSinkInvocation, FlinkDataSinkRuntime> implements SemanticEventConsumerDeclarer {

}
