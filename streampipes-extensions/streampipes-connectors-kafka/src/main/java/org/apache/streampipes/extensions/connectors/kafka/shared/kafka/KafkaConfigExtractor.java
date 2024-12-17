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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.messaging.kafka.config.AutoOffsetResetConfig;
import org.apache.streampipes.messaging.kafka.config.KafkaConfigAppender;
import org.apache.streampipes.messaging.kafka.config.SimpleConfigAppender;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityProtocolConfigAppender;
import org.apache.streampipes.messaging.kafka.security.KafkaSecuritySaslConfigAppender;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;

import org.apache.kafka.common.security.auth.SecurityProtocol;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.ACCESS_MODE;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.ADDITIONAL_PROPERTIES;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.AUTO_OFFSET_RESET_CONFIG;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.CONSUMER_GROUP;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.GROUP_ID_INPUT;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.HOST_KEY;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.PASSWORD_KEY;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.PORT_KEY;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.RANDOM_GROUP_ID;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.SECURITY_MECHANISM;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.TOPIC_KEY;
import static org.apache.streampipes.extensions.connectors.kafka.shared.kafka.KafkaConfigProvider.USERNAME_KEY;

public class KafkaConfigExtractor {

  public KafkaAdapterConfig extractAdapterConfig(IStaticPropertyExtractor extractor,
                                                 boolean containsTopic) {

    var config = extractCommonConfigs(extractor, new KafkaAdapterConfig());

    var topic = "";
    if (containsTopic) {
      topic = extractor.selectedSingleValue(TOPIC_KEY, String.class);
    }
    config.setTopic(topic);

    if (extractor.selectedAlternativeInternalId(CONSUMER_GROUP).equals(RANDOM_GROUP_ID)) {
      config.setGroupId("StreamPipesKafkaConsumer" + System.currentTimeMillis());
    } else {
      config.setGroupId(extractor.singleValueParameter(GROUP_ID_INPUT, String.class));
    }

    StaticPropertyAlternatives alternatives = extractor.getStaticPropertyByName(AUTO_OFFSET_RESET_CONFIG,
        StaticPropertyAlternatives.class);

    // Set default value if no value is provided.
    if (alternatives == null) {
      config.getConfigAppenders().add(new AutoOffsetResetConfig(KafkaConfigProvider.LATEST));
    } else {
      String auto = extractor.selectedAlternativeInternalId(AUTO_OFFSET_RESET_CONFIG);
      config.getConfigAppenders().add(new AutoOffsetResetConfig(auto));
    }
    return config;
  }

  public KafkaBaseConfig extractSinkConfig(IParameterExtractor extractor) {
    var config = extractCommonConfigs(extractor, new KafkaBaseConfig());
    config.setTopic(extractor.singleValueParameter(TOPIC_KEY, String.class));

    return config;
  }

  private <T extends KafkaBaseConfig> T extractCommonConfigs(IParameterExtractor extractor,
                                                             T config) {
    var configAppenders = new ArrayList<KafkaConfigAppender>();
    var env = Environments.getEnvironment();
    config.setKafkaHost(extractor.singleValueParameter(HOST_KEY, String.class));
    config.setKafkaPort(extractor.singleValueParameter(PORT_KEY, Integer.class));

    var authentication = extractor.selectedAlternativeInternalId(ACCESS_MODE);
    var securityProtocol = getSecurityProtocol(authentication);
    configAppenders.add(new KafkaSecurityProtocolConfigAppender(securityProtocol, env));

    // check if SASL authentication is defined
    if (isSaslSecurityMechanism(securityProtocol)) {
      String username = extractor.singleValueParameter(USERNAME_KEY, String.class);
      String password = extractor.secretValue(PASSWORD_KEY);
      String mechanism = extractor.selectedSingleValue(SECURITY_MECHANISM, String.class);

      configAppenders.add(new KafkaSecuritySaslConfigAppender(mechanism, username, password));
    }
    configAppenders.add(new SimpleConfigAppender(
        parseAdditionalProperties(extractor.codeblockValue(ADDITIONAL_PROPERTIES)))
    );
    config.setConfigAppenders(configAppenders);

    return config;
  }

  private boolean isSaslSecurityMechanism(SecurityProtocol securityProtocol) {
    return SecurityProtocol.SASL_PLAINTEXT == securityProtocol || SecurityProtocol.SASL_SSL == securityProtocol;
  }

  public SecurityProtocol getSecurityProtocol(String selectedSecurityConfiguration) {
    return switch (selectedSecurityConfiguration) {
      case "unauthenticated-ssl" -> SecurityProtocol.SSL;
      case "sasl-plain" -> SecurityProtocol.SASL_PLAINTEXT;
      case "sasl-ssl" -> SecurityProtocol.SASL_SSL;
      default -> SecurityProtocol.PLAINTEXT;
    };
  }

  public static Map<String, String> parseAdditionalProperties(String text) {
    return Arrays.stream(text.split("\\R"))
        .map(String::trim)
        .filter(line -> !line.isEmpty() && !line.startsWith("#"))
        .filter(line -> line.contains("="))
        .map(line -> line.split("=", 2))
        .collect(Collectors.toMap(
            parts -> parts[0].trim(),
            parts -> parts[1].trim(),
            (existing, replacement) -> replacement,
            LinkedHashMap::new
        ));
  }
}
