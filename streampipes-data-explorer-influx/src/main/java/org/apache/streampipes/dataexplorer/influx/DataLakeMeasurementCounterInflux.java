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

import org.apache.streampipes.dataexplorer.query.DataLakeMeasurementCounter;
import org.apache.streampipes.model.datalake.AggregationFunction;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class DataLakeMeasurementCounterInflux extends DataLakeMeasurementCounter {

  private static final Logger LOG = LoggerFactory.getLogger(DataLakeMeasurementCounterInflux.class);

  private static final String COUNT_FIELD = "count";

  public DataLakeMeasurementCounterInflux(
      List<DataLakeMeasure> allMeasurements,
      List<String> measurementNames
  ) {
    super(allMeasurements, measurementNames);
  }

  @Override
  protected CompletableFuture<Integer> createQueryAsAsyncFuture(DataLakeMeasure measure) {
    return CompletableFuture.supplyAsync(() -> {
      var firstColumn = getFirstMeasurementProperty(measure);
      if (firstColumn == null) {
        LOG.error(
            "Could not count events in measurement: {}, because no measurement property was found in event schema",
            measure.getMeasureName()
        );
        return 0;
      }

      var builder = DataLakeInfluxQueryBuilder
          .create(measure.getMeasureName())
          .withEndTime(System.currentTimeMillis())
          .withAggregatedColumn(firstColumn, AggregationFunction.COUNT);
      var queryResult = new DataExplorerInfluxQueryExecutor().executeQuery(builder.build(), Optional.empty(), true);

      return queryResult.getTotal() > 0 ? extractResult(queryResult, COUNT_FIELD) : 0;
    });
  }
}
