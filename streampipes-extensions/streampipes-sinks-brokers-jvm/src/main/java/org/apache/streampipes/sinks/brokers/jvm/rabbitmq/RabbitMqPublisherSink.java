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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.pe.shared.PlaceholderExtractor;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RabbitMqPublisherSink extends StreamPipesDataSink {

  private static final Logger LOG = LoggerFactory.getLogger(RabbitMqPublisherSink.class);
  private static final String TOPIC_KEY = "topic";

  private static final String HOST_KEY = "host";
  private static final String PORT_KEY = "port";
  private static final String USER_KEY = "user";
  private static final String PASSWORD_KEY = "password";
  private static final String EXCHANGE_NAME_KEY = "exchange-name";

  private RabbitMqPublisher publisher;
  private JsonDataFormatDefinition dataFormatDefinition;
  private String topic;


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
        .requiredTextParameter(Labels.withId(EXCHANGE_NAME_KEY), false, false)
        .requiredSecret(Labels.withId(PASSWORD_KEY))
        .build();
  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {

    var extractor = parameters.extractor();
    this.dataFormatDefinition = new JsonDataFormatDefinition();
    String publisherTopic = extractor.singleValueParameter(TOPIC_KEY, String.class);

    String rabbitMqHost = extractor.singleValueParameter(HOST_KEY, String.class);
    Integer rabbitMqPort = extractor.singleValueParameter(PORT_KEY, Integer.class);
    String rabbitMqUser = extractor.singleValueParameter(USER_KEY, String.class);
    String rabbitMqPassword = extractor.secretValue(PASSWORD_KEY);
    String exchangeName = extractor.singleValueParameter(EXCHANGE_NAME_KEY, String.class);

    var rabbitMqParameters = new RabbitMqParameters(rabbitMqHost, rabbitMqPort, publisherTopic,
        rabbitMqUser, rabbitMqPassword, exchangeName);

    this.publisher = new RabbitMqPublisher(rabbitMqParameters);
    this.topic = rabbitMqParameters.getRabbitMqTopic();

    if (!this.publisher.isConnected()) {
      throw new SpRuntimeException("Could not establish conntection to RabbitMQ broker. Host: "
          + rabbitMqParameters.getRabbitMqHost() + " Port: " + rabbitMqParameters.getRabbitMqPort());
    }
  }

  @Override
  public void onEvent(Event inputEvent) throws SpRuntimeException {
    try {
      publisher.fire(dataFormatDefinition.fromMap(inputEvent.getRaw()),
          PlaceholderExtractor.replacePlaceholders(inputEvent, topic));
    } catch (SpRuntimeException e) {
      LOG.error("Could not serialiaze event");
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    publisher.cleanup();
  }
}
