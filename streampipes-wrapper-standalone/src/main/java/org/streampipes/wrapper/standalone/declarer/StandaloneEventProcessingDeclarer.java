package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventProcessorRuntime;

public abstract class StandaloneEventProcessingDeclarer<B extends
        EventProcessorBindingParams> extends EventProcessorDeclarer<B, StandaloneEventProcessorRuntime> {

  public abstract ConfiguredEventProcessor<B> onInvocation(DataProcessorInvocation graph);

  @Override
  public StandaloneEventProcessorRuntime getRuntime(DataProcessorInvocation graph) {
    ConfiguredEventProcessor<B> configuredEngine = onInvocation(graph);
    StandaloneEventProcessorRuntimeParams<B> runtimeParams = new StandaloneEventProcessorRuntimeParams<>
            (configuredEngine.getEngineSupplier(), configuredEngine.getBindingParams(), false);

    return new StandaloneEventProcessorRuntime(runtimeParams);
  }
}
