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

import org.apache.streampipes.dataexplorer.api.IDataLakeQueryBuilder;
import org.apache.streampipes.dataexplorer.influx.client.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
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
import java.util.TreeMap;
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

    values.forEach(row -> {

      convertTimestampValueToLong(row);

      if (ignoreMissingValues) {
        if (!row.contains(null)) {
          resultingValues.add(row);
        }
      } else {
        resultingValues.add(row);
      }

    });

    return new DataSeries(resultingValues.size(), resultingValues, columns, series.getTags());
  }

  /**
   * The influx client always returns a double for timestamp values. This method converts them to long values.
   */
  private void convertTimestampValueToLong(List<Object> row) {
    if (!row.isEmpty() && row.get(0) instanceof Number) {
      row.set(0, ((Number) row.get(0)).longValue());
    }
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
        lastTimestamp.set(Math.max(lastTimestamp.get(), (long) lastValue.get(0)));
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

  public Map<String, Long> getLatestTimestamps(Map<String, String> measurementFields) {
    if (measurementFields.isEmpty()) {
      return Map.of();
    }

    var query = makeLatestTimestampQuery(measurementFields);
    try (final InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient()) {
      return parseLatestTimestampResult(influxDB.query(query, TimeUnit.MILLISECONDS));
    }
  }

  Query makeLatestTimestampQuery(Map<String, String> measurementFields) {
    var query = measurementFields.entrySet()
        .stream()
        .collect(Collectors.groupingBy(Map.Entry::getValue, TreeMap::new,
            Collectors.mapping(Map.Entry::getKey, Collectors.toList())))
        .entrySet()
        .stream()
        .map(entry -> makeLastSelectorQuery(entry.getKey(), entry.getValue()))
        .collect(Collectors.joining(";"));

    return new Query(query, getDatabaseName());
  }

  private String makeLastSelectorQuery(String field,
                                       List<String> measurements) {
    return "SELECT LAST(\""
        + field
        + "\") FROM /"
        + measurements.stream()
            .map(this::escapeRegex)
            .collect(Collectors.joining("|", "^(", ")$"))
        + "/";
  }

  private String escapeRegex(String measurement) {
    return measurement.replaceAll("([\\\\.\\[\\]{}()*+?^$|])", "\\\\$1");
  }

  private Map<String, Long> parseLatestTimestampResult(QueryResult queryResult) {
    Map<String, Long> latestTimestamps = new HashMap<>();
    if (queryResult.getResults() != null) {
      queryResult.getResults().forEach(result -> {
        if (result.getSeries() != null) {
          result.getSeries().forEach(series -> parseLatestTimestampSeries(series, latestTimestamps));
        }
      });
    }
    return latestTimestamps;
  }

  private void parseLatestTimestampSeries(QueryResult.Series series,
                                          Map<String, Long> latestTimestamps) {
    var values = series.getValues();
    if (values != null && !values.isEmpty() && !values.get(0).isEmpty()) {
      latestTimestamps.put(series.getName(), parseTimestamp(values.get(0).get(0)));
    }
  }

  private Long parseTimestamp(Object timestamp) {
    if (timestamp instanceof Number number) {
      return number.longValue();
    } else if (timestamp instanceof String timestampString) {
      try {
        return Long.parseLong(timestampString);
      } catch (NumberFormatException e) {
        return 0L;
      }
    } else {
      return 0L;
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
