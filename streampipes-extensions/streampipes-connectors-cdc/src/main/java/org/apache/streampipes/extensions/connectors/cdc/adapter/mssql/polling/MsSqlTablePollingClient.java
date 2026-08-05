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

import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.model.staticproperty.Option;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class MsSqlTablePollingClient implements MsSqlPollingRowSource {

  static final int QUERY_TIMEOUT_SECONDS = 30;

  private static final String TABLE_QUERY =
      "SELECT s.name AS schema_name, t.name AS table_name "
          + "FROM sys.tables t JOIN sys.schemas s ON t.schema_id = s.schema_id "
          + "WHERE t.is_ms_shipped = 0 AND s.name NOT IN ('sys', 'INFORMATION_SCHEMA') "
          + "ORDER BY s.name, t.name";

  private static final String SEQUENCE_COLUMN_QUERY =
      "SELECT c.name AS column_name, ty.name AS type_name, c.precision, c.scale, c.is_nullable "
          + "FROM sys.tables t "
          + "JOIN sys.schemas s ON t.schema_id = s.schema_id "
          + "JOIN sys.columns c ON c.object_id = t.object_id "
          + "JOIN sys.types ty ON c.user_type_id = ty.user_type_id "
          + "WHERE s.name = ? AND t.name = ? AND c.is_nullable = 0 "
          + "AND (ty.name IN ('tinyint', 'smallint', 'int', 'bigint') "
          + "OR (ty.name IN ('decimal', 'numeric') AND c.scale = 0)) "
          + "AND EXISTS (SELECT 1 FROM sys.indexes i "
          + "JOIN sys.index_columns ic ON i.object_id = ic.object_id AND i.index_id = ic.index_id "
          + "WHERE i.object_id = t.object_id AND i.is_unique_constraint = 1 "
          + "AND ic.column_id = c.column_id AND ic.key_ordinal = 1 "
          + "AND NOT EXISTS (SELECT 1 FROM sys.index_columns other "
          + "WHERE other.object_id = i.object_id AND other.index_id = i.index_id AND other.key_ordinal > 1)) "
          + "ORDER BY c.column_id";

  private final MsSqlTablePollingConfig config;

  public MsSqlTablePollingClient(MsSqlTablePollingConfig config) {
    this.config = config;
  }

  public List<Option> discoverTables() throws SpConfigurationException {
    try (Connection connection = openConnection();
         PreparedStatement statement = prepare(connection, TABLE_QUERY);
         ResultSet resultSet = statement.executeQuery()) {
      List<Option> tables = new ArrayList<>();
      while (resultSet.next()) {
        MsSqlTableIdentifier table = new MsSqlTableIdentifier(
            resultSet.getString("schema_name"),
            resultSet.getString("table_name")
        );
        tables.add(new Option(table.displayName(), table.encode()));
      }
      return tables;
    } catch (SQLException e) {
      throw new SpConfigurationException("Failed to discover SQL Server base tables: " + e.getMessage(), e);
    }
  }

  public List<Option> discoverSequenceColumns() throws SpConfigurationException {
    MsSqlTableIdentifier table = config.table();
    try (Connection connection = openConnection();
         PreparedStatement statement = prepare(connection, SEQUENCE_COLUMN_QUERY)) {
      statement.setString(1, table.schema());
      statement.setString(2, table.table());
      try (ResultSet resultSet = statement.executeQuery()) {
        List<Option> columns = new ArrayList<>();
        while (resultSet.next()) {
          columns.add(new Option(resultSet.getString("column_name")));
        }
        return columns;
      }
    } catch (SQLException e) {
      throw new SpConfigurationException("Failed to discover eligible sequence columns: " + e.getMessage(), e);
    }
  }

  public List<MsSqlColumn> describeSchema() throws AdapterException {
    try (Connection connection = openConnection()) {
      return describeSchema(connection, config.table());
    } catch (SQLException e) {
      throw new AdapterException("Failed to inspect SQL Server table schema: " + e.getMessage(), e);
    }
  }

  public Map<String, Object> sampleRow() throws AdapterException {
    try (PollSession session = openSession()) {
      List<MsSqlRow> rows = session.readAfter(Optional.empty(), 1);
      if (!rows.isEmpty()) {
        return rows.get(0).event();
      }

      Map<String, Object> sample = new LinkedHashMap<>();
      for (MsSqlColumn column : session.currentSchema()) {
        sample.put(column.name(), placeholder(column));
      }
      return sample;
    } catch (Exception e) {
      throw new AdapterException("Failed to generate SQL Server sample data: " + e.getMessage(), e);
    }
  }

  public void validateConfiguration() throws AdapterException {
    List<Option> sequenceColumns;
    try {
      sequenceColumns = discoverSequenceColumns();
    } catch (SpConfigurationException e) {
      throw new AdapterException(e.getMessage(), e);
    }
    if (sequenceColumns.stream().noneMatch(option -> option.getName().equals(config.sequenceColumn()))) {
      throw new AdapterException(
          "Selected sequence column is not a non-null exact integer backed by a single-column unique constraint: "
              + config.sequenceColumn()
      );
    }
  }

  @Override
  public PollSession openSession() throws SQLException, AdapterException {
    Connection connection = openConnection();
    boolean success = false;
    try {
      try {
        connection.setReadOnly(true);
      } catch (SQLException ignored) {
        // SQL Server drivers or deployments may not support changing this hint.
      }
      JdbcPollSession session = new JdbcPollSession(connection, config.table());
      success = true;
      return session;
    } finally {
      if (!success) {
        connection.close();
      }
    }
  }

  Connection openConnection() throws SQLException {
    String url = "jdbc:sqlserver://" + config.host() + ":" + config.port()
        + ";databaseName=" + config.database()
        + ";encrypt=" + config.encrypt()
        + ";trustServerCertificate=" + config.trustServerCertificate()
        + ";loginTimeout=15";
    return DriverManager.getConnection(url, config.username(), config.password());
  }

  static String quoteIdentifier(String identifier) {
    if (identifier == null || identifier.isBlank()) {
      throw new IllegalArgumentException("SQL Server identifier must not be blank.");
    }
    return "[" + identifier.replace("]", "]]") + "]";
  }

  private PreparedStatement prepare(Connection connection, String sql) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(sql);
    statement.setQueryTimeout(QUERY_TIMEOUT_SECONDS);
    return statement;
  }

  private List<MsSqlColumn> describeSchema(Connection connection,
                                           MsSqlTableIdentifier table) throws SQLException, AdapterException {
    DatabaseMetaData metadata = connection.getMetaData();
    List<MsSqlColumn> columns = new ArrayList<>();
    try (ResultSet resultSet = metadata.getColumns(null, table.schema(), table.table(), null)) {
      while (resultSet.next()) {
        columns.add(new MsSqlColumn(
            resultSet.getString("COLUMN_NAME"),
            resultSet.getInt("DATA_TYPE"),
            resultSet.getString("TYPE_NAME"),
            resultSet.getInt("COLUMN_SIZE"),
            resultSet.getInt("DECIMAL_DIGITS"),
            resultSet.getInt("NULLABLE") != DatabaseMetaData.columnNoNulls
        ));
      }
    }
    if (columns.isEmpty()) {
      throw new AdapterException("Selected base table does not exist or contains no columns: " + config.table().displayName());
    }
    return columns;
  }

  private Object convertValue(Object value, MsSqlColumn column) {
    if (value == null) {
      return null;
    }
    if (value instanceof byte[] bytes) {
      return Base64.getEncoder().encodeToString(bytes);
    }
    if (value instanceof Date date) {
      return date.toLocalDate().atStartOfDay(config.zoneId()).toInstant().toEpochMilli();
    }
    if (value instanceof Time time) {
      return time.toLocalTime().toNanoOfDay() / 1_000_000;
    }
    if (value instanceof Timestamp timestamp) {
      return timestamp.toLocalDateTime().atZone(config.zoneId()).toInstant().toEpochMilli();
    }
    if (value instanceof LocalDate date) {
      return date.atStartOfDay(config.zoneId()).toInstant().toEpochMilli();
    }
    if (value instanceof LocalTime time) {
      return time.toNanoOfDay() / 1_000_000;
    }
    if (value instanceof LocalDateTime dateTime) {
      return dateTime.atZone(config.zoneId()).toInstant().toEpochMilli();
    }
    if (value instanceof OffsetDateTime dateTime) {
      return dateTime.toInstant().toEpochMilli();
    }
    if (value instanceof Instant instant) {
      return instant.toEpochMilli();
    }
    if (isDateTimeOffset(column)) {
      try {
        return OffsetDateTime.parse(value.toString()).toInstant().toEpochMilli();
      } catch (DateTimeParseException ignored) {
        return value.toString();
      }
    }
    return value;
  }

  private boolean isDateTimeOffset(MsSqlColumn column) {
    return "datetimeoffset".equals(column.typeName().toLowerCase(Locale.ENGLISH));
  }

  private Object placeholder(MsSqlColumn column) {
    return switch (column.jdbcType()) {
      case Types.BIGINT, Types.INTEGER, Types.SMALLINT, Types.TINYINT -> 1;
      case Types.DECIMAL, Types.NUMERIC -> BigDecimal.ONE;
      case Types.DOUBLE, Types.FLOAT, Types.REAL -> 1.0;
      case Types.BIT, Types.BOOLEAN -> true;
      case Types.BINARY, Types.LONGVARBINARY, Types.VARBINARY -> "AQID";
      case Types.DATE, Types.TIME, Types.TIME_WITH_TIMEZONE, Types.TIMESTAMP, Types.TIMESTAMP_WITH_TIMEZONE ->
          1_778_149_800_000L;
      default -> "example";
    };
  }

  private class JdbcPollSession implements PollSession {
    private final Connection connection;
    private final MsSqlTableIdentifier table;
    private final List<MsSqlColumn> schema;

    private JdbcPollSession(Connection connection, MsSqlTableIdentifier table) throws SQLException, AdapterException {
      this.connection = connection;
      this.table = table;
      this.schema = describeSchema(connection, table);
    }

    @Override
    public List<MsSqlColumn> currentSchema() {
      return schema;
    }

    @Override
    public Optional<BigDecimal> maximumSequence() throws SQLException {
      String sql = "SELECT MAX(" + quoteIdentifier(config.sequenceColumn()) + ") FROM "
          + quoteIdentifier(table.schema()) + "." + quoteIdentifier(table.table());
      try (PreparedStatement statement = prepare(connection, sql);
           ResultSet resultSet = statement.executeQuery()) {
        if (!resultSet.next()) {
          return Optional.empty();
        }
        return exactDecimal(resultSet.getObject(1));
      }
    }

    @Override
    public List<MsSqlRow> readAfter(Optional<BigDecimal> cursor, int limit) throws SQLException {
      String sequence = quoteIdentifier(config.sequenceColumn());
      String sql = "SELECT TOP (?) * FROM " + quoteIdentifier(table.schema()) + "." + quoteIdentifier(table.table())
          + (cursor.isPresent() ? " WHERE " + sequence + " > ?" : "")
          + " ORDER BY " + sequence + " ASC";
      try (PreparedStatement statement = prepare(connection, sql)) {
        statement.setInt(1, limit);
        if (cursor.isPresent()) {
          statement.setBigDecimal(2, cursor.orElseThrow());
        }
        try (ResultSet resultSet = statement.executeQuery()) {
          List<MsSqlRow> rows = new ArrayList<>();
          ResultSetMetaData resultMetadata = resultSet.getMetaData();
          Map<String, MsSqlColumn> columnsByName = new LinkedHashMap<>();
          schema.forEach(column -> columnsByName.put(column.name(), column));
          while (resultSet.next()) {
            Map<String, Object> event = new LinkedHashMap<>();
            BigDecimal rowSequence = null;
            for (int index = 1; index <= resultMetadata.getColumnCount(); index++) {
              String name = resultMetadata.getColumnLabel(index);
              MsSqlColumn column = columnsByName.get(name);
              Object rawValue = resultSet.getObject(index);
              Object converted = column == null ? rawValue : convertValue(rawValue, column);
              if (name.equals(config.sequenceColumn())) {
                rowSequence = exactDecimal(rawValue).orElseThrow(
                    () -> new SQLException("Sequence column contains null: " + config.sequenceColumn())
                );
                converted = rowSequence;
              }
              event.put(name, converted);
            }
            if (rowSequence == null) {
              throw new SQLException("Sequence column is missing from query result: " + config.sequenceColumn());
            }
            rows.add(new MsSqlRow(rowSequence, event));
          }
          return rows;
        }
      }
    }

    @Override
    public void close() throws SQLException {
      connection.close();
    }
  }

  private Optional<BigDecimal> exactDecimal(Object value) {
    if (value == null) {
      return Optional.empty();
    }
    if (value instanceof BigDecimal decimal) {
      return Optional.of(decimal);
    }
    return Optional.of(new BigDecimal(value.toString()));
  }
}
