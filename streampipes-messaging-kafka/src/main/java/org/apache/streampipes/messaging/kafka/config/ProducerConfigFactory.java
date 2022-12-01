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
package org.apache.streampipes.messaging.kafka.config;

import org.apache.streampipes.model.grounding.KafkaTransportProtocol;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class ProducerConfigFactory extends AbstractConfigFactory {

  private static final String ACKS_CONFIG_DEFAULT = "all";
  private static final Integer RETRIES_CONFIG_DEFAULT = 0;
  private static final Integer BATCH_SIZE_CONFIG_DEFAULT = 1638400;
  private static final Integer LINGER_MS_DEFAULT = 20;
  private static final Integer BUFFER_MEMORY_CONFIG_DEFAULT = 33554432;
  private static final Integer MAX_REQUEST_SIZE_CONFIG_DEFAULT = 5000012;

  private static final String KEY_SERIALIZER_DEFAULT = StringSerializer.class.getName();
  private static final String VALUE_SERIALIZER_DEFAULT = ByteArraySerializer.class.getName();


  public ProducerConfigFactory(KafkaTransportProtocol protocol) {
    super(protocol);
  }

  @Override
  public Properties makeDefaultProperties() {
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokerUrl());
    props.put(ProducerConfig.ACKS_CONFIG, getConfigOrDefault(protocol::getAcks,
        ACKS_CONFIG_DEFAULT));
    props.put(ProducerConfig.RETRIES_CONFIG, RETRIES_CONFIG_DEFAULT);
    props.put(ProducerConfig.BATCH_SIZE_CONFIG,
        getConfigOrDefault(protocol::getBatchSize, BATCH_SIZE_CONFIG_DEFAULT));
    props.put(ProducerConfig.LINGER_MS_CONFIG,
        getConfigOrDefault(protocol::getLingerMs, LINGER_MS_DEFAULT));
    props.put(ProducerConfig.MAX_REQUEST_SIZE_CONFIG, getConfigOrDefault(protocol::getMaxRequestSize,
        MAX_REQUEST_SIZE_CONFIG_DEFAULT));
    props.put(ProducerConfig.BUFFER_MEMORY_CONFIG, BUFFER_MEMORY_CONFIG_DEFAULT);

    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, KEY_SERIALIZER_DEFAULT);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, VALUE_SERIALIZER_DEFAULT);

    return props;
  }

}
