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
import org.apache.streampipes.model.grounding.PulsarTransportProtocol;

public class SupportedProtocols {

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messages arriving from a
   * Kafka broker.
   *
   * @return The {@link org.apache.streampipes.model.grounding.KafkaTransportProtocol}.
   */
  public static KafkaTransportProtocol kafka() {
    return new KafkaTransportProtocol();
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messages arriving from a
   * JMS broker.
   *
   * @return The {@link org.apache.streampipes.model.grounding.JmsTransportProtocol}.
   */
  public static JmsTransportProtocol jms() {
    return new JmsTransportProtocol();
  }

  /**
   * Defines that a pipeline element (data processor or data sink) supports processing messages arriving from a
   * Mqtt broker.
   *
   * @return The {@link org.apache.streampipes.model.grounding.MqttTransportProtocol}.
   */
  public static MqttTransportProtocol mqtt() {
    return new MqttTransportProtocol();
  }

  public static PulsarTransportProtocol pulsar() {
    return new PulsarTransportProtocol();
  }

}
