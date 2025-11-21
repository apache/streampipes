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
package org.apache.streampipes.extensions.connectors.mqtt.shared;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.model.runtime.Event;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URI;


public class MqttPublisher extends MqttBase {

  private URI uri;
  private MQTT mqtt;
  private BlockingConnection conn;

  private static final Logger LOG = LoggerFactory.getLogger(MqttPublisher.class);

  public MqttPublisher(IDataSinkParameters params) {

    super(MqttConnectUtils.extractDataSinkParams(params.extractor()));
       try {
    this.mqtt = super.setupMqttClient();

       } catch (Exception e) {
            LOG.error("Error in MQTT consumer: ", e);
            throw new RuntimeException("Error when receiving data from MQTT", e);
        }

  }

  /**
   * Start blocking connection to MQTT broker.
   */
  public void connect() {
    try {
      this.conn = mqtt.blockingConnection();
      this.conn.connect();
    } catch (Exception e) {
      throw new SpRuntimeException("Could not connect to MQTT broker: "
          + mqtt.getHost() + ", " + e.getMessage(), e);
    }
  }

  /**
   * Publish received event to MQTT broker.
   *
   * @param event event to be published
   */
  public void publish(Event event) {
    JsonDataFormatDefinition dataFormatDefinition = new JsonDataFormatDefinition();
    byte[] payload = new String(dataFormatDefinition.fromMap(event.getRaw())).getBytes();
    try {
      this.conn.publish(super.mqttConfig.getTopic(), payload, super.mqttConfig.getQos(), super.mqttConfig.isRetain());
    } catch (Exception e) {
      throw new SpRuntimeException("Could not publish to MQTT broker: "
          + uri.toString() + ", " + e.getMessage(), e);
    }
  }

  /**
   * Disconnect from MQTT broker.
   */
  public void disconnect() {
    try {
      if (this.conn.isConnected()) {
        this.conn.disconnect();
      }
    } catch (Exception e) {
      throw new SpRuntimeException("Could not disconnect from MQTT broker: "
          + uri.toString() + ", " + e.getMessage(), e);
    }
  }

}
