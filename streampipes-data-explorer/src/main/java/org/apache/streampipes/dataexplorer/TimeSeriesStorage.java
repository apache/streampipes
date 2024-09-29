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
package org.apache.streampipes.dataexplorer;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.api.ITimeSeriesStorage;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class TimeSeriesStorage implements ITimeSeriesStorage {

  private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesStorage.class);

  protected final DataLakeMeasure measure;
  protected final List<EventProperty> allEventProperties;
  protected final Map<String, String> sanitizedRuntimeNames = new HashMap<>();

  public TimeSeriesStorage(DataLakeMeasure measure) {
    this.measure = measure;
    storeSanitizedRuntimeNames();
    allEventProperties = getAllEventPropertiesExceptTimestamp();
  }

  @Override
  public void onEvent(Event event) throws SpRuntimeException {
    validateInputEventAndLogMissingFields(event);
    sanitizeRuntimeNamesInEvent(event);
    writeToTimeSeriesStorage(event);
  }

  private void validateInputEventAndLogMissingFields(Event event) {
    checkEventIsNotNull(event);

    logMissingFields(event);

    logNullFields(event);
  }

  private void checkEventIsNotNull(Event event) {
    if (event == null) {
      throw new SpRuntimeException("Input event is null");
    }
  }

  /**
   * Logs all fields which are present in the schema, but not in the provided event
   */
  private void logMissingFields(Event event) {
    var missingFields = getMissingProperties(allEventProperties, event);
    if (!missingFields.isEmpty()) {
      LOG.debug("Ignored {} fields which were present in the schema, but not in the provided event: {}",
              missingFields.size(), String.join(", ", missingFields));
    }
  }

  /**
   * Returns a list of the runtime names that are missing within the event
   */
  private List<String> getMissingProperties(List<EventProperty> allEventProperties, Event event) {
    return allEventProperties.stream().map(EventProperty::getRuntimeName)
            .filter(runtimeName -> event.getOptionalFieldByRuntimeName(runtimeName).isEmpty()).toList();
  }

  /**
   * Logs all fields that contain null values
   */
  private void logNullFields(Event event) {
    List<String> nullFields = allEventProperties.stream().filter(EventPropertyPrimitive.class::isInstance)
            .filter(ep -> {
              var runtimeName = ep.getRuntimeName();
              var field = event.getOptionalFieldByRuntimeName(runtimeName);

              return field.isPresent() && field.get().getAsPrimitive().getRawValue() == null;
            }).map(EventProperty::getRuntimeName).collect(Collectors.toList());

    if (!nullFields.isEmpty()) {
      LOG.warn("Ignored {} fields which had a value 'null': {}", nullFields.size(), String.join(", ", nullFields));
    }
  }

  /**
   * Returns all measurements properties except the timestamp field
   */
  private List<EventProperty> getAllEventPropertiesExceptTimestamp() {
    return measure.getEventSchema().getEventProperties().stream()
            .filter(ep -> !measure.getTimestampField().endsWith(ep.getRuntimeName())).toList();
  }

  /**
   * store sanitized target property runtime names in variable `sanitizedRuntimeNames`
   */
  protected abstract void storeSanitizedRuntimeNames();

  /**
   * Iterates over all properties of the event and renames the key if it is a reserved keywords in InfluxDB
   */
  protected abstract void sanitizeRuntimeNamesInEvent(Event event);

  protected abstract void writeToTimeSeriesStorage(Event event) throws SpRuntimeException;
}
