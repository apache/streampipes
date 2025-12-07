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

import com.hivemq.client.mqtt.datatypes.MqttQos;

public class MqttConfig {

  private Boolean authenticated;

  private String url;
  private String topic;
  private String username;
  private String password;

  private String clientCertificate = null;
  private String clientKey = null;
  private Boolean tls = false;

  private boolean isLastWill = false;
  private MqttQos willQoS = MqttQos.AT_MOST_ONCE;
  private Boolean willRetain = false;
  private String willTopic = "";
  private String willMessage = "";
  private String mqttProtocolVersion = "3.1";
  private MqttQos qos = MqttQos.AT_MOST_ONCE;
  private long reconnectDelayMaxInMs = 10000L;
  private boolean cleanSession = true;
  private boolean retain = false;
  private short keepAliveInSec = 60;
  private String clientId = "";

  public MqttConfig(String url, String topic) {
    this.authenticated = false;
    this.url = url;
    this.topic = topic;
  }

  public MqttConfig(String url, String topic, String username, String password) {
    this(url, topic);
    this.authenticated = true;
    this.username = username;
    this.password = password;
  }

  public MqttConfig(String url, String topic, Boolean tlsEnabled, String clientCertificate, String clientKey) {
    this.authenticated = false;
    this.url = url;
    this.topic = topic;
    this.tls = tlsEnabled;
    this.clientCertificate = clientCertificate;
    this.clientKey = clientKey;
  }

  public MqttConfig(String url, String topic, String username, String password, Boolean tlsEnabled) {
    this(url, topic, username, password);
    this.tls = tlsEnabled;
  }

  public MqttConfig(String url, String topic, Boolean tlsEnabled) {
    this(url, topic);
    this.tls = tlsEnabled;
  }

  public Boolean getAuthenticated() {
    return authenticated;
  }

  public void setAuthenticated(Boolean authenticated) {
    this.authenticated = authenticated;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getClientCertificatePath() {
    return clientCertificate;
  }

  public void setClientCertificatePath(String clientCertificate) {
    this.clientCertificate = clientCertificate;
  }

  public String getClientKeyPath() {
    return clientKey;
  }

  public void setClientKey(String clientKey) {
    this.clientKey = clientKey;
  }

  public Boolean getTlsEnabled() {
    return tls;
  }

  public void setTlsEnabled(Boolean tlsEnabled) {
    this.tls = tlsEnabled;
  }

  public boolean isLastWill() {
    return isLastWill;
  }

  public void setLastWill(boolean lastWill) {
    this.isLastWill = lastWill;
  }

  public MqttQos getWillQoS() {
    return willQoS;
  }

  public void setWillQoS(MqttQos willQoS) {
    this.willQoS = willQoS;
  }

  public Boolean getWillRetain() {
    return willRetain;
  }

  public void setWillRetain(Boolean willRetain) {
    this.willRetain = willRetain;
  }

  public String getWillTopic() {
    return willTopic;
  }

  public void setWillTopic(String willTopic) {
    this.willTopic = willTopic;
  }

  public String getWillMessage() {
    return willMessage;
  }

  public void setWillMessage(String willMessage) {
    this.willMessage = willMessage;
  }

  public String getMqttProtocolVersion() {
    return mqttProtocolVersion;
  }

  public void setMqttProtocolVersion(String mqttProtocolVersion) {
    this.mqttProtocolVersion = mqttProtocolVersion;
  }

  public MqttQos getQos() {
    return qos;
  }

  public void setQos(MqttQos qos) {
    this.qos = qos;
  }

  public long getReconnectDelayMaxInMs() {
    return reconnectDelayMaxInMs;
  }

  public void setReconnectDelayMaxInMs(long reconnectDelayMaxInMs) {
    this.reconnectDelayMaxInMs = reconnectDelayMaxInMs;
  }

  public boolean isCleanSession() {
    return cleanSession;
  }

  public void setCleanSession(boolean cleanSession) {
    this.cleanSession = cleanSession;
  }

  public boolean isRetain() {
    return retain;
  }

  public void setRetain(boolean retain) {
    this.retain = retain;
  }

  public short getKeepAliveInSec() {
    return keepAliveInSec;
  }

  public void setKeepAliveInSec(short keepAliveInSec) {
    this.keepAliveInSec = keepAliveInSec;
  }
}