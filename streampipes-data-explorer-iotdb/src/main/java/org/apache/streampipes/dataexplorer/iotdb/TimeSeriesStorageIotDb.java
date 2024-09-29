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
package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.TimeSeriesStorage;
import org.apache.streampipes.dataexplorer.iotdb.sanitize.IotDbNameSanitizer;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.List;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSeriesStorageIotDb extends TimeSeriesStorage {

  private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesStorageIotDb.class);

  private final IotDbPropertyConverter propertyConverter;
  private final SessionPool sessionPool;

  public TimeSeriesStorageIotDb(DataLakeMeasure measure, IotDbPropertyConverter propertyConverter,
          IotDbSessionProvider iotDbSessionProvider) {
    super(measure);
    this.propertyConverter = propertyConverter;
    this.sessionPool = iotDbSessionProvider.getSessionPool(Environments.getEnvironment());
  }

  @Override
  protected void storeSanitizedRuntimeNames() {
    measure.getEventSchema().getEventProperties().forEach(ep -> sanitizedRuntimeNames.put(ep.getRuntimeName(),
            new IotDbNameSanitizer().renameReservedKeywords(ep.getRuntimeName())));
  }

  @Override
  protected void sanitizeRuntimeNamesInEvent(Event event) {
    event.getRaw().keySet().forEach(runtimeName -> {
      // timestamp field does not need to be renamed since it is not written as measurement
      // and taken care of separately
      if (!runtimeName.equals(measure.getTimestampFieldName())) {
        event.renameFieldByRuntimeName(runtimeName, new IotDbNameSanitizer().renameReservedKeywords(runtimeName));
      }
    });
  }

  @Override
  protected void writeToTimeSeriesStorage(Event event) throws SpRuntimeException {
    if (event == null) {
      LOG.warn("Input event is null - skipping event");
      return;
    }

    var timestampValue = event.getFieldBySelector(measure.getTimestampField()).getAsPrimitive().getAsLong();

    if (timestampValue == null) {
      LOG.warn("Timestamp of input event is null - skipping event");
      return;
    }

    if (event.getRaw().size() <= 1) {
      LOG.warn("Event only consists of timestamp and does not include any measurement - skipping event");
      return;
    }

    var iotDbRecords = extractMeasurementRecords(event);

    if (iotDbRecords.isEmpty()) {
      return;
    }

    insertIntoIotDb(timestampValue, iotDbRecords);
  }

  @Override
  public void close() throws SpRuntimeException {
    this.sessionPool.close();
  }

  /**
   * Extracts all relevant information for the IotDb from the incoming event.
   *
   * @param event
   *          The event from which measurement records are extracted.
   * @return An ArrayList of IotDbMeasurementRecord objects containing the extracted measurement records.
   */
  private ArrayList<IotDbMeasurementRecord> extractMeasurementRecords(Event event) {
    var iotDbRecords = new ArrayList<IotDbMeasurementRecord>();
    allEventProperties.forEach(ep -> {
      try {
        // timestamp is already known and is not part of the measurement thus we ignore it here
        if (!ep.getRuntimeName().equals(measure.getTimestampFieldName())) {

          try {
            var eventField = event.getFieldByRuntimeName(ep.getRuntimeName()).getAsPrimitive();

            if (ep instanceof EventPropertyPrimitive) {
              iotDbRecords.add(propertyConverter.convertPrimitiveProperty((EventPropertyPrimitive) ep,
                      event.getFieldByRuntimeName(ep.getRuntimeName()).getAsPrimitive(),
                      sanitizedRuntimeNames.get(ep.getRuntimeName())));
            } else {
              iotDbRecords.add(propertyConverter.convertNonPrimitiveProperty(ep,
                      sanitizedRuntimeNames.get(ep.getRuntimeName())));
            }
          } catch (SpRuntimeException e) {
            LOG.debug("Value and metadata for event field '{}' could not be extracted - "
                    + "property will not be written to storage", ep.getRuntimeName());
          }
        }
      } catch (SpRuntimeException e) {
        LOG.error("Event could not be converted to the IotDB representation: {}", e.getMessage());
      }
    });
    return iotDbRecords;
  }

  /**
   * Inserts measurement records into an IoTDB database as an aligned time series of one device.
   *
   * @param timestampValue
   *          The timestamp value associated with the measurement records.
   * @param iotDbRecords
   *          An ArrayList of IotDbMeasurementRecord objects containing the measurement records to be inserted.
   */
  private void insertIntoIotDb(long timestampValue, ArrayList<IotDbMeasurementRecord> iotDbRecords) {
    try {
      sessionPool.insertAlignedRecordsOfOneDevice("root.streampipes.%s".formatted(measure.getMeasureName()),
              List.of(timestampValue),
              List.of(iotDbRecords.stream().map(IotDbMeasurementRecord::measurementName).toList()),
              List.of(iotDbRecords.stream().map(IotDbMeasurementRecord::dataType).toList()),
              List.of(iotDbRecords.stream().map(IotDbMeasurementRecord::value).toList()));
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      LOG.error("Failed to write event to IoTDB  - {}", e.getMessage());
    }
  }
}
