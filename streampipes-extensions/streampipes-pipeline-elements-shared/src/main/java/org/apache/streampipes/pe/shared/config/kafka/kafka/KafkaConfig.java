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

package org.apache.streampipes.pe.shared.config.kafka.kafka;

import org.apache.streampipes.messaging.kafka.config.AutoOffsetResetConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityConfig;

public class KafkaConfig {

  private String kafkaHost;
  private Integer kafkaPort;
  private String topic;

  KafkaSecurityConfig securityConfig;
  AutoOffsetResetConfig autoOffsetResetConfig;

  public KafkaConfig(String kafkaHost,
                     Integer kafkaPort,
                     String topic,
                     KafkaSecurityConfig securityConfig,
                     AutoOffsetResetConfig autoOffsetResetConfig) {
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
    this.topic = topic;
    this.securityConfig = securityConfig;
    this.autoOffsetResetConfig = autoOffsetResetConfig;
  }

  public String getKafkaHost() {
    return kafkaHost;
  }

  public void setKafkaHost(String kafkaHost) {
    this.kafkaHost = kafkaHost;
  }

  public Integer getKafkaPort() {
    return kafkaPort;
  }

  public void setKafkaPort(Integer kafkaPort) {
    this.kafkaPort = kafkaPort;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public KafkaSecurityConfig getSecurityConfig() {
    return securityConfig;
  }

  public void setSecurityConfig(KafkaSecurityConfig securityConfig) {
    this.securityConfig = securityConfig;
  }

  public AutoOffsetResetConfig getAutoOffsetResetConfig() {
    return autoOffsetResetConfig;
  }

  public void setAutoOffsetResetConfig(AutoOffsetResetConfig autoOffsetResetConfig) {
    this.autoOffsetResetConfig = autoOffsetResetConfig;
  }
}
