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
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MsSqlMetadataClient {

  private static final String CDC_ENABLED_QUERY =
      "SELECT is_cdc_enabled FROM sys.databases WHERE name = DB_NAME()";

  private static final String CDC_TABLES_QUERY =
      "SELECT s.name AS schema_name, t.name AS table_name "
          + "FROM sys.tables t "
          + "JOIN sys.schemas s ON t.schema_id = s.schema_id "
          + "WHERE t.is_ms_shipped = 0 AND t.is_tracked_by_cdc = 1 "
          + "ORDER BY s.name, t.name";

  private static final String SAMPLE_ROW_QUERY_TEMPLATE = "SELECT TOP 1 * FROM [%s].[%s]";

  public List<Option> discoverTables(MsSqlCdcAdapterConfig config) throws SpConfigurationException {
    try (Connection connection = openConnection(config)) {
      validateCdcEnabled(connection);

      List<Option> options = new ArrayList<>();
      try (PreparedStatement statement = connection.prepareStatement(CDC_TABLES_QUERY);
           ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          options.add(new Option(resultSet.getString("schema_name") + "." + resultSet.getString("table_name")));
        }
      }

      if (options.isEmpty()) {
        throw new SpConfigurationException("No CDC-enabled tables found in database " + config.getDatabase());
      }

      return options;
    } catch (SQLException e) {
      throw new SpConfigurationException("Failed to resolve SQL Server tables: " + e.getMessage(), e);
    }
  }

  public void validateSelectedTable(MsSqlCdcAdapterConfig config) throws AdapterException {
    try (Connection connection = openConnection(config)) {
      validateCdcEnabled(connection);

      List<Option> options = discoverTables(config);
      boolean containsTable = options.stream().anyMatch(option -> option.getName().equals(config.getTable()));
      if (!containsTable) {
        throw new AdapterException("Selected table is not CDC-enabled or does not exist: " + config.getTable());
      }
    } catch (SQLException e) {
      throw new AdapterException("Failed to validate SQL Server configuration: " + e.getMessage(), e);
    } catch (SpConfigurationException e) {
      throw new AdapterException(e.getMessage(), e);
    }
  }

  public Map<String, Object> generateSample(MsSqlCdcAdapterConfig config) throws AdapterException {
    TableRef tableRef = parseTable(config.getTable());

    try (Connection connection = openConnection(config)) {
      validateCdcEnabled(connection);

      Map<String, ColumnDescriptor> columns = describeColumns(connection, tableRef);
      ZoneId zoneId = config.getZoneId();
      Map<String, Object> sample = fetchRealSampleRow(connection, tableRef, columns, zoneId);

      if (sample == null) {
        sample = new LinkedHashMap<>();
        for (Map.Entry<String, ColumnDescriptor> entry : columns.entrySet()) {
          sample.put(entry.getKey(), placeholderForType(entry.getValue(), zoneId));
        }
      }

      if (sample.isEmpty()) {
        throw new AdapterException("No columns found for table " + config.getTable());
      }

      return sample;
    } catch (SQLException | SpConfigurationException e) {
      throw new AdapterException("Failed to generate sample data: " + e.getMessage(), e);
    }
  }

  public Connection openConnection(MsSqlCdcAdapterConfig config) throws SQLException {
    String url = "jdbc:sqlserver://" + config.getHost() + ":" + config.getPort()
        + ";databaseName=" + config.getDatabase()
        + ";encrypt=" + config.getEncrypt()
        + ";trustServerCertificate=" + config.getTrustServerCertificate();

    return DriverManager.getConnection(url, config.getUsername(), config.getPassword());
  }

  public Map<String, TemporalColumnMode> describeTemporalColumns(MsSqlCdcAdapterConfig config) throws AdapterException {
    TableRef tableRef = parseTable(config.getTable());

    try (Connection connection = openConnection(config)) {
      validateCdcEnabled(connection);

      Map<String, TemporalColumnMode> temporalColumns = new LinkedHashMap<>();
      Map<String, ColumnDescriptor> columns = describeColumns(connection, tableRef);
      columns.forEach((columnName, descriptor) -> {
        TemporalColumnMode temporalMode = getTemporalColumnMode(descriptor);
        if (temporalMode != null) {
          temporalColumns.put(columnName, temporalMode);
        }
      });
      return temporalColumns;
    } catch (SQLException | SpConfigurationException e) {
      throw new AdapterException("Failed to inspect SQL Server table metadata: " + e.getMessage(), e);
    }
  }

  private void validateCdcEnabled(Connection connection) throws SQLException, SpConfigurationException {
    try (PreparedStatement statement = connection.prepareStatement(CDC_ENABLED_QUERY);
         ResultSet resultSet = statement.executeQuery()) {
      if (!resultSet.next() || !resultSet.getBoolean(1)) {
        throw new SpConfigurationException("CDC is not enabled on database " + connection.getCatalog());
      }
    }
  }

  private TableRef parseTable(String table) throws AdapterException {
    if (table == null || table.isBlank()) {
      throw new AdapterException("No table selected.");
    }

    String[] tokens = table.split("\\.", 2);
    if (tokens.length != 2) {
      throw new AdapterException("Selected table must be of the form schema.table: " + table);
    }

    return new TableRef(tokens[0], tokens[1]);
  }

  private Map<String, ColumnDescriptor> describeColumns(Connection connection,
                                                        TableRef tableRef) throws SQLException {
    DatabaseMetaData metaData = connection.getMetaData();
    Map<String, ColumnDescriptor> columns = new LinkedHashMap<>();
    try (ResultSet resultSet = metaData.getColumns(null, tableRef.schema(), tableRef.table(), null)) {
      while (resultSet.next()) {
        String columnName = resultSet.getString("COLUMN_NAME");
        int jdbcType = resultSet.getInt("DATA_TYPE");
        String typeName = resultSet.getString("TYPE_NAME");
        int decimalDigits = resultSet.getInt("DECIMAL_DIGITS");
        columns.put(columnName, new ColumnDescriptor(columnName, jdbcType, typeName, decimalDigits));
      }
    }
    return columns;
  }

  private Map<String, Object> fetchRealSampleRow(Connection connection,
                                                 TableRef tableRef,
                                                 Map<String, ColumnDescriptor> columns,
                                                 ZoneId zoneId) throws SQLException {
    String query = SAMPLE_ROW_QUERY_TEMPLATE.formatted(
        escapeIdentifier(tableRef.schema()),
        escapeIdentifier(tableRef.table())
    );

    try (PreparedStatement statement = connection.prepareStatement(query);
         ResultSet resultSet = statement.executeQuery()) {
      if (!resultSet.next()) {
        return null;
      }

      ResultSetMetaData metaData = resultSet.getMetaData();
      Map<String, Object> sample = new LinkedHashMap<>();
      for (int columnIndex = 1; columnIndex <= metaData.getColumnCount(); columnIndex++) {
        String columnName = metaData.getColumnLabel(columnIndex);
        ColumnDescriptor descriptor = columns.getOrDefault(
            columnName,
            new ColumnDescriptor(columnName, metaData.getColumnType(columnIndex), metaData.getColumnTypeName(columnIndex), 0)
        );
        Object rawValue = resultSet.getObject(columnIndex);
        sample.put(columnName, sampleValue(rawValue, descriptor, zoneId));
      }

      return sample;
    }
  }

  private Object placeholderForType(ColumnDescriptor descriptor, ZoneId zoneId) {
    TemporalColumnMode temporalMode = getTemporalColumnMode(descriptor);
    if (temporalMode != null) {
      return (double) sampleTemporalValue(temporalMode, zoneId);
    }

    return switch (descriptor.jdbcType()) {
      case Types.BIGINT, Types.INTEGER, Types.SMALLINT, Types.TINYINT -> 1;
      case Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.NUMERIC, Types.REAL -> 1.0;
      case Types.BIT, Types.BOOLEAN -> true;
      case Types.BINARY, Types.LONGVARBINARY, Types.VARBINARY -> "AQID";
      default -> "example";
    };
  }

  private Object sampleValue(Object rawValue, ColumnDescriptor descriptor, ZoneId zoneId) {
    if (rawValue == null) {
      return placeholderForType(descriptor, zoneId);
    }

    TemporalColumnMode temporalMode = getTemporalColumnMode(descriptor);
    if (temporalMode != null) {
      return (double) temporalValue(rawValue, temporalMode, zoneId);
    }

    if (rawValue instanceof BigDecimal decimalValue) {
      return decimalValue.doubleValue();
    }

    if (rawValue instanceof byte[] bytes) {
      return Base64.getEncoder().encodeToString(bytes);
    }

    return rawValue;
  }

  private TemporalColumnMode getTemporalColumnMode(ColumnDescriptor descriptor) {
    String normalizedTypeName = descriptor.typeName() == null
        ? ""
        : descriptor.typeName().toLowerCase(Locale.ENGLISH);

    return switch (normalizedTypeName) {
      case "date" -> TemporalColumnMode.DATE_EPOCH_MILLIS;
      case "time" -> {
        if (descriptor.decimalDigits() <= 3) {
          yield TemporalColumnMode.TIME_MILLIS;
        } else if (descriptor.decimalDigits() <= 6) {
          yield TemporalColumnMode.TIME_MICROS;
        } else {
          yield TemporalColumnMode.TIME_NANOS;
        }
      }
      case "datetime", "smalldatetime" -> TemporalColumnMode.TIMESTAMP_MILLIS;
      case "datetime2" -> {
        if (descriptor.decimalDigits() <= 3) {
          yield TemporalColumnMode.TIMESTAMP_MILLIS;
        } else if (descriptor.decimalDigits() <= 6) {
          yield TemporalColumnMode.TIMESTAMP_MICROS;
        } else {
          yield TemporalColumnMode.TIMESTAMP_NANOS;
        }
      }
      case "datetimeoffset" -> TemporalColumnMode.DATETIMEOFFSET_MILLIS;
      default -> {
        if (descriptor.jdbcType() == Types.DATE) {
          yield TemporalColumnMode.DATE_EPOCH_MILLIS;
        } else if (descriptor.jdbcType() == Types.TIME || descriptor.jdbcType() == Types.TIME_WITH_TIMEZONE) {
          yield TemporalColumnMode.TIME_MILLIS;
        } else if (descriptor.jdbcType() == Types.TIMESTAMP
            || descriptor.jdbcType() == Types.TIMESTAMP_WITH_TIMEZONE) {
          yield TemporalColumnMode.TIMESTAMP_MILLIS;
        }
        yield null;
      }
    };
  }

  private Object sampleTemporalValue(TemporalColumnMode temporalMode, ZoneId zoneId) {
    return switch (temporalMode) {
      case DATE_EPOCH_MILLIS ->
          LocalDate.of(2026, 5, 7).atStartOfDay(zoneId).toInstant().toEpochMilli();
      case TIME_MILLIS, TIME_MICROS, TIME_NANOS ->
          LocalTime.of(10, 15, 30, 123_000_000).toNanoOfDay() / 1_000_000;
      case TIMESTAMP_MILLIS, TIMESTAMP_MICROS, TIMESTAMP_NANOS, DATETIMEOFFSET_MILLIS ->
          LocalDate.of(2026, 5, 7)
              .atTime(10, 15, 30, 123_000_000)
              .atZone(zoneId)
              .toInstant()
              .toEpochMilli();
    };
  }

  private long temporalValue(Object rawValue, TemporalColumnMode temporalMode, ZoneId zoneId) {
    return switch (temporalMode) {
      case DATE_EPOCH_MILLIS -> toDateEpochMillis(rawValue, zoneId);
      case TIME_MILLIS, TIME_MICROS, TIME_NANOS -> toTimeMillis(rawValue);
      case TIMESTAMP_MILLIS, TIMESTAMP_MICROS, TIMESTAMP_NANOS -> toTimestampEpochMillis(rawValue, zoneId);
      case DATETIMEOFFSET_MILLIS -> toDateTimeOffsetEpochMillis(rawValue, zoneId);
    };
  }

  private long toDateEpochMillis(Object rawValue, ZoneId zoneId) {
    if (rawValue instanceof Date dateValue) {
      return dateValue.toLocalDate().atStartOfDay(zoneId).toInstant().toEpochMilli();
    } else if (rawValue instanceof LocalDate localDate) {
      return localDate.atStartOfDay(zoneId).toInstant().toEpochMilli();
    }

    return LocalDate.parse(rawValue.toString()).atStartOfDay(zoneId).toInstant().toEpochMilli();
  }

  private long toTimeMillis(Object rawValue) {
    LocalTime localTime;
    if (rawValue instanceof Time timeValue) {
      localTime = timeValue.toLocalTime();
    } else if (rawValue instanceof LocalTime parsedLocalTime) {
      localTime = parsedLocalTime;
    } else {
      localTime = LocalTime.parse(rawValue.toString());
    }

    return localTime.toNanoOfDay() / 1_000_000;
  }

  private long toTimestampEpochMillis(Object rawValue, ZoneId zoneId) {
    if (rawValue instanceof Timestamp timestampValue) {
      return timestampValue.toLocalDateTime().atZone(zoneId).toInstant().toEpochMilli();
    } else if (rawValue instanceof LocalDateTime localDateTime) {
      return localDateTime.atZone(zoneId).toInstant().toEpochMilli();
    } else if (rawValue instanceof Instant instant) {
      return instant.toEpochMilli();
    }

    String stringValue = rawValue.toString();
    try {
      return Instant.parse(stringValue).toEpochMilli();
    } catch (DateTimeParseException ignored) {
      return LocalDateTime.parse(stringValue.replace(' ', 'T')).atZone(zoneId).toInstant().toEpochMilli();
    }
  }

  private long toDateTimeOffsetEpochMillis(Object rawValue, ZoneId zoneId) {
    if (rawValue instanceof OffsetDateTime offsetDateTime) {
      return offsetDateTime.toInstant().toEpochMilli();
    } else if (rawValue instanceof Timestamp timestampValue) {
      return timestampValue.toInstant().toEpochMilli();
    }

    String stringValue = rawValue.toString().replace(' ', 'T');
    try {
      return OffsetDateTime.parse(stringValue).toInstant().toEpochMilli();
    } catch (DateTimeParseException ignored) {
      return LocalDateTime.parse(stringValue).atZone(zoneId).toInstant().toEpochMilli();
    }
  }

  private String escapeIdentifier(String identifier) {
    return identifier.replace("]", "]]");
  }

  private record TableRef(String schema, String table) {
  }

  public enum TemporalColumnMode {
    DATE_EPOCH_MILLIS,
    TIME_MILLIS,
    TIME_MICROS,
    TIME_NANOS,
    TIMESTAMP_MILLIS,
    TIMESTAMP_MICROS,
    TIMESTAMP_NANOS,
    DATETIMEOFFSET_MILLIS
  }

  private record ColumnDescriptor(String columnName, int jdbcType, String typeName, int decimalDigits) {
  }
}
