package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.EventSinkRuntime;

public class StandaloneEventSinkRuntime extends EventSinkRuntime {

  public StandaloneEventSinkRuntime(EventSinkRuntimeParams<?> params) {
    super(params);
  }

  @Override
  public void initRuntime() throws SpRuntimeException {
    for (SpInputCollector spInputCollector : params.getInputCollectors()) {
      spInputCollector.connect();
    }

  }

  @Override
  public void postDiscard() throws SpRuntimeException {
    for(SpInputCollector spInputCollector : params.getInputCollectors()) {
      spInputCollector.disconnect();
    }
  }

}
