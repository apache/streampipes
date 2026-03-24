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
import org.apache.streampipes.extensions.api.connect.DataSourceHealthCheckResult;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IDataSourceHealthCheck;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConfig;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConnectUtils;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttConsumer;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttHealthChecker;
import org.apache.streampipes.extensions.connectors.mqtt.shared.MqttSingleMessageReceiver;
import org.apache.streampipes.extensions.management.connect.adapter.BrokerEventProcessor;
import org.apache.streampipes.extensions.management.connect.adapter.parser.Parsers;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Locales;

import java.io.ByteArrayInputStream;

public class MqttProtocol implements StreamPipesAdapter, IDataSourceHealthCheck {

  public static final String ID = "org.apache.streampipes.connect.iiot.protocol.stream.mqtt";

  private MqttConsumer mqttConsumer;
  private MqttConfig mqttConfig;

  public MqttProtocol() {
  }


  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder
        .create(ID, 1, MqttProtocol::new)
        .withSupportedParsers(Parsers.defaultParsers())
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .requiredTextParameter(MqttConnectUtils.getBrokerUrlLabel())
        .requiredAlternatives(
            MqttConnectUtils.getAccessModeLabel(), MqttConnectUtils.getAnonymousAccess(),
            MqttConnectUtils.getUsernameAccess(), MqttConnectUtils.getClientCertAccess()
        )
        .requiredTextParameter(MqttConnectUtils.getTopicLabel())
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(
      IAdapterParameterExtractor extractor,
      IEventCollector collector,
      IAdapterRuntimeContext adapterRuntimeContext
  ) throws AdapterException {

    this.initializeMqttConfig(extractor.getStaticPropertyExtractor());
    this.mqttConsumer = new MqttConsumer(
        this.mqttConfig,
        new BrokerEventProcessor(extractor.selectedParser(), collector)
    );

    this.mqttConsumer.start();
  }

  @Override
  public void onAdapterStopped(
      IAdapterParameterExtractor extractor,
      IAdapterRuntimeContext adapterRuntimeContext
  ) {
    if (this.mqttConsumer != null) {
      this.mqttConsumer.stop();
    }
  }

  @Override
  public SampleData onSampleDataRequested(
      IAdapterParameterExtractor extractor,
      IAdapterGuessSchemaContext adapterGuessSchemaContext
  ) throws AdapterException {

    this.initializeMqttConfig(extractor.getStaticPropertyExtractor());

    var payload = getSampleEventAsByte();

    return extractor.selectedParser()
                    .getSampleData(new ByteArrayInputStream(payload));

  }

  private byte[] getSampleEventAsByte() throws AdapterException {
    var receiver = new MqttSingleMessageReceiver(this.mqttConfig, 10);
    return receiver.receiveSingleMessage();
  }

  public void initializeMqttConfig(IStaticPropertyExtractor extractor) {
    this.mqttConfig = MqttConnectUtils.getMqttConfig(extractor);
  }

  @Override
  public DataSourceHealthCheckResult checkDataSourceHealth() {
    if (mqttConfig == null) {
      return DataSourceHealthCheckResult.unhealthy("MQTT config not initialized");
    }
    return new MqttHealthChecker(mqttConfig).check();
  }

}
