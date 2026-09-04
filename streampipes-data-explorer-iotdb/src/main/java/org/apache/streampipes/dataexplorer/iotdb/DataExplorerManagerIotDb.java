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

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.DatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerManager;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDatasetMetadataCounter;
import org.apache.streampipes.dataexplorer.api.IDatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.api.IDatasetMetadataSanitizer;
import org.apache.streampipes.dataexplorer.api.ITimeSeriesStorage;
import org.apache.streampipes.dataexplorer.iotdb.sanitize.DatasetMetadataSanitizerIotDb;
import org.apache.streampipes.manager.permission.DatasetPermissionManager;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import java.util.List;

public class DataExplorerManagerIotDb implements IDataExplorerManager {

  @Override
  public IDatasetMetadataCounter getMeasurementCounter(List<DatasetMetadata> allMeasurements,
                                                           List<String> measurementsToCount,
                                                           int daysBack) {
    return new DatasetMetadataCounterIotDb(allMeasurements, measurementsToCount, daysBack);
  }

  @Override
  public IDataExplorerQueryManagement getQueryManagement(IDatasetMetadataManagement datasetMetadataManagement) {
    return new DataExplorerQueryManagementIotDb(
        datasetMetadataManagement,
        new DataExplorerIotDbQueryExecutor(new IotDbSessionProvider().getSessionPool(Environments.getEnvironment()))
    );
  }

  @Override
  public IDatasetMetadataManagement getSchemaManagement(ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator,
                                                           IPermissionStorage permissionStorage,
                                                           IDatasetMetadataStorage datasetStorage) {
    return new DatasetMetadataManagement(
        datasetStorage,
        new DatasetPermissionManager(permissionStorage),
        chartSchemaUpdateCoordinator
    );
  }

  @Override
  public ITimeSeriesStorage getTimeseriesStorage(DatasetMetadata measure, boolean ignoreDuplicates) {
    return new TimeSeriesStorageIotDb(measure, new IotDbPropertyConverter(), new IotDbSessionProvider());
  }

  @Override
  public IDatasetMetadataSanitizer getMeasurementSanitizer(IStreamPipesClient client, DatasetMetadata measure) {
    return new DatasetMetadataSanitizerIotDb(client, measure);
  }
}
