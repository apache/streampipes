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

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.nats.NatsConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.DataSinkBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.DataSinkParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventSink;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventSinkDeclarer;

public class NatsController extends StandaloneEventSinkDeclarer<NatsParameters> {

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

  @Override
  public ConfiguredEventSink<NatsParameters> onInvocation(DataSinkInvocation graph,
                                                          DataSinkParameterExtractor extractor) {

    NatsConfig natsConfig = NatsConfigUtils.from(StaticPropertyExtractor.from(graph.getStaticProperties()));

    NatsParameters params = new NatsParameters(graph, natsConfig);

    return new ConfiguredEventSink<>(params, NatsPublisher::new);
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
}
