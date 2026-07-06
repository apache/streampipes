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

import org.apache.streampipes.dataexplorer.influx.client.InfluxClientUtils;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("benchmark")
public class InfluxLatestTimestampBenchmarkTest {

  private static final String ENABLED = "sp.influx.benchmark";
  private static final String URL = "sp.influx.benchmark.url";
  private static final String USERNAME = "sp.influx.benchmark.username";
  private static final String PASSWORD = "sp.influx.benchmark.password";
  private static final String TOKEN = "sp.influx.benchmark.token";
  private static final String DATABASE = "sp.influx.benchmark.database";
  private static final String RETENTION_POLICY = "sp.influx.benchmark.retention-policy";
  private static final String RECREATE_DATABASE = "sp.influx.benchmark.recreate-database";
  private static final String CLEANUP_MEASUREMENTS = "sp.influx.benchmark.cleanup-measurements";
  private static final String MEASUREMENTS = "sp.influx.benchmark.measurements";
  private static final String POINTS_PER_MEASUREMENT = "sp.influx.benchmark.points-per-measurement";
  private static final String FIELDS = "sp.influx.benchmark.fields";
  private static final String BATCH_SIZE = "sp.influx.benchmark.batch-size";
  private static final String WARMUP_RUNS = "sp.influx.benchmark.warmup-runs";
  private static final String MEASUREMENT_RUNS = "sp.influx.benchmark.measurement-runs";
  private static final String ALLOW_NON_BENCHMARK_DATABASE = "sp.influx.benchmark.allow-non-benchmark-database";

  private static final long BASE_TIMESTAMP = 1700000000000L;

  @Test
  public void benchmarkLatestTimestampQueriesAgainstInfluxDb() {
    Assumptions.assumeTrue(Boolean.getBoolean(ENABLED),
        "Enable with -D" + ENABLED + "=true and provide a reachable InfluxDB 1.x instance");

    var config = BenchmarkConfig.fromSystemProperties();
    assertBenchmarkDatabase(config);
    try (var influxDb = config.connect()) {
      for (int measurementCount : config.measurementCounts()) {
        if (config.recreateDatabase()) {
          recreateDatabase(influxDb, config.database());
        } else if (config.cleanupMeasurements()) {
          influxDb.setDatabase(config.database());
          dropBenchmarkMeasurements(influxDb, config.database());
        } else {
          influxDb.setDatabase(config.database());
        }
        loadData(influxDb, config, measurementCount);

        printHeader();
        runScenario(
            influxDb,
            config,
            measurementCount,
            "current_select_star",
            index -> currentLatestTimestampQuery(index, config.pointsPerMeasurement()),
            measurementCount
        );
        runScenario(
            influxDb,
            config,
            measurementCount,
            "last_selector",
            InfluxLatestTimestampBenchmarkTest::lastSelectorQuery,
            measurementCount
        );
        runScenario(
            influxDb,
            config,
            measurementCount,
            "batched_last_selector",
            ignored -> batchedLastSelectorQuery(),
            1
        );
      }
    }
  }

  private static void printHeader() {
    System.out.println("measurements,points_per_measurement,total_points,strategy,query_count,total_ms,mean_ms,p50_ms,"
        + "p95_ms");
  }

  private static void runScenario(InfluxDB influxDb,
                                  BenchmarkConfig config,
                                  int measurementCount,
                                  String strategy,
                                  IntFunction<String> queryFactory,
                                  int queryCount) {
    for (int i = 0; i < config.warmupRuns(); i++) {
      executeQueries(influxDb, config.database(), queryFactory, queryCount);
    }

    var durations = new ArrayList<Long>();
    for (int i = 0; i < config.measurementRuns(); i++) {
      durations.add(executeQueries(influxDb, config.database(), queryFactory, queryCount));
    }

    durations.sort(Comparator.naturalOrder());
    long totalNanos = durations.stream().mapToLong(Long::longValue).sum();
    long totalPoints = (long) measurementCount * config.pointsPerMeasurement();
    System.out.printf(Locale.ROOT,
        "%d,%d,%d,%s,%d,%.3f,%.3f,%.3f,%.3f%n",
        measurementCount,
        config.pointsPerMeasurement(),
        totalPoints,
        strategy,
        queryCount,
        toMillis(totalNanos),
        toMillis(totalNanos / config.measurementRuns()),
        toMillis(percentile(durations, 0.50)),
        toMillis(percentile(durations, 0.95)));
  }

  private static long executeQueries(InfluxDB influxDb,
                                     String database,
                                     IntFunction<String> queryFactory,
                                     int queryCount) {
    long startNanos = System.nanoTime();
    for (int i = 0; i < queryCount; i++) {
      var result = influxDb.query(new Query(queryFactory.apply(i), database), TimeUnit.MILLISECONDS);
      assertNoQueryError(result);
    }
    return System.nanoTime() - startNanos;
  }

  private static void assertNoQueryError(QueryResult result) {
    assertFalse(result.hasError(), () -> "Influx query failed: " + result.getError());
    if (result.getResults() != null) {
      result.getResults()
          .forEach(r -> assertFalse(r.hasError(), () -> "Influx query failed: " + r.getError()));
    }
  }

  private static void assertBenchmarkDatabase(BenchmarkConfig config) {
    assertTrue(
        Boolean.getBoolean(ALLOW_NON_BENCHMARK_DATABASE) || config.database().toLowerCase(Locale.ROOT)
            .contains("benchmark"),
        "Refusing to drop and recreate database '"
            + config.database()
            + "'. Use a benchmark database name or set -D"
            + ALLOW_NON_BENCHMARK_DATABASE
            + "=true."
    );
  }

  private static void recreateDatabase(InfluxDB influxDb,
                                       String database) {
    influxDb.query(new Query("DROP DATABASE \"" + database + "\"", ""));
    influxDb.query(new Query("CREATE DATABASE \"" + database + "\"", ""));
    influxDb.setDatabase(database);
  }

  private static void dropBenchmarkMeasurements(InfluxDB influxDb,
                                                String database) {
    assertNoQueryError(influxDb.query(new Query("DROP SERIES FROM /^measure-[0-9]+$/", database)));
  }

  private static void loadData(InfluxDB influxDb,
                               BenchmarkConfig config,
                               int measurementCount) {
    BatchPoints batch = newBatch(config.database(), config.retentionPolicy());
    int pointsInBatch = 0;
    for (int measurementIndex = 0; measurementIndex < measurementCount; measurementIndex++) {
      for (int pointIndex = 0; pointIndex < config.pointsPerMeasurement(); pointIndex++) {
        batch.point(makePoint(measurementIndex, pointIndex, config.fields()));
        pointsInBatch++;
        if (pointsInBatch == config.batchSize()) {
          influxDb.write(batch);
          batch = newBatch(config.database(), config.retentionPolicy());
          pointsInBatch = 0;
        }
      }
    }

    if (pointsInBatch > 0) {
      influxDb.write(batch);
    }
  }

  private static BatchPoints newBatch(String database,
                                      String retentionPolicy) {
    var builder = BatchPoints.database(database)
        .consistency(InfluxDB.ConsistencyLevel.ALL);
    if (retentionPolicy != null && !retentionPolicy.isBlank()) {
      builder.retentionPolicy(retentionPolicy);
    }
    return builder.build();
  }

  private static Point makePoint(int measurementIndex,
                                 int pointIndex,
                                 int fields) {
    var point = Point.measurement(measurementName(measurementIndex))
        .time(BASE_TIMESTAMP + pointIndex * 1000L, TimeUnit.MILLISECONDS)
        .tag("device", "device-" + measurementIndex)
        .tag("site", "site-" + measurementIndex % 10)
        .addField("value", pointIndex);

    for (int fieldIndex = 1; fieldIndex < fields; fieldIndex++) {
      point.addField("value_" + fieldIndex, pointIndex + fieldIndex);
    }

    return point.build();
  }

  private static String currentLatestTimestampQuery(int measurementIndex,
                                                    int pointsPerMeasurement) {
    long endTimestamp = (BASE_TIMESTAMP + pointsPerMeasurement * 1000L + 1000L) * 1000000L;
    return "SELECT * FROM \"" + measurementName(measurementIndex)
        + "\" WHERE time > 0 AND time < "
        + endTimestamp
        + " ORDER BY time DESC LIMIT 1";
  }

  private static String lastSelectorQuery(int measurementIndex) {
    return "SELECT LAST(\"value\") FROM \"" + measurementName(measurementIndex) + "\"";
  }

  private static String batchedLastSelectorQuery() {
    return "SELECT LAST(\"value\") FROM /^measure-[0-9]+$/";
  }

  private static String measurementName(int measurementIndex) {
    return "measure-" + measurementIndex;
  }

  private static long percentile(List<Long> durations,
                                 double percentile) {
    int index = (int) Math.ceil(percentile * durations.size()) - 1;
    return durations.get(Math.max(index, 0));
  }

  private static double toMillis(long durationNanos) {
    return durationNanos / 1_000_000.0;
  }

  private record BenchmarkConfig(String url,
                                 String username,
                                 String password,
                                 String token,
                                 String database,
                                 String retentionPolicy,
                                 boolean recreateDatabase,
                                 boolean cleanupMeasurements,
                                 List<Integer> measurementCounts,
                                 int pointsPerMeasurement,
                                 int fields,
                                 int batchSize,
                                 int warmupRuns,
                                 int measurementRuns) {

    static BenchmarkConfig fromSystemProperties() {
      return new BenchmarkConfig(
          System.getProperty(URL, "http://localhost:8086"),
          System.getProperty(USERNAME, ""),
          System.getProperty(PASSWORD, ""),
          System.getProperty(TOKEN, ""),
          System.getProperty(DATABASE, "streampipes_benchmark"),
          System.getProperty(RETENTION_POLICY, ""),
          Boolean.parseBoolean(System.getProperty(RECREATE_DATABASE, "true")),
          Boolean.parseBoolean(System.getProperty(CLEANUP_MEASUREMENTS, "true")),
          getIntList(MEASUREMENTS, "10,100"),
          getInt(POINTS_PER_MEASUREMENT, 1000),
          getInt(FIELDS, 3),
          getInt(BATCH_SIZE, 5000),
          getInt(WARMUP_RUNS, 2),
          getInt(MEASUREMENT_RUNS, 5)
      );
    }

    InfluxDB connect() {
      if (token != null && !token.isBlank()) {
        return InfluxDBFactory.connect(url, InfluxClientUtils.getHttpClientBuilder(token));
      } else if (username == null || username.isBlank()) {
        return InfluxDBFactory.connect(url);
      } else {
        return InfluxDBFactory.connect(url, username, password);
      }
    }

    private static int getInt(String property,
                              int defaultValue) {
      return Integer.parseInt(System.getProperty(property, String.valueOf(defaultValue)));
    }

    private static List<Integer> getIntList(String property,
                                            String defaultValue) {
      return Arrays.stream(System.getProperty(property, defaultValue).split(","))
          .map(String::trim)
          .filter(value -> !value.isBlank())
          .map(Integer::parseInt)
          .toList();
    }
  }
}
