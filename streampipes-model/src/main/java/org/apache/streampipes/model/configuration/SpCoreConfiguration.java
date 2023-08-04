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

package org.apache.streampipes.model.configuration;

import com.google.gson.annotations.SerializedName;

public class SpCoreConfiguration {

  public static final String ID = "core";

  protected @SerializedName("_rev") String rev;
  private @SerializedName("_id") String id = ID;

  private MessagingSettings messagingSettings;
  private LocalAuthConfig localAuthConfig;
  private EmailConfig emailConfig;
  private GeneralConfig generalConfig;

  private String jmsHost;
  private int jmsPort;

  private String mqttHost;
  private int mqttPort;

  private String natsHost;
  private int natsPort;

  private String kafkaHost;
  private int kafkaPort;

  private String pulsarUrl;

  private String zookeeperHost;
  private int zookeeperPort;

  private boolean isConfigured;

  private String assetDir;
  private String filesDir;

  public SpCoreConfiguration() {
  }

  public String getRev() {
    return rev;
  }

  public void setRev(String rev) {
    this.rev = rev;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public MessagingSettings getMessagingSettings() {
    return messagingSettings;
  }

  public void setMessagingSettings(MessagingSettings messagingSettings) {
    this.messagingSettings = messagingSettings;
  }

  public LocalAuthConfig getLocalAuthConfig() {
    return localAuthConfig;
  }

  public void setLocalAuthConfig(LocalAuthConfig localAuthConfig) {
    this.localAuthConfig = localAuthConfig;
  }

  public EmailConfig getEmailConfig() {
    return emailConfig;
  }

  public void setEmailConfig(EmailConfig emailConfig) {
    this.emailConfig = emailConfig;
  }

  public GeneralConfig getGeneralConfig() {
    return generalConfig;
  }

  public void setGeneralConfig(GeneralConfig generalConfig) {
    this.generalConfig = generalConfig;
  }

  public String getJmsHost() {
    return jmsHost;
  }

  public void setJmsHost(String jmsHost) {
    this.jmsHost = jmsHost;
  }

  public int getJmsPort() {
    return jmsPort;
  }

  public void setJmsPort(int jmsPort) {
    this.jmsPort = jmsPort;
  }

  public String getMqttHost() {
    return mqttHost;
  }

  public void setMqttHost(String mqttHost) {
    this.mqttHost = mqttHost;
  }

  public int getMqttPort() {
    return mqttPort;
  }

  public void setMqttPort(int mqttPort) {
    this.mqttPort = mqttPort;
  }

  public String getNatsHost() {
    return natsHost;
  }

  public void setNatsHost(String natsHost) {
    this.natsHost = natsHost;
  }

  public int getNatsPort() {
    return natsPort;
  }

  public void setNatsPort(int natsPort) {
    this.natsPort = natsPort;
  }

  public String getKafkaHost() {
    return kafkaHost;
  }

  public void setKafkaHost(String kafkaHost) {
    this.kafkaHost = kafkaHost;
  }

  public int getKafkaPort() {
    return kafkaPort;
  }

  public void setKafkaPort(int kafkaPort) {
    this.kafkaPort = kafkaPort;
  }

  public String getPulsarUrl() {
    return pulsarUrl;
  }

  public void setPulsarUrl(String pulsarUrl) {
    this.pulsarUrl = pulsarUrl;
  }

  public String getZookeeperHost() {
    return zookeeperHost;
  }

  public void setZookeeperHost(String zookeeperHost) {
    this.zookeeperHost = zookeeperHost;
  }

  public int getZookeeperPort() {
    return zookeeperPort;
  }

  public void setZookeeperPort(int zookeeperPort) {
    this.zookeeperPort = zookeeperPort;
  }

  public boolean isConfigured() {
    return isConfigured;
  }

  public void setConfigured(boolean configured) {
    isConfigured = configured;
  }

  public String getAssetDir() {
    return assetDir;
  }

  public void setAssetDir(String assetDir) {
    this.assetDir = assetDir;
  }

  public String getFilesDir() {
    return filesDir;
  }

  public void setFilesDir(String filesDir) {
    this.filesDir = filesDir;
  }
}
