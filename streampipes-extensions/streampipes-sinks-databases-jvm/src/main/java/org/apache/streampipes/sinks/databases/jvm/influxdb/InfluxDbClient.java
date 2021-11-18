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

package org.apache.streampipes.sinks.databases.jvm.influxdb;

import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.logging.api.Logger;
import org.apache.streampipes.model.runtime.Event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxDbClient {
	private Integer influxDbPort;
	private String influxDbHost;
	private String databaseName;
	private String measureName;
	private String user;
	private String password;
	private String timestampField;
  private Integer batchSize;
  private Integer flushDuration;

	private Logger logger;

	private InfluxDB influxDb = null;

	InfluxDbClient(String influxDbHost,
			Integer influxDbPort,
			String databaseName,
			String measureName,
			String user,
			String password,
			String timestampField,
      Integer batchSize,
      Integer flushDuration,
			Logger logger) throws SpRuntimeException {
		this.influxDbHost = influxDbHost;
		this.influxDbPort = influxDbPort;
		this.databaseName = databaseName;
		this.measureName = measureName;
		this.user = user;
		this.password = password;
		this.timestampField = timestampField;
		this.batchSize = batchSize;
		this.flushDuration = flushDuration;
		this.logger = logger;

		validate();
		connect();
	}

  /**
   * Checks whether the {@link InfluxDbClient#influxDbHost} is valid
   *
   * @throws SpRuntimeException If the hostname is not valid
   */
	private void validate() throws SpRuntimeException {
    //TODO: replace regex with validation method (import org.apache.commons.validator.routines.InetAddressValidator;)
    // Validates the database name and the attributes
    // See following link for regular expressions:
    // https://stackoverflow.com/questions/106179/regular-expression-to-match-dns-hostname-or-ip-address
    /*String ipRegex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|"
        + "[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$";
    String hostnameRegex = "^(([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]*[a-zA-Z0-9])\\.)*"
        + "([A-Za-z0-9]|[A-Za-z0-9][A-Za-z0-9\\-]*[A-Za-z0-9])$";*/
    // https://stackoverflow.com/questions/3114595/java-regex-for-accepting-a-valid-hostname-ipv4-or-ipv6-address)
    //if (!influxDbHost.matches(ipRegex) && !influxDbHost.matches(hostnameRegex)) {
    //  throw new SpRuntimeException("Error: Hostname '" + influxDbHost
    //      + "' not allowed");
    //}
  }

  /**
   * Connects to the InfluxDB Server, sets the database and initializes the batch-behaviour
   *
   * @throws SpRuntimeException If not connection can be established or if the database could not
   * be found
   */
	private void connect() throws SpRuntimeException {
	  // Connecting to the server
    // "http://" must be in front
    String urlAndPort = influxDbHost + ":" + influxDbPort;
    influxDb = InfluxDBFactory.connect(urlAndPort, user, password);

    // Checking, if server is available
    Pong response = influxDb.ping();
    if (response.getVersion().equalsIgnoreCase("unknown")) {
      throw new SpRuntimeException("Could not connect to InfluxDb Server: " + urlAndPort);
    }

    // Checking whether the database exists
    System.out.println(databaseExists(databaseName));
    if(!databaseExists(databaseName)) {
      logger.info("Database '" + databaseName + "' not found. Gets created ...");
      createDatabase(databaseName);
    }

    // setting up the database
    influxDb.setDatabase(databaseName);
    influxDb.enableBatch(BatchOptions.DEFAULTS.actions(batchSize).flushDuration(flushDuration));
	}

  /**
   * Checks whether the given database exists. Needs a working connection to an InfluxDB Server
   * ({@link InfluxDbClient#influxDb} needs to be initialized)
   *
   * @param dbName The name of the database, the method should look for
   * @return True if the database exists, false otherwise
   */
	private boolean databaseExists(String dbName) {
    QueryResult queryResult = influxDb.query(new Query("SHOW DATABASES", ""));
    for(List<Object> a : queryResult.getResults().get(0).getSeries().get(0).getValues()) {
      if(a.get(0).equals(dbName)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Creates a new database with the given name
   *
   * @param dbName The name of the database which should be created
   */
  private void createDatabase(String dbName) throws SpRuntimeException {
    if(!dbName.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
      throw new SpRuntimeException("Databasename '" + dbName + "' not allowed. Allowed names: ^[a-zA-Z_][a-zA-Z0-9_]*$");
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
        p.addField(InfluxDb.prepareString(pair.getKey()), (Integer)pair.getValue());
      } else if (pair.getValue() instanceof Long) {
        p.addField(InfluxDb.prepareString(pair.getKey()), (Long)pair.getValue());
      } else if (pair.getValue() instanceof Double) {
        p.addField(InfluxDb.prepareString(pair.getKey()), (Double)pair.getValue());
      } else if (pair.getValue() instanceof Boolean) {
        p.addField(InfluxDb.prepareString(pair.getKey()), (Boolean)pair.getValue());
      } else {
        p.addField(InfluxDb.prepareString(pair.getKey()), pair.getValue().toString());
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
