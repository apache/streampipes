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

import org.apache.streampipes.dataexplorer.api.query.DatasetQueryBackend;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCursor;
import org.apache.streampipes.dataexplorer.api.query.QueryCapabilities;
import org.apache.streampipes.dataexplorer.api.query.QueryExecutionException;
import org.apache.streampipes.dataexplorer.api.query.QuerySpec;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.apache.iotdb.session.pool.SessionPool;

public final class IotDbQueryBackend implements DatasetQueryBackend {
  private final SessionPool sessions;
  private final IotDbQueryCompiler compiler = new IotDbQueryCompiler();

  public IotDbQueryBackend(SessionPool sessions) {
    this.sessions = sessions;
  }

  @Override
  public QueryCapabilities capabilities() {
    return compiler.capabilities();
  }

  @Override
  public DatasetQueryCursor open(DatasetMetadata dataset, QuerySpec query) {
    var sql = compiler.compile(query, dataset.getMeasureName());
    try {
      return new IotDbQueryCursor(sessions.executeQueryStatement(sql));
    } catch (IoTDBConnectionException | StatementExecutionException e) {
      throw new QueryExecutionException("IoTDB query failed", e);
    }
  }
}
