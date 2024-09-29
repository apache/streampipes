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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic;

import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.ADVANCED_GROUP_TRANSPORT;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.REQUIRED_GROUP_TRANSPORT;
import static org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xLabels.SUPPORTED_TRANSPORTS;

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
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.AdapterConfigurationProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.EventSchemaProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.config.MetadataOptionGenerator;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.ContinuousPlcRequestReader;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.OneTimePlcRequestReader;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection.PlcRequestProvider;
import org.apache.streampipes.extensions.connectors.plc.adapter.generic.model.Plc4xConnectionExtractor;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableGroupStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;

import java.util.List;
import java.util.function.Function;

import org.apache.plc4x.java.api.PlcConnectionManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.apache.plc4x.java.api.metadata.Option;
import org.apache.plc4x.java.api.metadata.OptionMetadata;
import org.apache.plc4x.java.utils.cache.CachedPlcConnectionManager;

public class GenericPlc4xAdapter implements StreamPipesAdapter, SupportsRuntimeConfig {

  private PullAdapterScheduler pullAdapterScheduler;
  private final PlcRequestProvider requestProvider;
  private final EventSchemaProvider schemaProvider;

  private final PlcDriver driver;
  private final PlcConnectionManager connectionManager;

  public GenericPlc4xAdapter(PlcDriver driver, PlcConnectionManager connectionManager) {
    this.requestProvider = new PlcRequestProvider();
    this.schemaProvider = new EventSchemaProvider();
    this.driver = driver;
    this.connectionManager = connectionManager;
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return new AdapterConfigurationProvider().makeConfig(driver, connectionManager);
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor, IEventCollector collector,
          IAdapterRuntimeContext adapterRuntimeContext) {
    var settings = new Plc4xConnectionExtractor(extractor.getStaticPropertyExtractor(), driver.getProtocolCode())
            .makeSettings();
    var plcRequestReader = new ContinuousPlcRequestReader(connectionManager, settings, requestProvider, collector);
    this.pullAdapterScheduler = new PullAdapterScheduler();
    this.pullAdapterScheduler.schedule(plcRequestReader, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor, IAdapterRuntimeContext adapterRuntimeContext) {
    this.pullAdapterScheduler.shutdown();
  }

  @Override
  public GuessSchema onSchemaRequested(IAdapterParameterExtractor extractor,
          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    try {
      var settings = new Plc4xConnectionExtractor(extractor.getStaticPropertyExtractor(), driver.getProtocolCode())
              .makeSettings();
      var schemaBuilder = GuessSchemaBuilder.create();
      var allProperties = schemaProvider.makeSchema(settings.nodes());

      var event = new OneTimePlcRequestReader(connectionManager, settings, requestProvider).readPlcDataSynchronized();

      schemaBuilder.properties(allProperties);
      schemaBuilder.preview(event);

      return schemaBuilder.build();
    } catch (Exception e) {
      throw new AdapterException("Could not read plc", e);
    }
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName, IStaticPropertyExtractor extractor)
          throws SpConfigurationException {
    Function<OptionMetadata, List<Option>> metadataOption = staticPropertyInternalName.equals(REQUIRED_GROUP_TRANSPORT)
            ? OptionMetadata::getRequiredOptions
            : OptionMetadata::getOptions;

    if (staticPropertyInternalName.equals(REQUIRED_GROUP_TRANSPORT)
            || staticPropertyInternalName.equals(ADVANCED_GROUP_TRANSPORT)) {
      var selectedTransport = extractor.selectedSingleValue(SUPPORTED_TRANSPORTS, String.class);
      var transportMetadataSelection = extractor.getStaticPropertyByName(staticPropertyInternalName,
              RuntimeResolvableGroupStaticProperty.class);
      var driverMetadata = this.driver.getMetadata();
      var transportMetadata = driverMetadata.getTransportConfigurationOptionMetadata(selectedTransport);
      if (transportMetadata.isPresent()) {
        var staticProperties = new MetadataOptionGenerator().getOptions(metadataOption.apply(transportMetadata.get()));
        transportMetadataSelection.setStaticProperties(List.of(staticProperties));
      } else {
        transportMetadataSelection.setStaticProperties(List.of());
      }
      return transportMetadataSelection;
    }
    throw new SpConfigurationException(String.format("Not supported internal name %s", staticPropertyInternalName));
  }
}
