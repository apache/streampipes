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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryBatch;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionOptions;
import org.apache.streampipes.dataexplorer.api.query.QueryTimestampFormat;
import org.apache.streampipes.model.dataset.SpQueryStatus;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QueryResultCollectorTest {
  @Test
  void formatsOnlyRetainedRowsAndKeepsNativeLastTimestampAcrossChunks() {
    var first = new QueryBatch(List.of("time", "value"), Map.of(),
        List.of(Arrays.asList(1003L, null), List.of(1002L, 42)));
    var second = new QueryBatch(List.of("time", "value"), Map.of(), List.of(List.of(1001L, 43)));
    var result = QueryResultCollector.collect(cursor(first, second),
        new QueryExecutionOptions(true, OptionalInt.empty(), false).withTimestampFormat(QueryTimestampFormat.RFC3339));
    assertEquals(2, result.getTotal());
    assertEquals(1, result.getAllDataSeries().size());
    assertNull(result.getAllDataSeries().getFirst().getTags());
    assertEquals("1970-01-01T00:00:01.002Z", result.getAllDataSeries().getFirst().getRows().getFirst().getFirst());
    assertEquals(1001L, result.getLastTimestamp());
    assertEquals(1002L, first.rows().get(1).getFirst());
  }

  @Test
  void identityKeepsTimestampObjectsAndRowsRemainIndependentlyMutable() {
    Long timestamp = Long.valueOf(123456789L);
    var batch = new QueryBatch(List.of("time"), null, List.of(List.of(timestamp)));
    var result = QueryResultCollector.collect(cursor(batch), QueryExecutionOptions.defaults());
    var row = result.getAllDataSeries().getFirst().getRows().getFirst();
    assertSame(timestamp, row.getFirst());
    row.set(0, 0L);
    assertEquals(timestamp, batch.rows().getFirst().getFirst());
  }

  @Test
  void limitsBeforeMappingExcessRowsAndDoesNotCreateFilteredSeries() {
    var batch = new QueryBatch(List.of("time", "value"), Map.of("machine", "a"),
        List.of(Arrays.asList(1000L, null), List.of(1001L, 1), List.of(1002L, 2)));
    var result = QueryResultCollector.collect(cursor(batch),
        new QueryExecutionOptions(true, OptionalInt.of(1), false).withTimestampFormat(QueryTimestampFormat.EPOCH_SECONDS));
    assertEquals(SpQueryStatus.TOO_MUCH_DATA, result.getSpQueryStatus());
    var filtered = new QueryBatch(List.of("time", "value"), Map.of(), List.of(Arrays.asList(1000L, null)));
    result = QueryResultCollector.collect(cursor(filtered),
        new QueryExecutionOptions(true, OptionalInt.empty(), false));
    assertEquals(0, result.getAllDataSeries().size());
    assertEquals(1000L, result.getLastTimestamp());
  }

  @Test
  void batchesDefendExternalRowsAndExposeOwnedRowsReadOnly() {
    var row = new ArrayList<Object>(Arrays.asList(1L, null));
    var rows = new ArrayList<List<Object>>();
    rows.add(row);
    var copied = new QueryBatch(List.of("time", "value"), null, rows);
    row.set(0, 2L);
    assertEquals(1L, copied.rows().getFirst().getFirst());
    var owned = QueryBatch.takeOwnership(List.of("time", "value"), null, rows);
    rows.clear();
    assertEquals(1, owned.rows().size());
    assertNull(owned.rows().getFirst().get(1));
    assertThrows(UnsupportedOperationException.class, () -> owned.rows().getFirst().set(0, 3L));
    assertThrows(UnsupportedOperationException.class, () -> owned.rows().clear());
    assertThrows(IllegalArgumentException.class,
        () -> QueryBatch.takeOwnership(List.of("time"), null, List.of(List.of(1L, 2L))));
  }

  @Test
  void passesThroughNativeIsoAndComputesMetadataFromTheLastValuePerSeries() {
    String precise = "2026-09-09T00:00:00.123456789Z";
    var batch = new QueryBatch(List.of("time", "value"), Map.of(),
        List.of(List.of("2026-09-09T00:00:00.1Z", 1), List.of(precise, 2)));
    var result = QueryResultCollector.collect(cursor(QueryTimestampFormat.RFC3339, batch),
        QueryExecutionOptions.defaults().withTimestampFormat(QueryTimestampFormat.RFC3339));
    assertEquals(1788912000123L, result.getLastTimestamp());
    assertSame(precise, result.getAllDataSeries().getFirst().getRows().getLast().getFirst());
    assertEquals("2026-09-09T00:00:00.1Z", result.getAllDataSeries().getFirst().getRows().getFirst().getFirst());
  }

  @Test
  void convertsFallbackFormatsAndLastTimestampUsingOutputPrecision() {
    var batch = new QueryBatch(List.of("time"), Map.of(), List.of(List.of(1999L)));
    var result = QueryResultCollector.collect(cursor(batch),
        QueryExecutionOptions.defaults().withTimestampFormat(QueryTimestampFormat.EPOCH_SECONDS));
    assertEquals(1L, result.getAllDataSeries().getFirst().getRows().getFirst().getFirst());
    assertEquals(1000L, result.getLastTimestamp());
    var seconds = new QueryBatch(List.of("time"), Map.of(), List.of(List.of(1L)));
    result = QueryResultCollector.collect(cursor(QueryTimestampFormat.EPOCH_SECONDS, seconds),
        QueryExecutionOptions.defaults().withTimestampFormat(QueryTimestampFormat.EPOCH_SECONDS));
    assertEquals(1000L, result.getLastTimestamp());
  }

  private DatasetQueryCursor cursor(QueryBatch... batches) {
    return cursor(QueryTimestampFormat.EPOCH_MILLIS, batches);
  }

  private DatasetQueryCursor cursor(QueryTimestampFormat format, QueryBatch... batches) {
    var iterator = Arrays.asList(batches).iterator();
    return new DatasetQueryCursor() {
      public QueryTimestampFormat timestampFormat() {
        return format;
      }

      public boolean hasNext() {
        return iterator.hasNext();
      }

      public QueryBatch next() {
        return iterator.next();
      }

      public void close() {
      }
    };
  }
}
