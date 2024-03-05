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
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class InfluxStore {

  private static final Logger LOG = LoggerFactory.getLogger(InfluxStore.class);

  private final DataLakeMeasure measure;
  private final List<EventProperty> allEventProperties;
  private final Map<String, String> sanitizedRuntimeNames = new HashMap<>();
  private final InfluxDB influxDb;

  private final PropertyHandler propertyHandler;


  public InfluxStore(
      DataLakeMeasure measure,
      Environment environment,
      InfluxClientProvider influxClientProvider
  ) throws SpRuntimeException {
    this.measure = measure;
    storeSanitizedRuntimeNames(measure);
    allEventProperties = getAllEventPropertiesExceptTimestamp(measure);
    influxDb = influxClientProvider.getInitializedInfluxDBClient(environment);
    propertyHandler = new PropertyHandler();
  }

  /**
   * Takes an StreamPipes event, transforms it to an InfluxDB point and writes it to the InfluxDB
   *
   * @param event The event which should be saved
   * @throws SpRuntimeException If the column name (key-value of the event map) is not allowed
   */
  public void onEvent(Event event) throws SpRuntimeException {

    validateInputEventAndLogMissingFields(event);

    sanitizeRuntimeNamesInEvent(event);

    var point = initializePointWithTimestamp(event);

    iterateOverallEventProperties(event, point);

    influxDb.write(point.build());
  }

  private void validateInputEventAndLogMissingFields(Event event) {
    checkEventIsNotNull(event);

    logMissingFields(event);

    logNullFields(event);
  }

  /**
   * Logs all fields which are present in the schema, but not in the provided event
   */
  private void logMissingFields(Event event) {
    var missingFields = getMissingProperties(allEventProperties, event);
    if (!missingFields.isEmpty()) {
      LOG.debug(
          "Ignored {} fields which were present in the schema, but not in the provided event: {}",
          missingFields.size(),
          String.join(", ", missingFields)
      );
    }
  }


  /**
   * Logs all fields that contain null values
   */
  private void logNullFields(Event event) {
    List<String> nullFields = allEventProperties
        .stream()
        .filter(EventPropertyPrimitive.class::isInstance)
        .filter(ep -> {
          var runtimeName = ep.getRuntimeName();
          var field = event.getOptionalFieldByRuntimeName(runtimeName);

          return field.isPresent() && field.get()
                                           .getAsPrimitive()
                                           .getRawValue() == null;
        })
        .map(EventProperty::getRuntimeName)
        .collect(Collectors.toList());

    if (!nullFields.isEmpty()) {
      LOG.warn("Ignored {} fields which had a value 'null': {}", nullFields.size(), String.join(", ", nullFields));
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
   * Returns a list of the runtime names that are missing within the event
   */
  private List<String> getMissingProperties(
      List<EventProperty> allEventProperties,
      Event event
  ) {
    return allEventProperties.stream()
                             .map(EventProperty::getRuntimeName)
                             .filter(runtimeName -> event.getOptionalFieldByRuntimeName(runtimeName)
                                                          .isEmpty())
                             .collect(Collectors.toList());
  }

  private void checkEventIsNotNull(Event event) {
    if (event == null) {
      throw new SpRuntimeException("event is null");
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
  private void storeSanitizedRuntimeNames(DataLakeMeasure measure) {
    measure.getEventSchema()
           .getEventProperties()
           .forEach(ep -> sanitizedRuntimeNames.put(
               ep.getRuntimeName(),
               InfluxNameSanitizer.renameReservedKeywords(ep.getRuntimeName())
           ));
  }

  /**
   * Returns all measurements properties except the timestamp field
   */
  private List<EventProperty> getAllEventPropertiesExceptTimestamp(DataLakeMeasure dataLakeMeasure) {
    return dataLakeMeasure.getEventSchema()
                          .getEventProperties()
                          .stream()
                          .filter(ep -> !dataLakeMeasure.getTimestampField()
                                                        .endsWith(ep.getRuntimeName()))
                          .collect(Collectors.toList());
  }

  /**
   * Iterates over all properties of the event and renames the key if it is a reserved keywords in InfluxDB
   */
  private void sanitizeRuntimeNamesInEvent(Event event) {
    // sanitize event
    for (var key : event.getRaw()
                        .keySet()) {
      if (InfluxDbReservedKeywords.KEYWORD_LIST.stream()
                                               .anyMatch(k -> k.equalsIgnoreCase(key))) {
        event.renameFieldByRuntimeName(key, key + "_");
      }
    }
  }

}
