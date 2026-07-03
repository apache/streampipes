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

package org.apache.streampipes.rest.impl.datalake;

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Tag("benchmark")
class DataLakeLatestEventsBenchmarkTest {

  private static final List<Integer> MEASUREMENT_COUNTS = List.of(10, 100, 1000);
  private static final int WARMUP_RUNS = 2;
  private static final int MEASUREMENT_RUNS = 5;

  @Test
  void benchmarkLatestEventsFanOut() throws Exception {
    System.out.println("measurement_count,mean_duration_ms,query_management_calls");
    for (int measurementCount : MEASUREMENT_COUNTS) {
      for (int i = 0; i < WARMUP_RUNS; i++) {
        runLatestEvents(measurementCount);
      }

      long totalDurationNanos = 0L;
      for (int i = 0; i < MEASUREMENT_RUNS; i++) {
        totalDurationNanos += runLatestEvents(measurementCount);
      }

      var meanDurationMillis = TimeUnit.NANOSECONDS.toMicros(totalDurationNanos / MEASUREMENT_RUNS) / 1000.0;
      System.out.printf(Locale.ROOT, "%d,%.3f,%d%n", measurementCount, meanDurationMillis, 1);
    }
  }

  private static long runLatestEvents(int measurementCount) throws Exception {
    var queryManagement = mock(IDataExplorerQueryManagement.class);
    var measurementNames = measurementNames(measurementCount);
    when(queryManagement.getLatestTimestamps(measurementNames)).thenReturn(latestTimestamps(measurementNames));
    var resource = dataLakeResource(queryManagement);

    long startNanos = System.nanoTime();
    var response = resource.getLatestEvents(measurementNames);
    long durationNanos = System.nanoTime() - startNanos;

    assertEquals(measurementCount, ((java.util.Map<?, ?>) response.getBody()).size());
    verify(queryManagement).getLatestTimestamps(measurementNames);
    verifyNoMoreInteractions(queryManagement);
    return durationNanos;
  }

  private static List<String> measurementNames(int measurementCount) {
    return java.util.stream.IntStream.range(0, measurementCount)
        .mapToObj(i -> "measure-" + i)
        .toList();
  }

  private static java.util.Map<String, Long> latestTimestamps(List<String> measurementNames) {
    var latestTimestamps = new HashMap<String, Long>();
    measurementNames.forEach(measurementName -> latestTimestamps.put(measurementName, (long) measurementName.length()));
    return latestTimestamps;
  }

  private static DataLakeResource dataLakeResource(IDataExplorerQueryManagement queryManagement) throws Exception {
    var resource = mock(DataLakeResource.class, CALLS_REAL_METHODS);
    doReturn(true).when(resource).checkPermissionByName(any(), eq("READ"));
    setQueryManagement(resource, queryManagement);
    return resource;
  }

  private static void setQueryManagement(DataLakeResource resource,
                                         IDataExplorerQueryManagement queryManagement) throws Exception {
    Field field = DataLakeResource.class.getDeclaredField("dataExplorerQueryManagement");
    field.setAccessible(true);
    field.set(resource, queryManagement);
  }
}
