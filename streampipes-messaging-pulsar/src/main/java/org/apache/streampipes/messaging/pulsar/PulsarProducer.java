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

package org.apache.streampipes.messaging.pulsar;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.io.Serializable;

public class PulsarProducer implements EventProducer, Serializable {

  private PulsarClient pulsarClient;
  private Producer<byte[]> producer;
  private PulsarTransportProtocol protocolSettings;

  public PulsarProducer(PulsarTransportProtocol protocolSettings) {
    this.protocolSettings = protocolSettings;
  }

  @Override
  public void connect() throws SpRuntimeException {
    try {
      pulsarClient = PulsarClient.builder()
          .serviceUrl(protocolSettings.getBrokerHostname())
          .build();
      producer = pulsarClient.newProducer()
          .topic(protocolSettings.getTopicDefinition().getActualTopicName())
          .create();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void publish(byte[] event) throws SpRuntimeException {
    try {
      producer.send(event);
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      producer.close();
      pulsarClient.close();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public boolean isConnected() {
    return producer != null && producer.isConnected();
  }
}
