package org.streampipes.wrapper.runtime;

import org.streampipes.wrapper.params.binding.BindingParams;

public interface PipelineElement<B extends BindingParams> {

  void bind(B parameters);

  void discard();
}
