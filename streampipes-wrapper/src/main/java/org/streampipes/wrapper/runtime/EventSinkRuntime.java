package org.streampipes.wrapper.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;

public abstract class EventSinkRuntime extends
        PipelineElementRuntime<EventSinkRuntimeParams<?>> {

  public EventSinkRuntime(EventSinkRuntimeParams<?> params) {
    super(params);
  }

  @Override
  public void discardRuntime() throws SpRuntimeException {
    params.getInputCollectors().forEach(is -> is.unregisterConsumer(instanceId));
    params.discardEngine();
    postDiscard();
  }

  @Override
  public void bindRuntime() throws SpRuntimeException {
    params.bindEngine();
    params.getInputCollectors().forEach(is -> is.registerConsumer(instanceId, params.getEngine()));
    initRuntime();
  }
}
