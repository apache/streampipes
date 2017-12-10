package org.streampipes.wrapper;

import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.function.Supplier;

public class ConfiguredEventProcessor<B extends EventProcessorBindingParams, E
        extends EventProcessor<B>> {

  private B bindingParams;
  private Supplier<EventProcessor<B>> engineSupplier;

  public ConfiguredEventProcessor(B bindingParams, Supplier<EventProcessor<B>> engineSupplier) {
    this.bindingParams = bindingParams;
    this.engineSupplier = engineSupplier;
  }

  public B getBindingParams() {
    return bindingParams;
  }

  public Supplier<EventProcessor<B>> getEngineSupplier() {
    return engineSupplier;
  }
}
