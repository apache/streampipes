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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.TimeSeriesStorage;
import org.apache.streampipes.dataexplorer.influx.client.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.influx.sanitize.InfluxNameSanitizer;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.InfluxDBMapper;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeSeriesStorageInflux extends TimeSeriesStorage {

  private final InfluxDB influxDb;

  private final InfluxDBMapper influxDBMapper;

  private final PropertyHandler propertyHandler;

  public TimeSeriesStorageInflux(
      DataLakeMeasure measure,
      Environment environment,
      InfluxClientProvider influxClientProvider
  ) throws SpRuntimeException {
    this(measure, false, environment, influxClientProvider);
  }

  public TimeSeriesStorageInflux(
      DataLakeMeasure measure,
      boolean ignoreDuplicates,
      Environment environment,
      InfluxClientProvider influxClientProvider
  ) throws SpRuntimeException {
    super(measure);
    this.influxDb = influxClientProvider.getSetUpInfluxDBClient(environment);
    propertyHandler = new PropertyHandler(new PropertyDuplicateFilter(ignoreDuplicates));
    this.influxDBMapper = new InfluxDBMapper(influxDb);
  }

  protected void writeToTimeSeriesStorage(Event event) throws SpRuntimeException {
    var point = initializePointWithTimestamp(event);
    iterateOverallEventProperties(event, point);
    influxDb.write(point.build());
  
  }

protected void upsertTimeSeriesStorage(Event event) throws SpRuntimeException {

    // Prepare the Point object
    var point = initializePointWithTimestamp(event);
    iterateOverallEventProperties(event, point);

    // Extract the timestamp from the event
      var timestamp = event.getFieldByRuntimeName(this.measure.getTimestampFieldName()).getAsPrimitive().getAsLong();
    

    try {
        // Query InfluxDB to check if the point with the same timestamp exists
        boolean exists = checkIfPointExists(timestamp);

        // If the point exists, we update it (this is effectively an upsert)
        if (exists) {
            influxDBMapper.save(point.build()); // Use InfluxDBMapper to update
        } else {
            // Otherwise, perform an insert
            influxDb.write(point.build());
        }
    } catch (Exception e) {
        throw new SpRuntimeException("Failed to upsert data to InfluxDB", e);
    }
}

/**
 * Queries InfluxDB to check if a point with the given timestamp already exists
 */
private boolean checkIfPointExists(long timestamp) {
    // Construct the query string
    String queryString = String.format("SELECT * FROM \"%s\" WHERE time = %d LIMIT 1", measure.getMeasureName(), timestamp);
    // Create a Query object
    Query query = new Query(queryString, this.measure.getMeasureName());  // Replace with your actual database name

    try {
        // Execute the query
        QueryResult queryResult = influxDb.query(query);

        // Check if the query returned any results
        if (queryResult.getResults().isEmpty()) {
            return false;
        }

        // Iterate through the results to check if any series contains data
        List<QueryResult.Series> seriesList = queryResult.getResults().get(0).getSeries();
        return seriesList != null && !seriesList.isEmpty();
    } catch (Exception e) {
        // Handle the exception (logging, rethrowing, etc.)
        System.err.println("Error querying InfluxDB: " + e.getMessage());
        return false;  // Return false if there's an error (could be handled differently based on your needs)
    }
}
  private void iterateOverallEventProperties(
      Event event,
      Point.Builder point
  ) {

    allEventProperties.forEach(ep -> {
      var runtimeName = ep.getRuntimeName();
      var sanitizedRuntimeName = sanitizedRuntimeNames.get(runtimeName);
      var fieldOptional = event.getOptionalFieldByRuntimeName(runtimeName);

      fieldOptional.ifPresent(field -> {
        if (ep instanceof EventPropertyPrimitive) {
          propertyHandler.handlePrimitiveProperty(
              point,
              (EventPropertyPrimitive) ep,
              field.getAsPrimitive(),
              sanitizedRuntimeName
          );
        } else {
          propertyHandler.handleNonPrimitiveProperty(
              point,
              event,
              sanitizedRuntimeName
          );
        }
      });
    });
  }

  /**
   * Shuts down the connection to the InfluxDB server
   */
  public void close() throws SpRuntimeException {
    influxDb.flush();
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new SpRuntimeException(e);
    }
    influxDb.close();
  }

  /**
   * Creates a point object which is later written to the influxDB and adds the value of the timestamp field
   */
  private Point.Builder initializePointWithTimestamp(Event event) {
    var timestampValue = event.getFieldBySelector(measure.getTimestampField())
        .getAsPrimitive()
        .getAsLong();
    return Point.measurement(measure.getMeasureName())
        .time((long) timestampValue, TimeUnit.MILLISECONDS);
  }

  /**
   * store sanitized target property runtime names in local variable
   */
  protected void storeSanitizedRuntimeNames() {
    measure.getEventSchema()
           .getEventProperties()
           .forEach(ep -> sanitizedRuntimeNames.put(
             ep.getRuntimeName(),
             InfluxNameSanitizer.renameReservedKeywords(ep.getRuntimeName())
           ));
  }

  /**
   * Iterates over all properties of the event and renames the key if it is a reserved keywords in InfluxDB
   */
  protected void sanitizeRuntimeNamesInEvent(Event event) {
    // sanitize event
    event.getRaw()
         .keySet()
         .forEach(key -> event.renameFieldByRuntimeName(key, InfluxNameSanitizer.renameReservedKeywords(key)));
  }
}
