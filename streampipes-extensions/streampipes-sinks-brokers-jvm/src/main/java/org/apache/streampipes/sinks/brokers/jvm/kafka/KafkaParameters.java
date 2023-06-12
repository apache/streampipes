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

package org.apache.streampipes.sinks.brokers.jvm.kafka;

import org.apache.streampipes.pe.shared.config.kafka.KafkaConnectUtils;
import org.apache.streampipes.wrapper.params.compat.SinkParams;

public class KafkaParameters {

  private final String kafkaHost;

  private final Integer kafkaPort;

  private final String topic;

  private final String authentication;

  private String username;

  private String password;

  private final boolean useSSL;

  public KafkaParameters(SinkParams params) {
    var extractor = params.extractor();
    this.topic = extractor.singleValueParameter(KafkaConnectUtils.TOPIC_KEY, String.class);
    this.kafkaHost = extractor.singleValueParameter(KafkaConnectUtils.HOST_KEY, String.class);
    this.kafkaPort = extractor.singleValueParameter(KafkaConnectUtils.PORT_KEY, Integer.class);
    this.authentication = extractor.selectedAlternativeInternalId(KafkaConnectUtils.ACCESS_MODE);

    if (!useAuthentication()) {
      this.useSSL = KafkaConnectUtils.UNAUTHENTICATED_SSL.equals(this.authentication);
    } else {
      String username = extractor.singleValueParameter(KafkaConnectUtils.USERNAME_KEY, String.class);
      String password = extractor.secretValue(KafkaConnectUtils.PASSWORD_KEY);
      this.username = username;
      this.password = password;
      this.useSSL = KafkaConnectUtils.SASL_SSL.equals(this.authentication);
    }
  }

  public String getKafkaHost() {
    return kafkaHost;
  }

  public Integer getKafkaPort() {
    return kafkaPort;
  }

  public String getTopic() {
    return topic;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getAuthentication() {
    return authentication;
  }

  public boolean isUseSSL() {
    return useSSL;
  }

  public boolean useAuthentication() {
    return KafkaConnectUtils.SASL_PLAIN.equals(this.authentication)
        || KafkaConnectUtils.SASL_SSL.equals(this.authentication);
  }
}
