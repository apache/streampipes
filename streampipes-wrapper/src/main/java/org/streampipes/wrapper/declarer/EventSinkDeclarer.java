package org.streampipes.wrapper.declarer;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;

public abstract class EventSinkDeclarer<B extends EventSinkBindingParams, ES extends
        PipelineElementRuntime>
        extends PipelineElementDeclarer<B, ES, DataSinkInvocation,
        DataSinkParameterExtractor> implements SemanticEventConsumerDeclarer {

  @Override
  protected DataSinkParameterExtractor getExtractor(DataSinkInvocation graph) {
    return DataSinkParameterExtractor.from(graph);
  }

  @Override
  public Response invokeRuntime(DataSinkInvocation graph) {
    return invokeEPRuntime(graph);
  }

}
