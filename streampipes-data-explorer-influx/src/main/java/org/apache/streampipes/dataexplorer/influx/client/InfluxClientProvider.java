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

package org.apache.streampipes.dataexplorer.influx.client;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.influx.auth.InfluxAuthMode;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class InfluxClientProvider {

  private static final int DEFAULT_BATCH_SIZE = 2000;
  private static final int DEFAULT_FLUSH_DURATION = 500;

  private static final Logger LOG = LoggerFactory.getLogger(InfluxClientProvider.class);

  /**
   * Create a new InfluxDB client from Environment and ensures database is available
   * @param environment Environment
   * @return InfluxDB
   */
  public InfluxDB getSetUpInfluxDBClient(Environment environment){
    return getSetUpInfluxDBClient(InfluxConnectionSettings.from(environment));
  }

  /**
   * Create a new InfluxDB client from Connection Settings and ensures database is available
   * @param settings Connection Settings
   * @return InfluxDB
   */
  public InfluxDB getSetUpInfluxDBClient(InfluxConnectionSettings settings){
    var influxDb = getInitializedInfluxDBClient(settings);
    this.setupDatabaseAndBatching(influxDb, settings.getDatabaseName());

    return influxDb;
  }

  /**
   * Create a new InfluxDB client from provided settings and verify it's available
   * @param settings Connection settings
   * @return InfluxDB
   */
  public InfluxDB getInitializedInfluxDBClient(InfluxConnectionSettings settings){
    var influxDb = InfluxClientProvider.getInfluxDBClient(settings);

    // Checking, if server is available
    var response = influxDb.ping();
    if (response.getVersion()
        .equalsIgnoreCase("unknown")) {
      throw new SpRuntimeException("Could not connect to InfluxDb Server: " + settings.getConnectionUrl());
    }

    return influxDb;
  }

  /**
   * Create a new InfluxDB client from environment variables
   *
   * @return InfluxDB
   */
  public static InfluxDB getInfluxDBClient() {
    var env = getEnvironment();
    return getInfluxDBClient(InfluxConnectionSettings.from(env));
  }

  /**
   * Create a new InfluxDB client from provided settings
   *
   * @param settings Connection settings
   * @return InfluxDB
   */
  public static InfluxDB getInfluxDBClient(InfluxConnectionSettings settings) {
    if (settings.getAuthMode() == InfluxAuthMode.TOKEN) {
      var okHttpClientBuilder = InfluxClientUtils.getHttpClientBuilder(settings.getToken());

      return InfluxDBFactory.connect(settings.getConnectionUrl(), okHttpClientBuilder);
    } else {
      var okHttpClientBuilder = InfluxClientUtils.getHttpClientBuilder();
      return InfluxDBFactory.connect(
          settings.getConnectionUrl(),
          settings.getUsername(),
          settings.getPassword(),
          okHttpClientBuilder
      );
    }
  }

  /**
   * Creates the specified database in the influxDb instance if it does not exist. Enables batching with default values
   * @param influxDb The InfluxDB client instance
   * @param databaseName The name of the database
   */
  public void setupDatabaseAndBatching(InfluxDB influxDb, String databaseName) {
    this.setupDatabaseAndBatching(influxDb, databaseName, DEFAULT_BATCH_SIZE, DEFAULT_FLUSH_DURATION);
  }

  /**
   * Creates the specified database in the influxDb instance if it does not exist. Enables batching
   * @param influxDb The InfluxDB client instance
   * @param databaseName The name of the database
   * @param batchSize Batch Size
   * @param flushDuration Flush Duration
   */
  public void setupDatabaseAndBatching(InfluxDB influxDb, String databaseName, int batchSize, int flushDuration) {
    // Checking whether the database exists
    if (!databaseExists(influxDb, databaseName)) {
      LOG.info("Database '" + databaseName + "' not found. Gets created ...");
      createDatabase(influxDb, databaseName);
    }

    // setting up the database
    influxDb.setDatabase(databaseName);
    influxDb.enableBatch(batchSize, flushDuration, TimeUnit.MILLISECONDS);
  }

  /**
   * Creates a new database with the given name
   *
   * @param influxDb The InfluxDB client
   * @param dbName   The name of the database which should be created
   */
  public void createDatabase(
      InfluxDB influxDb,
      String dbName
  ) throws SpRuntimeException {
    if (!dbName.matches("^[a-zA-Z_]\\w*$")) {
      throw new SpRuntimeException(
          "Database name '" + dbName + "' not allowed. Allowed names: ^[a-zA-Z_][a-zA-Z0-9_]*$");
    }
    influxDb.query(new Query("CREATE DATABASE \"" + dbName + "\"", ""));
  }

  /**
   * Checks whether the given database exists.
   *
   * @param influxDb The InfluxDB client instance
   * @param dbName The name of the database, the method should look for
   * @return True if the database exists, false otherwise
   */
  public boolean databaseExists(
      InfluxDB influxDb,
      String dbName
  ) {
    var queryResult = influxDb.query(new Query("SHOW DATABASES", ""));
    for (List<Object> a : queryResult.getResults()
                                     .get(0)
                                     .getSeries()
                                     .get(0)
                                     .getValues()) {
      if (!a.isEmpty() && dbName.equals(a.get(0))) {
        return true;
      }
    }
    return false;
  }


  private static Environment getEnvironment() {
    return Environments.getEnvironment();
  }


}
