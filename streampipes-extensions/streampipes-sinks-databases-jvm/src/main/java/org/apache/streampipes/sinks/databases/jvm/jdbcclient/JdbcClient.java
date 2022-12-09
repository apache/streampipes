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
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.DbDescription;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.JdbcConnectionParameters;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.StatementHandler;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.SupportedDbEngines;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.model.TableDescription;
import org.apache.streampipes.sinks.databases.jvm.jdbcclient.utils.SQLStatementUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


public class JdbcClient {

  protected DbDescription dbDescription;

  protected TableDescription tableDescription;

  protected Connection connection = null;

  protected StatementHandler statementHandler;

  protected Logger logger;

  /**
   * A wrapper class for all supported SQL data types (INT, BIGINT, FLOAT, DOUBLE, VARCHAR(255)).
   * If no matching type is found, it is interpreted as a String (VARCHAR(255))
   */
  public JdbcClient() {
  }

  protected void initializeJdbc(EventSchema eventSchema,
                                JdbcConnectionParameters connectionParameters,
                                SupportedDbEngines dbEngine,
                                Logger logger) throws SpRuntimeException {
    this.dbDescription = new DbDescription(connectionParameters, dbEngine);
    this.tableDescription = new TableDescription(connectionParameters.getDbTable(), eventSchema);
    this.statementHandler = new StatementHandler(null, null);
    this.logger = logger;
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
      connection = DriverManager.getConnection(
          url, this.dbDescription.getUsername(),
          this.dbDescription.getPassword());
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
    String url =
        "jdbc:" + this.dbDescription.getEngine().getUrlName() + "://" + host + ":" + port + "/" + databaseName
            + "?user="
            + this.dbDescription.getUsername() + "&password=" + this.dbDescription.getPassword()
            + "&ssl=true&sslfactory=" + this.dbDescription.getSslFactory() + "&sslmode=require";
    try {
      connection = DriverManager.getConnection(url);
      ensureDatabaseExists(databaseName);
      ensureTableExists(url, "");
    } catch (SQLException e) {
      throw new SpRuntimeException("Could not establish a connection with the server: " + e.getMessage());
    }
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
      this.statementHandler.statement.executeUpdate(createStatement + databaseName + ";");
      logger.info("Created new database '" + databaseName + "'");
    } catch (SQLException e1) {
      if (!e1.getSQLState().substring(0, 2).equals("42")) {
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
      connection = DriverManager.getConnection(url + databaseName, this.dbDescription.getUsername(),
          this.dbDescription.getPassword());
      this.statementHandler.setStatement(connection.createStatement());
      ResultSet rs = connection.getMetaData().getTables(null, null, this.tableDescription.getName(), null);
      if (rs.next()) {
        validateTable();
      } else {
        createTable();
      }
      this.tableDescription.setTableExists();
      rs.close();
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
    //TODO: Add batch support (https://stackoverflow.com/questions/3784197/efficient-way-to-do-batch-inserts-with-jdbc)
    checkConnected();
    Map<String, Object> eventMap = event.getRaw();
    if (event == null) {
      throw new SpRuntimeException("event is null");
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
      if (e.getSQLState().substring(0, 2).equals("42")) {
        // If the table does not exists (because it got deleted or something, will cause the error
        // code "42") we will try to create a new one. Otherwise we do not handle the exception.
        logger.warn("Table '" + this.tableDescription.getName() + "' was unexpectedly not found and gets recreated.");
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
        this.statementHandler.preparedStatement, connection,
        "", new String[]{});
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
      if (this.statementHandler.statement != null) {
        this.statementHandler.statement.close();
        this.statementHandler.statement = null;
      }
    } catch (SQLException e) {
      error = true;
      logger.warn("Exception when closing the statement: " + e.getMessage());
    }
    try {
      if (connection != null) {
        connection.close();
        connection = null;
      }
    } catch (SQLException e) {
      error = true;
      logger.warn("Exception when closing the connection: " + e.getMessage());
    }
    try {
      if (this.statementHandler.preparedStatement != null) {
        this.statementHandler.preparedStatement.close();
        this.statementHandler.preparedStatement = null;
      }
    } catch (SQLException e) {
      error = true;
      logger.warn("Exception when closing the prepared statement: " + e.getMessage());
    }
    if (!error) {
      logger.info("Shutdown all connections successfully.");
    }
  }

  public void checkConnected() throws SpRuntimeException {
    if (connection == null) {
      throw new SpRuntimeException("Connection is not established.");
    }
  }
}
