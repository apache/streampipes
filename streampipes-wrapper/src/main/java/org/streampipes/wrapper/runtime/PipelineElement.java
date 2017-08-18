package org.streampipes.wrapper.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.BindingParams;

import java.util.Map;

public interface PipelineElement<B extends BindingParams> {

  void onEvent(Map<String, Object> event, String sourceInfo);

  void discard() throws SpRuntimeException;
}
