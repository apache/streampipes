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
package org.apache.streampipes.storage.couchdb;

import org.apache.streampipes.storage.api.*;
import org.apache.streampipes.storage.couchdb.impl.*;

public enum CouchDbStorageManager implements INoSqlStorage {

  INSTANCE;

  @Override
  public IAdapterStorage getAdapterDescriptionStorage() {
    return new AdapterDescriptionStorageImpl();
  }

  @Override
  public IGenericStorage getGenericStorage() {
    return new GenericStorageImpl();
  }

  @Override
  public IAdapterStorage getAdapterInstanceStorage() {
    return new AdapterInstanceStorageImpl();
  }


  @Override
  public ICategoryStorage getCategoryStorageAPI() { return new CategoryStorageImpl(); }

  @Override
  public IImageStorage getImageStorage() {
    return new ImageStorageImpl();
  }

  @Override
  public IUserGroupStorage getUserGroupStorage() {
    return new UserGroupStorageImpl();
  }

  @Override
  public ILabelStorage getLabelStorageAPI() { return new LabelStorageImpl(); }

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
  public IExtensionsServiceEndpointStorage getRdfEndpointStorage() {
    return new ExtensionsServiceEndpointStorageImpl();
  }

  @Override
  public IAssetDashboardStorage getAssetDashboardStorage() {
    return new AssetDashboardStorageImpl();
  }

  @Override
  public IDataLakeStorage getDataLakeStorage() {
    return new DataLakeStorageImpl();
  }

  @Override
  public IFileMetadataStorage getFileMetadataStorage() {
    return new FileMetadataStorageImpl();
  }

  @Override
  public IDashboardStorage getDashboardStorage() {
    return new DashboardStorageImpl();
  }

  @Override
  public IDashboardStorage getDataExplorerDashboardStorage() {
    return new DataExplorerDashboardStorageImpl();
  }

  @Override
  public IDashboardWidgetStorage getDashboardWidgetStorage() {
    return new DashboardWidgetStorageImpl();
  }

  @Override
  public IDataExplorerWidgetStorage getDataExplorerWidgetStorage() {
    return new DataExplorerWidgetStorageImpl();
  }

  @Override
  public IPipelineElementTemplateStorage getPipelineElementTemplateStorage() {
    return new PipelineElementTemplateStorageImpl();
  }

  @Override
  public IPipelineCanvasMetadataStorage getPipelineCanvasMetadataStorage() {
    return new PipelineCanvasMetadataStorageImpl();
  }

  @Override
  public IPipelineElementDescriptionStorageCache getPipelineElementDescriptionStorage() {
    return new PipelineElementDescriptionStorageImpl();
  }

  @Override
  public IPermissionStorage getPermissionStorage() {
    return new PermissionStorageImpl();
  }

  @Override
  public IDataProcessorStorage getDataProcessorStorage() {
    return new DataProcessorStorageImpl();
  }

  @Override
  public IDataSinkStorage getDataSinkStorage() {
    return new DataSinkStorageImpl();
  }

  @Override
  public IDataStreamStorage getDataStreamStorage() {
    return new DataStreamStorageImpl();
  }

  @Override
  public IPasswordRecoveryTokenStorage getPasswordRecoveryTokenStorage() {
    return new PasswordRecoveryTokenImpl();
  }

  @Override
  public IUserActivationTokenStorage getUserActivationTokenStorage() {
    return new UserActivationTokenImpl();
  }


}
