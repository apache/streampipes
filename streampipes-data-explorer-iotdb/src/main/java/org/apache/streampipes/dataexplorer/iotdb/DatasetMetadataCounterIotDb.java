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

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.query.DatasetMetadataCounter;
import org.apache.streampipes.dataexplorer.query.QueryResultCollector;
import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DatasetMetadataCounterIotDb extends DatasetMetadataCounter {

  public DatasetMetadataCounterIotDb(List<DatasetMetadata> allMeasurements,
                                         List<String> measurementNames,
                                         int daysBack) {
    super(allMeasurements, measurementNames, daysBack);
  }

  /**
   * Creates a CompletableFuture to execute a count query on a DatasetMetadata asynchronously.
   *
   * @param measure The DatasetMetadata object representing the measure to query.
   * @return A {@link CompletableFuture} representing the count query result as a future.
   */
  @Override
  protected CompletableFuture<Integer> createQueryAsAsyncFuture(DatasetMetadata measure) {
    var sessionPool = IotDbSessionProvider.sharedQueryPool();
    return CompletableFuture.supplyAsync(() -> {

      // We want to apply the count query to only one stored property of the measurement, as this is sufficient and
      // significantly reduces the complexity of the query compared to counting all available properties.
      // So we can just take the first countable property.
      var propertyName = getFirstCountableProperty(measure);

      if (propertyName == null) {
        return 0;
      }
      var query = QuerySpec.builder().aggregate(propertyName, AggregationFunction.COUNT, "count").build();
      try (var cursor = new IotDbQueryBackend(sessionPool).open(measure, query)) {
        var result = QueryResultCollector.collect(cursor, QueryExecutionOptions.defaults());
        return result.getTotal() > 0 ? extractResult(result, "count") : 0;
      }
    });
  }
}
