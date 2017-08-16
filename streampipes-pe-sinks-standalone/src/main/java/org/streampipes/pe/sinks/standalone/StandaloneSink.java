package org.streampipes.pe.sinks.standalone;

import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class StandaloneSink<T extends EventSinkBindingParams> implements EventSink<T>  {

  @Override
  public void bind(T parameters) {

  }

  @Override
  public void onEvent(Map<String, Object> event, String sourceInfo) {

  }

  @Override
  public void discard() {

  }
}
