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

package org.apache.streampipes.dataexplorer.management;

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryBackend;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.api.query.UnsupportedQueryException;
import org.apache.streampipes.dataexplorer.query.QueryResultCollector;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryResult;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

/** Plans automatic chart aggregation using typed helper queries against the already resolved backend. */
final class QueryPlanner {
  private final DatasetQueryBackend backend;

  QueryPlanner(DatasetQueryBackend backend) {
    this.backend = backend;
  }

  QuerySpec plan(DatasetMetadata dataset, QuerySpec query, QueryExecutionOptions options) {
    if (!options.autoAggregate() || query.timeBucket().isPresent()
        || query.projections().stream().noneMatch(projection -> projection.aggregation().isPresent())) {
      return query;
    }
    if (!backend.capabilities().timeBuckets()) {
      throw new UnsupportedQueryException("Automatic aggregation requires time buckets");
    }
    int maximum = options.maximumRows().orElse(2000);
    if (maximum <= 0) {
      maximum = 2000;
    }
    var oldest = sample(dataset, query, options, 1, DataLakeQueryOrdering.ASC);
    if (oldest.getTotal() == 0) {
      return query;
    }
    var newest = sample(dataset, query, options, 1, DataLakeQueryOrdering.DESC);
    if (newest.getTotal() == 0) {
      return query;
    }
    int sampleLimit = maximum == Integer.MAX_VALUE ? maximum : maximum + 1;
    int count = sample(dataset, query, options, sampleLimit, DataLakeQueryOrdering.ASC).getTotal();
    long range = Math.addExact(Math.max(0, Math.subtractExact(timestamp(newest), timestamp(oldest))), 1);
    long interval = count <= maximum ? 1 : Math.max(1, Math.ceilDiv(range, maximum));
    return new QuerySpec(query.projections(), query.predicates(),
        Optional.of(new QuerySpec.TimeBucket(new QuerySpec.TimeInterval(interval + "ms"))),
        query.dimensions(), query.ordering(), query.limit(), query.offset(),
        options.autoAggregationFill().or(query::fill)
            .or(() -> Optional.of(new QuerySpec.Fill(QuerySpec.FillMode.NONE, Optional.empty()))),
        query.orderFields());
  }

  private SpQueryResult sample(DatasetMetadata dataset, QuerySpec source, QueryExecutionOptions options,
                                int limit, DataLakeQueryOrdering order) {
    var projections = source.projections().stream()
        .map(projection -> new QuerySpec.Projection(projection.field(), Optional.empty(), Optional.empty())).toList();
    var query = new QuerySpec(projections, source.predicates(), Optional.empty(), List.of(), Optional.of(order),
        OptionalInt.of(limit), OptionalInt.empty(), Optional.empty());
    try (var cursor = backend.open(dataset, query)) {
      return QueryResultCollector.collect(cursor,
          new QueryExecutionOptions(options.ignoreMissingValues(), OptionalInt.empty(), false));
    }
  }

  private long timestamp(SpQueryResult result) {
    var series = result.getAllDataSeries().getFirst();
    return ((Number) series.getRows().getFirst().get(series.getHeaders().indexOf("time"))).longValue();
  }
}
