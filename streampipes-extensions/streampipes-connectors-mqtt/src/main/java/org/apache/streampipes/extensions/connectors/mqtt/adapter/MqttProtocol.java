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
package org.apache.streampipes.extensions.connectors.mqtt.adapter;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConfig;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConsumer;
import org.apache.streampipes.extensions.management.connect.adapter.BrokerEventProcessor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.AdapterType;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Locales;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttProtocol implements StreamPipesAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(MqttProtocol.class);

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.mqtt";

  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  public MqttProtocol() {
  }

  public void applyConfiguration(IStaticPropertyExtractor extractor) {
    this.mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 0, MqttProtocol::new).withSupportedParsers(Parsers.defaultParsers())
            .withLocales(Locales.EN).withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
            .withCategory(AdapterType.Generic, AdapterType.Manufacturing)
            .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
            .requiredAlternatives(MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAlternativesOne(),
                    MqttConnectUtils.getAlternativesTwo())
            .requiredTextParameter(MqttConnectUtils.getTopicLabel()).buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor, IEventCollector collector,
          IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {

    this.applyConfiguration(extractor.getStaticPropertyExtractor());
    this.mqttConsumer = new MqttConsumer(this.mqttConfig,
            new BrokerEventProcessor(extractor.selectedParser(), collector));

    Thread thread = new Thread(this.mqttConsumer);
    thread.start();
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext)
          throws AdapterException {
    this.mqttConsumer.close();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    try {
      AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
      this.applyConfiguration(extractor.getStaticPropertyExtractor());
      List<byte[]> elements = new ArrayList<>();
      InternalEventProcessor<byte[]> eventProcessor = elements::add;

      MqttConsumer consumer = new MqttConsumer(this.mqttConfig, eventProcessor);

      Thread thread = new Thread(consumer);
      thread.setUncaughtExceptionHandler((t, e) -> exceptionRef.set(e.getCause()));
      thread.start();

      while (consumer.getMessageCount() < 1 && exceptionRef.get() == null) {
        try {
          TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
          break;
        }
      }
      consumer.close();

      Throwable threadException = exceptionRef.get();
      if (threadException != null) {
        throw new AdapterException(threadException.getMessage(), threadException);
      }

      return extractor.selectedParser().getGuessSchema(new ByteArrayInputStream(elements.get(0)));
    } catch (Exception e) {
      throw new AdapterException(e.getMessage(), e);
    }
  }
}
