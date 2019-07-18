/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.streampipes.storage.couchdb;

import org.streampipes.storage.api.*;
import org.streampipes.storage.couchdb.impl.*;

public enum CouchDbStorageManager implements INoSqlStorage {

  INSTANCE;

  @Override
  public IPipelineStorage getPipelineStorageAPI() {
    return new PipelineStorageImpl();
  }

  @Override
  public IPipelineElementConnectionStorage getConnectionStorageApi() {
    return new ConnectionStorageImpl();
  }

  @Override
  public IUserStorage getUserStorageAPI() {
    return new UserStorage();
  }

  @Override
  public IPipelineMonitoringDataStorage getMonitoringDataStorageApi() {
    return new MonitoringDataStorageImpl();
  }

  @Override
  public INotificationStorage getNotificationStorageApi() {
    return new NotificationStorageImpl();
  }

  @Override
  public IPipelineCategoryStorage getPipelineCategoryStorageApi() {
    return new PipelineCategoryStorageImpl();
  }

  @Override
  public IVisualizationStorage getVisualizationStorageApi() {
    return new VisualizationStorageImpl();
  }

  @Override
  public IRdfEndpointStorage getRdfEndpointStorage() {
    return new RdfEndpointStorageImpl();
  }

  @Override
  public IAssetDashboardStorage getAssetDashboardStorage() {
    return new AssetDashboardStorageImpl();
  }

  @Override
  public IDataLakeStorage getDataLakeStorage() {
    return new DataLakeStorageImpl();
  }
}
