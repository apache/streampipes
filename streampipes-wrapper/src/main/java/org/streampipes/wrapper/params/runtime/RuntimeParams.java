package org.streampipes.wrapper.params.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.runtime.PipelineElement;

public abstract class RuntimeParams<B extends BindingParams, P extends PipelineElement<B>> {

  protected final B bindingParams;


  public RuntimeParams(B bindingParams)
  {
    this.bindingParams = bindingParams;
  }

  public B getBindingParams() {
    return bindingParams;
  }

  public abstract void bindEngine() throws SpRuntimeException;

  public abstract void discardEngine() throws SpRuntimeException;


}
