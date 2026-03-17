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

package org.apache.streampipes.rest.impl.datalake;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.TimeSeriesStore;
import org.apache.streampipes.dataexplorer.management.DataExplorerDispatcher;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DataLakeDataWriter {

  private final boolean ignoreSchemaMismatch;
  private final boolean allowMissingFields;

  public DataLakeDataWriter(boolean ignoreSchemaMismatch) {
    this(ignoreSchemaMismatch, false);
  }

  public DataLakeDataWriter(boolean ignoreSchemaMismatch, boolean allowMissingFields) {
    this.ignoreSchemaMismatch = ignoreSchemaMismatch;
    this.allowMissingFields = allowMissingFields;
  }

  public void writeData(String measureName, SpQueryResult queryResult) {
    var measure = StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage().getByMeasureName(measureName);
    if (measure == null) {
      throw new SpRuntimeException("Measure \"" + measureName + "\" not found");
    }
    writeData(measure, queryResult);
  }

  public void writeData(DataLakeMeasure measure, SpQueryResult queryResult) {
    var dataSeries = getDataSeries(queryResult);
    getTimeSeriesStoreAndPersistQueryResult(dataSeries, measure);
  }

  public void writeData(DataLakeMeasure measure, List<String> headers, List<List<Object>> rows) {
    var dataSeries = new DataSeries();
    dataSeries.setHeaders(headers);
    dataSeries.setRows(rows);
    dataSeries.setTotal(rows.size());
    getTimeSeriesStoreAndPersistQueryResult(dataSeries, measure);
  }

  private void getTimeSeriesStoreAndPersistQueryResult(DataSeries dataSeries,
                                                       DataLakeMeasure measure){
    var timeSeriesStore = getTimeSeriesStore(measure);
    var runtimeNames = getRuntimeNames(measure);
    for (var row : dataSeries.getRows()) {
      var event = rowToEvent(row, dataSeries.getHeaders());
      renameTimestampField(event, measure.getTimestampField());
      checkRuntimeNames(runtimeNames, event);
      try {
        timeSeriesStore.onEvent(event);
      } catch (IllegalArgumentException e) {
        throw new SpRuntimeException("Fields don't match for event: " + event.getRaw());
      }
    }
    timeSeriesStore.close();
  }

  private TimeSeriesStore getTimeSeriesStore(DataLakeMeasure measure){
    return new TimeSeriesStore(
        new DataExplorerDispatcher().getDataExplorerManager()
            .getTimeseriesStorage(measure, false),
        measure,
        Environments.getEnvironment(),
        true
    );
  }

  private DataSeries getDataSeries(SpQueryResult queryResult) {
    if (queryResult.getAllDataSeries().size() == 1) {
      return queryResult.getAllDataSeries().get(0);
    } else {
      throw new SpRuntimeException("SpQueryResult must contain exactly one data series");
    }
  }

  private void checkRuntimeNames(List<String> runtimeNames, Event event) {
    if (!ignoreSchemaMismatch) {
      var strippedEventKeys = event.getFields().keySet().stream()
          .map(this::getSubstringAfterColons)
          .collect(Collectors.toSet());
      var runtimeNameSet = new HashSet<>(runtimeNames);

      if (!matchesRuntimeNames(runtimeNameSet, strippedEventKeys, allowMissingFields)) {
        throw new SpRuntimeException("The fields of the event do not match. Use \"ignoreSchemaMismatch\" to "
            + "ignore this error. Fields of the event: " + strippedEventKeys);
      }
    }
  }

  static boolean matchesRuntimeNames(
      Set<String> expectedRuntimeNames,
      Set<String> actualRuntimeNames,
      boolean allowMissingFields
  ) {
    if (allowMissingFields) {
      return expectedRuntimeNames.containsAll(actualRuntimeNames);
    }
    return expectedRuntimeNames.equals(actualRuntimeNames);
  }

  private List<String> getRuntimeNames(DataLakeMeasure measure) {
    var runtimeNames = new ArrayList<String>();
    runtimeNames.add(measure.getTimestampFieldName());
    for (var eventProperties: measure.getEventSchema().getEventProperties()) {
      runtimeNames.add(eventProperties.getRuntimeName());
    }
    return runtimeNames;
  }

  private String getSubstringAfterColons(String input) {
    int index = input.indexOf("::");
    if (index != -1) {
      return input.substring(index + 2);
    }
    return input;
  }

  private Event rowToEvent(List<Object> row, List<String> headers){
    return EventFactory.fromMap(toEventMap(row, headers));
  }

  static Map<String, Object> toEventMap(List<Object> row, List<String> headers) {
    var eventMap = new LinkedHashMap<String, Object>();
    for (int i = 0; i < headers.size(); i++) {
      var value = row.get(i);
      if (value != null) {
        eventMap.put(headers.get(i), value);
      }
    }
    return eventMap;
  }

  private void renameTimestampField(Event event, String timestampField){
    var strippedTime = getSubstringAfterColons(timestampField);
    event.addField(timestampField, event.getFieldByRuntimeName(strippedTime).getAsPrimitive()
          .getAsLong());
  }

}
