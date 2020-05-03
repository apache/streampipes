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

package org.apache.streampipes.connect.adapters.mysql;

import org.apache.streampipes.connect.adapter.exception.AdapterException;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySqlClient {

  public static final String ID = "http://streampipes.org/adapter/specific/mysql";

  static final String HOST = "mysqlHost";
  static final String PORT = "mysqlPort";
  static final String DATABASE = "mysqlDatabase";
  static final String TABLE = "mysqlTable";
  static final String USER = "mysqlUser";
  static final String PASSWORD = "mysqlPassword";

  static final String REPLACE_NULL_VALUES = "replaceNullValues";
  static final String DO_REPLACE_NULL_VALUES = "doReplaceNullValues";
  static final String DO_NOT_REPLACE_NULL_VALUES = "doNotReplaceNullValues";

  private String host;
  private Integer port;
  private String database;
  private String table;

  private String username;
  private String password;


  private List<Column> columns;

  Connection connection;

  MySqlClient(String host,
              int port,
              String database,
              String table,
              String username,
              String password) {
    this.host = host;
    this.port = port;
    this.database = database;
    this.table = table;
    this.username = username;
    this.password = password;

    connection = null;
  }

  public void connect() throws AdapterException {
    checkJdbcDriver();
    String server = "jdbc:mysql://" + host + ":" + port + "/" + "?sslMode=DISABLED&allowPublicKeyRetrieval=true";
    try {
      connection = DriverManager.getConnection(server, username, password);
    } catch (SQLException e) {
      throw new AdapterException("Could not connect to server: " + e.getMessage());
    }
  }

  public void disconnect() throws AdapterException {
    if (connection != null) {
      try {
        connection.close();
      } catch (SQLException e) {
        throw new AdapterException("Error while disconnecting: " + e.getMessage());
      }
      connection = null;
    }
  }

  public GuessSchema getSchema() throws AdapterException {
    connect();
    loadColumns();

    EventSchema eventSchema = new EventSchema();
    GuessSchema guessSchema = new GuessSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    for (Column column : columns) {
      allProperties.add(PrimitivePropertyBuilder
              .create(column.getType(), column.getName())
              .label(column.getName())
              .build());
    }

    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);

    disconnect();
    return guessSchema;
  }

  /**
   * Checks that the MySql-JDBC-Driver is "installed". Throws an AdapterException otherwise
   */
  private void checkJdbcDriver() throws AdapterException {
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch (ClassNotFoundException e) {
      throw new AdapterException("MySql Driver not found.");
    }
  }

  /**
   * Fills the columns with the columns from the SQL Table
   */
  public void loadColumns() throws AdapterException {
    if (connection == null) {
      throw new AdapterException("Client must be connected in order to load the columns");
    }
    ResultSet resultSet = null;
    columns = new ArrayList<>();

    String query = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_TYPE FROM "
            + "INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND TABLE_SCHEMA = ? ORDER BY "
            + "ORDINAL_POSITION ASC;";

    try (PreparedStatement statement = connection.prepareStatement(query)) {

      statement.setString(1, table);
      statement.setString(2, database);
      resultSet = statement.executeQuery();

      if (resultSet.next()) {
        do {
          String name = resultSet.getString("COLUMN_NAME");
          String dataType = resultSet.getString("DATA_TYPE");
          String columnType = resultSet.getString("COLUMN_TYPE");
          columns.add(new Column(name, dataType, columnType));
        } while(resultSet.next());
      } else {
        // No columns found -> Table/Database does not exist
        throw new IllegalArgumentException("Database/table not found");
      }
    } catch (SQLException e) {
      throw new AdapterException("SqlException while loading columns: " + e.getMessage()
              + ", Error code: " + e.getErrorCode()
              + ", SqlState: " + e.getSQLState());
    } finally {
      try {
        resultSet.close();
      } catch (Exception e) {}
    }
  }

  public String getHost() {
    return host;
  }

  public Integer getPort() {
    return port;
  }

  public String getDatabase() {
    return database;
  }

  public String getTable() {
    return table;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public List<Column> getColumns() {
    return columns;
  }

  public boolean isConnected() {
    return connection != null;
  }

  Connection getConnection() {
    return connection;
  }
}
