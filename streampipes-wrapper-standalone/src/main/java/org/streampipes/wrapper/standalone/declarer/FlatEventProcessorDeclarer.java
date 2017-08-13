package org.streampipes.wrapper.standalone.declarer;

import org.streampipes.wrapper.declarer.EventProcessorDeclarer;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;
import org.streampipes.wrapper.standalone.FlatEPRuntime;
import org.streampipes.wrapper.standalone.param.FlatEventProcessorRuntimeParams;

import java.util.function.Supplier;

public abstract class FlatEventProcessorDeclarer<B extends EventProcessorBindingParams> extends EventProcessorDeclarer<B, FlatEPRuntime> {


  @Override
  public FlatEPRuntime prepareRuntime(B bindingParameters,
                                      Supplier<EventProcessor<B>> supplier) {

    FlatEventProcessorRuntimeParams<B> runtimeParams = new FlatEventProcessorRuntimeParams<>(supplier, bindingParameters);

    return new FlatEPRuntime(runtimeParams);
  }


}
