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

package org.apache.streampipes.sinks.brokers.jvm.rabbitmq;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class RabbitMqController extends StandaloneEventSinkDeclarer<RabbitMqParameters> {

  private static final String RABBITMQ_BROKER_SETTINGS_KEY = "broker-settings";
  private static final String TOPIC_KEY = "topic";

  private static final String HOST_KEY = "host";
  private static final String PORT_KEY = "port";
  private static final String USER_KEY = "user";
  private static final String PASSWORD_KEY = "password";
  private static final String EXCHANGE_NAME_KEY = "exchange-name";


//  private static final String RABBITMQ_HOST_URI = "http://schema.org/rabbitMqHost";
//  private static final String RABBITMQ_PORT_URI = "http://schema.org/rabbitMqPort";
//  private static final String RABBITMQ_USER_URI = "http://schema.org/rabbitMqUser";
//  private static final String RABBITMQ_PASSWORD_URI = "http://schema.org/rabbitMqPassword";
//  private static final String EXCHANGE_NAME_URI = "http://schema.org/exchangeName";

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.rabbitmq")
            .category(DataSinkType.MESSAGING)
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .requiredTextParameter(Labels.withId(TOPIC_KEY), false, false)
            .requiredTextParameter(Labels.withId(HOST_KEY), false, false)
            .requiredIntegerParameter(Labels.withId(PORT_KEY), 5672)
            .requiredTextParameter(Labels.withId(USER_KEY), false, false)
            .requiredSecret(Labels.withId(PASSWORD_KEY))
//            .requiredTextParameter(Labels.withId(EXCHANGE_NAME_KEY), false, false)
//            .requiredOntologyConcept(Labels.withId(RABBITMQ_BROKER_SETTINGS_KEY),
//                    OntologyProperties.mandatory(RABBITMQ_HOST_URI),
//                    OntologyProperties.mandatory(RABBITMQ_PORT_URI),
//                    OntologyProperties.mandatory(RABBITMQ_USER_URI),
//                    OntologyProperties.mandatory(RABBITMQ_PASSWORD_URI),
//                    OntologyProperties.optional(EXCHANGE_NAME_URI))
            .build();
  }

  @Override
  public ConfiguredEventSink<RabbitMqParameters> onInvocation(DataSinkInvocation graph,
                                                              DataSinkParameterExtractor extractor) {
    String publisherTopic = extractor.singleValueParameter(TOPIC_KEY, String.class);

//    String rabbitMqHost = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY, RABBITMQ_HOST_URI,
//            String.class);
//    Integer rabbitMqPort = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY, RABBITMQ_PORT_URI,
//            Integer.class);
//    String rabbitMqUser = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY, RABBITMQ_USER_URI,
//            String.class);
//    String rabbitMqPassword = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY,
//            RABBITMQ_PASSWORD_URI,
//            String.class);
//    String exchangeName = extractor.supportedOntologyPropertyValue(RABBITMQ_BROKER_SETTINGS_KEY,
//            EXCHANGE_NAME_URI,
//            String.class);

    String rabbitMqHost = extractor.singleValueParameter(HOST_KEY, String.class);
    Integer rabbitMqPort = extractor.singleValueParameter(PORT_KEY, Integer.class);
    String rabbitMqUser = extractor.singleValueParameter(USER_KEY, String.class);
    String rabbitMqPassword = extractor.secretValue(PASSWORD_KEY);
//    String exchangeName = extractor.singleValueParameter(EXCHANGE_NAME_KEY, String.class);
    String exchangeName = "logs";

    RabbitMqParameters params = new RabbitMqParameters(graph, rabbitMqHost, rabbitMqPort, publisherTopic,
            rabbitMqUser, rabbitMqPassword, exchangeName);

    return new ConfiguredEventSink<>(params, RabbitMqConsumer::new);



  }
}
