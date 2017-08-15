package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.param.StandaloneEventProcessorRuntimeParams;
import org.streampipes.wrapper.standalone.runtime.StandaloneEventProcessorRuntime;

import java.util.function.Supplier;

public abstract class StandaloneEventProcessingDeclarerSingleton<B extends
        EventProcessorBindingParams> extends EventProcessorDeclarer<B, StandaloneEventProcessorRuntime> {

  @Override
  public StandaloneEventProcessorRuntime prepareRuntime(B bindingParameters,
                                                        Supplier<EventProcessor<B>> supplier) {

    StandaloneEventProcessorRuntimeParams<B> runtimeParams = new StandaloneEventProcessorRuntimeParams<>
            (supplier, bindingParameters, true);

    return new StandaloneEventProcessorRuntime(runtimeParams);
  }
}
