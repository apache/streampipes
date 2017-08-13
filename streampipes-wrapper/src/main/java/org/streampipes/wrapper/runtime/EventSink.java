package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

import java.util.Map;

public interface EventSink<B extends EventSinkBindingParams> extends PipelineElement<B> {

  void onEvent(Map<String, Object> event, String sourceInfo);

}
