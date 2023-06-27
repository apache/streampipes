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
import org.apache.streampipes.messaging.EventProducer;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;

import org.fusesource.mqtt.client.QoS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttPublisher extends AbstractMqttConnector implements EventProducer {

  private static final Logger LOG = LoggerFactory.getLogger(MqttPublisher.class);

  private String currentTopic;

  public MqttPublisher(MqttTransportProtocol protocol) {
    super(protocol);
  }

  @Override
  public void connect() throws SpRuntimeException {
    try {
      this.createBrokerConnection(protocol);
      this.currentTopic = protocol.getTopicDefinition().getActualTopicName();
    } catch (Exception e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public void publish(byte[] event) {
    if (connected && currentTopic != null) {
      try {
        this.connection.publish(currentTopic, event, QoS.AT_LEAST_ONCE, false);
      } catch (Exception e) {
        // TODO exception handling once system-wide logging is implemented
        LOG.error(e.getMessage());
      }
    }
  }

  @Override
  public void disconnect() throws SpRuntimeException {
    try {
      this.connection.disconnect();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      this.connected = false;
    }
  }

  @Override
  public boolean isConnected() {
    return connected;
  }
}
