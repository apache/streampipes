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

package org.apache.streampipes.connect.iiot.protocol.stream;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.exceptions.connect.ParseException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.messaging.nats.NatsConsumer;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.nats.NatsConfig;
import org.apache.streampipes.model.staticproperty.StaticPropertyAlternative;
import org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.ACCESS_MODE;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.ANONYMOUS_ACCESS;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.CONNECTION_PROPERTIES;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.CONNECTION_PROPERTIES_GROUP;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.CUSTOM_PROPERTIES;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.NONE_PROPERTIES;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.PASSWORD_KEY;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.PROPERTIES_KEY;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.SUBJECT_KEY;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.URLS_KEY;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.USERNAME_ACCESS;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.USERNAME_GROUP;
import static org.apache.streampipes.pe.shared.config.nats.NatsConfigUtils.USERNAME_KEY;

public class NatsProtocol implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.nats";
  private NatsConfig natsConfig;
  private NatsConsumer natsConsumer;

  private static final int MAX_TIMEOUT = 8000;
  private static final int TIMEOUT = 100;

  public NatsProtocol() {

  }

  public void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.natsConfig = NatsConfigUtils.from(extractor);
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
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, NatsProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withCategory(AdapterType.Generic)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredTextParameter(Labels.withId(URLS_KEY), false, false)
        .requiredTextParameter(Labels.withId(SUBJECT_KEY), false, false)
        .requiredAlternatives(Labels.withId(ACCESS_MODE), getAccessModeAlternativesOne(),
            getAccessModeAlternativesTwo())
        .requiredAlternatives(Labels.withId(CONNECTION_PROPERTIES), getConnectionPropertiesAlternativesOne(),
            getConnectionPropertiesAlternativesTwo())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.natsConsumer = new NatsConsumer(natsConfig);
    try {
      this.natsConsumer.connect(new BrokerEventProcessor(extractor.selectedParser(), collector));
    } catch (SpRuntimeException e) {
      throw new AdapterException("Error when connecting to the Nats broker on "
          + natsConfig.getNatsUrls() + " . ", e);
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.natsConsumer.disconnect();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    List<byte[]> elements = new ArrayList<>();
    this.natsConsumer = new NatsConsumer(natsConfig);
    final boolean[] completed = {false};
    InternalEventProcessor<byte[]> processor = event -> {
      elements.add(event);
      completed[0] = true;
    };

    try {
      this.natsConsumer.connect(processor);
    } catch (SpRuntimeException e) {
      throw new ParseException("Could not connect to Nats broker", e);
    }

    int totalTimeout = 0;
    while (!completed[0] && totalTimeout < MAX_TIMEOUT) {
      try {
        TimeUnit.MILLISECONDS.sleep(TIMEOUT);
        totalTimeout += TIMEOUT;
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    if (elements.size() > 0) {
      return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(elements.get(0)));
    } else {
      throw new ParseException("Did not receive any data within " + MAX_TIMEOUT / 1000
          + " seconds, is this subjects currently providing data?");
    }
  }
}
