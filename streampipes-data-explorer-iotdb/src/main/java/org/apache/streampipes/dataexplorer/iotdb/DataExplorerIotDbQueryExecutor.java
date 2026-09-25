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
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCompiler;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.query.DataExplorerQueryExecutor;
import org.apache.streampipes.model.dataset.DataSeries;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryResult;

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DataExplorerIotDbQueryExecutor extends DataExplorerQueryExecutor<String, SessionDataSetWrapper> {

  private final SessionPool sessionPool;

  public DataExplorerIotDbQueryExecutor(SessionPool sessionPool){
    this.sessionPool = sessionPool;
  }

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerIotDbQueryExecutor.class);
  @Override
  protected SpQueryResult postQuery(SessionDataSetWrapper queryResult, Optional<String> forIdOpt, boolean ignoreMissingValues) {
    try (queryResult) {
      List<String> nativeColumns = queryResult.getColumnNames();
      boolean hasTime = !nativeColumns.isEmpty() && "Time".equalsIgnoreCase(nativeColumns.get(0));
      List<String> headers = new ArrayList<>();
      headers.add("time");
      for (int i = hasTime ? 1 : 0; i < nativeColumns.size(); i++) {
        headers.add(normalizeColumn(nativeColumns.get(i)));
      }
      List<List<Object>> rows = new ArrayList<>();
      long lastTimestamp = 0;
      while (queryResult.hasNext()) {
        var record = queryResult.next();
        List<Object> row = new ArrayList<>();
        row.add(record.getTimestamp());
        for (var field : record.getFields()) {
          if (field == null || field.getDataType() == null) {
            row.add(null);
          } else {
            var value = field.getObjectValue(field.getDataType());
            row.add(value instanceof org.apache.tsfile.utils.Binary ? field.getStringValue() : value);
          }
        }
        // Match the legacy result's last-row timestamp used for export continuation.
        lastTimestamp = record.getTimestamp();
        if (!ignoreMissingValues || !row.contains(null)) {
          rows.add(row);
        }
      }
      var result = new SpQueryResult();
      result.setHeaders(headers);
      result.setTotal(rows.size());
      result.setLastTimestamp(lastTimestamp);
      if (!rows.isEmpty()) {
        result.addDataResult(new DataSeries(rows.size(), rows, headers, Map.of()));
      }
      forIdOpt.ifPresent(result::setForId);
      return result;
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      throw new SpRuntimeException(e);
    }
  }

  @Override
  public SessionDataSetWrapper executeQuery(String query) {
    try {
      return sessionPool.executeQueryStatement(query);
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      throw new SpRuntimeException(e);
    }
  }

  public boolean executeNonQueryStatement(String statement) {
    try {
      sessionPool.executeNonQueryStatement(statement);
    } catch (StatementExecutionException | IoTDBConnectionException e) {
      LOG.error("Error while executing non-query statement '{}': {}", statement, e.getMessage());
      return false;
    }
    return true;
  }

  @Override
  protected String asQueryString(String query) {
    return query;
  }

  @Override
  protected String makeDeleteQuery(DeleteQueryParams params) {
    var sql = "DELETE FROM " + IotDbQueryCompiler.datasetPath(params.measurementName()) + ".*";
    return params.timeRestricted()
        ? sql + " WHERE time > " + params.startTime() + " AND time < " + params.endTime() : sql;
  }

  @Override
  public SpQueryResult executeQuery(DeleteQueryParams params) {
    if (!executeNonQueryStatement(makeDeleteQuery(params))) {
      throw new SpRuntimeException("IoTDB data deletion failed");
    }
    return new SpQueryResult();
  }

  @Override
  protected DatasetQueryCompiler<String> queryCompiler() {
    return new IotDbQueryCompiler();
  }

  private String normalizeColumn(String column) {
    String prefix = "root.streampipes.";
    if (!column.startsWith(prefix)) {
      return column;
    }
    boolean quoted = false;
    for (int i = prefix.length(); i < column.length(); i++) {
      char character = column.charAt(i);
      if (character == '`') {
        if (quoted && i + 1 < column.length() && column.charAt(i + 1) == '`') {
          i++;
        } else {
          quoted = !quoted;
        }
      } else if (character == '.' && !quoted) {
        var field = column.substring(i + 1);
        return field.startsWith("`") && field.endsWith("`")
            ? field.substring(1, field.length() - 1).replace("``", "`") : field;
      }
    }
    return column;
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId, String fields) {
    throw new UnsupportedOperationException("IoTDB tree storage does not expose dimension values as tags");
  }

  @Override
  public boolean deleteData(DatasetMetadata measure) {
    var deleteTimeSeriesQuery = "DELETE TIMESERIES " + IotDbQueryCompiler.datasetPath(measure.getMeasureName()) + ".*";
    return executeNonQueryStatement(deleteTimeSeriesQuery);
  }
}
