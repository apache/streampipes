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

import org.apache.streampipes.commons.environment.Environments;

import java.util.Arrays;
import java.util.List;

public class DefaultMessagingSettings {

  public MessagingSettings make() {
    List<SpProtocol> protocolList;
    var env = Environments.getEnvironment();
    if (env.getPrioritizedProtocol().exists()) {
      protocolList = switch (env.getPrioritizedProtocol().getValueOrDefault().toLowerCase()) {
        case "mqtt" ->
            Arrays.asList(SpProtocol.MQTT, SpProtocol.KAFKA, SpProtocol.JMS, SpProtocol.NATS, SpProtocol.PULSAR);
        case "kafka" ->
            Arrays.asList(SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS, SpProtocol.PULSAR);
        case "jms" ->
            Arrays.asList(SpProtocol.JMS, SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.NATS, SpProtocol.PULSAR);
        case "nats" ->
            Arrays.asList(SpProtocol.NATS, SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.PULSAR);
        case "pulsar" ->
            Arrays.asList(SpProtocol.PULSAR, SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS);
        default -> Arrays.asList(SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS, SpProtocol.PULSAR);
      };
    } else {
      protocolList =
          Arrays.asList(SpProtocol.KAFKA, SpProtocol.MQTT, SpProtocol.JMS, SpProtocol.NATS, SpProtocol.PULSAR);
    }

    var defaultSettings = new MessagingSettings(
        1638400,
        5000012,
        20,
        2,
        Arrays.asList(SpDataFormat.JSON, SpDataFormat.CBOR, SpDataFormat.FST, SpDataFormat.SMILE),
        protocolList);

    defaultSettings.setJmsHost("activemq");
    defaultSettings.setJmsPort(61616);

    defaultSettings.setMqttHost("mosquitto");
    defaultSettings.setMqttPort(1883);

    defaultSettings.setNatsHost("nats");
    defaultSettings.setNatsPort(4222);

    defaultSettings.setKafkaHost("kafka");
    defaultSettings.setKafkaPort(9092);

    defaultSettings.setPulsarUrl("pulsar://localhost:6650");

    defaultSettings.setZookeeperHost("zookeeper");
    defaultSettings.setZookeeperPort(2181);
    return defaultSettings;
  }
}
