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
package org.apache.streampipes.resource.management;

import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

public class SpResourceManager {

  private final IPermissionStorage permissionStorage;
  private final IDataExplorerWidgetStorage chartStorage;

  public SpResourceManager(IPermissionStorage permissionStorage,
                           IDataExplorerWidgetStorage chartStorage) {
    this.permissionStorage = permissionStorage;
    this.chartStorage = chartStorage;
  }

  public AdapterDescriptionResourceManager manageAdapterDescriptions() {
    return new AdapterDescriptionResourceManager(managePermissions());
  }

  public DataSinkResourceManager manageDataSinks() {
    return new DataSinkResourceManager(managePermissions());
  }

  public DataProcessorResourceManager manageDataProcessors() {
    return new DataProcessorResourceManager(managePermissions());
  }

  public DataStreamResourceManager manageDataStreams() {
    return new DataStreamResourceManager(managePermissions());
  }

  public AssetResourceManager manageAssets() {
    return new AssetResourceManager();
  }

  public AdapterResourceManager manageAdapters() {
    return new AdapterResourceManager();
  }

  public DataLakeMeasureResourceManager manageDataLakeMeasures() {
    return new DataLakeMeasureResourceManager();
  }

  public PermissionResourceManager managePermissions() {
    return new PermissionResourceManager(permissionStorage);
  }

  public DataExplorerResourceManager manageDashboards() {
    return new DataExplorerResourceManager(chartStorage, managePermissions());
  }

  public DataExplorerWidgetResourceManager manageCharts() {
    return new DataExplorerWidgetResourceManager(manageDashboards(), chartStorage,  managePermissions());
  }

  public PipelineResourceManager managePipelines() {
    return new PipelineResourceManager(
        StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI(), managePermissions()
    );
  }

  public UserResourceManager manageUsers() {
    return new UserResourceManager();
  }
}
