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

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;

import java.util.Properties;
import java.util.UUID;

public class ConsumerConfigFactory extends AbstractConfigFactory {

  private static final String ENABLE_AUTO_COMMIT_CONFIG_DEFAULT = "true";
  private static final String AUTO_COMMIT_INTERVAL_MS_CONFIG_DEFAULT = "5000";
  private static final String SESSION_TIMEOUT_MS_CONFIG_DEFAULT = "30000";
  private static final Integer FETCH_MAX_BYTES_CONFIG_DEFAULT = 52428800;
  private static final String KEY_DESERIALIZER_CLASS_CONFIG_DEFAULT = ByteArrayDeserializer.class.getName();
  private static final String VALUE_DESERIALIZER_CLASS_CONFIG_DEFAULT = ByteArrayDeserializer.class.getName();
  private static final String AUTO_OFFSET_RESET_CONFIG_DEFAULT = "latest";

  public ConsumerConfigFactory(KafkaTransportProtocol protocol) {
    super(protocol);
  }

  @Override
  public Properties makeDefaultProperties() {
    Properties props = new Properties();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, getBrokerUrl());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, getConfigOrDefault(protocol::getGroupId, UUID.randomUUID().toString()));
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, ENABLE_AUTO_COMMIT_CONFIG_DEFAULT);
    props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,
        AUTO_COMMIT_INTERVAL_MS_CONFIG_DEFAULT);
    props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, SESSION_TIMEOUT_MS_CONFIG_DEFAULT);
    props.put(ConsumerConfig.FETCH_MAX_BYTES_CONFIG,
        getConfigOrDefault(protocol::getMessageMaxBytes, FETCH_MAX_BYTES_CONFIG_DEFAULT));

    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KEY_DESERIALIZER_CLASS_CONFIG_DEFAULT);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, VALUE_DESERIALIZER_CLASS_CONFIG_DEFAULT);


    props.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, AUTO_OFFSET_RESET_CONFIG_DEFAULT);
    return props;
  }
}
