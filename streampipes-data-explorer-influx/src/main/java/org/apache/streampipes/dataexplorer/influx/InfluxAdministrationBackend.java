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

import org.apache.streampipes.dataexplorer.api.query.DatasetAdministrationBackend;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/** Executes storage administration directly, without constructing chart query results. */
public final class InfluxAdministrationBackend implements DatasetAdministrationBackend {
  private final String database;
  private final Supplier<InfluxDB> clients;

  public InfluxAdministrationBackend(String database, Supplier<InfluxDB> clients) {
    this.database = database;
    this.clients = clients;
  }

  @Override
  public boolean delete(DatasetMetadata dataset) {
    return executeMutation("DROP MEASUREMENT " + identifier(dataset.getMeasureName()));
  }

  @Override
  public boolean deleteRange(DatasetMetadata dataset, Long startExclusive, Long endExclusive) {
    var conditions = new ArrayList<String>();
    if (startExclusive != null) {
      conditions.add("time > " + startExclusive + "ms");
    }
    if (endExclusive != null) {
      conditions.add("time < " + endExclusive + "ms");
    }
    String query = "DELETE FROM " + identifier(dataset.getMeasureName());
    if (!conditions.isEmpty()) {
      query += " WHERE " + String.join(" AND ", conditions);
    }
    return executeMutation(query);
  }

  private boolean executeMutation(String statement) {
    try (var client = clients.get()) {
      return !hasError(client.query(new Query(statement, database, true)));
    }
  }

  @Override
  public Map<String, Object> dimensionValues(DatasetMetadata dataset, List<String> fields) {
    Map<String, Object> tags = new LinkedHashMap<>();
    if (fields.isEmpty()) {
      return tags;
    }
    try (var client = clients.get()) {
      for (var field : fields) {
        var query = new Query("SHOW TAG VALUES ON " + identifier(database) + " FROM "
            + identifier(dataset.getMeasureName()) + " WITH KEY = " + identifier(field), database);
        var result = client.query(query);
        if (hasError(result)) {
          throw new QueryExecutionException("Influx dimension lookup failed");
        }
        if (result.getResults() == null) {
          continue;
        }
        for (var statement : result.getResults()) {
          if (statement.getSeries() == null) {
            continue;
          }
          for (var series : statement.getSeries()) {
            if (series.getValues() != null && !series.getValues().isEmpty()) {
              var values = series.getValues().stream().map(row -> row.get(1).toString()).toList();
              tags.put(series.getValues().getFirst().getFirst().toString(), values);
            }
          }
        }
      }
    }
    return tags;
  }

  private boolean hasError(QueryResult result) {
    return result.hasError() || result.getResults() != null
        && result.getResults().stream().anyMatch(QueryResult.Result::hasError);
  }

  private String identifier(String value) {
    return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
  }
}
