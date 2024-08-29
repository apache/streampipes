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

import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.storage.api.CRUDStorage;

public class SpResourceManager {

  public AdapterResourceManager manageAdapters() {
    return new AdapterResourceManager();
  }

  public AdapterDescriptionResourceManager manageAdapterDescriptions() {
    return new AdapterDescriptionResourceManager();
  }

  public DashboardResourceManager manageDashboards() {
    return new DashboardResourceManager();
  }

  public DataExplorerResourceManager manageDataExplorer() {
    return new DataExplorerResourceManager();
  }

  public DataExplorerWidgetResourceManager manageDataExplorerWidget(DataExplorerResourceManager dashboardManager,
                                                                    CRUDStorage<DataExplorerWidgetModel> db) {
    return new DataExplorerWidgetResourceManager(dashboardManager, db);
  }

  public DataProcessorResourceManager manageDataProcessors() {
    return new DataProcessorResourceManager();
  }

  public DataSinkResourceManager manageDataSinks() {
    return new DataSinkResourceManager();
  }

  public DataStreamResourceManager manageDataStreams() {
    return new DataStreamResourceManager();
  }

  public PipelineResourceManager managePipelines() {
    return new PipelineResourceManager();
  }

  public PermissionResourceManager managePermissions() {
    return new PermissionResourceManager();
  }

  public UserResourceManager manageUsers() {
    return new UserResourceManager();
  }
}
