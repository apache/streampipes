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

public enum SpProtocol {

  KAFKA("Kafka", "org.apache.streampipes.model.grounding.KafkaTransportProtocol"),
  JMS("JMS", "org.apache.streampipes.model.grounding.JmsTransportProtocol"),
  MQTT("MQTT", "org.apache.streampipes.model.grounding.MqttTransportProtocol"),
  NATS("NATS", "org.apache.streampipes.model.grounding.NatsTransportProtocol"),
  PULSAR("PULSAR", "org.apache.streampipes.model.grounding.PulsarTransportProtocol");

  private final String name;
  private final String protocolClass;

  SpProtocol(String name, String protocolClass) {
    this.name = name;
    this.protocolClass = protocolClass;
  }

  public String getName() {
    return name;
  }

  public String getProtocolClass() {
    return protocolClass;
  }
}
