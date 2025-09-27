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
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarConsumer implements EventConsumer {

  private PulsarClient pulsarClient;
  private Consumer<byte[]> consumer;
  private PulsarTransportProtocol protocolSettings;

  public PulsarConsumer(PulsarTransportProtocol protocolSettings) {
    this.protocolSettings = protocolSettings;
  }

  @Override
  public void connect(InternalEventProcessor<byte[]> eventProcessor) throws SpRuntimeException {
    try {
      String serviceURL = "";
      if (!protocolSettings.getBrokerHostname().startsWith("pulsar://")) {
        serviceURL = "pulsar://" + protocolSettings.getBrokerHostname();
      } else {
        serviceURL = protocolSettings.getBrokerHostname();
      }
      pulsarClient = PulsarClient.builder()
          .serviceUrl(serviceURL)
          .build();
      consumer = pulsarClient.newConsumer()
          .topic(protocolSettings.getTopicDefinition().getActualTopicName())
          .subscriptionName("streampipes")
          .messageListener(new MessageListener<byte[]>() {
            @Override
            public void received(Consumer<byte[]> consumer, Message<byte[]> msg) {
              eventProcessor.onEvent(msg.getData());
            }
          })
          .subscribe();
    } catch (PulsarClientException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      consumer.close();
      pulsarClient.close();
    } catch (PulsarClientException e) {
      // throw new SpRuntimeException(e);
    }
  }

  @Override
  public boolean isConnected() {
    if (consumer == null) {
      return false;
    }
    return consumer.isConnected();
  }
}
