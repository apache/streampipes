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

package org.apache.streampipes.dataexplorer.commons.influx;

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class InfluxStore {

  private static final Logger LOG = LoggerFactory.getLogger(InfluxStore.class);
  DataLakeMeasure measure;
  Map<String, String> sanitizedRuntimeNames = new HashMap<>();
  private InfluxDB influxDb = null;

  public InfluxStore(DataLakeMeasure measure,
                     InfluxConnectionSettings settings) {
    this.measure = measure;
    // store sanitized target property runtime names in local variable
    measure.getEventSchema()
        .getEventProperties()
        .forEach(ep -> sanitizedRuntimeNames.put(ep.getRuntimeName(),
            InfluxNameSanitizer.renameReservedKeywords(ep.getRuntimeName())));

    connect(settings);
  }

  public InfluxStore(DataLakeMeasure measure,
                     Environment environment) throws SpRuntimeException {
    this(measure, InfluxConnectionSettings.from(environment));
  }

  /**
   * Connects to the InfluxDB Server, sets the database and initializes the batch-behaviour
   *
   * @throws SpRuntimeException If not connection can be established or if the database could not
   *                            be found
   */
  private void connect(InfluxConnectionSettings settings) throws SpRuntimeException {
    influxDb = InfluxClientProvider.getInfluxDBClient(settings);

    // Checking, if server is available
    Pong response = influxDb.ping();
    if (response.getVersion().equalsIgnoreCase("unknown")) {
      throw new SpRuntimeException("Could not connect to InfluxDb Server: " + settings.getConnectionUrl());
    }

    String databaseName = settings.getDatabaseName();
    // Checking whether the database exists
    if (!InfluxRequests.databaseExists(influxDb, databaseName)) {
      LOG.info("Database '" + databaseName + "' not found. Gets created ...");
      createDatabase(databaseName);
    }

    // setting up the database
    influxDb.setDatabase(databaseName);
    int batchSize = 2000;
    int flushDuration = 500;
    influxDb.enableBatch(batchSize, flushDuration, TimeUnit.MILLISECONDS);
  }

  /**
   * Creates a new database with the given name
   *
   * @param dbName The name of the database which should be created
   */
  private void createDatabase(String dbName) throws SpRuntimeException {
    if (!dbName.matches("^[a-zA-Z_]\\w*$")) {
      throw new SpRuntimeException(
          "Database name '" + dbName + "' not allowed. Allowed names: ^[a-zA-Z_][a-zA-Z0-9_]*$");
    }
    influxDb.query(new Query("CREATE DATABASE \"" + dbName + "\"", ""));
  }

  /**
   * Saves an event to the connected InfluxDB database
   *
   * @param event The event which should be saved
   * @throws SpRuntimeException If the column name (key-value of the event map) is not allowed
   */
  public void onEvent(Event event) throws SpRuntimeException {
    var missingFields = new ArrayList<String>();
    var nullFields = new ArrayList<String>();
    if (event == null) {
      throw new SpRuntimeException("event is null");
    }

    Long timestampValue = event.getFieldBySelector(measure.getTimestampField()).getAsPrimitive().getAsLong();
    Point.Builder point =
        Point.measurement(measure.getMeasureName()).time((long) timestampValue, TimeUnit.MILLISECONDS);

    for (EventProperty ep : measure.getEventSchema().getEventProperties()) {
      if (ep instanceof EventPropertyPrimitive) {
        String runtimeName = ep.getRuntimeName();

        // timestamp should not be added as a field
        if (!measure.getTimestampField().endsWith(runtimeName)) {
          String sanitizedRuntimeName = sanitizedRuntimeNames.get(runtimeName);

          try {
            var field = event.getOptionalFieldByRuntimeName(runtimeName);
            if (field.isPresent()) {
              PrimitiveField eventPropertyPrimitiveField = field.get().getAsPrimitive();
              if (eventPropertyPrimitiveField.getRawValue() == null) {
                nullFields.add(sanitizedRuntimeName);
              } else {

                // store property as tag when the field is a dimension property
                if (PropertyScope.DIMENSION_PROPERTY.name().equals(ep.getPropertyScope())) {
                  point.tag(sanitizedRuntimeName, eventPropertyPrimitiveField.getAsString());
                } else {
                  handleMeasurementProperty(
                      point,
                      (EventPropertyPrimitive) ep,
                      sanitizedRuntimeName,
                      eventPropertyPrimitiveField);
                }
              }
            } else {
              missingFields.add(runtimeName);
            }
          } catch (SpRuntimeException iae) {
            LOG.warn("Runtime exception while extracting field value of field {} - this field will be ignored",
                runtimeName, iae);
          }
        }
      }
    }

    if (missingFields.size() > 0) {
      LOG.debug("Ignored {} fields which were present in the schema, but not in the provided event: {}",
          missingFields.size(),
          String.join(", ", missingFields));
    }

    if (nullFields.size() > 0) {
      LOG.warn("Ignored {} fields which had a value 'null': {}", nullFields.size(), String.join(", ", nullFields));
    }

    influxDb.write(point.build());
  }

  private void handleMeasurementProperty(Point.Builder p,
                                         @NotNull EventPropertyPrimitive ep,
                                         String preparedRuntimeName,
                                         PrimitiveField eventPropertyPrimitiveField) {
    try {
      // Store property according to property type
      String runtimeType = ep.getRuntimeType();
      if (XSD.INTEGER.toString().equals(runtimeType)) {
        try {
          p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsInt());
        } catch (NumberFormatException ef) {
          p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsFloat());
        }
      } else if (XSD.LONG.toString().equals(runtimeType)) {
        try {
          p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsLong());
        } catch (NumberFormatException ef) {
          p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsFloat());
        }
      } else if (XSD.FLOAT.toString().equals(runtimeType)) {
        p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsFloat());
      } else if (XSD.DOUBLE.toString().equals(runtimeType)) {
        p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsDouble());
      } else if (XSD.BOOLEAN.toString().equals(runtimeType)) {
        p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsBoolean());
      } else if (SO.NUMBER.equals(runtimeType)) {
        p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsDouble());
      } else {
        p.addField(preparedRuntimeName, eventPropertyPrimitiveField.getAsString());
      }
    } catch (NumberFormatException e) {
      LOG.warn("Wrong number format for field {}, ignoring.", preparedRuntimeName);
    }
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
}
