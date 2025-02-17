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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;

import java.util.ArrayList;
import java.util.List;

public class KafkaBaseConfig {

  private String kafkaHost;
  private Integer kafkaPort;
  private String topic;
  private List<KafkaConfigAppender> configAppenders;

  public KafkaBaseConfig() {
    this.configAppenders = new ArrayList<>();
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

  public List<KafkaConfigAppender> getConfigAppenders() {
    return configAppenders;
  }

  public void setConfigAppenders(List<KafkaConfigAppender> configAppenders) {
    this.configAppenders = configAppenders;
  }
}
