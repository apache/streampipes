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

package org.apache.streampipes.sdk.helpers;

import org.apache.streampipes.model.grounding.JmsTransportProtocol;
import org.apache.streampipes.model.grounding.KafkaTransportProtocol;
import org.apache.streampipes.model.grounding.MqttTransportProtocol;
import org.apache.streampipes.model.grounding.WildcardTopicDefinition;

public class Protocols {

  /**
   * Defines the transport protocol Kafka used by a data stream at runtime using a
   * {@link org.apache.streampipes.model.grounding.SimpleTopicDefinition}.
   *
   * @param kafkaHost The hostname of any Kafka broker
   * @param kafkaPort The port of any Kafka broker
   * @param topic     The topic identifier
   * @return The {@link org.apache.streampipes.model.grounding.KafkaTransportProtocol}
   * containing URL and topic where data arrives.
   */
  public static KafkaTransportProtocol kafka(String kafkaHost, Integer kafkaPort, String topic) {
    return new KafkaTransportProtocol(kafkaHost, kafkaPort, topic, kafkaHost, kafkaPort);
  }

  /**
   * Defines the transport protocol Kafka used by a data stream at runtime using a
   * {@link org.apache.streampipes.model.grounding.WildcardTopicDefinition}
   *
   * @param kafkaHost               The hostname of any Kafka broker
   * @param kafkaPort               The port of any Kafka broker
   * @param wildcardTopicDefinition The wildcard topic definition.
   * @return The {@link org.apache.streampipes.model.grounding.KafkaTransportProtocol}
   * containing URL and topic where data arrives.
   */
  public static KafkaTransportProtocol kafka(String kafkaHost, Integer kafkaPort, WildcardTopicDefinition
      wildcardTopicDefinition) {
    return new KafkaTransportProtocol(kafkaHost, kafkaPort, wildcardTopicDefinition);

  }

  /**
   * Defines the transport protocol JMS used by a data stream at runtime.
   *
   * @param jmsHost The hostname of any JMS broker
   * @param jmsPort The port of any JMS broker
   * @param topic   The topic identifier
   * @return The {@link org.apache.streampipes.model.grounding.JmsTransportProtocol} containing URL and topic where data
   * arrives.
   */
  public static JmsTransportProtocol jms(String jmsHost, Integer jmsPort, String topic) {
    return new JmsTransportProtocol(jmsHost, jmsPort, topic);
  }

  /**
   * Defines the transport protocol MQTT used by a data stream at runtime.
   *
   * @param mqttHost The hostname of any MQTT broker
   * @param mqttPort The port of any MQTT broker
   * @param topic    The topic identifier
   * @return The {@link org.apache.streampipes.model.grounding.MqttTransportProtocol}
   * containing URL and topic where data arrives.
   */
  public static MqttTransportProtocol mqtt(String mqttHost, Integer mqttPort, String topic) {
    return new MqttTransportProtocol(mqttHost, mqttPort, topic);
  }
}
