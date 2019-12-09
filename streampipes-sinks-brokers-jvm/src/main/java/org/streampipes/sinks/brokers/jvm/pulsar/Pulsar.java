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
package org.streampipes.sinks.brokers.jvm.pulsar;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.dataformat.SpDataFormatDefinition;
import org.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.streampipes.model.runtime.Event;
import org.streampipes.wrapper.context.EventSinkRuntimeContext;
import org.streampipes.wrapper.runtime.EventSink;

import java.util.Map;

public class Pulsar implements EventSink<PulsarParameters> {

  private static final String PulsarScheme = "pulsar://";
  private static final String Colon = ":";

  private Producer<byte[]> producer;
  private PulsarClient pulsarClient;
  private SpDataFormatDefinition spDataFormatDefinition;

  public Pulsar() {
    this.spDataFormatDefinition = new JsonDataFormatDefinition();
  }

  @Override
  public void onInvocation(PulsarParameters params,
                           EventSinkRuntimeContext eventSinkRuntimeContext) throws SpRuntimeException {
    try {
     this.pulsarClient = PulsarClient.builder()
              .serviceUrl(makePulsarUrl(params.getPulsarHost(), params.getPulsarPort()))
              .build();

      this.producer = this.pulsarClient.newProducer()
              .topic(params.getTopic())
              .create();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    Map<String, Object> rawMap = event.getRaw();
    byte[] jsonMessage = this.spDataFormatDefinition.fromMap(rawMap);

    try {
      this.producer.send(jsonMessage);
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    try {
      this.pulsarClient.close();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  private String makePulsarUrl(String hostname, Integer port) {
    return PulsarScheme + hostname + Colon + port;
  }
}
