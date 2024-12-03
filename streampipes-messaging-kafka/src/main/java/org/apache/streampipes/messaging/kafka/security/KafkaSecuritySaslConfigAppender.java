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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;

import org.apache.kafka.common.config.SaslConfigs;

import java.util.Properties;

public class KafkaSecuritySaslConfigAppender implements KafkaConfigAppender {

  private final String securityMechanism;
  private final String username;
  private final String password;

  public KafkaSecuritySaslConfigAppender(String securityMechanism,
                                         String username,
                                         String password) {
    this.securityMechanism = securityMechanism;
    this.username = username;
    this.password = password;
  }

  @Override
  public void appendConfig(Properties props) throws SpRuntimeException {
    props.put(SaslConfigs.SASL_MECHANISM, securityMechanism);
    String saslJaasConfig = makeJaasConfig();

    props.put(SaslConfigs.SASL_JAAS_CONFIG, saslJaasConfig);
  }

  private String makeJaasConfig() {
    if (securityMechanism.equals("PLAIN")) {
      return makeSaslPlainConfig();
    } else {
      return makeSaslScramConfig();
    }
  }

  private String makeSaslPlainConfig() {
    return "org.apache.kafka.common.security.plain.PlainLoginModule required username=\""
        + username
        + "\" password=\""
        + password
        + "\";";
  }

  private String makeSaslScramConfig() {
    return "org.apache.kafka.common.security.scram.ScramLoginModule required username=\""
        + username
        + "\" password=\""
        + password
        + "\";";
  }
}
