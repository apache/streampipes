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
package org.apache.streampipes.sinks.brokers.jvm.mqtt;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.sinks.brokers.jvm.mqtt.common.MqttOptions;
import org.apache.streampipes.sinks.brokers.jvm.mqtt.common.MqttUtils;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;

import java.net.URI;

public class MqttClient {

  private final MqttOptions options;
  private URI uri;
  private MQTT mqtt;
  private BlockingConnection conn;

  public MqttClient(SinkParams params) {
    this.options = new MqttOptions(params);
    this.createMqttClient();
  }

  /**
   * Create new MQTT client
   */
  public void createMqttClient() {
    this.mqtt = new MQTT();
    this.uri = MqttUtils.makeMqttServerUri(options.getProtocol(), options.getHost(), options.getPort());
    try {
      /**
       * Sets the url for connecting to the MQTT broker, e.g. {@code: tcp://localhost:1883}.
       */
      mqtt.setHost(uri);

      // authentication
      if (options.isBasicAuth()) {
        /**
         * The username for authenticated sessions.
         */
        mqtt.setUserName(options.getUsername());
        /**
         * The password for authenticated sessions.
         */
        mqtt.setPassword(options.getPassword());
      }

      /**
       * The client id used when connecting to the MQTT broker.
       */
      mqtt.setClientId(options.getClientId());

      /**
       * Set to false if you want the MQTT server to persist topic subscriptions and ack positions across
       * client sessions. Defaults to true.
       */
      mqtt.setCleanSession(options.isCleanSession());

      /**
       * The maximum amount of time in ms to wait between reconnect attempts. Defaults to 30,000.
       */
      mqtt.setReconnectDelayMax(options.getReconnectDelayMaxInMs());

      /**
       * Configures the Keep Alive timer in seconds. Defines the maximum time interval between messages
       * received from a client. It enables the server to detect that the network connection to a client has
       * dropped, without having to wait for the long TCP/IP timeout.
       */
      mqtt.setKeepAlive(options.getKeepAliveInSec());

      /**
       * Set to "3.1.1" to use MQTT version 3.1.1. Otherwise defaults to the 3.1 protocol version.
       */
      mqtt.setVersion(options.getMqttProtocolVersion());

      // last will and testament options
      if (options.isLastWill()) {
        /**
         * If set the server will publish the client's Will message to the specified topics if the client has
         * an unexpected disconnection.
         */
        mqtt.setWillTopic(options.getWillTopic());

        /**
         * Sets the quality of service to use for the Will message. Defaults to QoS.AT_MOST_ONCE.
         */
        mqtt.setWillQos(options.getWillQoS());

        /**
         * The Will message to send. Defaults to a zero length message.
         */
        mqtt.setWillMessage(options.getWillMessage());

        /**
         * Set to true if you want the Will to be published with the retain option.
         */
        mqtt.setWillRetain(options.getWillRetain());
      }
    } catch (Exception e) {
      throw new SpRuntimeException("Failed to initialize MQTT Client: " + e.getMessage(), e);
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
          + uri.toString() + ", " + e.getMessage(), e);
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
      this.conn.publish(options.getTopic(), payload, options.getQos(), options.isRetain());
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
