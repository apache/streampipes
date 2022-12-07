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
package org.apache.streampipes.connect.iiot.protocol.stream.rocketmq;

import org.apache.streampipes.messaging.InternalEventProcessor;

import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;

import java.io.IOException;

public class RocketMQConsumer implements Runnable {

  private InternalEventProcessor<byte[]> eventProcessor;
  private String brokerUrl;
  private String topic;
  private String consumerGroup;

  private volatile boolean running = false;

  private PushConsumer consumer;

  public RocketMQConsumer(String brokerUrl, String topic, String consumerGroup,
                          InternalEventProcessor<byte[]> eventProcessor) {
    this.brokerUrl = brokerUrl;
    this.topic = topic;
    this.consumerGroup = consumerGroup;
    this.eventProcessor = eventProcessor;
  }

  @Override
  public void run() {
    try {
      this.consumer = RocketMQUtils.createConsumer(brokerUrl, topic, consumerGroup, messageView -> {
        eventProcessor.onEvent(messageView.getBody().array());
        return ConsumeResult.SUCCESS;
      });
    } catch (ClientException e) {
      e.printStackTrace();
      return;
    }
    this.running = true;
  }

  public void stop() throws IOException {
    this.running = false;
    this.consumer.close();
  }

}
