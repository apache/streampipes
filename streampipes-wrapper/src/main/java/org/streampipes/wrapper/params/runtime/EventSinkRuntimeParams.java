package org.streampipes.wrapper.params.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.List;
import java.util.function.Supplier;

public abstract class EventSinkRuntimeParams<B extends EventSinkBindingParams> extends
        RuntimeParams<B, EventSink<B>> {

  public EventSinkRuntimeParams(Supplier<EventSink<B>> supplier, B bindingParams) {
    super(supplier, bindingParams);
  }

  @Override
  public void bindEngine() throws SpRuntimeException {
    engine.bind(bindingParams);
  }

  public void discardEngine() {
    engine.discard();
  }




}
