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
package org.apache.streampipes.extensions.connectors.mqtt.sink.common;

import org.apache.streampipes.extensions.api.pe.param.IDataSinkParameters;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;

import org.fusesource.mqtt.client.QoS;

import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.ACCESS_MODE;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.BROKER_URL;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.CLEAN_SESSION_KEY;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.CLIENTCERT;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.CLIENTKEY;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.KEEP_ALIVE_IN_SEC;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.MQTT_COMPLIANT;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.PASSWORD;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.QOS_LEVEL_KEY;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.RECONNECT_PERIOD_IN_SEC;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.RETAIN;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.TOPIC;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.USERNAME;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.USERNAME_ACCESS;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.WILL_ALTERNATIVE;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.WILL_MESSAGE;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.WILL_MODE;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.WILL_QOS;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.WILL_RETAIN;
import static org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils.WILL_TOPIC;




public class MqttOptions {

  private final String clientId;
  private final String broker;
  private final String topic;
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
  private String clientCertificate = null;
  private String clientKey = null;

  public MqttOptions(IDataSinkParameters params) {
    var extract = params.extractor();

    this.clientId = MqttConnectUtils.runningInstanceId(params.getModel().getElementId());
    this.topic = extract.singleValueParameter(TOPIC, String.class);
    this.broker = extract.singleValueParameter(BROKER_URL, String.class);


    this.qos = MqttConnectUtils.extractQoSFromString(extract.selectedSingleValue(QOS_LEVEL_KEY, String.class));
    this.reconnectDelayMaxInMs = MqttConnectUtils
        .fromSecToMs(extract.singleValueParameter(RECONNECT_PERIOD_IN_SEC, Long.class));
    this.keepAliveInSec = extract.singleValueParameter(KEEP_ALIVE_IN_SEC, Short.class);
    this.cleanSession = MqttConnectUtils.extractBoolean(extract.selectedSingleValue(CLEAN_SESSION_KEY, String.class));
    this.retain = MqttConnectUtils.extractBoolean(extract.selectedSingleValue(RETAIN, String.class));

    try {
      //TODO How to do this better 
      this.clientCertificate = extract.singleValueParameter(CLIENTCERT, String.class);
      this.clientKey = extract.secretValue(CLIENTKEY);
    } catch (Exception e) {
      this.clientCertificate = null;
      this.clientKey = null;
    }

    boolean isCompliant = MqttConnectUtils.extractBoolean(extract.selectedSingleValue(MQTT_COMPLIANT, String.class));

    if (isCompliant) {
      this.mqttProtocolVersion = "3.1.1";
    }

    String accessMode = extract.selectedAlternativeInternalId(ACCESS_MODE);
    if (accessMode.equals(USERNAME_ACCESS)) {
      this.isBasicAuth = true;
      this.username = extract.singleValueParameter(USERNAME, String.class);
      this.password = extract.secretValue(PASSWORD);
    }

    String willMode = extract.selectedAlternativeInternalId(WILL_MODE);
    if (willMode.equals(WILL_ALTERNATIVE)) {
      this.isLastWill = true;
      this.willTopic = extract.singleValueParameter(WILL_TOPIC, String.class);
      this.willMessage = extract.singleValueParameter(WILL_MESSAGE, String.class);
      this.willQoS = MqttConnectUtils.extractQoSFromString(extract.selectedSingleValue(WILL_QOS, String.class));
      this.willRetain = MqttConnectUtils.extractBoolean(extract.selectedSingleValue(WILL_RETAIN, String.class));
    }
  }

  public String getClientId() {
    return clientId;
  }

  public String getBroker() {
    return broker;
  }

  public String getTopic() {
    return topic;
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

  public String getClientCertificate() {
    return clientCertificate;
  }

  public String getClientKey() {
    return clientKey;
  }
}
