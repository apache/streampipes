package org.streampipes.wrapper.flink;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.graph.DataSinkDescription;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public abstract class FlinkDataSinkDeclarer<B extends EventSinkBindingParams>
	extends AbstractFlinkDeclarer<DataSinkDescription, DataSinkInvocation, FlinkDataSinkRuntime<B>> implements
				SemanticEventConsumerDeclarer {

}
