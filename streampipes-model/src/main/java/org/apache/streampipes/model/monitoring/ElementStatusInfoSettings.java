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

package org.apache.streampipes.model.monitoring;

public class ElementStatusInfoSettings {

  private String elementIdentifier;

  private String kafkaHost;

  private int kafkaPort;

  private String errorTopic;

  private String statsTopic;

  public ElementStatusInfoSettings() {
    super();
  }

  public ElementStatusInfoSettings(ElementStatusInfoSettings other) {
    this.kafkaHost = other.getKafkaHost();
    this.kafkaPort = other.getKafkaPort();
    this.errorTopic = other.getErrorTopic();
    this.statsTopic = other.getStatsTopic();
  }


  public ElementStatusInfoSettings(String elementIdentifier, String kafkaHost, int kafkaPort,
                                   String errorTopic, String
                                       statsTopic) {
    this.elementIdentifier = elementIdentifier;
    this.kafkaHost = kafkaHost;
    this.kafkaPort = kafkaPort;
    this.errorTopic = errorTopic;
    this.statsTopic = statsTopic;
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

  public String getErrorTopic() {
    return errorTopic;
  }

  public void setErrorTopic(String errorTopic) {
    this.errorTopic = errorTopic;
  }

  public String getStatsTopic() {
    return statsTopic;
  }

  public void setStatsTopic(String statsTopic) {
    this.statsTopic = statsTopic;
  }

  public String getElementIdentifier() {
    return elementIdentifier;
  }

  public void setElementIdentifier(String elementIdentifier) {
    this.elementIdentifier = elementIdentifier;
  }
}
