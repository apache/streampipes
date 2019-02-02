/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.wrapper.params.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.PipelineElement;

import java.util.List;
import java.util.function.Supplier;

public abstract class RuntimeParams<B extends BindingParams<I>, I extends
        InvocableStreamPipesEntity, P extends
        PipelineElement<B, I>> {

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
