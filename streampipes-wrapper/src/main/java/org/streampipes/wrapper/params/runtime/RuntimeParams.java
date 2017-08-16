package org.streampipes.wrapper.params.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.PipelineElement;

import java.util.List;
import java.util.function.Supplier;

public abstract class RuntimeParams<B extends BindingParams, P extends PipelineElement<B>> {

  protected final B bindingParams;
  protected final P engine;


  public RuntimeParams(Supplier<P> supplier, B bindingParams)
  {
    this.engine = supplier.get();
    this.bindingParams = bindingParams;
  }

  public P getEngine() {
    return engine;
  }

  public B getBindingParams() {
    return bindingParams;
  }

  public abstract void bindEngine() throws SpRuntimeException;

  public abstract void discardEngine() throws SpRuntimeException;

  public abstract List<SpInputCollector> getInputCollectors() throws
          SpRuntimeException;


}
