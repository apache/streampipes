/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.streampipes.wrapper.standalone.routing;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.routing.RawDataProcessor;
import org.apache.streampipes.extensions.api.pe.routing.SpInputCollector;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.TransportFormat;
import org.apache.streampipes.model.grounding.TransportProtocol;
import org.apache.streampipes.wrapper.standalone.manager.ProtocolManager;

public class StandaloneSpInputCollector<T extends TransportProtocol> extends
    StandaloneSpCollector<T, RawDataProcessor>
    implements
    InternalEventProcessor<byte[]>, SpInputCollector {

  private final Boolean singletonEngine;
  private final EventConsumer consumer;

  public StandaloneSpInputCollector(T protocol,
                                    TransportFormat format,
                                    Boolean singletonEngine) throws SpRuntimeException {
    super(protocol, format);
    this.consumer = protocolDefinition.getConsumer(protocol);
    this.singletonEngine = singletonEngine;
  }

  @Override
  public void onEvent(byte[] event) {
    if (singletonEngine) {
      send(consumers.get(consumers.keySet().toArray()[0]), event);
    } else {
      consumers.forEach((key, value) -> send(value, event));
    }
  }

  private void send(RawDataProcessor rawDataProcessor, byte[] event) {
    rawDataProcessor.process(dataFormatDefinition.toMap(event), topic);
  }

  @Override
  public void connect() throws SpRuntimeException {
    if (!consumer.isConnected()) {
      consumer.connect(this);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    if (consumer.isConnected()) {
      if (consumers.size() == 0) {
        consumer.disconnect();
        ProtocolManager.removeInputCollector(transportProtocol);
      }
    }
  }
}
