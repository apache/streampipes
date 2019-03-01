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
package org.streampipes.wrapper.standalone.runtime;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.model.base.InvocableStreamPipesEntity;
import org.streampipes.wrapper.context.RuntimeContext;
import org.streampipes.wrapper.params.binding.BindingParams;
import org.streampipes.wrapper.params.runtime.RuntimeParams;
import org.streampipes.wrapper.routing.RawDataProcessor;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.PipelineElement;
import org.streampipes.wrapper.runtime.PipelineElementRuntime;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public abstract class StandalonePipelineElementRuntime<B extends BindingParams<I>,
        I extends InvocableStreamPipesEntity,
        RP extends RuntimeParams<B, I, RC>,
        RC extends RuntimeContext,
        P extends PipelineElement<B, I>>
        extends PipelineElementRuntime implements RawDataProcessor {

  protected RP params;
  protected final P engine;

  public StandalonePipelineElementRuntime(Supplier<P> supplier, RP runtimeParams) {
    super();
    this.engine = supplier.get();
    this.params = runtimeParams;
  }

  public P getEngine() {
    return engine;
  }

  public void discardEngine() throws SpRuntimeException {
    engine.onDetach();
  }

  public List<SpInputCollector> getInputCollectors() throws SpRuntimeException {
    List<SpInputCollector> inputCollectors = new ArrayList<>();
    for (SpDataStream is : params.getBindingParams().getGraph().getInputStreams()) {
      inputCollectors.add(ProtocolManager.findInputCollector(is.getEventGrounding()
                      .getTransportProtocol(), is.getEventGrounding().getTransportFormats().get(0),
              params.isSingletonEngine()));
    }
    return inputCollectors;
  }

  public abstract void bindEngine() throws SpRuntimeException;


}
