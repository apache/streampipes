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

package org.streampipes.wrapper.standalone.param;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.model.SpDataStream;
import org.streampipes.wrapper.params.binding.EventSinkBindingParams;
import org.streampipes.wrapper.params.runtime.EventSinkRuntimeParams;
import org.streampipes.wrapper.routing.SpInputCollector;
import org.streampipes.wrapper.runtime.EventSink;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class StandaloneEventSinkRuntimeParams<B extends EventSinkBindingParams> extends
        EventSinkRuntimeParams<B> {

  Boolean singletonEngine;

  public StandaloneEventSinkRuntimeParams(Supplier<EventSink<B>> supplier, B bindingParams, Boolean
          singletonEngine) {
    super(supplier, bindingParams);
    this.singletonEngine = singletonEngine;
  }

  @Override
  public List<SpInputCollector> getInputCollectors() throws SpRuntimeException {
    List<SpInputCollector> inputCollectors = new ArrayList<>();
    for (SpDataStream is : bindingParams.getGraph().getInputStreams()) {
      inputCollectors.add(ProtocolManager.findInputCollector(is.getEventGrounding()
                      .getTransportProtocol(), is.getEventGrounding().getTransportFormats().get(0),
              singletonEngine));
    }
    return inputCollectors;
  }

  public Boolean isSingletonEngine() {
    return singletonEngine;
  }
}
