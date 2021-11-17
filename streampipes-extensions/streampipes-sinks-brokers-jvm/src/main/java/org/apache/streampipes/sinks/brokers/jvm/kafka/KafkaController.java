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

import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class KafkaController extends StandaloneEventSinkDeclarer<KafkaParameters> {

//  private static final String KAFKA_BROKER_SETTINGS_KEY = "broker-settings";
  private static final String TOPIC_KEY = "topic";
  private static final String HOST_KEY = "host";
  private static final String PORT_KEY = "port";
  private static final String ACCESS_MODE = "access-mode";
  private static final String ANONYMOUS_ACCESS = "anonymous-alternative";
  private static final String USERNAME_ACCESS = "username-alternative";
  private static final String USERNAME_GROUP = "username-group";
  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";

//  private static final String KAFKA_HOST_URI = "http://schema.org/kafkaHost";
//  private static final String KAFKA_PORT_URI = "http://schema.org/kafkaPort";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.kafka")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredTextParameter(Labels.withId(TOPIC_KEY), false, false)
            .requiredTextParameter(Labels.withId(HOST_KEY), false, false)
            .requiredIntegerParameter(Labels.withId(PORT_KEY), 9092)
            .requiredAlternatives(Labels.withId(ACCESS_MODE), getAlternativesOne(), getAlternativesTwo())
            .build();
  }

  @Override
  public ConfiguredEventSink<KafkaParameters> onInvocation(DataSinkInvocation graph,
                                                           DataSinkParameterExtractor extractor) {
    String topic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    String kafkaHost = extractor.singleValueParameter(HOST_KEY, String.class);
    Integer kafkaPort = extractor.singleValueParameter(PORT_KEY, Integer.class);
    String authentication = extractor.selectedAlternativeInternalId(ACCESS_MODE);

    KafkaParameters params;
    if (authentication.equals(ANONYMOUS_ACCESS)) {
      params = new KafkaParameters(graph, kafkaHost, kafkaPort, topic, authentication, null, null);
    }
    else {
      String username = extractor.singleValueParameter(USERNAME_KEY, String.class);
      String password = extractor.secretValue(PASSWORD_KEY);
      params = new KafkaParameters(graph, kafkaHost, kafkaPort, topic, authentication, username, password);
    }

    return new ConfiguredEventSink<>(params, KafkaPublisher::new);
  }

  public static StaticPropertyAlternative getAlternativesOne() {
    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS));

  }

  public static StaticPropertyAlternative getAlternativesTwo() {
    return Alternatives.from(Labels.withId(USERNAME_ACCESS),
            StaticProperties.group(Labels.withId(USERNAME_GROUP),
                    StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME_KEY)),
                    StaticProperties.secretValue(Labels.withId(PASSWORD_KEY))));

  }

  public static  String getSaslAccessKey() {
    return USERNAME_ACCESS;
  }
}
