/*
 *   Licensed to the Apache Software Foundation (ASF) under one or more
 *   contributor license agreements.  See the NOTICE file distributed with
 *   this work for additional information regarding copyright ownership.
 *   The ASF licenses this file to You under the Apache License, Version 2.0
 *   (the "License"); you may not use this file except in compliance with
 *   the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.apache.streampipes.messaging.mqtt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.EventConsumer;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;

public class MqttConsumer extends AbstractMqttConnector implements EventConsumer<MqttTransportProtocol> {

  @Override
  public void connect(MqttTransportProtocol protocolSettings, InternalEventProcessor<byte[]> eventProcessor) throws SpRuntimeException {
    try {
      this.createBrokerConnection(protocolSettings);
      Topic[] topics = {new Topic(protocolSettings.getTopicDefinition().getActualTopicName(), QoS.AT_LEAST_ONCE)};
      byte[] qoses = connection.subscribe(topics);

      while (connected) {
        Message message = connection.receive();
        byte[] payload = message.getPayload();
        eventProcessor.onEvent(payload);
        message.ack();
      }
    } catch (Exception e) {
      throw new SpRuntimeException(e);
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
  public Boolean isConnected() {
    return this.connected;
  }
}
