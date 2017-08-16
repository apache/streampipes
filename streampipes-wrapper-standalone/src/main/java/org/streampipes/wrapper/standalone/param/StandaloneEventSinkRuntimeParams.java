package org.streampipes.wrapper.standalone.param;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.List;
import java.util.function.Supplier;

public class StandaloneEventSinkRuntimeParams<B extends EventSinkBindingParams> extends
        EventSinkRuntimeParams<B> {

  Boolean singletonEngine;

  public StandaloneEventSinkRuntimeParams(Supplier<EventSink<B>> supplier, B bindingParams, Boolean
          singletonEngine) {
    super(supplier, bindingParams);
    this.singletonEngine = singletonEngine;
  }

  @Override
  public List<SpInputCollector> getInputCollectors() throws SpRuntimeException {
    return null;
  }

  public Boolean isSingletonEngine() {
    return singletonEngine;
  }
}
