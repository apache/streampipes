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
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import org.influxdb.InfluxDB;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

public class TimeSeriesStorageInflux extends TimeSeriesStorage {

  private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesStorageInflux.class);

  /**
   * Maximum number of points sent to InfluxDB in a single write request when writing batches.
   */
  static final int MAX_POINTS_PER_WRITE = 10000;

  private final InfluxDB influxDb;

  /**
   * True if this storage created the client and closes it in {@link #close()}; false for a shared client that
   * outlives this storage and is only flushed on close.
   */
  private final boolean ownsClient;

  private final PropertyHandler propertyHandler;

  private final BiConsumer<String, String> warningReporter;

  private final Set<String> reportedInvalidPrimitiveFields = ConcurrentHashMap.newKeySet();


  public TimeSeriesStorageInflux(
      DatasetMetadata measure,
      Environment environment,
      InfluxClientProvider influxClientProvider
  ) throws SpRuntimeException {
    this(measure, false, environment, influxClientProvider);
  }

  public TimeSeriesStorageInflux(
      DatasetMetadata measure,
      boolean ignoreDuplicates,
      Environment environment,
      InfluxClientProvider influxClientProvider
  ) throws SpRuntimeException {
    this(measure, ignoreDuplicates, environment, influxClientProvider,
        (title, details) -> LOG.warn("{}: {}", title, details));
  }

  public TimeSeriesStorageInflux(
      DatasetMetadata measure,
      boolean ignoreDuplicates,
      Environment environment,
      InfluxClientProvider influxClientProvider,
      BiConsumer<String, String> warningReporter
  ) throws SpRuntimeException {
    this(measure, ignoreDuplicates, influxClientProvider.getSetUpInfluxDBClient(environment), true, warningReporter);
  }

  /**
   * Creates a storage on top of an existing client.
   *
   * @param influxDb   the client to write to
   * @param ownsClient true if the storage should close the client in {@link #close()}, false if the client is
   *                   shared and only flushed
   */
  public TimeSeriesStorageInflux(
      DatasetMetadata measure,
      boolean ignoreDuplicates,
      InfluxDB influxDb,
      boolean ownsClient,
      BiConsumer<String, String> warningReporter
  ) throws SpRuntimeException {
    super(measure);
    this.warningReporter = warningReporter;
    this.influxDb = influxDb;
    this.ownsClient = ownsClient;
    propertyHandler = new PropertyHandler(new PropertyDuplicateFilter(ignoreDuplicates));
  }

  protected void writeToTimeSeriesStorage(Event event) throws SpRuntimeException {
    var point = buildPoint(event);
    if (point.hasFields()) {
      influxDb.write(point.build());
    }
  }

  /**
   * Writes all events synchronously in as few requests as possible (at most {@link #MAX_POINTS_PER_WRITE} points
   * per request), bypassing the asynchronous batch queue of the client. The events are durable when this method
   * returns.
   */
  @Override
  protected void writeToTimeSeriesStorage(List<Event> events) throws SpRuntimeException {
    var batch = BatchPoints.builder();
    var pointsInBatch = 0;
    for (var event : events) {
      var point = buildPoint(event);
      if (point.hasFields()) {
        batch.point(point.build());
        pointsInBatch++;
        if (pointsInBatch >= MAX_POINTS_PER_WRITE) {
          influxDb.write(batch.build());
          batch = BatchPoints.builder();
          pointsInBatch = 0;
        }
      }
    }
    if (pointsInBatch > 0) {
      influxDb.write(batch.build());
    }
  }

  private Point.Builder buildPoint(Event event) {
    var point = initializePointWithTimestamp(event);
    iterateOverallEventProperties(event, point);
    return point;
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
          if (!field.isPrimitive()) {
            handleInvalidPrimitiveField(runtimeName, field.getClass().getSimpleName());
            return;
          }

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

  private void handleInvalidPrimitiveField(String runtimeName, String actualFieldType) {
    if (reportedInvalidPrimitiveFields.add(runtimeName)) {
      warningReporter.accept(
          "Invalid field ignored",
          "Event property '%s' is declared as primitive in the schema but received %s."
              .formatted(runtimeName, actualFieldType)
      );
    } else {
      LOG.debug(
          "Ignoring event property '{}' because its schema declares a primitive value but received {}.",
          runtimeName,
          actualFieldType
      );
    }
  }

  /**
   * Closes the connection to the InfluxDB server if this storage owns the client (closing flushes the batch
   * queue); a shared client is only flushed.
   */
  public void close() throws SpRuntimeException {
    if (!ownsClient) {
      influxDb.flush();
      return;
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
   * Renames the fields of the event whose runtime name is a reserved keyword in InfluxDB (the schema was
   * registered with the sanitized names, the events still carry the raw ones). A set lookup per key; the
   * rename itself only happens for the rare reserved names.
   */
  protected void sanitizeRuntimeNamesInEvent(Event event) {
    for (var key : new ArrayList<>(event.getRaw().keySet())) {
      if (InfluxNameSanitizer.isReservedKeyword(key)) {
        event.renameFieldByRuntimeName(key, InfluxNameSanitizer.renameReservedKeywords(key));
      }
    }
  }
}
