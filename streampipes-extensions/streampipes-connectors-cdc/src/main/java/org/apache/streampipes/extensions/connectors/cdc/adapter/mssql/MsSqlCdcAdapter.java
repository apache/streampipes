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

package org.apache.streampipes.extensions.connectors.cdc.adapter.mssql;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.api.connect.IAdapterConfiguration;
import org.apache.streampipes.extensions.api.connect.IEventCollector;
import org.apache.streampipes.extensions.api.connect.StreamPipesAdapter;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.extensions.api.runtime.SupportsRuntimeConfig;
import org.apache.streampipes.model.connect.guess.SampleData;
import org.apache.streampipes.model.extensions.ExtensionAssetType;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.model.staticproperty.StaticProperty;
import org.apache.streampipes.sdk.builder.adapter.AdapterConfigurationBuilder;
import org.apache.streampipes.sdk.builder.adapter.SampleDataBuilder;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.DATABASE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.ENCRYPT_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.HOST_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.PASSWORD_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.PORT_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.TABLE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.TIMEZONE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.TRUST_SERVER_CERTIFICATE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.MsSqlCdcAdapterConfig.USERNAME_KEY;

public class MsSqlCdcAdapter implements StreamPipesAdapter, SupportsRuntimeConfig {

  private static final Logger LOG = LoggerFactory.getLogger(MsSqlCdcAdapter.class);

  public static final String ID = "org.apache.streampipes.connect.cdc.adapter.mssql";
  static final long ENGINE_STARTUP_TIMEOUT_SECONDS = 10;

  private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
  };

  private final ObjectMapper mapper;
  private final MsSqlMetadataClient metadataClient;

  private DebeziumEngine<ChangeEvent<String, String>> engine;
  private ExecutorService executorService;
  private Map<String, MsSqlMetadataClient.TemporalColumnMode> temporalColumns;
  private ZoneId configuredZoneId;
  private volatile Throwable engineFailure;
  private volatile boolean connectorStarted;
  private volatile boolean stopRequested;

  public MsSqlCdcAdapter() {
    this(new ObjectMapper(), new MsSqlMetadataClient());
  }

  MsSqlCdcAdapter(ObjectMapper mapper, MsSqlMetadataClient metadataClient) {
    this.mapper = mapper;
    this.metadataClient = metadataClient;
  }

  @Override
  public StaticProperty resolveConfiguration(String staticPropertyInternalName,
                                             IStaticPropertyExtractor extractor) throws SpConfigurationException {
     if (!TABLE_KEY.equals(staticPropertyInternalName)) {
      return null;
    }

    MsSqlCdcAdapterConfig config = MsSqlCdcAdapterConfig.from(extractor, false);
    RuntimeResolvableOneOfStaticProperty tableProperty =
        extractor.getStaticPropertyByName(TABLE_KEY, RuntimeResolvableOneOfStaticProperty.class);
    tableProperty.setOptions(metadataClient.discoverTables(config));
    return tableProperty;
  }

  @Override
  public IAdapterConfiguration declareConfig() {
    return AdapterConfigurationBuilder.create(ID, 0, MsSqlCdcAdapter::new)
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
        .requiredSingleValueSelectionFromContainer(
            Labels.withId(TABLE_KEY),
            java.util.List.of(HOST_KEY, PORT_KEY, DATABASE_KEY, USERNAME_KEY, PASSWORD_KEY, ENCRYPT_KEY,
                TIMEZONE_KEY,
                TRUST_SERVER_CERTIFICATE_KEY)
        )
        .buildConfiguration();
  }

  @Override
  public void onAdapterStarted(IAdapterParameterExtractor extractor,
                               IEventCollector collector,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    MsSqlCdcAdapterConfig config = MsSqlCdcAdapterConfig.from(extractor.getStaticPropertyExtractor());
    metadataClient.validateSelectedTable(config);
    this.temporalColumns = metadataClient.describeTemporalColumns(config);
    this.configuredZoneId = config.getZoneId();
    this.engineFailure = null;
    this.connectorStarted = false;
    this.stopRequested = false;

    CountDownLatch startupLatch = new CountDownLatch(1);
    Properties properties = buildDebeziumProperties(config, extractor.getAdapterDescription().getElementId());
    this.engine = DebeziumEngine.create(Json.class)
        .using(properties)
        .using((success, message, error) -> handleEngineCompletion(
            success,
            message,
            error,
            adapterRuntimeContext.getLogger(),
            startupLatch
        ))
        .using(createConnectorCallback(startupLatch))
        .notifying(record -> processRecord(record, collector))
        .build();

    this.executorService = Executors.newSingleThreadExecutor();
    this.executorService.execute(engine);
    awaitStartup(startupLatch, adapterRuntimeContext.getLogger());
  }

  @Override
  public void onAdapterStopped(IAdapterParameterExtractor extractor,
                               IAdapterRuntimeContext adapterRuntimeContext) throws AdapterException {
    this.stopRequested = true;

    if (engine != null) {
      try {
        engine.close();
      } catch (IOException e) {
        throw new AdapterException("Failed to stop Debezium engine: " + e.getMessage(), e);
      }
    }

    if (executorService != null) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(15, TimeUnit.SECONDS)) {
          LOG.warn("Debezium engine executor did not shut down within 15 seconds.");
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        throw new AdapterException("Interrupted while waiting for Debezium engine shutdown.", e);
      }
    }

    this.engine = null;
    this.executorService = null;
    this.temporalColumns = null;
    this.configuredZoneId = null;
    this.engineFailure = null;
    this.connectorStarted = false;
    this.stopRequested = false;
  }

  @Override
  public SampleData onSampleDataRequested(IAdapterParameterExtractor extractor,
                                          IAdapterGuessSchemaContext adapterGuessSchemaContext) throws AdapterException {
    MsSqlCdcAdapterConfig config = MsSqlCdcAdapterConfig.from(extractor.getStaticPropertyExtractor());
    metadataClient.validateSelectedTable(config);
    return SampleDataBuilder.create()
        .sample(metadataClient.generateSample(config))
        .build();
  }

  Properties buildDebeziumProperties(MsSqlCdcAdapterConfig config, String elementId) {
    Properties props = new Properties();
    String logicalName = createLogicalName(config, elementId);
    props.setProperty("name", logicalName + "-engine");
    props.setProperty("connector.class", "io.debezium.connector.sqlserver.SqlServerConnector");
    props.setProperty("offset.storage", "org.apache.kafka.connect.storage.MemoryOffsetBackingStore");
    props.setProperty("schema.history.internal", "io.debezium.relational.history.MemorySchemaHistory");
    props.setProperty("converter.schemas.enable", "false");
    props.setProperty("key.converter.schemas.enable", "false");
    props.setProperty("value.converter.schemas.enable", "false");
    props.setProperty("include.schema.changes", "false");
    props.setProperty("tombstones.on.delete", "false");
    props.setProperty("snapshot.mode", "no_data");
    props.setProperty("tasks.max", "1");
    props.setProperty("decimal.handling.mode", "double");
    props.setProperty(
        "poll.interval.ms",
        String.valueOf(Environments.getEnvironment().getMsSqlCdcPollIntervalMs().getValueOrDefault())
    );

    props.setProperty("database.hostname", config.getHost());
    props.setProperty("database.port", String.valueOf(config.getPort()));
    props.setProperty("database.user", config.getUsername());
    props.setProperty("database.password", config.getPassword());
    props.setProperty("database.names", config.getDatabase());
    props.setProperty("database.encrypt", String.valueOf(config.getEncrypt()));
    props.setProperty("database.trustServerCertificate", String.valueOf(config.getTrustServerCertificate()));
    props.setProperty("topic.prefix", logicalName);
    props.setProperty("table.include.list", config.getTable());

    return props;
  }

  String createLogicalName(MsSqlCdcAdapterConfig config, String elementId) {
    String effectiveElementId = elementId == null || elementId.isBlank() ? "adapter" : elementId;

    return (config.getDatabase() + "_" + config.getTable() + "_" + effectiveElementId)
        .replace('.', '_')
        .replaceAll("[^A-Za-z0-9_]", "_")
        .toLowerCase();
  }

  private void processRecord(ChangeEvent<String, String> record, IEventCollector collector) {
    if (record == null || record.value() == null) {
      return;
    }

    try {
      JsonNode root = mapper.readTree(record.value());
      if (!"c".equals(root.path("op").asText())) {
        return;
      }

      JsonNode afterNode = root.path("after");
      if (!afterNode.isObject()) {
        return;
      }

      collector.collect(normalizeRow(mapper.convertValue(afterNode, MAP_TYPE)));
    } catch (Exception e) {
      LOG.error("Failed to process Debezium change event", e);
    }
  }

  private Map<String, Object> normalizeRow(Map<String, Object> row) {
    if (row == null || row.isEmpty() || temporalColumns == null || temporalColumns.isEmpty()) {
      return row;
    }

    Map<String, Object> normalized = new LinkedHashMap<>(row);
    temporalColumns.forEach((columnName, temporalMode) -> {
      if (normalized.containsKey(columnName)) {
        normalized.put(columnName, normalizeTemporalValue(normalized.get(columnName), temporalMode));
      }
    });
    return normalized;
  }

  Object normalizeTemporalValue(Object value, MsSqlMetadataClient.TemporalColumnMode temporalMode) {
    if (value == null) {
      return null;
    }

    return switch (temporalMode) {
      case DATE_EPOCH_MILLIS -> normalizeDateValue(asLong(value));
      case TIME_MILLIS -> asLong(value);
      case TIME_MICROS -> asLong(value) / 1_000L;
      case TIME_NANOS -> asLong(value) / 1_000_000L;
      case TIMESTAMP_MILLIS -> normalizeTimestampValue(asLong(value), 1L);
      case TIMESTAMP_MICROS -> normalizeTimestampValue(asLong(value), 1_000L);
      case TIMESTAMP_NANOS -> normalizeTimestampValue(asLong(value), 1_000_000L);
      case DATETIMEOFFSET_MILLIS -> parseOffsetDateTime(value);
    };
  }

  private long normalizeDateValue(long epochDays) {
    LocalDate localDate = LocalDate.ofEpochDay(epochDays);
    return localDate.atStartOfDay(getConfiguredZoneId()).toInstant().toEpochMilli();
  }

  private long normalizeTimestampValue(long rawValue, long divisor) {
    long epochMillis = rawValue / divisor;
    LocalDateTime localDateTime = Instant.ofEpochMilli(epochMillis).atOffset(ZoneOffset.UTC).toLocalDateTime();
    return localDateTime.atZone(getConfiguredZoneId()).toInstant().toEpochMilli();
  }

  private long asLong(Object value) {
    if (value instanceof Number number) {
      return number.longValue();
    }

    return Long.parseLong(String.valueOf(value));
  }

  private Object parseOffsetDateTime(Object value) {
    try {
      return OffsetDateTime.parse(String.valueOf(value)).toInstant().toEpochMilli();
    } catch (DateTimeParseException e) {
      LOG.warn("Failed to parse DATETIMEOFFSET value '{}', forwarding raw value.", value);
      return value;
    }
  }

  private ZoneId getConfiguredZoneId() {
    return configuredZoneId == null ? ZoneOffset.UTC : configuredZoneId;
  }

  private DebeziumEngine.ConnectorCallback createConnectorCallback(CountDownLatch startupLatch) {
    return new DebeziumEngine.ConnectorCallback() {
      @Override
      public void connectorStarted() {
        connectorStarted = true;
        startupLatch.countDown();
      }
    };
  }

  void handleEngineCompletion(boolean success,
                              String message,
                              Throwable error,
                              IExtensionsLogger logger,
                              CountDownLatch startupLatch) {
    if (error != null) {
      this.engineFailure = error;
    } else if (!success) {
      this.engineFailure = new AdapterException(
          "Debezium engine stopped unexpectedly" + (message == null || message.isBlank() ? "." : ": " + message)
      );
    }

    if (startupLatch != null) {
      startupLatch.countDown();
    }

    if (stopRequested) {
      return;
    }

    if (engineFailure != null) {
      logger.error("Debezium engine failed for MSSQL CDC adapter.", asException(engineFailure));
    } else if (success) {
      logger.warn("Debezium Engine Stopped", "The embedded Debezium engine stopped unexpectedly.");
    } else {
      logger.warn(
          "Debezium Engine Stopped",
          message == null || message.isBlank() ? "The embedded Debezium engine stopped unexpectedly." : message
      );
    }
  }

  private void awaitStartup(CountDownLatch startupLatch, IExtensionsLogger logger) throws AdapterException {
    try {
      boolean completed = startupLatch.await(ENGINE_STARTUP_TIMEOUT_SECONDS, TimeUnit.SECONDS);
      if (engineFailure != null) {
        throw new AdapterException("Failed to start Debezium engine: " + engineFailure.getMessage(), engineFailure);
      }

      if (!completed || !connectorStarted) {
        logger.warn(
            "Debezium Startup Delayed",
            "The embedded Debezium engine did not confirm startup within "
                + ENGINE_STARTUP_TIMEOUT_SECONDS + " seconds."
        );
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AdapterException("Interrupted while waiting for Debezium engine startup.", e);
    }
  }

  private Exception asException(Throwable throwable) {
    if (throwable instanceof Exception exception) {
      return exception;
    }

    return new AdapterException(throwable.getMessage(), throwable);
  }
}
