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

import org.apache.streampipes.dataexplorer.influx.client.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
import org.apache.streampipes.dataexplorer.api.IDataLakeQueryBuilder;
import org.apache.streampipes.dataexplorer.query.DataExplorerQueryExecutor;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static org.apache.streampipes.commons.environment.Environments.getEnvironment;

public class DataExplorerInfluxQueryExecutor extends DataExplorerQueryExecutor<Query, QueryResult> {

  protected DataSeries convertResult(QueryResult.Series series,
                                     boolean ignoreMissingValues) {
    List<String> columns = series.getColumns();
    List<List<Object>> values = series.getValues();

    List<List<Object>> resultingValues = new ArrayList<>();

    values.forEach(v -> {
      if (ignoreMissingValues) {
        if (!v.contains(null)) {
          resultingValues.add(v);
        }
      } else {
        resultingValues.add(v);
      }

    });

    return new DataSeries(resultingValues.size(), resultingValues, columns, series.getTags());
  }

  protected SpQueryResult postQuery(QueryResult queryResult,
                                    Optional<String> forIdOpt,
                                    boolean ignoreMissingValues) throws RuntimeException {
    SpQueryResult result = new SpQueryResult();
    AtomicLong lastTimestamp = new AtomicLong();

    if (hasResult(queryResult)) {
      queryResult.getResults().get(0).getSeries().forEach(rs -> {
        DataSeries series = convertResult(rs, ignoreMissingValues);
        result.setHeaders(series.getHeaders());
        result.addDataResult(series);
        List<Object> lastValue = rs.getValues().get(rs.getValues().size() - 1);
        lastTimestamp.set(Math.max(lastTimestamp.get(), ((Double) lastValue.get(0)).longValue()));
      });

      result.setTotal(result.getAllDataSeries().stream().mapToInt(DataSeries::getTotal).sum());
      result.setLastTimestamp(lastTimestamp.get());
    }

    forIdOpt.ifPresent(result::setForId);

    return result;
  }

  private IDataLakeQueryBuilder<Query> getQueryBuilder(String measurementId) {
    return DataLakeInfluxQueryBuilder.create(measurementId);
  }

  @Override
  public QueryResult executeQuery(Query query) {
    try (final InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient()) {
      return influxDB.query(query, TimeUnit.MILLISECONDS);
    }
  }

  @Override
  protected String asQueryString(Query query) {
    return "(database:" + query.getDatabase() + "): " + query.getCommand();
  }

  @Override
  protected Query makeDeleteQuery(DeleteQueryParams params) {
    String query = "DELETE FROM \"" + params.measurementName() + "\"";
    if (params.timeRestricted()) {
      query += "WHERE time > "
          + params.startTime() * 1000000
          + " AND time < "
          + params.endTime() * 1000000;
    }
    return new Query(query, getDatabaseName());
  }

  @Override
  protected Query makeSelectQuery(SelectQueryParams params) {
    var builder = getQueryBuilder(params.getIndex());
    return getQueryWithDatabaseName(params.toQuery(builder));
  }

  private boolean hasResult(QueryResult queryResult) {
    return queryResult.getResults() != null
        && !queryResult.getResults().isEmpty()
        && queryResult.getResults().get(0).getSeries() != null;
  }

  private Query getQueryWithDatabaseName(Query query) {
    var databaseName = getDatabaseName();
    return new Query(query.getCommand(), databaseName);
  }

  private String getDatabaseName() {
    return getEnvironment().getTsStorageBucket().getValueOrDefault();
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId, String fields) {
    try (final InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient()) {
      Map<String, Object> tags = new HashMap<>();
      if (fields != null && !(fields.isEmpty())) {
        List<String> fieldList = Arrays.asList(fields.split(","));
        fieldList.forEach(f -> {
          String q =
              "SHOW TAG VALUES ON \"" + getDatabaseName() + "\" FROM \"" + measurementId
              + "\" WITH KEY = \"" + f + "\"";
          Query query = new Query(q);
          QueryResult queryResult = influxDB.query(query);
          queryResult.getResults().forEach(res -> {
            res.getSeries().forEach(series -> {
              if (!series.getValues().isEmpty()) {
                String field = series.getValues().get(0).get(0).toString();
                List<String> values =
                    series.getValues().stream().map(v -> v.get(1).toString()).collect(Collectors.toList());
                tags.put(field, values);
              }
            });
          });
        });
      }

      return tags;
    }
  }

  @Override
  public boolean deleteData(DataLakeMeasure measure) {
    QueryResult queryResult = new DeleteDataQuery(measure).executeQuery();

    return !queryResult.hasError() && (queryResult.getResults() == null || queryResult.getResults()
                                                                                      .get(0)
                                                                                      .getError() == null);

  }
}
