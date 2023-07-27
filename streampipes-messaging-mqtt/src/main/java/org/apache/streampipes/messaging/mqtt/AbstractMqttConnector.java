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

import org.apache.streampipes.model.grounding.MqttTransportProtocol;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;

public class AbstractMqttConnector {

  protected MQTT mqtt;
  protected BlockingConnection connection;
  protected boolean connected = false;

  protected final MqttTransportProtocol protocol;

  public AbstractMqttConnector(MqttTransportProtocol protocol) {
    this.protocol = protocol;
  }

  protected void createBrokerConnection(MqttTransportProtocol protocolSettings) throws Exception {
    this.mqtt = new MQTT();
    this.mqtt.setHost(makeBrokerUrl(protocolSettings));
    this.connection = mqtt.blockingConnection();
    this.connection.connect();
    this.connected = true;
  }

  private String makeBrokerUrl(MqttTransportProtocol protocolSettings) {
    return "tcp://" + protocolSettings.getBrokerHostname() + ":" + protocolSettings.getPort();
  }

}
