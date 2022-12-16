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

package org.apache.streampipes.messaging.kafka.security;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;

import java.util.Properties;

public class KafkaSecuritySaslPlainConfig extends KafkaSecurityConfig {

  private final String username;
  private final String password;

  public KafkaSecuritySaslPlainConfig(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void appendConfig(Properties props) {

    props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, SecurityProtocol.SASL_PLAINTEXT.toString());

    String saslJaasConfig = "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""
        + username
        + "\" password=\""
        + password
        + "\";";

    props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
  }
}
