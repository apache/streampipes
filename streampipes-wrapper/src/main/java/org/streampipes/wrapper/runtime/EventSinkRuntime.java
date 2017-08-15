package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;

public abstract class EventSinkRuntime extends
        PipelineElementRuntime<EventSinkRuntimeParams<?>> {

  public EventSinkRuntime(EventSinkRuntimeParams<?> params) {
    super(params);
  }
}
