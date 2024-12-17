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

import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.model.staticproperty.StaticPropertyGroup;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.List;

public class KafkaConfigProvider {

  public static final String TOPIC_KEY = "topic";
  public static final String HOST_KEY = "host";
  public static final String PORT_KEY = "port";

  public static final String ACCESS_MODE = "access-mode";
  public static final String UNAUTHENTICATED_PLAIN = "unauthenticated-plain";
  public static final String UNAUTHENTICATED_SSL = "unauthenticated-ssl";
  public static final String SASL_PLAIN = "sasl-plain";
  public static final String SASL_SSL = "sasl-ssl";

  public static final String SECURITY_MECHANISM = "security-mechanism";
  public static final String USERNAME_GROUP = "username-group";
  public static final String USERNAME_KEY = "username";
  public static final String PASSWORD_KEY = "password";

  public static final String CONSUMER_GROUP = "consumer-group";
  public static final String RANDOM_GROUP_ID = "random-group-id";
  public static final String GROUP_ID = "group-id";
  public static final String GROUP_ID_INPUT = "group-id-input";
  public static final String ADDITIONAL_PROPERTIES = "additional-properties";


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

  public static Label getConsumerGroupLabel() {
    return Labels.withId(CONSUMER_GROUP);
  }

  public static Label getAutoOffsetResetConfigLabel() {
    return Labels.withId(AUTO_OFFSET_RESET_CONFIG);
  }

  public static StaticPropertyAlternative getAlternativeUnauthenticatedPlain() {
    return Alternatives.from(Labels.withId(KafkaConfigProvider.UNAUTHENTICATED_PLAIN));
  }

  public static StaticPropertyAlternative getAlternativeUnauthenticatedSSL() {
    return Alternatives.from(Labels.withId(KafkaConfigProvider.UNAUTHENTICATED_SSL));
  }

  public static StaticPropertyAlternative getAlternativesSaslPlain() {
    return Alternatives.from(Labels.withId(KafkaConfigProvider.SASL_PLAIN),
        makeAuthenticationGroup()
    );
  }

  public static StaticPropertyAlternative getAlternativesSaslSSL() {
    return Alternatives.from(Labels.withId(KafkaConfigProvider.SASL_SSL),
        makeAuthenticationGroup());
  }

  public static StaticPropertyAlternative getAlternativesRandomGroupId() {
    return Alternatives.from(Labels.withId(RANDOM_GROUP_ID));
  }

  public static StaticPropertyAlternative getAlternativesGroupId() {
    return Alternatives.from(Labels.withId(KafkaConfigProvider.GROUP_ID),
        StaticProperties.stringFreeTextProperty(Labels.withId(KafkaConfigProvider.GROUP_ID_INPUT)));
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

  private static StaticPropertyGroup makeAuthenticationGroup() {
    var group = StaticProperties.group(Labels.withId(KafkaConfigProvider.USERNAME_GROUP),
        StaticProperties.singleValueSelection(
            Labels.withId(KafkaConfigProvider.SECURITY_MECHANISM),
            makeSecurityMechanism()),
        StaticProperties.stringFreeTextProperty(Labels.withId(KafkaConfigProvider.USERNAME_KEY)),
        StaticProperties.secretValue(Labels.withId(KafkaConfigProvider.PASSWORD_KEY)));
    group.setHorizontalRendering(false);
    return group;
  }

  public static List<Option> makeSecurityMechanism() {
    return List.of(
        new Option("PLAIN"),
        new Option("SCRAM-SHA-256"),
        new Option("SCRAM-SHA-512")
    );
  }
}
