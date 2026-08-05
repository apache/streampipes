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

import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.model.monitoring.SpLogMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MsSqlTablePollingClientIntegrationTest {

  private MsSqlTablePollingConfig config;
  private MsSqlTableIdentifier table;
  private String viewName;

  @BeforeEach
  void createSqlServerFixture() throws Exception {
    String host = System.getenv("SP_TEST_MSSQL_HOST");
    Assumptions.assumeTrue(host != null && !host.isBlank(), "SP_TEST_MSSQL_HOST is not configured");

    String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    this.table = new MsSqlTableIdentifier("sp.test_" + suffix, "orders.current_" + suffix);
    this.viewName = "orders_view_" + suffix;
    this.config = config(table);

    try (Connection connection = new MsSqlTablePollingClient(config).openConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("CREATE SCHEMA " + MsSqlTablePollingClient.quoteIdentifier(table.schema()));
      statement.execute("CREATE TABLE " + table.displayName() + " ("
          + "sequence_id DECIMAL(38, 0) NOT NULL, "
          + "recorded_at DATETIME2(3) NULL, "
          + "payload VARBINARY(16) NULL, "
          + "description NVARCHAR(100) NULL, "
          + "fractional DECIMAL(10, 2) NULL, "
          + "approximate FLOAT NULL, "
          + "nullable_cursor BIGINT NULL, "
          + "CONSTRAINT " + MsSqlTablePollingClient.quoteIdentifier("uq_sequence_" + suffix)
          + " UNIQUE (sequence_id))");
      statement.execute("CREATE VIEW " + MsSqlTablePollingClient.quoteIdentifier(table.schema()) + "."
          + MsSqlTablePollingClient.quoteIdentifier(viewName) + " AS SELECT sequence_id FROM " + table.displayName());
    }
  }

  @AfterEach
  void dropSqlServerFixture() throws Exception {
    if (config == null) {
      return;
    }
    try (Connection connection = new MsSqlTablePollingClient(config).openConnection();
         Statement statement = connection.createStatement()) {
      statement.execute("DROP VIEW " + MsSqlTablePollingClient.quoteIdentifier(table.schema()) + "."
          + MsSqlTablePollingClient.quoteIdentifier(viewName));
      statement.execute("DROP TABLE " + table.displayName());
      statement.execute("DROP SCHEMA " + MsSqlTablePollingClient.quoteIdentifier(table.schema()));
    }
  }

  @Test
  void discoversMetadataAndExecutesExactOrderedKeysetPolling() throws Exception {
    MsSqlTablePollingClient client = new MsSqlTablePollingClient(config);

    assertTrue(client.discoverTables().stream().anyMatch(option -> table.encode().equals(option.getInternalName())));
    assertFalse(client.discoverTables().stream().anyMatch(option -> option.getName().contains(viewName)));
    assertEquals(List.of("sequence_id"), client.discoverSequenceColumns().stream().map(option -> option.getName()).toList());
    assertEquals(BigDecimal.ONE, client.sampleRow().get("sequence_id"));

    insertRows(client);
    List<MsSqlColumn> schema = client.describeSchema();
    MsSqlColumn cursor = schema.stream().filter(column -> column.name().equals("sequence_id")).findFirst().orElseThrow();
    assertEquals(Types.DECIMAL, cursor.jdbcType());
    assertEquals(38, cursor.precision());
    assertEquals(0, cursor.scale());

    try (MsSqlPollingRowSource.PollSession session = client.openSession()) {
      assertEquals(new BigDecimal("9223372036854775809"), session.maximumSequence().orElseThrow());
      List<MsSqlRow> rows = session.readAfter(Optional.of(new BigDecimal("9223372036854775808")), 10);
      assertEquals(1, rows.size());
      assertEquals(new BigDecimal("9223372036854775809"), rows.get(0).sequence());
      assertInstanceOf(Long.class, rows.get(0).event().get("recorded_at"));
      assertEquals("BAUG", rows.get(0).event().get("payload"));
    }

    InMemoryCheckpointStore store = new InMemoryCheckpointStore();
    store.save("adapter-live", 0, Optional.of(new BigDecimal("99999999999999999999999999999999999999")));
    TestLogger logger = new TestLogger();
    List<Map<String, Object>> emitted = new ArrayList<>();
    new MsSqlTablePoller(
        "adapter-live",
        new MsSqlPollingSettings(StartupMode.ALL_EXISTING, null, 10, 10),
        schema,
        "sequence_id",
        client,
        store,
        emitted::add,
        logger
    ).poll();
    assertTrue(emitted.isEmpty());
    assertEquals(1, logger.errors);
  }

  private void insertRows(MsSqlTablePollingClient client) throws Exception {
    String sql = "INSERT INTO " + table.displayName()
        + " (sequence_id, recorded_at, payload, description) VALUES (?, ?, ?, ?)";
    try (Connection connection = client.openConnection(); PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setBigDecimal(1, new BigDecimal("9223372036854775808"));
      statement.setString(2, "2026-08-05T10:15:30.123");
      statement.setBytes(3, new byte[]{1, 2, 3});
      statement.setString(4, "first");
      statement.executeUpdate();
      statement.setBigDecimal(1, new BigDecimal("9223372036854775809"));
      statement.setString(2, "2026-08-05T10:15:31.123");
      statement.setBytes(3, new byte[]{4, 5, 6});
      statement.setString(4, "second");
      statement.executeUpdate();
    }
  }

  private MsSqlTablePollingConfig config(MsSqlTableIdentifier selectedTable) {
    return new MsSqlTablePollingConfig(
        System.getenv("SP_TEST_MSSQL_HOST"),
        Integer.parseInt(valueOrDefault("SP_TEST_MSSQL_PORT", "1433")),
        valueOrDefault("SP_TEST_MSSQL_DATABASE", "master"),
        valueOrDefault("SP_TEST_MSSQL_USERNAME", "sa"),
        valueOrDefault("SP_TEST_MSSQL_PASSWORD", ""),
        Boolean.parseBoolean(valueOrDefault("SP_TEST_MSSQL_ENCRYPT", "true")),
        Boolean.parseBoolean(valueOrDefault("SP_TEST_MSSQL_TRUST_SERVER_CERTIFICATE", "true")),
        "UTC",
        selectedTable,
        "sequence_id",
        StartupMode.ALL_EXISTING,
        null,
        1,
        10,
        10
    );
  }

  private String valueOrDefault(String name, String defaultValue) {
    String value = System.getenv(name);
    return value == null ? defaultValue : value;
  }

  private static class TestLogger implements IExtensionsLogger {
    private int errors;

    @Override
    public void log(SpLogMessage logMessage) {
    }

    @Override
    public void error(Exception e) {
      errors++;
    }

    @Override
    public void error(String details, Exception e) {
      errors++;
    }

    @Override
    public void info(String title, String details) {
    }

    @Override
    public void warn(String title, String details) {
    }
  }
}
