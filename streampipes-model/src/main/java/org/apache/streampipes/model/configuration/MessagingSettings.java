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

import org.apache.streampipes.model.shared.annotation.TsModel;

import java.util.ArrayList;
import java.util.List;

@TsModel
public class MessagingSettings {

  private Integer batchSize;
  private Integer messageMaxBytes;
  private Integer lingerMs;
  private Integer acks;

  private List<SpDataFormat> prioritizedFormats;
  private List<SpProtocol> prioritizedProtocols;

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

  private List<String> supportedProtocols;

  public MessagingSettings(Integer batchSize,
                           Integer messageMaxBytes,
                           Integer lingerMs,
                           Integer acks,
                           List<SpDataFormat> prioritizedFormats,
                           List<SpProtocol> prioritizedProtocols) {
    this.batchSize = batchSize;
    this.messageMaxBytes = messageMaxBytes;
    this.lingerMs = lingerMs;
    this.acks = acks;
    this.prioritizedFormats = prioritizedFormats;
    this.prioritizedProtocols = prioritizedProtocols;
    this.supportedProtocols = new ArrayList<>();
  }

  public MessagingSettings() {

  }

  public Integer getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(Integer batchSize) {
    this.batchSize = batchSize;
  }

  public Integer getMessageMaxBytes() {
    return messageMaxBytes;
  }

  public void setMessageMaxBytes(Integer messageMaxBytes) {
    this.messageMaxBytes = messageMaxBytes;
  }

  public Integer getLingerMs() {
    return lingerMs;
  }

  public void setLingerMs(Integer lingerMs) {
    this.lingerMs = lingerMs;
  }

  public Integer getAcks() {
    return acks;
  }

  public void setAcks(Integer acks) {
    this.acks = acks;
  }

  public List<SpDataFormat> getPrioritizedFormats() {
    return prioritizedFormats;
  }

  public void setPrioritizedFormats(List<SpDataFormat> prioritizedFormats) {
    this.prioritizedFormats = prioritizedFormats;
  }

  public List<SpProtocol> getPrioritizedProtocols() {
    return prioritizedProtocols;
  }

  public void setPrioritizedProtocols(List<SpProtocol> prioritizedProtocols) {
    this.prioritizedProtocols = prioritizedProtocols;
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

  public List<String> getSupportedProtocols() {
    return supportedProtocols;
  }

  public void setSupportedProtocols(List<String> supportedProtocols) {
    this.supportedProtocols = supportedProtocols;
  }
}
