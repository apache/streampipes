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

package org.apache.streampipes.pe.shared.config.kafka.kafka;

import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.messaging.kafka.config.AutoOffsetResetConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecuritySaslPlainConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecuritySaslSSLConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityUnauthenticatedPlainConfig;
import org.apache.streampipes.messaging.kafka.security.KafkaSecurityUnauthenticatedSSLConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternatives;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

import org.apache.kafka.clients.consumer.ConsumerConfig;

public class KafkaConnectUtils {

  public static final String TOPIC_KEY = "topic";
  public static final String HOST_KEY = "host";
  public static final String PORT_KEY = "port";

  public static final String KEY_SERIALIZATION = "key-serialization";
  public static final String VALUE_SERIALIZATION = "value-serialization";

  public static final String KEY_DESERIALIZATION = "key-deserialization";
  public static final String VALUE_DESERIALIZATION = "value-deserialization";

  public static final String ACCESS_MODE = "access-mode";
  public static final String UNAUTHENTICATED_PLAIN = "unauthenticated-plain";
  public static final String UNAUTHENTICATED_SSL = "unauthenticated-ssl";
  public static final String SASL_PLAIN = "sasl-plain";
  public static final String SASL_SSL = "sasl-ssl";

  public static final String USERNAME_GROUP = "username-group";
  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";


  private static final String HIDE_INTERNAL_TOPICS = "hide-internal-topics";

  public static final String AUTO_OFFSET_RESET_CONFIG = ConsumerConfig.AUTO_OFFSET_RESET_CONFIG;
  public static final String EARLIEST = "earliest";
  public static final String LATEST = "latest";
  public static final String NONE = "none";

  public static Label getTopicLabel() {
    return Labels.withId(TOPIC_KEY);
  }

  public static Label getHideInternalTopicsLabel() {
    return Labels.withId(HIDE_INTERNAL_TOPICS);
  }

  public static String getHideInternalTopicsKey() {
    return HIDE_INTERNAL_TOPICS;
  }

  public static Label getHostLabel() {
    return Labels.withId(HOST_KEY);
  }

  public static Label getPortLabel() {
    return Labels.withId(PORT_KEY);
  }

  public static Label getAccessModeLabel() {
    return Labels.withId(ACCESS_MODE);
  }

  public static Label getAutoOffsetResetConfigLabel() {
    return Labels.withId(AUTO_OFFSET_RESET_CONFIG);
  }


  public static KafkaConfig getConfig(IStaticPropertyExtractor extractor, boolean containsTopic) {
    String brokerUrl = extractor.singleValueParameter(HOST_KEY, String.class);
    String topic = "";
    if (containsTopic) {
      topic = extractor.selectedSingleValue(TOPIC_KEY, String.class);
    }

    Integer port = extractor.singleValueParameter(PORT_KEY, Integer.class);

    String authentication = extractor.selectedAlternativeInternalId(ACCESS_MODE);
    boolean isUseSSL = isUseSSL(authentication);

    KafkaSecurityConfig securityConfig;

    //KafkaSerializerConfig serializerConfig = new KafkaSerializerByteArrayConfig()

    // check if a user for the authentication is defined
    if (authentication.equals(KafkaConnectUtils.SASL_SSL) || authentication.equals(KafkaConnectUtils.SASL_PLAIN)) {
      String username = extractor.singleValueParameter(USERNAME_KEY, String.class);
      String password = extractor.secretValue(PASSWORD_KEY);

      securityConfig = isUseSSL
          ? new KafkaSecuritySaslSSLConfig(username, password) :
          new KafkaSecuritySaslPlainConfig(username, password);
    } else {
      // set security config for none authenticated access
      securityConfig = isUseSSL
          ? new KafkaSecurityUnauthenticatedSSLConfig() :
          new KafkaSecurityUnauthenticatedPlainConfig();
    }

    StaticPropertyAlternatives alternatives = extractor.getStaticPropertyByName(AUTO_OFFSET_RESET_CONFIG,
            StaticPropertyAlternatives.class);

    // Set default value if no value is provided.
    if (alternatives == null) {
      AutoOffsetResetConfig autoOffsetResetConfig = new AutoOffsetResetConfig(KafkaConnectUtils.LATEST);

      return new KafkaConfig(brokerUrl, port, topic, securityConfig, autoOffsetResetConfig);
    } else {
      String auto = extractor.selectedAlternativeInternalId(AUTO_OFFSET_RESET_CONFIG);
      AutoOffsetResetConfig autoOffsetResetConfig = new AutoOffsetResetConfig(auto);

      return new KafkaConfig(brokerUrl, port, topic, securityConfig, autoOffsetResetConfig);
    }
  }

  private static boolean isUseSSL(String authentication) {
    if (authentication.equals(KafkaConnectUtils.UNAUTHENTICATED_PLAIN)
        || authentication.equals(KafkaConnectUtils.SASL_PLAIN)) {
      return false;
    } else {
      return true;
    }
  }


  public static StaticPropertyAlternative getAlternativeUnauthenticatedPlain() {
    return Alternatives.from(Labels.withId(KafkaConnectUtils.UNAUTHENTICATED_PLAIN));
  }

  public static StaticPropertyAlternative getAlternativeUnauthenticatedSSL() {
    return Alternatives.from(Labels.withId(KafkaConnectUtils.UNAUTHENTICATED_SSL));
  }

  public static StaticPropertyAlternative getAlternativesSaslPlain() {
    return Alternatives.from(Labels.withId(KafkaConnectUtils.SASL_PLAIN),
        StaticProperties.group(Labels.withId(KafkaConnectUtils.USERNAME_GROUP),
            StaticProperties.stringFreeTextProperty(Labels.withId(KafkaConnectUtils.USERNAME_KEY)),
            StaticProperties.secretValue(Labels.withId(KafkaConnectUtils.PASSWORD_KEY))));
  }

  public static StaticPropertyAlternative getAlternativesSaslSSL() {
    return Alternatives.from(Labels.withId(KafkaConnectUtils.SASL_SSL),
        StaticProperties.group(Labels.withId(KafkaConnectUtils.USERNAME_GROUP),
            StaticProperties.stringFreeTextProperty(Labels.withId(KafkaConnectUtils.USERNAME_KEY)),
            StaticProperties.secretValue(Labels.withId(KafkaConnectUtils.PASSWORD_KEY))));
  }


  public static StaticPropertyAlternative getAlternativesLatest() {
    return Alternatives.from(Labels.withId(LATEST));
  }

  public static StaticPropertyAlternative getAlternativesEarliest() {
    return Alternatives.from(Labels.withId(EARLIEST));
  }

  public static StaticPropertyAlternative getAlternativesNone() {
    return Alternatives.from(Labels.withId(NONE));
  }
}
