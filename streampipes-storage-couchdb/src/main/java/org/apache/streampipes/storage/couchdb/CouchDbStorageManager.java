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

import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.client.user.PasswordRecoveryToken;
import org.apache.streampipes.model.client.user.Privilege;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.UserActivationToken;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.file.FileMetadata;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.api.IDataProcessorStorage;
import org.apache.streampipes.storage.api.IDataSinkStorage;
import org.apache.streampipes.storage.api.IDataStreamStorage;
import org.apache.streampipes.storage.api.IGenericStorage;
import org.apache.streampipes.storage.api.IImageStorage;
import org.apache.streampipes.storage.api.INoSqlStorage;
import org.apache.streampipes.storage.api.INotificationStorage;
import org.apache.streampipes.storage.api.IPermissionStorage;
import org.apache.streampipes.storage.api.IPipelineCanvasMetadataStorage;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.api.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.IUserStorage;
import org.apache.streampipes.storage.couchdb.impl.AdapterDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.CoreConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataProcessorStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataSinkStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DataStreamStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.DefaultCrudStorage;
import org.apache.streampipes.storage.couchdb.impl.DefaultViewCrudStorage;
import org.apache.streampipes.storage.couchdb.impl.GenericStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.ImageStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.NotificationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PermissionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineCanvasMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineElementDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineElementTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.PrivilegeStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.RoleStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.UserStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

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
  public IImageStorage getImageStorage() {
    return new ImageStorageImpl();
  }

  @Override
  public CRUDStorage<Group> getUserGroupStorage() {
    return new DefaultViewCrudStorage<>(
        Utils::getCouchDbUserClient,
        Group.class,
        "users/groups"
    );
  }

  @Override
  public IPipelineStorage getPipelineStorageAPI() {
    return new PipelineStorageImpl();
  }

  @Override
  public IUserStorage getUserStorageAPI() {
    return new UserStorage();
  }

  @Override
  public INotificationStorage getNotificationStorageApi() {
    return new NotificationStorageImpl();
  }

  @Override
  public CRUDStorage<DataLakeMeasure> getDataLakeStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("data-lake"),
        DataLakeMeasure.class
    );
  }

  @Override
  public CRUDStorage<FileMetadata> getFileMetadataStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("filemetadata"),
        FileMetadata.class
    );
  }

  @Override
  public CRUDStorage<DashboardModel> getDashboardStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("dashboard"),
        DashboardModel.class
    );
  }

  @Override
  public CRUDStorage<DashboardModel> getDataExplorerDashboardStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("dataexplorerdashboard"),
        DashboardModel.class
    );
  }

  @Override
  public CRUDStorage<DashboardWidgetModel> getDashboardWidgetStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("dashboardwidget"),
        DashboardWidgetModel.class
    );
  }

  @Override
  public CRUDStorage<DataExplorerWidgetModel> getDataExplorerWidgetStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("dataexplorerwidget"),
        DataExplorerWidgetModel.class
    );
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
  public IPipelineElementDescriptionStorage getPipelineElementDescriptionStorage() {
    return new PipelineElementDescriptionStorageImpl();
  }

  @Override
  public IPermissionStorage getPermissionStorage() {
    return new PermissionStorageImpl("users/permissions");
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
  public CRUDStorage<PasswordRecoveryToken> getPasswordRecoveryTokenStorage() {
    return new DefaultViewCrudStorage<>(
        Utils::getCouchDbUserClient,
        PasswordRecoveryToken.class,
        "users/password-recovery"
    );
  }

  @Override
  public CRUDStorage<UserActivationToken> getUserActivationTokenStorage() {
    return new DefaultViewCrudStorage<>(
        Utils::getCouchDbUserClient,
        UserActivationToken.class,
        "users/user-activation"
    );
  }

  @Override
  public CRUDStorage<SpServiceRegistration> getExtensionsServiceStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("extensions-services"),
        SpServiceRegistration.class
    );
  }

  @Override
  public CRUDStorage<SpServiceConfiguration> getExtensionsServiceConfigurationStorage() {
    return new DefaultCrudStorage<>(
        () -> Utils.getCouchDbGsonClient("extensions-services-configurations"),
        SpServiceConfiguration.class
    );
  }

  @Override
  public ISpCoreConfigurationStorage getSpCoreConfigurationStorage() {
    return new CoreConfigurationStorageImpl();
  }

  @Override
  public CRUDStorage<Role> getRoleStorage() {
    return new RoleStorageImpl();
  }

  @Override
  public CRUDStorage<Privilege> getPrivilegeStorage() {
    return new PrivilegeStorageImpl();
  }
}
