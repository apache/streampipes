package org.streampipes.wrapper.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public interface EventSink<B extends EventSinkBindingParams> extends PipelineElement<B> {

  void bind(B parameters) throws SpRuntimeException;

}
