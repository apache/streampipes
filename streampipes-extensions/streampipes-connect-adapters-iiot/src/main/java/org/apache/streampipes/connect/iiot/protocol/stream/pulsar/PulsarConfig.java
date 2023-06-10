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

package org.apache.streampipes.connect.iiot.protocol.stream.pulsar;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;

import static org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol.PULSAR_BROKER_HOST;
import static org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol.PULSAR_BROKER_PORT;
import static org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol.PULSAR_SUBSCRIPTION_NAME;
import static org.apache.streampipes.connect.iiot.protocol.stream.pulsar.PulsarProtocol.PULSAR_TOPIC;

public class PulsarConfig {

  private final String brokerUrl;
  private final String topic;
  private final String subscriptionName;

  public PulsarConfig(String brokerUrl,
                      String topic,
                      String subscriptionName) {
    this.brokerUrl = brokerUrl;
    this.topic = topic;
    this.subscriptionName = subscriptionName;
  }

  public static PulsarConfig from(IStaticPropertyExtractor extractor) {
    String brokerHost = extractor.singleValueParameter(PULSAR_BROKER_HOST, String.class);
    Integer brokerPort = extractor.singleValueParameter(PULSAR_BROKER_PORT, Integer.class);
    String brokerUrl = brokerHost + ":" + brokerPort;
    String topic = extractor.singleValueParameter(PULSAR_TOPIC, String.class);
    String subscriptionName = extractor.singleValueParameter(PULSAR_SUBSCRIPTION_NAME, String.class);

    return new PulsarConfig(brokerUrl, topic, subscriptionName);
  }

  public String getBrokerUrl() {
    return brokerUrl;
  }

  public String getTopic() {
    return topic;
  }

  public String getSubscriptionName() {
    return subscriptionName;
  }
}
