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

package org.apache.streampipes.sinks.brokers.jvm.nats;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataformat.json.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.pe.context.EventSinkRuntimeContext;
import org.apache.streampipes.messaging.nats.NatsUtils;
import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.nats.NatsConfig;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.SinkParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataSink;

import io.nats.client.Connection;
import io.nats.client.Nats;
import io.nats.client.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class NatsController extends StreamPipesDataSink {

  private static final Logger LOG = LoggerFactory.getLogger(NatsController.class);

  private static final String SUBJECT_KEY = "subject";
  private static final String URLS_KEY = "natsUrls";

  private static final String ACCESS_MODE = "access-mode";
  private static final String ANONYMOUS_ACCESS = "anonymous-alternative";
  private static final String USERNAME_ACCESS = "username-alternative";
  private static final String USERNAME_GROUP = "username-group";
  private static final String USERNAME_KEY = "username";
  private static final String PASSWORD_KEY = "password";

  private static final String CONNECTION_PROPERTIES = "connection-properties";
  private static final String NONE_PROPERTIES = "none-properties-alternative";
  private static final String CUSTOM_PROPERTIES = "custom-properties-alternative";
  private static final String CONNECTION_PROPERTIES_GROUP = "connection-group";
  private static final String PROPERTIES_KEY = "properties";

  private String subject;
  private Connection natsConnection;
  private JsonDataFormatDefinition dataFormatDefinition;

  @Override
  public DataSinkDescription declareModel() {
    return DataSinkBuilder.create("org.apache.streampipes.sinks.brokers.jvm.nats")
        .category(DataSinkType.MESSAGING)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .requiredTextParameter(Labels.withId(SUBJECT_KEY), false, false)
        .requiredTextParameter(Labels.withId(URLS_KEY), false, false)
        .requiredAlternatives(Labels.withId(ACCESS_MODE), getAccessModeAlternativesOne(),
            getAccessModeAlternativesTwo())
        .requiredAlternatives(Labels.withId(CONNECTION_PROPERTIES), getConnectionPropertiesAlternativesOne(),
            getConnectionPropertiesAlternativesTwo())
        .build();
  }

  public static StaticPropertyAlternative getAccessModeAlternativesOne() {
    return Alternatives.from(Labels.withId(ANONYMOUS_ACCESS));

  }

  public static StaticPropertyAlternative getAccessModeAlternativesTwo() {
    return Alternatives.from(Labels.withId(USERNAME_ACCESS),
        StaticProperties.group(Labels.withId(USERNAME_GROUP),
            StaticProperties.stringFreeTextProperty(Labels.withId(USERNAME_KEY)),
            StaticProperties.secretValue(Labels.withId(PASSWORD_KEY))));

  }

  public static StaticPropertyAlternative getConnectionPropertiesAlternativesOne() {
    return Alternatives.from(Labels.withId(NONE_PROPERTIES));

  }

  public static StaticPropertyAlternative getConnectionPropertiesAlternativesTwo() {
    return Alternatives.from(Labels.withId(CUSTOM_PROPERTIES),
        StaticProperties.group(Labels.withId(CONNECTION_PROPERTIES_GROUP),
            StaticProperties.stringFreeTextProperty(Labels.withId(PROPERTIES_KEY))));

  }

  @Override
  public void onInvocation(SinkParams parameters,
                           EventSinkRuntimeContext runtimeContext) throws SpRuntimeException {
    this.dataFormatDefinition = new JsonDataFormatDefinition();
    NatsConfig natsConfig = NatsConfigUtils.from(
        StaticPropertyExtractor.from(parameters.getModel().getStaticProperties()));
    this.subject = natsConfig.getSubject();
    Options options = NatsUtils.makeNatsOptions(natsConfig);

    try {
      this.natsConnection = Nats.connect(options);
    } catch (Exception e) {
      LOG.error("Error when connecting to the Nats broker on " + natsConfig.getNatsUrls() + " . " + e);
    }
  }

  @Override
  public void onEvent(Event inputEvent) throws SpRuntimeException {
    try {
      Map<String, Object> event = inputEvent.getRaw();
      natsConnection.publish(subject, dataFormatDefinition.fromMap(event));
    } catch (SpRuntimeException e) {
      LOG.error("Could not publish events to Nats broker. " + e);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {
    try {
      natsConnection.flush(Duration.ofMillis(50));
      natsConnection.close();
    } catch (TimeoutException | InterruptedException e) {
      LOG.error("Error when disconnecting with Nats broker. " + e);
    }
  }
}
