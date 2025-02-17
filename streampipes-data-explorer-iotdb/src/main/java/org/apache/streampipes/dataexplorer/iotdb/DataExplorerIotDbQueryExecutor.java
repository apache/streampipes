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

import org.apache.iotdb.isession.pool.SessionDataSetWrapper;
import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
import org.apache.streampipes.dataexplorer.query.DataExplorerQueryExecutor;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    return null;
  }

  @Override
  public SessionDataSetWrapper executeQuery(String query) {
    try (var result = sessionPool.executeQueryStatement(query)) {
      return result;
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
    return "";
  }

  @Override
  protected String makeDeleteQuery(DeleteQueryParams params) {
    LOG.error("This method is not required for IoTDB.");
    return "";
  }

  @Override
  protected String makeSelectQuery(SelectQueryParams params) {
    return "";
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId, String fields) {
    return Map.of();
  }

  @Override
  public boolean deleteData(DataLakeMeasure measure) {
    var deleteTimeSeriesQuery = "DELETE timeseries root.streampipes.%s.*".formatted(measure.getMeasureName());
    return executeNonQueryStatement(deleteTimeSeriesQuery);
  }
}