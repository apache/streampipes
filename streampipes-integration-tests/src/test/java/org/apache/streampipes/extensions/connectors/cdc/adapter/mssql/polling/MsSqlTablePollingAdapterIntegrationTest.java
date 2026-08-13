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

package org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling;

import org.apache.streampipes.connect.transformer.api.TransformationEngines;
import org.apache.streampipes.connect.transformer.js.GraalJsScriptEngine;
import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.extensions.api.connect.IAdapterPipelineElement;
import org.apache.streampipes.extensions.api.connect.context.IAdapterGuessSchemaContext;
import org.apache.streampipes.extensions.api.connect.context.IAdapterRuntimeContext;
import org.apache.streampipes.extensions.api.extractor.IAdapterParameterExtractor;
import org.apache.streampipes.extensions.api.extractor.IStaticPropertyExtractor;
import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.extensions.management.connect.adapter.model.EventCollector;
import org.apache.streampipes.extensions.management.connect.adapter.model.pipeline.AdapterPipeline;
import org.apache.streampipes.model.connect.TransformationConfig;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.model.staticproperty.RuntimeResolvableOneOfStaticProperty;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.BATCH_SIZE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.DATABASE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.ENCRYPT_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.HOST_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.MAX_ROWS_PER_POLL_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.PASSWORD_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.POLLING_INTERVAL_SECONDS_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.PORT_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.SEQUENCE_COLUMN_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.TABLE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.TIMEZONE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.TRUST_SERVER_CERTIFICATE_KEY;
import static org.apache.streampipes.extensions.connectors.cdc.adapter.mssql.polling.MsSqlTablePollingConfig.USERNAME_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnabledIfEnvironmentVariable(named = "SP_TEST_MSSQL_HOST", matches = ".+")
class MsSqlTablePollingAdapterIntegrationTest {

  private static final String DATABASE = valueOrDefault("SP_TEST_MSSQL_DATABASE", "streampipes_demo");
  private static final String USERNAME = valueOrDefault("SP_TEST_MSSQL_USERNAME", "sa");
  private static final String PASSWORD = valueOrDefault("SP_TEST_MSSQL_PASSWORD", "");
  private static final int PORT = Integer.parseInt(valueOrDefault("SP_TEST_MSSQL_PORT", "1433"));

  @Test
  void previewAndPullEmitTheSameScalarTypesForRowsAddedAfterStartup() throws Exception {
    String tableName = "sp_poll_it_" + UUID.randomUUID().toString().replace("-", "");
    String adapterId = "sp:mssql-poll-it:" + UUID.randomUUID();
    IAdapterParameterExtractor extractor = extractor(tableName, adapterId);
    IAdapterRuntimeContext runtimeContext = runtimeContext();
    List<Map<String, Object>> events = new CopyOnWriteArrayList<>();

    createTable(tableName);
    try {
      insertRow(tableName, 10L, 537_190_230L, new BigDecimal("108.000"), "first");
      insertRow(tableName, 11L, 537_190_234L, new BigDecimal("122.000"), "second");

      MsSqlTablePollingAdapter previewAdapter = new MsSqlTablePollingAdapter();
      Map<String, Object> preview = previewAdapter.onSampleDataRequested(
          extractor,
          mock(IAdapterGuessSchemaContext.class)
      ).getSamples().get(0);
      assertScalarTypes(preview);

      MsSqlTablePollingAdapter firstRun = new MsSqlTablePollingAdapter();
      try {
        firstRun.onAdapterStarted(extractor, event -> events.add(new HashMap<>(event)), runtimeContext);
        insertRow(tableName, 12L, 537_190_229L, new BigDecimal("254.000"), "after-startup");
        awaitEventCount(events, 1);
      } finally {
        firstRun.onAdapterStopped(extractor, runtimeContext);
      }

      assertEquals(
          List.of(new BigDecimal("12")),
          events.stream().map(event -> event.get("poll_sequence")).toList()
      );
      events.forEach(this::assertScalarTypes);
      events.forEach(this::assertJsonContainsNumbers);

      insertRow(tableName, 13L, 537_190_229L, new BigDecimal("254.000"), "before-restart");
      events.clear();

      MsSqlTablePollingAdapter restarted = new MsSqlTablePollingAdapter();
      try {
        restarted.onAdapterStarted(extractor, event -> events.add(new HashMap<>(event)), runtimeContext);
        insertRow(tableName, 14L, 537_190_229L, new BigDecimal("255.000"), "after-restart");
        awaitEventCount(events, 1);
      } finally {
        restarted.onAdapterStopped(extractor, runtimeContext);
      }

      assertEquals(new BigDecimal("14"), events.get(0).get("poll_sequence"));
      assertEquals("after-restart", events.get(0).get("event_note"));
      assertFalse(events.get(0).values().stream().anyMatch(Map.class::isInstance));
    } finally {
      dropTable(tableName);
    }
  }

  @Test
  void scriptedRuntimePipelineKeepsExactNumericColumnsAsJsonNumbers() throws Exception {
    TransformationEngines.INSTANCE.registerEngine(GraalJsScriptEngine::new);
    String tableName = "sp_poll_script_it_" + UUID.randomUUID().toString().replace("-", "");
    String adapterId = "sp:mssql-poll-script-it:" + UUID.randomUUID();
    IAdapterParameterExtractor extractor = extractor(tableName, adapterId);
    IAdapterRuntimeContext runtimeContext = runtimeContext();
    List<byte[]> serializedEvents = new CopyOnWriteArrayList<>();

    createTable(tableName);
    try {
      TransformationConfig transformationConfig = TransformationConfig.withDefaultScript();
      transformationConfig.setScriptActive(true);
      extractor.getAdapterDescription().setTransformationConfig(transformationConfig);

      IAdapterPipelineElement serializationSink = event -> {
        serializedEvents.add(new JsonDataFormatDefinition().fromMap(event));
        return event;
      };
      AdapterPipeline pipeline = new AdapterPipeline(
          List.of(),
          transformationConfig,
          serializationSink,
          null,
          new EventSchema()
      );
      EventCollector collector = new EventCollector(pipeline, runtimeContext);
      MsSqlTablePollingAdapter adapter = new MsSqlTablePollingAdapter();
      try {
        adapter.onAdapterStarted(extractor, collector, runtimeContext);
        insertRow(tableName, 21L, 734_820_116L, new BigDecimal("73.250"), "scripted-event");
        awaitSerializedEventCount(serializedEvents, 1);
      } finally {
        adapter.onAdapterStopped(extractor, runtimeContext);
        collector.close();
      }

      var root = JacksonSerializer.getObjectMapper().readTree(serializedEvents.get(0));
      assertTrue(root.get("poll_sequence").isIntegralNumber());
      assertTrue(root.get("asset_reference").isIntegralNumber());
      assertTrue(root.get("measurement_amount").isNumber());
      assertFalse(root.get("poll_sequence").isObject());
      assertFalse(root.get("measurement_amount").isObject());
    } finally {
      dropTable(tableName);
    }
  }

  private void assertScalarTypes(Map<String, Object> event) {
    assertInstanceOf(BigDecimal.class, event.get("poll_sequence"));
    assertInstanceOf(Long.class, event.get("asset_reference"));
    assertEquals(
        new BigDecimal("108.000").scale(),
        assertInstanceOf(BigDecimal.class, event.get("measurement_amount")).scale()
    );
    assertInstanceOf(Long.class, event.get("captured_at"));
    assertInstanceOf(String.class, event.get("event_note"));
  }

  private void assertJsonContainsNumbers(Map<String, Object> event) {
    byte[] json = new JsonDataFormatDefinition().fromMap(event);
    try {
      var root = JacksonSerializer.getObjectMapper().readTree(json);
      assertTrue(root.get("poll_sequence").isIntegralNumber());
      assertTrue(root.get("asset_reference").isIntegralNumber());
      assertTrue(root.get("measurement_amount").isNumber());
      assertFalse(root.get("poll_sequence").isObject());
      assertFalse(root.get("measurement_amount").isObject());
    } catch (Exception e) {
      throw new AssertionError("Could not inspect serialized adapter event", e);
    }
  }

  private void awaitEventCount(List<Map<String, Object>> events, int expected) throws InterruptedException {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (events.size() < expected && System.nanoTime() < deadline) {
      Thread.sleep(50);
    }
    assertEquals(expected, events.size(), "Timed out waiting for MSSQL polling events");
  }

  private void awaitSerializedEventCount(List<byte[]> events, int expected) throws InterruptedException {
    long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(10);
    while (events.size() < expected && System.nanoTime() < deadline) {
      Thread.sleep(50);
    }
    assertEquals(expected, events.size(), "Timed out waiting for serialized MSSQL polling events");
  }

  private IAdapterParameterExtractor extractor(String tableName, String adapterId) {
    IStaticPropertyExtractor properties = mock(IStaticPropertyExtractor.class);
    when(properties.singleValueParameter(HOST_KEY, String.class)).thenReturn(System.getenv("SP_TEST_MSSQL_HOST"));
    when(properties.singleValueParameter(PORT_KEY, Integer.class)).thenReturn(PORT);
    when(properties.singleValueParameter(DATABASE_KEY, String.class)).thenReturn(DATABASE);
    when(properties.singleValueParameter(USERNAME_KEY, String.class)).thenReturn(USERNAME);
    when(properties.secretValue(PASSWORD_KEY)).thenReturn(PASSWORD);
    when(properties.slideToggleValue(ENCRYPT_KEY)).thenReturn(true);
    when(properties.slideToggleValue(TRUST_SERVER_CERTIFICATE_KEY)).thenReturn(true);
    when(properties.singleValueParameter(TIMEZONE_KEY, String.class)).thenReturn("UTC");
    when(properties.getStaticPropertyByName(TABLE_KEY, RuntimeResolvableOneOfStaticProperty.class))
        .thenReturn(selectedRuntimeProperty("dbo." + tableName));
    when(properties.getStaticPropertyByName(SEQUENCE_COLUMN_KEY, RuntimeResolvableOneOfStaticProperty.class))
        .thenReturn(selectedRuntimeProperty("poll_sequence"));
    when(properties.singleValueParameter(POLLING_INTERVAL_SECONDS_KEY, Integer.class)).thenReturn(60);
    when(properties.singleValueParameter(BATCH_SIZE_KEY, Integer.class)).thenReturn(10);
    when(properties.singleValueParameter(MAX_ROWS_PER_POLL_KEY, Integer.class)).thenReturn(100);

    AdapterDescription description = new AdapterDescription();
    description.setElementId(adapterId);

    IAdapterParameterExtractor extractor = mock(IAdapterParameterExtractor.class);
    when(extractor.getStaticPropertyExtractor()).thenReturn(properties);
    when(extractor.getAdapterDescription()).thenReturn(description);
    return extractor;
  }

  private RuntimeResolvableOneOfStaticProperty selectedRuntimeProperty(String value) {
    Option option = new Option(value);
    option.setSelected(true);
    RuntimeResolvableOneOfStaticProperty property = new RuntimeResolvableOneOfStaticProperty();
    property.setOptions(new ArrayList<>(List.of(option)));
    return property;
  }

  private IAdapterRuntimeContext runtimeContext() {
    IAdapterRuntimeContext context = mock(IAdapterRuntimeContext.class);
    when(context.getLogger()).thenReturn(mock(IExtensionsLogger.class));
    return context;
  }

  private void createTable(String tableName) throws Exception {
    try (Connection connection = connection(); Statement statement = connection.createStatement()) {
      statement.execute("CREATE TABLE dbo." + tableName + " ("
          + "poll_sequence BIGINT NOT NULL PRIMARY KEY, "
          + "asset_reference BIGINT NOT NULL, "
          + "measurement_amount DECIMAL(18, 3) NOT NULL, "
          + "captured_at DATETIME2(3) NOT NULL, "
          + "event_note NVARCHAR(100) NOT NULL)");
    }
  }

  private void insertRow(String tableName,
                         long pollSequence,
                         long assetReference,
                         BigDecimal amount,
                         String eventNote) throws Exception {
    String sql = "INSERT INTO dbo." + tableName
        + " (poll_sequence, asset_reference, measurement_amount, captured_at, event_note) "
        + "VALUES (?, ?, ?, ?, ?)";
    try (Connection connection = connection(); PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setLong(1, pollSequence);
      statement.setLong(2, assetReference);
      statement.setBigDecimal(3, amount);
      statement.setTimestamp(4, Timestamp.from(Instant.parse("2026-08-10T10:15:30Z").plusSeconds(pollSequence)));
      statement.setString(5, eventNote);
      statement.executeUpdate();
    }
  }

  private void dropTable(String tableName) throws Exception {
    try (Connection connection = connection(); Statement statement = connection.createStatement()) {
      statement.execute("DROP TABLE IF EXISTS dbo." + tableName);
    }
  }

  private Connection connection() throws Exception {
    String url = "jdbc:sqlserver://" + System.getenv("SP_TEST_MSSQL_HOST") + ":" + PORT
        + ";databaseName=" + DATABASE + ";encrypt=true;trustServerCertificate=true;loginTimeout=15";
    return DriverManager.getConnection(url, USERNAME, PASSWORD);
  }

  private static String valueOrDefault(String name, String defaultValue) {
    String value = System.getenv(name);
    return value == null || value.isBlank() ? defaultValue : value;
  }

}
