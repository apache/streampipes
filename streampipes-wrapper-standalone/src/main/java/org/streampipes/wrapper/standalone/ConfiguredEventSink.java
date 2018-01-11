package org.streampipes.wrapper.standalone;

import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.function.Supplier;

public class ConfiguredEventSink<B extends EventSinkBindingParams> {

  private B bindingParams;
  private Supplier<EventSink<B>> engineSupplier;

  public ConfiguredEventSink(B bindingParams, Supplier<EventSink<B>> engineSupplier) {
    this.bindingParams = bindingParams;
    this.engineSupplier = engineSupplier;
  }

  public B getBindingParams() {
    return bindingParams;
  }

  public Supplier<EventSink<B>> getEngineSupplier() {
    return engineSupplier;
  }
}

