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
package org.apache.streampipes.pe.shared.config.mqtt;

import org.apache.streampipes.messaging.InternalEventProcessor;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class MqttConsumer implements Runnable {

  private InternalEventProcessor<byte[]> consumer;
  private boolean running;
  private int maxElementsToReceive = -1;
  private int messageCount = 0;

  private MqttConfig mqttConfig;

  public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer) {
    this.mqttConfig = mqttConfig;
    this.consumer = consumer;
  }

  public MqttConsumer(MqttConfig mqttConfig, InternalEventProcessor<byte[]> consumer,
                      int maxElementsToReceive) {
    this(mqttConfig, consumer);
    this.maxElementsToReceive = maxElementsToReceive;
  }

  @Override
  public void run() {
    this.running = true;
    MQTT mqtt = new MQTT();
    try {
      mqtt.setHost(mqttConfig.getUrl());
      if (mqttConfig.getAuthenticated()) {
        mqtt.setUserName(mqttConfig.getUsername());
        mqtt.setPassword(mqttConfig.getPassword());
      }
      BlockingConnection connection = mqtt.blockingConnection();
      connection.connect();
      Topic[] topics = {new Topic(mqttConfig.getTopic(), QoS.AT_LEAST_ONCE)};
      byte[] qoses = connection.subscribe(topics);

      while (running && ((maxElementsToReceive == -1) || (this.messageCount <= maxElementsToReceive))) {
        Message message = connection.receive();
        byte[] payload = message.getPayload();
        consumer.onEvent(payload);
        message.ack();
        this.messageCount++;
      }
      connection.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void close() {
    this.running = false;
  }

  public Integer getMessageCount() {
    return messageCount;
  }
}
