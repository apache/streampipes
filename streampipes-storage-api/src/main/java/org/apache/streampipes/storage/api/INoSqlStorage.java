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
package org.apache.streampipes.storage.api;

import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.client.user.PasswordRecoveryToken;
import org.apache.streampipes.model.client.user.UserActivationToken;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dashboard.DashboardWidgetModel;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.file.FileMetadata;

public interface INoSqlStorage {

  IGenericStorage getGenericStorage();

  IAdapterStorage getAdapterInstanceStorage();

  IAdapterStorage getAdapterDescriptionStorage();

  IImageStorage getImageStorage();

  CRUDStorage<Group> getUserGroupStorage();

  IPipelineStorage getPipelineStorageAPI();

  IPipelineElementConnectionStorage getConnectionStorageApi();

  IUserStorage getUserStorageAPI();

  INotificationStorage getNotificationStorageApi();

  CRUDStorage<DataLakeMeasure> getDataLakeStorage();

  CRUDStorage<FileMetadata> getFileMetadataStorage();

  CRUDStorage<DashboardModel> getDashboardStorage();

  CRUDStorage<DashboardModel> getDataExplorerDashboardStorage();

  CRUDStorage<DashboardWidgetModel> getDashboardWidgetStorage();

  CRUDStorage<DataExplorerWidgetModel> getDataExplorerWidgetStorage();

  IPipelineElementTemplateStorage getPipelineElementTemplateStorage();

  IPipelineCanvasMetadataStorage getPipelineCanvasMetadataStorage();

  IPipelineElementDescriptionStorage getPipelineElementDescriptionStorage();

  IPermissionStorage getPermissionStorage();

  IDataProcessorStorage getDataProcessorStorage();

  IDataSinkStorage getDataSinkStorage();

  IDataStreamStorage getDataStreamStorage();

  CRUDStorage<PasswordRecoveryToken> getPasswordRecoveryTokenStorage();

  CRUDStorage<UserActivationToken> getUserActivationTokenStorage();

  CRUDStorage<SpServiceRegistration> getExtensionsServiceStorage();

  CRUDStorage<SpServiceConfiguration> getExtensionsServiceConfigurationStorage();

  ISpCoreConfigurationStorage getSpCoreConfigurationStorage();
}
