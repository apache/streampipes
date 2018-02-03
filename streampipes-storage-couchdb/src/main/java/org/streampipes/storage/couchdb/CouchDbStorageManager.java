/*
Copyright 2018 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.storage.couchdb;

import org.streampipes.storage.api.INoSqlStorage;
import org.streampipes.storage.api.INotificationStorage;
import org.streampipes.storage.api.IPipelineCategoryStorage;
import org.streampipes.storage.api.IPipelineElementConnectionStorage;
import org.streampipes.storage.api.IPipelineMonitoringDataStorage;
import org.streampipes.storage.api.IPipelineStorage;
import org.streampipes.storage.api.IRdfEndpointStorage;
import org.streampipes.storage.api.IUserStorage;
import org.streampipes.storage.api.IVisualizationStorage;
import org.streampipes.storage.couchdb.impl.ConnectionStorageImpl;
import org.streampipes.storage.couchdb.impl.MonitoringDataStorageImpl;
import org.streampipes.storage.couchdb.impl.NotificationStorageImpl;
import org.streampipes.storage.couchdb.impl.PipelineCategoryStorageImpl;
import org.streampipes.storage.couchdb.impl.PipelineStorageImpl;
import org.streampipes.storage.couchdb.impl.RdfEndpointStorageImpl;
import org.streampipes.storage.couchdb.impl.UserStorage;
import org.streampipes.storage.couchdb.impl.VisualizationStorageImpl;

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
}
