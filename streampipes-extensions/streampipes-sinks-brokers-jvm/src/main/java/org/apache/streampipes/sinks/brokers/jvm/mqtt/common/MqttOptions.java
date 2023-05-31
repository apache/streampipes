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
package org.apache.streampipes.sinks.brokers.jvm.mqtt.common;

import org.apache.streampipes.wrapper.params.compat.SinkParams;

import org.fusesource.mqtt.client.QoS;

import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.AUTH_ALTERNATIVE;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.AUTH_MODE;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.CLEAN_SESSION_KEY;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.ENCRYPTION_MODE;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.HOST;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.KEEP_ALIVE_IN_SEC;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.MQTT_COMPLIANT;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.PASSWORD;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.PORT;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.QOS_LEVEL_KEY;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.RECONNECT_PERIOD_IN_SEC;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.RETAIN;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.TOPIC;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.USERNAME;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.WILL_ALTERNATIVE;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.WILL_MESSAGE;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.WILL_MODE;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.WILL_QOS;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.WILL_RETAIN;
import static org.apache.streampipes.sinks.brokers.jvm.mqtt.MqttPublisherSink.WILL_TOPIC;

public class MqttOptions {

  private final String clientId;
  private final String host;
  private final int port;
  private final String topic;
  private final String protocol;
  private final QoS qos;
  private final long reconnectDelayMaxInMs;
  private final boolean cleanSession;
  private final boolean retain;
  private final short keepAliveInSec;

  private String username = "";
  private String password = "";
  private boolean isBasicAuth = false;
  private boolean isLastWill = false;
  private QoS willQoS = QoS.AT_MOST_ONCE;
  private Boolean willRetain = false;
  private String willTopic = "";
  private String willMessage = "";
  private String mqttProtocolVersion = "3.1";

  public MqttOptions(SinkParams params) {
    var extract = params.extractor();

    this.clientId = MqttUtils.runningInstanceId(params.getModel().getElementId());
    this.topic = extract.singleValueParameter(TOPIC, String.class);
    this.host = extract.singleValueParameter(HOST, String.class);
    this.port = extract.singleValueParameter(PORT, Integer.class);
    this.protocol = extract.selectedSingleValue(ENCRYPTION_MODE, String.class);

    this.qos = MqttUtils.extractQoSFromString(extract.selectedSingleValue(QOS_LEVEL_KEY, String.class));
    this.reconnectDelayMaxInMs =
        MqttUtils.fromSecToMs(extract.singleValueParameter(RECONNECT_PERIOD_IN_SEC, Long.class));
    this.keepAliveInSec = extract.singleValueParameter(KEEP_ALIVE_IN_SEC, Short.class);
    this.cleanSession = MqttUtils.extractBoolean(extract.selectedSingleValue(CLEAN_SESSION_KEY, String.class));
    this.retain = MqttUtils.extractBoolean(extract.selectedSingleValue(RETAIN, String.class));

    boolean isCompliant = MqttUtils.extractBoolean(extract.selectedSingleValue(MQTT_COMPLIANT, String.class));
    if (isCompliant) {
      this.mqttProtocolVersion = "3.1.1";
    }

    String accessMode = extract.selectedAlternativeInternalId(AUTH_MODE);
    if (accessMode.equals(AUTH_ALTERNATIVE)) {
      this.isBasicAuth = true;
      this.username = extract.singleValueParameter(USERNAME, String.class);
      this.password = extract.secretValue(PASSWORD);
    }

    String willMode = extract.selectedAlternativeInternalId(WILL_MODE);
    if (willMode.equals(WILL_ALTERNATIVE)) {
      this.isLastWill = true;
      this.willTopic = extract.singleValueParameter(WILL_TOPIC, String.class);
      this.willMessage = extract.singleValueParameter(WILL_MESSAGE, String.class);
      this.willQoS = MqttUtils.extractQoSFromString(extract.selectedSingleValue(WILL_QOS, String.class));
      this.willRetain = MqttUtils.extractBoolean(extract.selectedSingleValue(WILL_RETAIN, String.class));
    }
  }

  public String getClientId() {
    return clientId;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public String getTopic() {
    return topic;
  }

  public String getProtocol() {
    return protocol;
  }

  public QoS getQos() {
    return qos;
  }

  public long getReconnectDelayMaxInMs() {
    return reconnectDelayMaxInMs;
  }

  public boolean isCleanSession() {
    return cleanSession;
  }

  public boolean isRetain() {
    return retain;
  }

  public short getKeepAliveInSec() {
    return keepAliveInSec;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isBasicAuth() {
    return isBasicAuth;
  }

  public boolean isLastWill() {
    return isLastWill;
  }

  public QoS getWillQoS() {
    return willQoS;
  }

  public Boolean getWillRetain() {
    return willRetain;
  }

  public String getWillTopic() {
    return willTopic;
  }

  public String getWillMessage() {
    return willMessage;
  }

  public String getMqttProtocolVersion() {
    return mqttProtocolVersion;
  }
}
