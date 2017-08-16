package org.streampipes.wrapper.declarer;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.impl.graph.SecInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.runtime.EventSinkRuntime;

public abstract class EventSinkDeclarer<B extends EventSinkBindingParams, ES extends
        EventSinkRuntime>
        extends PipelineElementDeclarer<B, ES, SecInvocation,
                DataSinkParameterExtractor, EventSink<B>> implements SemanticEventConsumerDeclarer {

  @Override
  protected DataSinkParameterExtractor getExtractor(SecInvocation graph) {
    return DataSinkParameterExtractor.from(graph);
  }

}
