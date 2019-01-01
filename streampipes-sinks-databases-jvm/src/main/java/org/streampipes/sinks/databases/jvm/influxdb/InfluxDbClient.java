/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.streampipes.sinks.databases.jvm.influxdb;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BoundParameterQuery.QueryBuilder;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.logging.api.Logger;

public class InfluxDbClient {
	private Integer influxDbPort;
	private String influxDbHost;
	private String databaseName;
	private String measureName;
	private String user;
	private String password;
	private String timestampField;

	private boolean tableExists = false;

	private Logger logger = null;


	private InfluxDB influxDb = null;

	InfluxDbClient(String influxDbHost,
			Integer influxDbPort,
			String databaseName,
			String measureName,
			String user,
			String password,
			String timestampField,
			Logger logger) throws SpRuntimeException {
		//TODO: Why are we not just passing an InfluxDbParameters object?
		this.influxDbHost = influxDbHost;
		this.influxDbPort = influxDbPort;
		this.databaseName = databaseName;
		this.measureName = measureName;
		this.user = user;
		this.password = password;
		this.timestampField = timestampField;
		this.logger = logger;

		validate();
		connect();
	}

	private void validate() {
  }

	private void connect() throws SpRuntimeException {
	  // Connecting to the server
    //TODO: localhost not working. choose http://127.0.0.1 instead
    String urlAndPort = influxDbHost + ":" + influxDbPort;
    influxDb = InfluxDBFactory.connect(urlAndPort, user, password);

    // Checking, if server is available
    Pong response = influxDb.ping();
    if (response.getVersion().equalsIgnoreCase("unknown")) {
      //TODO: Throwing exception -> logger needed?
      // throw new SpRuntimeException("Could not connect to InfluxDb Server: " + urlAndPort);
    }

    // Checking whether the database exists
    System.out.println(databaseExists(databaseName));
    if(!databaseExists(databaseName)) {
      throw new SpRuntimeException("Database '" + databaseName + "' not found.");
      //TODO: Or should a missing database get created?
      //createDatabase(databaseName);
    }

    influxDb.setDatabase(databaseName);
    influxDb.enableBatch(BatchOptions.DEFAULTS.actions(2).flushDuration(1000));
	}

	private boolean databaseExists(String dbName) {
	  //TODO: Check errors and/or catch exceptions
    QueryResult queryResult = influxDb.query(new Query("SHOW DATABASES", ""));
    for(List<Object> a : queryResult.getResults().get(0).getSeries().get(0).getValues()) {
      if(a.get(0).equals(dbName)) {
        return true;
      }
    }
    return false;
  }

  private void createDatabase(String dbName) {
    //throws exception: "org.influxdb.InfluxDBException: error parsing query: found $dbName, expected identifier at line 1, char 17"
    Query query = QueryBuilder.newQuery("CREATE DATABASE $dbName")
        .forDatabase("")
        .bind("dbName", 4)
        .create();
    QueryResult results = influxDb.query(query);
  }


	void save(Map<String, Object> event) throws SpRuntimeException {
		if (event == null) {
			throw new SpRuntimeException("event is null");
		}
		Point.Builder p = Point.measurement(measureName).time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    for (Map.Entry<String, Object> pair : event.entrySet()) {
      if(!pair.getKey().matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
        throw new SpRuntimeException("Column name '" + pair.getKey() + "' not allowed "
            + "(allowed: '^[a-zA-Z_][a-zA-Z0-9_]*$')");
      }
      //TODO: Add support for Long, Int etc.
      p.addField(pair.getKey(), pair.getValue().toString());
    }
    influxDb.write(p.build());
	}


	void stop() {
    influxDb.close();
	}
}
