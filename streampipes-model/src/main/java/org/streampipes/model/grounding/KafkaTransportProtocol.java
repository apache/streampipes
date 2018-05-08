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
	
	public KafkaTransportProtocol(String kafkaHost, int kafkaPort, String topic, String zookeeperHost, int zookeeperPort)
	{
		super(kafkaHost, new SimpleTopicDefinition(topic));
		this.zookeeperHost = zookeeperHost;
		this.zookeeperPort = zookeeperPort;
		this.kafkaPort = kafkaPort;
	}
	
	public KafkaTransportProtocol(KafkaTransportProtocol other)
	{
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
	
	public KafkaTransportProtocol()
	{
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

}
