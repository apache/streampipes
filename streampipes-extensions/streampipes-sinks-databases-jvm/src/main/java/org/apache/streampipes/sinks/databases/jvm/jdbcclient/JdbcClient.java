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

package org.apache.streampipes.sinks.databases.jvm.jdbcclient;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDescription;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.JdbcConnectionParameters;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.StatementHandler;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.TableDescription;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.SQLStatementUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;


public class JdbcClient {

  private static final Logger LOG = LoggerFactory.getLogger(JdbcClient.class);

  protected DbDescription dbDescription;

  protected TableDescription tableDescription;

  protected Connection connection = null;

  protected StatementHandler statementHandler;

  /**
   * A wrapper class for all supported SQL data types (INT, BIGINT, FLOAT, DOUBLE, VARCHAR(255)).
   * If no matching type is found, it is interpreted as a String (VARCHAR(255))
   */
  public JdbcClient() {
  }

  protected void initializeJdbc(EventSchema eventSchema,
                                JdbcConnectionParameters connectionParameters,
                                SupportedDbEngines dbEngine) throws SpRuntimeException {
    this.dbDescription = new DbDescription(connectionParameters, dbEngine);
    this.tableDescription = new TableDescription(connectionParameters.getDbTable(), eventSchema);
    this.statementHandler = new StatementHandler(null, null);
    try {
      Class.forName(this.dbDescription.getDriverName());
    } catch (ClassNotFoundException e) {
      throw new SpRuntimeException("Driver '" + this.dbDescription.getDriverName() + "' not found.");
    }

    if (this.dbDescription.isSslEnabled()) {
      connectWithSSL(
          this.dbDescription.getHost(),
          this.dbDescription.getPort(),
          this.dbDescription.getName()
      );
    } else {
      connect(
          this.dbDescription.getHost(),
          this.dbDescription.getPort(),
          this.dbDescription.getName()
      );
    }
  }


  /**
   * Connects to the SQL database and initializes {@link JdbcClient#connection}
   *
   * @throws SpRuntimeException When the connection could not be established (because of a
   *                            wrong identification, missing database etc.)
   */
  private void connect(String host, int port, String databaseName) throws SpRuntimeException {
    String url = "jdbc:" + this.dbDescription.getEngine().getUrlName() + "://" + host + ":" + port + "/";
    try {
      connection = openConnection(url);
      ensureDatabaseExists(databaseName);
      ensureTableExists(url, databaseName);
    } catch (SQLException e) {
      throw new SpRuntimeException("Could not establish a connection with the server: " + e.getMessage());
    }
  }

  /**
   * WIP
   *
   * @param host
   * @param port
   * @param databaseName
   * @throws SpRuntimeException
   */
  private void connectWithSSL(String host, int port, String databaseName) throws SpRuntimeException {
    String url = "jdbc:" + this.dbDescription.getEngine().getUrlName() + "://" + host + ":" + port + "/";
    try {
      connection = openConnection(url);
      ensureDatabaseExists(databaseName);
      ensureTableExists(url, databaseName);
    } catch (SQLException e) {
      throw new SpRuntimeException("Could not establish a connection with the server: " + e.getMessage());
    }
  }

  private Connection openConnection(String url) throws SQLException {
    return DriverManager.getConnection(url, buildConnectionProperties());
  }

  private Properties buildConnectionProperties() {
    var properties = new Properties();
    properties.setProperty("user", this.dbDescription.getUsername());
    properties.setProperty("password", this.dbDescription.getPassword());
    if (this.dbDescription.isSslEnabled()) {
      properties.setProperty("ssl", "true");
      properties.setProperty("sslfactory", this.dbDescription.getSslFactory());
      properties.setProperty("sslmode", "require");
    }
    return properties;
  }


  /**
   * If this method returns successfully a database with the given name exists on the server, specified by the url.
   *
   * @param databaseName The name of the database that should exist
   * @throws SpRuntimeException If the database does not exists and could not be created
   */
  protected void ensureDatabaseExists(String databaseName) throws SpRuntimeException {

    String createStatement = "CREATE DATABASE ";

    ensureDatabaseExists(createStatement, databaseName);
  }

  protected void ensureDatabaseExists(String createStatement, String databaseName) throws SpRuntimeException {

    SQLStatementUtils.checkRegEx(databaseName, "databasename", dbDescription);

    try {
      // Checks whether the database already exists (using catalogs has not worked with postgres)
      this.statementHandler.setStatement(connection.createStatement());
      this.statementHandler.getStatement().executeUpdate(createStatement + databaseName + ";");
      LOG.info("Created new database '" + databaseName + "'");
    } catch (SQLException e1) {
      if (!isSqlStateClass(e1, "42")) {
        throw new SpRuntimeException("Error while creating database: " + e1.getMessage());
      }
    }
    closeAll();
  }

  /**
   * If this method returns successfully a table with the name in
   * {@link JdbcConnectionParameters#getDbTable()} exists in the database
   * with the given database name exists on the server, specified by the url.
   *
   * @param url          The JDBC url containing the needed information (e.g. "jdbc:iotdb://127.0.0.1:6667/")
   * @param databaseName The database in which the table should exist
   * @throws SpRuntimeException If the table does not exist and could not be created
   */
  protected void ensureTableExists(String url, String databaseName) throws SpRuntimeException {
    try {
      // Database should exist by now so we can establish a connection
      connection = openConnection(url + databaseName);
      this.statementHandler.setStatement(connection.createStatement());
      try (ResultSet rs = connection.getMetaData().getTables(null, null, this.tableDescription.getName(), null)) {
        if (rs.next()) {
          validateTable();
        } else {
          createTable();
        }
      }
      this.tableDescription.setTableExists();
    } catch (SQLException e) {
      closeAll();
      throw new SpRuntimeException(e.getMessage());
    }
  }

  /**
   * Prepares a statement for the insertion of values or the
   *
   * @param event The event which should be saved to the Postgres table
   * @throws SpRuntimeException When there was an error in the saving process
   */
  protected void save(final Event event) throws SpRuntimeException {
    if (event == null) {
      throw new SpRuntimeException("event is null");
    }
    checkConnected();
    Map<String, Object> eventMap = event.getRaw();
    if (eventMap == null) {
      throw new SpRuntimeException("event data is null");
    }
    if (!this.tableDescription.tableExists()) {
      // Creates the table
      createTable();
      this.tableDescription.setTableExists();
    }
    try {
      checkConnected();
      this.statementHandler.executePreparedStatement(
          this.dbDescription, this.tableDescription,
          connection, eventMap);
    } catch (SQLException e) {
      if (isSqlStateClass(e, "42")) {
        // If the table does not exists (because it got deleted or something, will cause the error
        // code "42") we will try to create a new one. Otherwise we do not handle the exception.
        LOG.warn("Table '" + this.tableDescription.getName() + "' was unexpectedly not found and gets recreated.");
        this.tableDescription.setTableMissing();
        createTable();
        this.tableDescription.setTableExists();

        try {
          checkConnected();
          this.statementHandler.executePreparedStatement(
              this.dbDescription, this.tableDescription,
              connection, eventMap);
        } catch (SQLException e1) {
          throw new SpRuntimeException(e1.getMessage());
        }
      } else {
        throw new SpRuntimeException(e.getMessage());
      }
    }
  }

  protected void createTable() throws SpRuntimeException {
    String createStatement = "CREATE TABLE ";

    checkConnected();
    this.tableDescription.createTable(createStatement, this.statementHandler, this.dbDescription,
        this.tableDescription);
  }

  protected void extractTableInformation() {
    this.tableDescription.extractTableInformation(
        connection, "", new String[]{});
  }

  protected void validateTable() throws SpRuntimeException {
    checkConnected();
    extractTableInformation();

    this.tableDescription.validateTable();
  }

  /**
   * Closes all open connections and statements of JDBC
   */
  protected void closeAll() {
    boolean error = false;
    try {
      if (this.statementHandler.getStatement() != null) {
        this.statementHandler.closeStatement();
      }
    } catch (SQLException e) {
      error = true;
      LOG.warn("Exception when closing the statement: " + e.getMessage());
    }
    try {
      if (connection != null) {
        connection.close();
        connection = null;
      }
    } catch (SQLException e) {
      error = true;
      LOG.warn("Exception when closing the connection: " + e.getMessage());
    }
    try {
      if (this.statementHandler.getPreparedStatement() != null) {
        this.statementHandler.closePreparedStatement();
      }
    } catch (SQLException e) {
      error = true;
      LOG.warn("Exception when closing the prepared statement: " + e.getMessage());
    }
    if (!error) {
      LOG.info("Shutdown all connections successfully.");
    }
  }

  public void checkConnected() throws SpRuntimeException {
    if (connection == null) {
      throw new SpRuntimeException("Connection is not established.");
    }
  }

  protected static boolean isSqlStateClass(SQLException exception, String sqlStateClass) {
    String sqlState = exception.getSQLState();
    return sqlState != null
        && sqlState.length() >= sqlStateClass.length()
        && sqlState.startsWith(sqlStateClass);
  }
}
