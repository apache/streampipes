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

package org.apache.streampipes.extensions.connectors.influx.sink;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxConnectionSettings;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxRequests;
import org.apache.streampipes.extensions.connectors.influx.shared.SharedInfluxClient;
import org.apache.streampipes.model.runtime.Event;

import org.influxdb.BatchOptions;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxDbClient extends SharedInfluxClient {

  private static final Logger LOG = LoggerFactory.getLogger(InfluxDbClient.class);

  private final String timestampField;
  private final Integer batchSize;
  private final Integer flushDuration;


  InfluxDbClient(InfluxConnectionSettings connectionSettings,
                 String measureName,
                 String timestampField,
                 Integer batchSize,
                 Integer flushDuration) throws SpRuntimeException {
    super(connectionSettings, measureName);
    this.measureName = measureName;
    this.timestampField = timestampField;
    this.batchSize = batchSize;
    this.flushDuration = flushDuration;

    connect();
  }

  /**
   * Connects to the InfluxDB Server, sets the database and initializes the batch-behaviour
   *
   * @throws SpRuntimeException If not connection can be established or if the database could not
   *                            be found
   */
  private void connect() throws SpRuntimeException {
    super.initClient();
    var databaseName = connectionSettings.getDatabaseName();

    // Checking whether the database exists
    if (!InfluxRequests.databaseExists(influxDb, databaseName)) {
      LOG.info("Database '" + databaseName + "' not found. Gets created ...");
      createDatabase(databaseName);
    }

    // setting up the database
    influxDb.setDatabase(databaseName);
    influxDb.enableBatch(BatchOptions.DEFAULTS.actions(batchSize).flushDuration(flushDuration));
  }


  /**
   * Creates a new database with the given name
   *
   * @param dbName The name of the database which should be created
   */
  private void createDatabase(String dbName) throws SpRuntimeException {
    if (!dbName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
      throw new SpRuntimeException(
          "Databasename '" + dbName + "' not allowed. Allowed names: ^[a-zA-Z_][a-zA-Z0-9_]*$");
    }
    influxDb.query(new Query("CREATE DATABASE \"" + dbName + "\"", ""));
  }

  /**
   * Saves an event to the connnected InfluxDB database
   *
   * @param event The event which should be saved
   * @throws SpRuntimeException If the column name (key-value of the event map) is not allowed
   */
  void save(Event event) throws SpRuntimeException {
    if (event == null) {
      throw new SpRuntimeException("event is null");
    }
    Long timestampValue = event.getFieldBySelector(timestampField).getAsPrimitive().getAsLong();
    Point.Builder p = Point.measurement(measureName).time(timestampValue, TimeUnit.MILLISECONDS);
    for (Map.Entry<String, Object> pair : event.getRaw().entrySet()) {
      if (pair.getValue() instanceof Integer) {
        p.addField(InfluxDbSink.prepareString(pair.getKey()), (Integer) pair.getValue());
      } else if (pair.getValue() instanceof Long) {
        p.addField(InfluxDbSink.prepareString(pair.getKey()), (Long) pair.getValue());
      } else if (pair.getValue() instanceof Double) {
        p.addField(InfluxDbSink.prepareString(pair.getKey()), (Double) pair.getValue());
      } else if (pair.getValue() instanceof Boolean) {
        p.addField(InfluxDbSink.prepareString(pair.getKey()), (Boolean) pair.getValue());
      } else {
        p.addField(InfluxDbSink.prepareString(pair.getKey()), pair.getValue().toString());
      }
    }

    influxDb.write(p.build());
  }

  /**
   * Shuts down the connection to the InfluxDB server
   */
  void stop() {
    influxDb.close();
  }
}
