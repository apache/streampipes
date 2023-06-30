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
package org.apache.streampipes.messaging.mqtt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;

import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

import java.io.Serializable;

public class MqttConsumer extends AbstractMqttConnector implements
    EventConsumer,
    AutoCloseable, Serializable {

  public MqttConsumer(MqttTransportProtocol protocol) {
    super(protocol);
  }

  @Override
  public void connect(InternalEventProcessor<byte[]> eventProcessor)
      throws SpRuntimeException {

    try {
      this.createBrokerConnection(protocol);
      Topic[] topics = {new Topic(protocol.getTopicDefinition().getActualTopicName(), QoS.AT_LEAST_ONCE)};
      connection.subscribe(topics);
      new Thread(new ConsumerThread(eventProcessor)).start();

    } catch (Exception e) {
      throw new SpRuntimeException(e);
    }
  }

  private class ConsumerThread implements Runnable {

    private final InternalEventProcessor<byte[]> eventProcessor;

    public ConsumerThread(InternalEventProcessor<byte[]> eventProcessor) {
      this.eventProcessor = eventProcessor;
    }

    @Override
    public void run() {
      try {
        while (connected) {
          Message message = connection.receive();
          byte[] payload = message.getPayload();
          eventProcessor.onEvent(payload);
          message.ack();
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      this.connection.disconnect();
    } catch (Exception e) {
      throw new SpRuntimeException(e);
    } finally {
      this.connected = false;
    }
  }

  @Override
  public boolean isConnected() {
    return this.connected;
  }

  @Override
  public void close() throws Exception {
    disconnect();
  }
}
