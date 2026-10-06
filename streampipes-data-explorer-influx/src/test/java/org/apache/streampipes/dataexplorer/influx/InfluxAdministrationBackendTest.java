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

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InfluxAdministrationBackendTest {
  @Test
  void rangeDeletionPreservesExclusiveMillisecondBoundsWithoutOverflow() {
    var client = mock(InfluxDB.class);
    when(client.query(any(Query.class))).thenReturn(new QueryResult());
    assertTrue(new InfluxAdministrationBackend("db", () -> client)
        .deleteRange(dataset(), 100L, Long.MAX_VALUE));
    var query = ArgumentCaptor.forClass(Query.class);
    verify(client).query(query.capture());
    assertEquals("DELETE FROM \"machine\" WHERE time > 100ms AND time < 9223372036854775807ms",
        query.getValue().getCommand());
    assertEquals("db", query.getValue().getDatabase());
    assertTrue(query.getValue().requiresPost());
    verify(client).close();
  }

  @Test
  void absentBoundsAreOmitted() {
    var client = mock(InfluxDB.class);
    when(client.query(any(Query.class))).thenReturn(new QueryResult());
    assertTrue(new InfluxAdministrationBackend("db", () -> client).deleteRange(dataset(), null, 300L));
    var query = ArgumentCaptor.forClass(Query.class);
    verify(client).query(query.capture());
    assertEquals("DELETE FROM \"machine\" WHERE time < 300ms", query.getValue().getCommand());
  }

  @Test
  void errorsInAnyStatementAreReportedAndClientIsReleased() {
    var client = mock(InfluxDB.class);
    var nativeResult = new QueryResult();
    var failed = new QueryResult.Result();
    failed.setError("failed");
    nativeResult.setResults(List.of(new QueryResult.Result(), failed));
    when(client.query(any(Query.class))).thenReturn(nativeResult);
    assertFalse(new InfluxAdministrationBackend("db", () -> client).delete(dataset()));
    verify(client).close();
  }

  @Test
  void dimensionErrorsAreNotReportedAsEmptySuccess() {
    var client = mock(InfluxDB.class);
    var nativeResult = new QueryResult();
    nativeResult.setError("failed");
    when(client.query(any(Query.class))).thenReturn(nativeResult);
    assertThrows(QueryExecutionException.class,
        () -> new InfluxAdministrationBackend("db", () -> client).dimensionValues(dataset(), List.of("location")));
    verify(client).close();
  }

  @Test
  void dimensionFieldContainingCommaRemainsOneIdentifier() {
    var client = mock(InfluxDB.class);
    var nativeResult = new QueryResult();
    var statement = new QueryResult.Result();
    var series = new QueryResult.Series();
    series.setValues(List.of(List.of("site,name", "Berlin"), List.of("site,name", "Karlsruhe")));
    statement.setSeries(List.of(series));
    nativeResult.setResults(List.of(statement));
    when(client.query(any(Query.class))).thenReturn(nativeResult);
    var values = new InfluxAdministrationBackend("db", () -> client)
        .dimensionValues(dataset(), List.of("site,name"));
    assertEquals(List.of("Berlin", "Karlsruhe"), values.get("site,name"));
    var query = ArgumentCaptor.forClass(Query.class);
    verify(client).query(query.capture());
    assertEquals("SHOW TAG VALUES ON \"db\" FROM \"machine\" WITH KEY = \"site,name\"",
        query.getValue().getCommand());
    verify(client).close();
  }

  private DatasetMetadata dataset() {
    var dataset = new DatasetMetadata();
    dataset.setMeasureName("machine");
    return dataset;
  }
}
