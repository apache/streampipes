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

package org.apache.streampipes.pe.shared.config.mqtt;

import org.apache.streampipes.extensions.api.extractor.IParameterExtractor;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Label;
import org.apache.streampipes.sdk.helpers.Labels;

public class MqttConnectUtils {

  /**
   * Keys of user configuration parameters
   */
  public static final String ACCESS_MODE = "access-mode";
  public static final String ANONYMOUS_ACCESS = "anonymous-alternative";
  public static final String USERNAME_ACCESS = "username-alternative";
  public static final String USERNAME_GROUP = "username-group";
  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String BROKER_URL = "broker_url";
  public static final String TOPIC = "topic";

  public static Label getAccessModeLabel() {
    return Labels.withId(ACCESS_MODE);
  }

  public static Label getBrokerUrlLabel() {
    return Labels.withId(BROKER_URL);
  }

  public static Label getTopicLabel() {
    return Labels.withId(TOPIC);
  }

  public static StaticPropertyAlternative getAlternativesOne() {
    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS));

  }

  public static StaticPropertyAlternative getAlternativesOne(boolean selected) {
    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS), selected);

  }

  public static StaticPropertyAlternative getAlternativesTwo() {
    return Alternatives.from(Labels.withId(USERNAME_ACCESS),
        StaticProperties.group(Labels.withId(USERNAME_GROUP),
            StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME)),
            StaticProperties.secretValue(Labels.withId(PASSWORD))));

  }

  public static MqttConfig getMqttConfig(IParameterExtractor<?> extractor) {
    return getMqttConfig(extractor, null);
  }

  public static MqttConfig getMqttConfig(IParameterExtractor<?> extractor, String topicInput) {
    MqttConfig mqttConfig;
    String brokerUrl = extractor.singleValueParameter(BROKER_URL, String.class);

    String topic;
    if (topicInput == null) {
      topic = extractor.singleValueParameter(TOPIC, String.class);
    } else {
      topic = topicInput;
    }

    String selectedAlternative = extractor.selectedAlternativeInternalId(ACCESS_MODE);

    if (selectedAlternative.equals(ANONYMOUS_ACCESS)) {
      mqttConfig = new MqttConfig(brokerUrl, topic);
    } else {
      String username = extractor.singleValueParameter(USERNAME, String.class);
      String password = extractor.secretValue(PASSWORD);
      mqttConfig = new MqttConfig(brokerUrl, topic, username, password);
    }

    return mqttConfig;
  }

}
