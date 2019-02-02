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
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.wrapper.params.binding.EventProcessorBindingParams;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.runtime.EventProcessor;

import java.util.function.Supplier;

public abstract class EventProcessorRuntimeParams<B extends EventProcessorBindingParams> extends
        RuntimeParams<B, DataProcessorInvocation, EventProcessor<B>> { // B - Bind Type


  public EventProcessorRuntimeParams(Supplier<EventProcessor<B>> supplier,
                                     B bindingParams) {
    super(supplier, bindingParams);
  }

  public void bindEngine() throws SpRuntimeException {
    engine.bind(bindingParams, getOutputCollector());
  }

  public void discardEngine() throws SpRuntimeException {
    engine.discard();
  }

  public abstract SpOutputCollector getOutputCollector()
          throws
          SpRuntimeException;

}
