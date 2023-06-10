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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConfig;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConnectUtils;
import org.apache.streampipes.pe.shared.config.mqtt.MqttConsumer;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.utils.Assets;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MqttProtocol implements StreamPipesAdapter {

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.mqtt";

  private Thread thread;
  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  public MqttProtocol() {
  }

  public void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, MqttProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
            MqttConnectUtils.getAlternativesTwo())
        .requiredTextParameter(MqttConnectUtils.getTopicLabel())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {

    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.mqttConsumer = new MqttConsumer(
        this.mqttConfig,
        new BrokerEventProcessor(extractor.selectedParser(), (event) -> {
          collector.collect(event);
        })
    );

    thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.mqttConsumer.close();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
                                       IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    List<byte[]> elements = new ArrayList<>();
    InternalEventProcessor<byte[]> eventProcessor = elements::add;

    MqttConsumer consumer = new MqttConsumer(this.mqttConfig, eventProcessor);

    Thread thread = new Thread(consumer);
    thread.start();

    while (consumer.getMessageCount() < 1) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(elements.get(0)));
  }
}
