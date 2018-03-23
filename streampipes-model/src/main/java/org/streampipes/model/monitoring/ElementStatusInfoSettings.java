/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.monitoring;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.model.base.UnnamedStreamPipesEntity;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.ELEMENT_STATUS_INFO_SETTINGS)
@Entity
public class ElementStatusInfoSettings extends UnnamedStreamPipesEntity {

  @RdfProperty(StreamPipes.ELEMENT_IDENTIFIER)
  private String elementIdentifier;

  @RdfProperty(StreamPipes.KAFKA_HOST)
  private String kafkaHost;

  @RdfProperty(StreamPipes.KAFKA_PORT)
  private int kafkaPort;

  @RdfProperty(StreamPipes.ERROR_TOPIC)
  private String errorTopic;

  @RdfProperty(StreamPipes.STATS_TOPIC)
  private String statsTopic;

  public ElementStatusInfoSettings() {
    super();
  }

  public ElementStatusInfoSettings(ElementStatusInfoSettings other) {
    super(other);
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
