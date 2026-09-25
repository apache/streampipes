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

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryBatch;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

final class IotDbQueryCursor implements DatasetQueryCursor {
  private final SessionDataSetWrapper result;
  private final List<String> columns;
  private boolean closed;
  private boolean schemaPending = true;

  IotDbQueryCursor(SessionDataSetWrapper result) {
    this.result = result;
    try {
      var nativeColumns = result.getColumnNames();
      columns = new ArrayList<>();
      columns.add("time");
      int start = !nativeColumns.isEmpty() && "Time".equalsIgnoreCase(nativeColumns.getFirst()) ? 1 : 0;
      for (int i = start; i < nativeColumns.size(); i++) {
        columns.add(DataExplorerIotDbQueryExecutor.normalizeColumn(nativeColumns.get(i)));
      }
    } catch (RuntimeException e) {
      close();
      throw e;
    }
  }

  @Override
  public boolean hasNext() {
    if (closed) {
      return false;
    }
    try {
      if (result.hasNext() || schemaPending) {
        return true;
      }
      close();
      return false;
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      close();
      throw new QueryExecutionException("Could not read IoTDB results", e);
    }
  }

  @Override
  public QueryBatch next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    var rows = new ArrayList<List<Object>>();
    try {
      schemaPending = false;
      while (rows.size() < 1000 && result.hasNext()) {
        var record = result.next();
        var row = new ArrayList<Object>();
        row.add(record.getTimestamp());
        for (var field : record.getFields()) {
          if (field == null || field.getDataType() == null) {
            row.add(null);
          } else {
            var value = field.getObjectValue(field.getDataType());
            row.add(value instanceof org.apache.tsfile.utils.Binary ? field.getStringValue() : value);
          }
        }
        rows.add(row);
      }
      return new QueryBatch(columns, Map.of(), rows);
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      close();
      throw new QueryExecutionException("Could not read IoTDB results", e);
    }
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      result.close();
    }
  }
}
