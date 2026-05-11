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

package org.apache.streampipes.extensions.connectors.opcua.alarms;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.connectors.opcua.client.ConnectedOpcUaClient;
import org.apache.streampipes.extensions.connectors.opcua.client.OpcUaClientProvider;
import org.apache.streampipes.extensions.connectors.opcua.config.SharedUserConfiguration;
import org.apache.streampipes.extensions.connectors.opcua.config.SpOpcUaConfigExtractor;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaUtils;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.helpers.Locales;

public class OpcUaAlarmAdapter implements StreamPipesAdapter, SupportsRuntimeConfig {

  public static final String ID = "org.apache.streampipes.connect.iiot.adapters.opcua-alarms";

  private final OpcUaClientProvider clientProvider;

  private OpcUaAlarmAdapterConfig opcUaConfig;
  private ConnectedOpcUaClient connectedClient;
  private OpcUaAlarmEventSubscriber subscriber;

  public OpcUaAlarmAdapter(OpcUaClientProvider clientProvider) {
    this.clientProvider = clientProvider;
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    var builder = AdapterConfigurationBuilder.create(ID, 1, () -> new OpcUaAlarmAdapter(clientProvider))
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON);

    SharedUserConfiguration.appendSharedOpcUaConnectionConfig(builder);
    OpcUaAlarmConfiguration.appendFilterConfiguration(builder);

    return builder.buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.opcUaConfig = SpOpcUaConfigExtractor.extractAlarmAdapterConfig(
        extractor.getStaticPropertyExtractor(),
        adapterRuntimeContext.getStreamPipesClient(),
        extractor.getAdapterDescription().getElementId()
    );

    try {
      this.connectedClient = clientProvider.getClient(opcUaConfig);
      this.subscriber = new OpcUaAlarmEventSubscriber(connectedClient, opcUaConfig, collector::collect);
      this.subscriber.start();
    } catch (Exception e) {
      cleanup();
      throw new AdapterException("The connection to the OPC UA server could not be established.", e);
    }
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    cleanup();
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor,
                                          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    return new OpcUaAlarmSchemaProvider().getSampleData(
        clientProvider,
        extractor,
        adapterGuessSchemaContext.getStreamPipesClient()
    );
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor parameterExtractor) throws SpConfigurationException {
    if (OpcUaAlarmConfiguration.NOTIFIER_NODE.equals(staticPropertyInternalName)) {
      return OpcUaUtils.resolveNotifierTreeConfig(clientProvider, staticPropertyInternalName, parameterExtractor);
    }

    if (OpcUaAlarmConfiguration.EVENT_TYPE.equals(staticPropertyInternalName)) {
      return OpcUaUtils.resolveEventTypeTreeConfig(clientProvider, staticPropertyInternalName, parameterExtractor);
    }

    if (OpcUaAlarmConfiguration.ADDITIONAL_FIELDS.equals(staticPropertyInternalName)) {
      return OpcUaUtils.resolveEventFieldConfig(clientProvider, staticPropertyInternalName, parameterExtractor);
    }

    throw new SpConfigurationException("Unsupported runtime-resolvable property: " + staticPropertyInternalName);
  }

  private void cleanup() {
    if (subscriber != null) {
      subscriber.close();
      subscriber = null;
    }

    if (opcUaConfig != null) {
      clientProvider.releaseClient(opcUaConfig);
      opcUaConfig = null;
    }

    connectedClient = null;
  }
}
