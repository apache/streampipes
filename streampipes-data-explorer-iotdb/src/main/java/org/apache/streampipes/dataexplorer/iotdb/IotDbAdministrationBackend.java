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

import org.apache.streampipes.dataexplorer.api.query.DatasetAdministrationBackend;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class IotDbAdministrationBackend implements DatasetAdministrationBackend {
  private static final Logger LOG = LoggerFactory.getLogger(IotDbAdministrationBackend.class);
  private final SessionPool sessions;

  public IotDbAdministrationBackend(SessionPool sessions) {
    this.sessions = sessions;
  }

  @Override
  public boolean delete(DatasetMetadata dataset) {
    return execute("DELETE TIMESERIES " + IotDbQueryCompiler.datasetPath(dataset.getMeasureName()) + ".*");
  }

  @Override
  public boolean deleteRange(DatasetMetadata dataset, Long startExclusive, Long endExclusive) {
    var conditions = new ArrayList<String>();
    if (startExclusive != null) {
      conditions.add("time > " + startExclusive);
    }
    if (endExclusive != null) {
      conditions.add("time < " + endExclusive);
    }
    String sql = "DELETE FROM " + IotDbQueryCompiler.datasetPath(dataset.getMeasureName()) + ".*";
    if (!conditions.isEmpty()) {
      sql += " WHERE " + String.join(" AND ", conditions);
    }
    return execute(sql);
  }

  private boolean execute(String statement) {
    try {
      sessions.executeNonQueryStatement(statement);
      return true;
    } catch (StatementExecutionException | IoTDBConnectionException e) {
      LOG.error("IoTDB administration failed: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public Map<String, Object> dimensionValues(DatasetMetadata dataset, List<String> fields) {
    throw new UnsupportedOperationException("IoTDB tree storage does not expose dimension values as tags");
  }
}
