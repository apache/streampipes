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

package org.apache.streampipes.dataexplorer.management;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.api.IDataExplorerManager;
import org.apache.streampipes.dataexplorer.api.IDatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.influx.DataExplorerManagerInflux;
import org.apache.streampipes.dataexplorer.iotdb.DataExplorerManagerIotDb;
import org.apache.streampipes.manager.permission.DatasetPermissionManager;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import java.util.Map;
import java.util.function.Supplier;

public class DataExplorerDispatcher {
  private final Map<String, Supplier<IDataExplorerManager>> providers;
  private final String selected;

  public DataExplorerDispatcher() {
    this(Environments.getEnvironment().getTsStorage().getValueOrDefault(), Map.of(
        SupportedDataExplorerStorages.INFLUX_DB, DataExplorerManagerInflux::new,
        SupportedDataExplorerStorages.IOT_DB, DataExplorerManagerIotDb::new));
  }

  public DataExplorerDispatcher(String selected,
                                Map<String, Supplier<IDataExplorerManager>> providers) {
    this.selected = selected;
    this.providers = Map.copyOf(providers);
    if (!this.providers.containsKey(selected)) {
      throw new IllegalArgumentException("Unknown data explorer storage: " + selected);
    }
  }

  public IDataExplorerManager getDataExplorerManager() {
    return providers.get(selected).get();
  }

  public IDatasetMetadataManagement getSchemaManagement(ChartSchemaUpdateCoordinator charts,
                                                        IPermissionStorage permissions,
                                                        IDatasetMetadataStorage datasets) {
    return new DatasetMetadataManagement(datasets, new DatasetPermissionManager(permissions), charts);
  }

  public DatasetServices getDatasetServices(
      IDatasetMetadataManagement catalog) {
    var provider = getDataExplorerManager();
    var queries = new DatasetQueryService(catalog, provider.getQueryBackend());
    return new DatasetServices(queries, new DatasetAdministrationService(catalog, provider.getAdministrationBackend()),
        new DatasetExportService(queries));
  }
}
