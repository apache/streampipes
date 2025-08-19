/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import org.apache.streampipes.storage.couchdb.CouchDbStorageManager;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DataLakeDataWriter {

  public void writeData(String measureName, SpQueryResult queryResult) {
    var measure = CouchDbStorageManager.INSTANCE.getDataLakeStorage().getByMeasureName(measureName);
    var dataSeries = getDataSeries(queryResult);
    getTimeSeriesStoreAndPersistQueryResult(dataSeries, measure);
  }

  private void getTimeSeriesStoreAndPersistQueryResult(DataSeries dataSeries,
                                                       DataLakeMeasure measure){
    var timeSeriesStore = getTimeSeriesStore(measure);
    for (var row : dataSeries.getRows()) {
      var event = rowToEvent(row, dataSeries.getHeaders());
      renameTimestampField(event, measure.getTimestampField());
      timeSeriesStore.onEvent(event);
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

  private String getSubstringAfterColons(String input) {
    int index = input.indexOf("::");
    if (index != -1) {
      return input.substring(index + 2);
    }
    return input;
  }

  private Event rowToEvent(List<Object> row, List<String> headers){
    Map<String, Object> eventMap = IntStream.range(0, headers.size())
        .boxed()
        .collect(Collectors.toMap(headers::get, row::get));
    return EventFactory.fromMap(eventMap);
  }

  private void renameTimestampField(Event event, String timestampField){
    var strippedTime = getSubstringAfterColons(timestampField);
    event.addField(timestampField, event.getFieldByRuntimeName(strippedTime).getRawValue());
  }

}


