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

package org.streampipes.model.grounding;

import org.streampipes.empire.annotations.RdfProperty;
import org.streampipes.empire.annotations.RdfsClass;
import org.streampipes.vocabulary.StreamPipes;

import javax.persistence.Entity;

@RdfsClass(StreamPipes.KAFKA_TRANSPORT_PROTOCOL)
@Entity
public class KafkaTransportProtocol extends TransportProtocol {

  private static final long serialVersionUID = -4067982203807146257L;

  @RdfProperty(StreamPipes.ZOOKEEPER_HOST)
  private String zookeeperHost;

  @RdfProperty(StreamPipes.ZOOKEEPER_PORT)
  private int zookeeperPort;

  @RdfProperty(StreamPipes.KAFKA_PORT)
  private int kafkaPort;

  @RdfProperty(StreamPipes.KAFKA_LINGER_MS)
  private Integer lingerMs;

  @RdfProperty(StreamPipes.KAFKA_MESSAGE_MAX_BYTES)
  private String messageMaxBytes;

  @RdfProperty(StreamPipes.KAFKA_ACKS)
  private String acks;

  @RdfProperty(StreamPipes.KAFKA_BATCH_SIZE)
  private String batchSize;

  @RdfProperty(StreamPipes.KAFKA_OFFSET)
  private String offset;

  @RdfProperty(StreamPipes.KAFKA_GROUP_ID)
  private String groupId;

  public KafkaTransportProtocol(String kafkaHost, int kafkaPort, String topic) {
    super(kafkaHost, new SimpleTopicDefinition(topic));
    this.zookeeperHost = kafkaHost;
    this.zookeeperPort = kafkaPort;
    this.kafkaPort = kafkaPort;
  }

  public KafkaTransportProtocol(String kafkaHost, int kafkaPort, String topic, String zookeeperHost, int zookeeperPort) {
    super(kafkaHost, new SimpleTopicDefinition(topic));
    this.zookeeperHost = zookeeperHost;
    this.zookeeperPort = zookeeperPort;
    this.kafkaPort = kafkaPort;
  }

  public KafkaTransportProtocol(KafkaTransportProtocol other) {
    super(other);
    this.kafkaPort = other.getKafkaPort();
    this.zookeeperHost = other.getZookeeperHost();
    this.zookeeperPort = other.getZookeeperPort();
  }

  public KafkaTransportProtocol(String kafkaHost, Integer kafkaPort, WildcardTopicDefinition wildcardTopicDefinition) {
    super(kafkaHost, wildcardTopicDefinition);
    this.kafkaPort = kafkaPort;
    this.zookeeperHost = kafkaHost;
    this.zookeeperPort = kafkaPort;
  }

  public KafkaTransportProtocol() {
    super();
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

  public int getKafkaPort() {
    return kafkaPort;
  }

  public void setKafkaPort(int kafkaPort) {
    this.kafkaPort = kafkaPort;
  }

  public static long getSerialVersionUID() {
    return serialVersionUID;
  }

  public Integer getLingerMs() {
    return lingerMs;
  }

  public void setLingerMs(Integer lingerMs) {
    this.lingerMs = lingerMs;
  }

  public String getMessageMaxBytes() {
    return messageMaxBytes;
  }

  public void setMessageMaxBytes(String messageMaxBytes) {
    this.messageMaxBytes = messageMaxBytes;
  }

  public String getAcks() {
    return acks;
  }

  public void setAcks(String acks) {
    this.acks = acks;
  }

  public String getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(String batchSize) {
    this.batchSize = batchSize;
  }

  public String getOffset() {
    return offset;
  }

  public void setOffset(String offset) {
    this.offset = offset;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
}
