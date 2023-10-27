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

import org.apache.streampipes.dataexplorer.param.model.AggregationFunction;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.PropertyScope;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class DataLakeMeasurementCount {

  private final List<DataLakeMeasure> allMeasurements;
  private final List<String> measurementNames;

  private static final String COUNT_FIELD = "count";

  public DataLakeMeasurementCount(List<DataLakeMeasure> allMeasurements,
                                  List<String> measurementNames) {
    this.allMeasurements = allMeasurements;
    this.measurementNames = measurementNames;
  }

  public Map<String, Integer> countMeasurementSizes() {
    Map<String, CompletableFuture<Integer>> futures = measurementNames.stream()
        .distinct()
        .map(this::getMeasure)
        .collect(Collectors.toMap(DataLakeMeasure::getMeasureName, m -> CompletableFuture.supplyAsync(() -> {
          var firstColumn = getFirstColumn(m);
          var builder = DataLakeInfluxQueryBuilder
              .create(m.getMeasureName()).withEndTime(System.currentTimeMillis())
              .withAggregatedColumn(firstColumn, AggregationFunction.COUNT);
          var queryResult = new DataExplorerInfluxQueryExecutor().executeQuery(builder.build(), true);
          if (queryResult.getTotal() > 0) {
            var headers = queryResult.getHeaders();
            return extractResult(queryResult, headers);
          } else {
            return 0;
          }
        })));

    Map<String, Integer> result = new HashMap<>();
    futures.entrySet().forEach((entry -> {
      try {
        result.put(entry.getKey(), entry.getValue().get());
      } catch (InterruptedException | ExecutionException e) {
        result.put(entry.getKey(), 0);
      }
    }));

    return result;
  }

  private Integer extractResult(SpQueryResult queryResult,
                                List<String> headers) {
    return ((Double) (
        queryResult.getAllDataSeries().get(0).getRows().get(0).get(headers.indexOf(COUNT_FIELD)))
    ).intValue();
  }

  private DataLakeMeasure getMeasure(String measureName) {
    return allMeasurements
        .stream()
        .filter(m -> m.getMeasureName().equals(measureName))
        .findFirst()
        .orElse(null);
  }

  private String getFirstColumn(DataLakeMeasure measure) {
    return measure.getEventSchema().getEventProperties()
        .stream()
        .filter(ep -> ep.getPropertyScope() != null
            && ep.getPropertyScope().equals(PropertyScope.MEASUREMENT_PROPERTY.name()))
        .map(EventProperty::getRuntimeName)
        .findFirst()
        .orElse(null);
  }
}
