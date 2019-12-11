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
package org.apache.streampipes.connect.protocol.stream.pulsar;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.apache.pulsar.client.api.SubscriptionType;
import org.apache.streampipes.messaging.InternalEventProcessor;

public class PulsarConsumer implements Runnable {

  private InternalEventProcessor<byte[]> adapterConsumer;
  private String brokerUrl;
  private String topic;
  private Integer maxElementsToReceive = -1;
  private Integer messageCount = 0;
  private Boolean running = true;

  private PulsarClient pulsarClient;


  public PulsarConsumer(String brokerUrl, String topic, InternalEventProcessor<byte[]> adapterConsumer) {
    this.brokerUrl = brokerUrl;
    this.topic = topic;
    this.adapterConsumer = adapterConsumer;
  }

  public PulsarConsumer(String brokerUrl, String topic, InternalEventProcessor<byte[]> adapterConsumer,
                        int maxElementsToReceive) {
    this(brokerUrl, topic, adapterConsumer);
    this.maxElementsToReceive = maxElementsToReceive;
  }

  @Override
  public void run() {
    this.running = true;
    try {
      this.pulsarClient = PulsarUtils.makePulsarClient(this.brokerUrl);

      Consumer<byte[]> consumer = this.pulsarClient.newConsumer()
              .topic(this.topic)
              .subscriptionName(RandomStringUtils.randomAlphanumeric(10))
              .subscriptionType(SubscriptionType.Shared)
              .subscribe();

      while (running && ((maxElementsToReceive == -1) || (this.messageCount <= maxElementsToReceive))) {
        Message msg = consumer.receive();
        adapterConsumer.onEvent(msg.getData());
        consumer.acknowledge(msg);
        this.messageCount++;
      }

      consumer.close();
      this.pulsarClient.close();
    } catch (PulsarClientException e) {
      e.printStackTrace();
    }
  }

  public void stop() throws PulsarClientException {
    this.running = false;
    this.pulsarClient.close();
  }

  public Integer getMessageCount() {
    return this.messageCount;
  }


}
