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

import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.read.common.Field;
import org.apache.tsfile.read.common.RowRecord;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class IotDbQueryCursorTest {
  @Test
  void retainsDuplicateTimestampsAcrossBoundedBatchesAndClosesOnce() throws Exception {
    var nativeCursor = mock(SessionDataSetWrapper.class);
    when(nativeCursor.getColumnNames()).thenReturn(List.of("Time", "root.streampipes.machine.value"));
    var remaining = new AtomicInteger(1001);
    when(nativeCursor.hasNext()).thenAnswer(invocation -> remaining.get() > 0);
    when(nativeCursor.next()).thenAnswer(invocation -> {
      remaining.decrementAndGet();
      return new RowRecord(123L, List.of(Field.getField(42L, TSDataType.INT64)));
    });
    try (var cursor = new IotDbQueryCursor(nativeCursor)) {
      var batch = cursor.next();
      assertEquals(List.of("time", "value"), batch.columns());
      assertEquals(1000, batch.rows().size());
      assertEquals(1, cursor.next().rows().size());
      assertFalse(cursor.hasNext());
    }
    verify(nativeCursor).close();
  }

  @Test
  void emptyResultsStillExposeTheirSchema() {
    var nativeCursor = mock(SessionDataSetWrapper.class);
    when(nativeCursor.getColumnNames()).thenReturn(List.of("Time", "root.streampipes.machine.value"));
    try (var cursor = new IotDbQueryCursor(nativeCursor)) {
      assertTrue(cursor.hasNext());
      var batch = cursor.next();
      assertEquals(List.of("time", "value"), batch.columns());
      assertTrue(batch.rows().isEmpty());
      assertFalse(cursor.hasNext());
    }
    verify(nativeCursor).close();
  }

  @Test
  void readFailureClosesTheNativeCursor() throws Exception {
    var nativeCursor = mock(SessionDataSetWrapper.class);
    when(nativeCursor.getColumnNames()).thenReturn(List.of("Time", "value"));
    when(nativeCursor.hasNext()).thenThrow(new StatementExecutionException("failed"));
    var cursor = new IotDbQueryCursor(nativeCursor);
    assertThrows(QueryExecutionException.class, cursor::hasNext);
    verify(nativeCursor).close();
  }
}
