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

package org.apache.streampipes.extensions.connectors.mssql.adapter.polling;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.IPullAdapter;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.extensions.management.connect.PullAdapterScheduler;
import org.apache.streampipes.extensions.management.connect.adapter.util.PollingSettings;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import java.math.BigDecimal;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.BATCH_SIZE_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.DATABASE_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.DEFAULT_BATCH_SIZE;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.DEFAULT_MAX_ROWS_PER_POLL;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.ENCRYPT_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.HOST_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.MAX_ROWS_PER_POLL_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.PASSWORD_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.POLLING_INTERVAL_SECONDS_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.PORT_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.SEQUENCE_COLUMN_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.TABLE_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.TIMEZONE_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.TRUST_SERVER_CERTIFICATE_KEY;
import static org.apache.streampipes.extensions.connectors.mssql.adapter.polling.MsSqlTablePollingConfig.USERNAME_KEY;

public class MsSqlTablePollingAdapter implements StreamPipesAdapter, IPullAdapter, SupportsRuntimeConfig {

  public static final String ID = "org.apache.streampipes.connect.cdc.adapter.mssql.polling";

  private PullAdapterScheduler scheduler;
  private MsSqlTablePoller poller;
  private int pollingIntervalSeconds;

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor) throws SpConfigurationException {
    if (TABLE_KEY.equals(staticPropertyInternalName)) {
      MsSqlTablePollingConfig config = MsSqlTablePollingConfig.from(extractor, false, false);
      RuntimeResolvableOneOfStaticProperty property =
          extractor.getStaticPropertyByName(TABLE_KEY, RuntimeResolvableOneOfStaticProperty.class);
      property.setOptions(new MsSqlTablePollingClient(config).discoverTables());
      return property;
    }
    if (SEQUENCE_COLUMN_KEY.equals(staticPropertyInternalName)) {
      MsSqlTablePollingConfig config = MsSqlTablePollingConfig.from(extractor, true, false);
      RuntimeResolvableOneOfStaticProperty property =
          extractor.getStaticPropertyByName(SEQUENCE_COLUMN_KEY, RuntimeResolvableOneOfStaticProperty.class);
      property.setOptions(new MsSqlTablePollingClient(config).discoverSequenceColumns());
      return property;
    }
    return null;
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    List<String> connectionDependencies = List.of(
        HOST_KEY,
        PORT_KEY,
        DATABASE_KEY,
        USERNAME_KEY,
        PASSWORD_KEY,
        ENCRYPT_KEY,
        TRUST_SERVER_CERTIFICATE_KEY,
        TIMEZONE_KEY
    );
    List<String> sequenceDependencies = new java.util.ArrayList<>(connectionDependencies);
    sequenceDependencies.add(TABLE_KEY);

    return AdapterConfigurationBuilder.create(ID, 0, MsSqlTablePollingAdapter::new)
        .withLocales(Locales.EN)
        .withAssets(ExtensionAssetType.DOCUMENTATION, ExtensionAssetType.ICON)
        .requiredTextParameter(Labels.withId(HOST_KEY))
        .requiredIntegerParameter(Labels.withId(PORT_KEY), 1433)
        .requiredTextParameter(Labels.withId(DATABASE_KEY))
        .requiredTextParameter(Labels.withId(USERNAME_KEY))
        .requiredSecret(Labels.withId(PASSWORD_KEY))
        .requiredSlideToggle(Labels.withId(ENCRYPT_KEY), true)
        .requiredSlideToggle(Labels.withId(TRUST_SERVER_CERTIFICATE_KEY), false)
        .requiredTextParameter(Labels.withId(TIMEZONE_KEY), ZoneOffset.UTC.getId())
        .requiredSingleValueSelectionFromContainer(Labels.withId(TABLE_KEY), connectionDependencies)
        .requiredSingleValueSelectionFromContainer(Labels.withId(SEQUENCE_COLUMN_KEY), sequenceDependencies)
        .requiredIntegerParameter(Labels.withId(POLLING_INTERVAL_SECONDS_KEY), 5)
        .requiredIntegerParameter(Labels.withId(BATCH_SIZE_KEY), DEFAULT_BATCH_SIZE)
        .requiredIntegerParameter(Labels.withId(MAX_ROWS_PER_POLL_KEY), DEFAULT_MAX_ROWS_PER_POLL)
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    MsSqlTablePollingConfig config = MsSqlTablePollingConfig.from(extractor.getStaticPropertyExtractor());
    int minimumInterval = Environments.getEnvironment()
        .getMsSqlPollingMinIntervalSeconds()
        .getValueOrDefault();
    try {
      config.validate(minimumInterval);
    } catch (IllegalArgumentException e) {
      throw new AdapterException(e.getMessage(), e);
    }

    MsSqlTablePollingClient client = new MsSqlTablePollingClient(config);
    client.validateConfiguration();
    List<MsSqlColumn> currentSchema;
    Optional<BigDecimal> startupCursor;
    try (MsSqlPollingRowSource.PollSession startupSession = client.openSession()) {
      currentSchema = startupSession.currentSchema();
      startupCursor = startupSession.maximumSequence();
    } catch (Exception e) {
      throw new AdapterException("Failed to capture the SQL Server startup watermark: " + e.getMessage(), e);
    }

    this.pollingIntervalSeconds = config.pollingIntervalSeconds();
    this.poller = new MsSqlTablePoller(
        config.pollingSettings(),
        currentSchema,
        config.sequenceColumn(),
        startupCursor,
        client,
        collector::collect,
        adapterRuntimeContext.getLogger()
    );
    this.scheduler = new PullAdapterScheduler();
    this.scheduler.schedule(this, extractor.getAdapterDescription().getElementId());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) {
    if (scheduler != null) {
      scheduler.shutdown();
      scheduler = null;
    }
    poller = null;
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor,
                                          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    MsSqlTablePollingConfig config = MsSqlTablePollingConfig.from(extractor.getStaticPropertyExtractor());
    MsSqlTablePollingClient client = new MsSqlTablePollingClient(config);
    client.validateConfiguration();
    return SampleDataBuilder.create().sample(client.sampleRow()).build();
  }

  @Override
  public void pullData() {
    if (poller == null) {
      return;
    }
    try {
      poller.poll();
    } catch (RuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new IllegalStateException("MSSQL table polling failed: " + e.getMessage(), e);
    }
  }

  @Override
  public PollingSettings getPollingInterval() {
    return PollingSettings.from(TimeUnit.SECONDS, pollingIntervalSeconds);
  }
}
