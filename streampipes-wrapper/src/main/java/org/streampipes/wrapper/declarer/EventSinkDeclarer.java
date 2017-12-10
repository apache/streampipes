package org.streampipes.wrapper.declarer;

import org.streampipes.container.declarer.SemanticEventConsumerDeclarer;
import org.streampipes.model.Response;
import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.ConfiguredEventSink;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.runtime.EventSinkRuntime;

public abstract class EventSinkDeclarer<B extends EventSinkBindingParams, ES extends
        EventSinkRuntime>
        extends PipelineElementDeclarer<B, ES, DataSinkInvocation,
                DataSinkParameterExtractor, EventSink<B>> implements SemanticEventConsumerDeclarer {

  @Override
  protected DataSinkParameterExtractor getExtractor(DataSinkInvocation graph) {
    return DataSinkParameterExtractor.from(graph);
  }

  @Override
  public Response invokeRuntime(DataSinkInvocation graph) {

    try {
      ConfiguredEventSink<B, EventSink<B>> configuredEngine = onInvocation(graph);
      invokeEPRuntime(configuredEngine.getBindingParams(), configuredEngine.getEngineSupplier());
      return new Response(configuredEngine.getBindingParams().getGraph().getElementId(), true);
    } catch (Exception e) {
      e.printStackTrace();
      return new Response(graph.getElementId(), false, e.getMessage());
    }

  }

  public abstract ConfiguredEventSink<B, EventSink<B>> onInvocation(DataSinkInvocation graph);

}
