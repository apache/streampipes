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

package org.apache.streampipes.extensions.connectors.influx.adapter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxConnectionSettings;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxRequests;
import org.apache.streampipes.extensions.connectors.influx.shared.SharedInfluxClient;
import org.apache.streampipes.model.connect.guess.GuessSchema;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;

import org.influxdb.InfluxDBIOException;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.streampipes.vocabulary.SO.DATE_TIME;

public class InfluxDbClient extends SharedInfluxClient {

  static final String REPLACE_NULL_VALUES = "replaceNullValues";
  static final String DO_REPLACE = "doReplace";
  static final String DO_NOT_REPLACE = "doNotReplace";

  private final boolean replaceNullValues;

  private List<Column> columns;
  private String columnsString;

  private boolean connected;

  public static class Column {
    private final String name;
    private final Datatypes datatypes;

    Column(String name, Datatypes datatypes) {
      this.name = name;
      this.datatypes = datatypes;
    }

    String getName() {
      return name;
    }

    Datatypes getDatatypes() {
      return datatypes;
    }
  }

  InfluxDbClient(InfluxConnectionSettings connectionSettings,
                 String measurement,
                 boolean replaceNullValues) {
    super(connectionSettings, measurement);
    this.replaceNullValues = replaceNullValues;

    this.connected = false;
  }

  public void connect() throws AdapterException {
    try {
      super.initClient();
      var database = connectionSettings.getDatabaseName();

      // Checking whether the database exists
      if (!InfluxRequests.databaseExists(influxDb, database)) {
        throw new AdapterException("Database " + database + " could not be found.");
      }

      // Checking, whether the measurement exists
      if (!measurementExists(measureName)) {
        throw new AdapterException("Measurement " + measureName + " could not be found.");
      }

      connected = true;
    } catch (InfluxDBIOException e) {
      throw new AdapterException("Problem connecting with the server: " + e.getMessage());
    }
  }

  public void disconnect() {
    if (connected) {
      influxDb.close();
      connected = false;
    }
  }

  private boolean measurementExists(String measurement) {
    // Database must exist
    QueryResult queryResult = influxDb
        .query(new Query("SHOW MEASUREMENTS", connectionSettings.getDatabaseName()));
    for (List<Object> a : queryResult.getResults().get(0).getSeries().get(0).getValues()) {
      if (a.get(0).equals(measurement)) {
        return true;
      }
    }
    return false;
  }

  public GuessSchema getSchema() throws AdapterException {
    connect();
    loadColumns();

    EventSchema eventSchema = new EventSchema();
    GuessSchema guessSchema = new GuessSchema();
    List<EventProperty> allProperties = new ArrayList<>();

    for (Column column : columns) {
      PrimitivePropertyBuilder property = PrimitivePropertyBuilder
          .create(column.getDatatypes(), column.getName())
          .label(column.getName());
      // Setting the timestamp field to the correct domainProperty
      if (column.getName().equals("time")) {
        property.domainProperty(DATE_TIME);
      }
      allProperties.add(property.build());
    }

    eventSchema.setEventProperties(allProperties);
    guessSchema.setEventSchema(eventSchema);

    disconnect();
    return guessSchema;
  }

  // Client must be connected before calling this method
  void loadColumns() throws AdapterException {
    if (!connected) {
      throw new AdapterException("Client must be connected to the server in order to load the columns.");
    }
    List<List<Object>> fieldKeys = query("SHOW FIELD KEYS FROM " + measureName);
    List<List<Object>> tagKeys = query("SHOW TAG KEYS FROM " + measureName);
//        if (fieldKeys.size() == 0 || tagKeys.size() == 0) {
    if (fieldKeys.size() == 0) {
      throw new AdapterException("Error while checking the Schema (does the measurement exist?)");
    }

    columns = new ArrayList<>();
    columns.add(new Column("time", Datatypes.Long));

    for (List o : fieldKeys) {
      // o.get(0): Name, o.get(1): Datatype
      // Data types: https://docs.influxdata.com/influxdb/v1.7/write_protocols/line_protocol_reference/#data-types
      String name = o.get(0).toString();
      Datatypes datatype;
      switch (o.get(1).toString()) {
        case "float":
          datatype = Datatypes.Float;
          break;
        case "boolean":
          datatype = Datatypes.Boolean;
          break;
        case "integer":
          datatype = Datatypes.Integer;
          break;
        default:
          datatype = Datatypes.String;
          break;
      }
      columns.add(new Column(name, datatype));
    }
    for (List o : tagKeys) {
      // All tag keys are strings
      String name = o.get(0).toString();
      columns.add(new Column(name, Datatypes.String));
    }

    // Update the column String
    // Do it only here, because it is needed every time for the query (performance)
    StringBuilder sb = new StringBuilder();
    for (Column column : columns) {
      sb.append(column.getName()).append(", ");
    }
    sb.setLength(sb.length() - 2);
    columnsString = sb.toString();
  }

  // Returns a list with the entries of the query. If there are no entries, it returns an empty list
  List<List<Object>> query(String query) {
    if (!connected) {
      throw new RuntimeException("InfluxDbClient not connected");
    }
    QueryResult queryResult = influxDb.query(new Query(query, connectionSettings.getDatabaseName()));
    if (queryResult.getResults().get(0).getSeries() != null) {
      return queryResult.getResults().get(0).getSeries().get(0).getValues();
    } else {
      return new ArrayList<>();
    }
  }

  // Returns null, if replaceNullValues == false and if in items is a null value
  // Otherwise it returns a Map containing the runtimenames and the correctly parsed values
  Map<String, Object> extractEvent(List<Object> items) throws SpRuntimeException {
    if (items.size() != columns.size()) {
      throw new SpRuntimeException("Converter: Item list length is not the same as column list length");
    }
    Map<String, Object> out = new HashMap<>();

    // First element is the timestamp, which will be converted to milli seconds
    TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse((String) items.get(0));
    Instant time = Instant.from(temporalAccessor);
    out.put("time", time.toEpochMilli());

    for (int i = 1; i < items.size(); i++) {
      // The order of columns and items is the same, because the order in columnsString (which is used for the
      // query) is based on the order of columns
      if (items.get(i) != null) {
        out.put(columns.get(i).getName(), items.get(i));
      } else {
        if (replaceNullValues) {
          // Replace null values with defaults
          switch (columns.get(i).getDatatypes()) {
            case String:
              out.put(columns.get(i).getName(), "");
              break;
            case Integer:
              out.put(columns.get(i).getName(), 0);
              break;
            case Float:
              out.put(columns.get(i).getName(), 0.0f);
              break;
            case Boolean:
              out.put(columns.get(i).getName(), false);
              break;
            default:
              throw new SpRuntimeException("Unexpected value: " + columns.get(i).getDatatypes());
          }
        } else {
          // One field == null is enough to skip this event
          // Or maybe throw an exception instead?
          return null;
        }
      }
    }
    return out;
  }

  // Converts a string date from ISO_INSTANT format in a unix timestamp in nanoseconds
  static String getTimestamp(String date) {
    TemporalAccessor temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(date);

    Instant time = Instant.from(temporalAccessor);
    return time.getEpochSecond() + String.format("%09d", time.getNano());
  }

  String getColumnsString() {
    return columnsString;
  }

  String getMeasurement() {
    return measureName;
  }

  boolean isConnected() {
    return connected;
  }
}
