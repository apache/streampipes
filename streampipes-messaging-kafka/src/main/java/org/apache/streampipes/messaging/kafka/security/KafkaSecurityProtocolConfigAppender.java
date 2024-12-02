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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.security.auth.SecurityProtocol;

import java.util.Properties;

public class KafkaSecurityProtocolConfigAppender implements KafkaConfigAppender {

  private final SecurityProtocol securityProtocol;
  private final Environment env;

  public KafkaSecurityProtocolConfigAppender(SecurityProtocol securityProtocol,
                                             Environment env) {
    this.securityProtocol = securityProtocol;
    this.env = env;
  }

  @Override
  public void appendConfig(Properties props) {
    props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol.toString());

    if (isSslProtocol()) {
      props.put(SslConfigs.SSL_KEYSTORE_TYPE_CONFIG, env.getKeystoreType().getValueOrDefault());
      props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, env.getKeystoreFilename().getValueOrDefault());
      props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, env.getKeystorePassword().getValueOrDefault());

      if (env.getKeyPassword().exists()) {
        props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, env.getKeyPassword().getValueOrDefault());
      }

      props.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, env.getTruststoreType().getValueOrDefault());
      props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, env.getTruststoreFilename().getValueOrDefault());

      if (env.getTruststorePassword().exists()) {
        props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, env.getTruststorePassword().getValueOrDefault());
      }

      if (env.getAllowSelfSignedCertificates().getValueOrDefault()) {
        props.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
      }
    }
  }

  private boolean isSslProtocol() {
    return securityProtocol == SecurityProtocol.SSL || securityProtocol == SecurityProtocol.SASL_SSL;
  }
}
