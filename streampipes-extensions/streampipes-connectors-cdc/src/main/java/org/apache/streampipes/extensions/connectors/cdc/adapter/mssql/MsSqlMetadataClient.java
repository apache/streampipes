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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
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
      Map<String, Object> sample = new LinkedHashMap<>();
      ZoneId zoneId = config.getZoneId();
      columns.forEach((columnName, descriptor) -> sample.put(columnName, placeholderForType(descriptor, zoneId)));

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

  private Object placeholderForType(ColumnDescriptor descriptor, ZoneId zoneId) {
    TemporalColumnMode temporalMode = getTemporalColumnMode(descriptor);
    if (temporalMode != null) {
      return sampleTemporalValue(temporalMode, zoneId);
    }

    return switch (descriptor.jdbcType()) {
      case Types.BIGINT, Types.INTEGER, Types.SMALLINT, Types.TINYINT -> 1;
      case Types.DECIMAL, Types.DOUBLE, Types.FLOAT, Types.NUMERIC, Types.REAL -> 1.0;
      case Types.BIT, Types.BOOLEAN -> true;
      case Types.BINARY, Types.LONGVARBINARY, Types.VARBINARY -> "AQID";
      default -> "example";
    };
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
