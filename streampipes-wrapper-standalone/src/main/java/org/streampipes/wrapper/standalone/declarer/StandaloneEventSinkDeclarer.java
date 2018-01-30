package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.model.graph.DataSinkInvocation;
import org.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.streampipes.wrapper.declarer.EventSinkDeclarer;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.streampipes.wrapper.standalone.param.StandaloneEventSinkRuntimeParams;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventSinkRuntime;

public abstract class StandaloneEventSinkDeclarer<B extends
        EventSinkBindingParams> extends EventSinkDeclarer<B, StandaloneEventSinkRuntime> {

  @Override
  public StandaloneEventSinkRuntime getRuntime(DataSinkInvocation graph, DataSinkParameterExtractor extractor) {

    ConfiguredEventSink<B> configuredEngine = onInvocation(graph, extractor);
    StandaloneEventSinkRuntimeParams<B> runtimeParams = new StandaloneEventSinkRuntimeParams<>
            (configuredEngine.getEngineSupplier(), configuredEngine.getBindingParams(), false);

    return new StandaloneEventSinkRuntime(runtimeParams);
  }

  public abstract ConfiguredEventSink<B> onInvocation(DataSinkInvocation graph, DataSinkParameterExtractor extractor);

}
