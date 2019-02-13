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

package org.streampipes.wrapper.standalone.routing;

import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.messaging.InternalEventProcessor;
import org.streampipes.model.grounding.TransportFormat;
import org.streampipes.model.grounding.TransportProtocol;
import org.streampipes.model.runtime.Event;
import org.streampipes.model.runtime.EventConverter;
import org.streampipes.wrapper.routing.SpOutputCollector;
import org.streampipes.wrapper.standalone.manager.ProtocolManager;

import java.util.Map;

public class StandaloneSpOutputCollector<T extends TransportProtocol> extends
        StandaloneSpCollector<T, InternalEventProcessor<Map<String,
                Object>>> implements SpOutputCollector {


  public StandaloneSpOutputCollector(T protocol, TransportFormat format) throws SpRuntimeException {
   super(protocol, format);
  }

  public void collect(Event event) {
    Map<String, Object> outEvent = new EventConverter(event).toMap();
    try {
      protocolDefinition.getProducer().publish(dataFormatDefinition.fromMap(outEvent));
    } catch (SpRuntimeException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void connect() throws SpRuntimeException {
    if (!protocolDefinition.getProducer().isConnected()) {
      protocolDefinition.getProducer().connect(transportProtocol);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    if (protocolDefinition.getProducer().isConnected()) {
      protocolDefinition.getProducer().disconnect();
      ProtocolManager.removeOutputCollector(transportProtocol);
    }
  }

}
