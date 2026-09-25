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

import org.apache.streampipes.dataexplorer.api.IDatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.api.query.DatasetQuery;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryBackend;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.dataexplorer.query.QueryResultCollector;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryResult;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Stateless application entry point for REST and internal typed-query callers. */
public final class DatasetQueryService implements AutoCloseable {
  private final IDatasetMetadataManagement catalog;
  private final DatasetQueryBackend backend;
  private final QueryPlanner planner;

  public DatasetQueryService(IDatasetMetadataManagement catalog, DatasetQueryBackend backend) {
    this.catalog = catalog;
    this.backend = backend;
    this.planner = new QueryPlanner(backend);
  }

  @Override
  public void close() {
    backend.close();
  }

  public SpQueryResult query(DatasetQuery query, QueryExecutionOptions options) {
    var dataset = catalog.getById(query.datasetId().value());
    if (dataset == null) {
      throw new IllegalArgumentException("Unknown dataset: " + query.datasetId().value());
    }
    return query(dataset, query.specification(), options);
  }

  /** Resolves the existing measurement-name routes without exposing physical names to the backend caller. */
  public SpQueryResult queryByName(String name, QuerySpec query, QueryExecutionOptions options) {
    return query(resolveByName(name), query, options);
  }

  public DatasetMetadata resolveByName(String name) {
    return catalog.getExistingMeasureByName(name)
        .orElseThrow(() -> new IllegalArgumentException("Unknown dataset: " + name));
  }

  public DatasetQueryCursor open(DatasetMetadata dataset, QuerySpec query, QueryExecutionOptions options) {
    backend.capabilities().validate(query);
    var planned = planner.plan(dataset, query, options);
    backend.capabilities().validate(planned);
    return backend.open(dataset, planned, options);
  }

  private SpQueryResult query(DatasetMetadata dataset, QuerySpec query, QueryExecutionOptions options) {
    try (var cursor = open(dataset, query, options)) {
      return QueryResultCollector.collect(cursor, options);
    }
  }

  public Map<String, Long> getLatestTimestamps(List<String> names) {
    Map<String, Long> timestamps = new LinkedHashMap<>();
    var query = QuerySpec.builder().select("*").orderBy(DataLakeQueryOrdering.DESC).limit(1).build();
    var datasets = new LinkedHashMap<String, DatasetMetadata>();
    names.forEach(name -> catalog.getExistingMeasureByName(name).ifPresent(dataset -> datasets.put(name, dataset)));
    var optimized = backend.latestTimestamps(List.copyOf(datasets.values()));
    for (String name : names) {
      var dataset = datasets.get(name);
      if (dataset == null) {
        timestamps.put(name, 0L);
      } else if (optimized.containsKey(dataset.getElementId())) {
        timestamps.put(name, optimized.get(dataset.getElementId()));
      } else {
        var result = query(dataset, query, QueryExecutionOptions.defaults());
        long latest = result.getAllDataSeries().stream().flatMap(series -> series.getRows().stream()
            .map(row -> ((Number) row.get(series.getHeaders().indexOf("time"))).longValue()))
            .max(Long::compare).orElse(0L);
        timestamps.put(name, latest);
      }
    }
    return timestamps;
  }
}
