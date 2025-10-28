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
import org.apache.streampipes.extensions.connectors.mqtt.adapter.MqttProtocol;
import org.apache.streampipes.messaging.InternalEventProcessor;

import org.fusesource.mqtt.client.BlockingConnection;
import org.fusesource.mqtt.client.MQTT;
import org.fusesource.mqtt.client.Message;
import org.fusesource.mqtt.client.QoS;
import org.fusesource.mqtt.client.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MqttConsumer implements Runnable {

  private final InternalEventProcessor<byte[]> consumer;
  private boolean running;
  private int maxElementsToReceive = -1;
  private int messageCount = 0;

  private final MqttConfig mqttConfig;

  private static final Logger LOG = LoggerFactory.getLogger(MqttProtocol.class);

  public MqttConsumer(MqttConfig mqttConfig,
                      InternalEventProcessor<byte[]> consumer) {
    this.mqttConfig = mqttConfig;
    this.consumer = consumer;
  }

  public MqttConsumer(MqttConfig mqttConfig,
                      InternalEventProcessor<byte[]> consumer,
                      int maxElementsToReceive) {
    this(mqttConfig, consumer);
    this.maxElementsToReceive = maxElementsToReceive;
  }

  @Override
  public void run() {
    this.running = true;
    MQTT mqtt = new MQTT();
    LOG.info("TLS Enabled "+ mqttConfig.getTlsEnabled());
    try {
      mqtt.setHost(mqttConfig.getUrl());
      mqtt.setConnectAttemptsMax(1);
      if (mqttConfig.getAuthenticated()) {
        mqtt.setUserName(mqttConfig.getUsername());
        mqtt.setPassword(mqttConfig.getPassword());
      }
     if (mqttConfig.getTlsEnabled()) {
      //mqttConfig.getTlsEnabled() Currently for testing purposes set to True by default 
  try {
    // Create a TrustManager that trusts all certificates (for development or self-signed certs)
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }
          public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
          public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
        }
    };

    // Initialize SSLContext with the trust-all manager
    SSLContext sslContext = SSLContext.getInstance("TLS");
    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

    // Configure MQTT to use SSL/TLS
    mqtt.setSslContext(sslContext);

    // Optionally: force secure port (usually 8883)
    //if (!mqttConfig.getUrl().startsWith("ssl://")) {
    //  mqtt.setHost("ssl://" + mqttConfig.getUrl());
   // }

  } catch (Exception e) {
    throw new RuntimeException("Failed to initialize TLS for MQTT", e);
  }
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
      throw new RuntimeException("Error when receiving data from MQTT", e);
    }
  }

  public void close() {
    this.running = false;
  }

  public Integer getMessageCount() {
    return messageCount;
  }
}
