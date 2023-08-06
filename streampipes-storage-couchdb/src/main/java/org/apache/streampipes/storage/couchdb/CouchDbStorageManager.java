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

import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IAssetDashboardStorage;
import org.apache.streampipes.storage.api.ICategoryStorage;
import org.apache.streampipes.storage.api.IDashboardStorage;
import org.apache.streampipes.storage.api.IDashboardWidgetStorage;
import org.apache.streampipes.storage.api.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.api.IDataStreamStorage;
import org.apache.streampipes.storage.api.IExtensionsServiceEndpointStorage;
import org.apache.streampipes.storage.api.IFileMetadataStorage;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.api.IImageStorage;
import org.apache.streampipes.storage.api.ILabelStorage;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.INotificationStorage;
import org.apache.streampipes.storage.api.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.api.IPipelineCanvasMetadataStorage;
import org.apache.streampipes.storage.api.IPipelineCategoryStorage;
import org.apache.streampipes.storage.api.IPipelineElementConnectionStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorageCache;
import org.apache.streampipes.storage.api.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.IPipelineMonitoringDataStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.IUserGroupStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.api.IVisualizationStorage;
import org.apache.streampipes.storage.couchdb.impl.AdapterDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.AssetDashboardStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.CategoryStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.ConnectionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.CoreConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DashboardStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DashboardWidgetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataExplorerDashboardStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataExplorerWidgetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataLakeStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataProcessorStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataSinkStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataStreamStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.ExtensionsServiceConfigStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.ExtensionsServiceEndpointStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.ExtensionsServiceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.FileMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.GenericStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.ImageStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.LabelStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.MonitoringDataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.NotificationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PasswordRecoveryTokenImpl;
import org.apache.streampipes.storage.couchdb.impl.PermissionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineCanvasMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineCategoryStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineElementDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineElementTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.UserActivationTokenImpl;
import org.apache.streampipes.storage.couchdb.impl.UserGroupStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.UserStorage;
import org.apache.streampipes.storage.couchdb.impl.VisualizationStorageImpl;

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
  public ICategoryStorage getCategoryStorageAPI() {
    return new CategoryStorageImpl();
  }

  @Override
  public IImageStorage getImageStorage() {
    return new ImageStorageImpl();
  }

  @Override
  public IUserGroupStorage getUserGroupStorage() {
    return new UserGroupStorageImpl();
  }

  @Override
  public ILabelStorage getLabelStorageAPI() {
    return new LabelStorageImpl();
  }

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

  @Override
  public CRUDStorage<String, SpServiceRegistration> getExtensionsServiceStorage() {
    return new ExtensionsServiceStorageImpl();
  }

  @Override
  public CRUDStorage<String, SpServiceConfiguration> getExtensionsServiceConfigurationStorage() {
    return new ExtensionsServiceConfigStorageImpl();
  }

  @Override
  public ISpCoreConfigurationStorage getSpCoreConfigurationStorage() {
    return new CoreConfigurationStorageImpl();
  }


}
