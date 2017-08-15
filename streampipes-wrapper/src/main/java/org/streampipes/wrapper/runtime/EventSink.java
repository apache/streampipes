package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.binding.EventSinkBindingParams;

public interface EventSink<B extends EventSinkBindingParams> extends PipelineElement<B> {

  void bind(B parameters);

}
