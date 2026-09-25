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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.tsfile.enums.TSDataType;
import org.apache.tsfile.read.common.Field;
import org.apache.tsfile.read.common.RowRecord;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DataExplorerIotDbQueryExecutorTest {
  @Test
  void nativeResultStaysOpenUntilItsOwnerClosesIt() throws Exception {
    var pool = mock(SessionPool.class);
    var cursor = mock(SessionDataSetWrapper.class);
    when(pool.executeQueryStatement("SELECT *")).thenReturn(cursor);
    var executor = new DataExplorerIotDbQueryExecutor(pool);
    assertSame(cursor, executor.executeQuery("SELECT *"));
    verify(cursor, never()).close();
  }

  @Test
  void convertsRowsAndClosesCursor() throws Exception {
    var cursor = mock(SessionDataSetWrapper.class);
    when(cursor.getColumnNames()).thenReturn(List.of("Time", "root.streampipes.machine.value"));
    when(cursor.hasNext()).thenReturn(true, false);
    when(cursor.next()).thenReturn(new RowRecord(123L, List.of(Field.getField(42L, TSDataType.INT64))));
    var result = new DataExplorerIotDbQueryExecutor(mock(SessionPool.class))
        .postQuery(cursor, Optional.of("widget"), false);
    assertEquals(List.of("time", "value"), result.getHeaders());
    assertEquals(List.of(123L, 42L), result.getAllDataSeries().getFirst().getRows().getFirst());
    assertEquals(123L, result.getLastTimestamp());
    assertEquals("widget", result.getForId());
    verify(cursor).close();
  }

  @Test
  void closesCursorOnIterationFailure() throws Exception {
    var cursor = mock(SessionDataSetWrapper.class);
    when(cursor.getColumnNames()).thenReturn(List.of("Time", "value"));
    when(cursor.hasNext()).thenThrow(new StatementExecutionException("failed"));
    assertThrows(SpRuntimeException.class, () -> new DataExplorerIotDbQueryExecutor(mock(SessionPool.class))
        .postQuery(cursor, Optional.empty(), false));
    verify(cursor).close();
  }

  @Test
  void missingValuesAreFilteredWithoutLosingPaginationProgress() throws Exception {
    var cursor = mock(SessionDataSetWrapper.class);
    when(cursor.getColumnNames()).thenReturn(List.of("Time", "root.streampipes.machine.`temperature.a`"));
    when(cursor.hasNext()).thenReturn(true, false);
    when(cursor.next()).thenReturn(new RowRecord(123L, Arrays.asList((Field) null)));
    var result = new DataExplorerIotDbQueryExecutor(mock(SessionPool.class))
        .postQuery(cursor, Optional.empty(), true);
    assertEquals(List.of("time", "temperature.a"), result.getHeaders());
    assertEquals(0, result.getTotal());
    assertEquals(123L, result.getLastTimestamp());
    verify(cursor).close();
  }

  @Test
  void emptyResultsRetainSchema() throws Exception {
    var cursor = mock(SessionDataSetWrapper.class);
    when(cursor.getColumnNames()).thenReturn(List.of("Time", "value"));
    var result = new DataExplorerIotDbQueryExecutor(mock(SessionPool.class))
        .postQuery(cursor, Optional.empty(), false);
    assertEquals(List.of("time", "value"), result.getHeaders());
    assertEquals(0, result.getTotal());
    verify(cursor).close();
  }
}
