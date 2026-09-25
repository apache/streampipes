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

import org.apache.streampipes.dataexplorer.api.query.DatasetAdministrationBackend;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryStatus;

import java.util.List;
import java.util.Map;

public final class InfluxAdministrationBackend implements DatasetAdministrationBackend {
  private final DataExplorerInfluxQueryExecutor executor;

  public InfluxAdministrationBackend(DataExplorerInfluxQueryExecutor executor) {
    this.executor = executor;
  }

  @Override
  public boolean delete(DatasetMetadata dataset) {
    return executor.deleteData(dataset);
  }

  @Override
  public boolean deleteRange(DatasetMetadata dataset, Long startExclusive, Long endExclusive) {
    return executor.executeQuery(new DeleteQueryParams(dataset.getMeasureName(), startExclusive, endExclusive))
        .getSpQueryStatus() == SpQueryStatus.OK;
  }

  @Override
  public Map<String, Object> dimensionValues(DatasetMetadata dataset, List<String> fields) {
    return executor.getTagValues(dataset.getMeasureName(), String.join(",", fields));
  }
}
