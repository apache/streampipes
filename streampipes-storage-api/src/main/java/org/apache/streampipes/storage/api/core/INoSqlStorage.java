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
package org.apache.streampipes.storage.api.core;

import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IDataExplorerDashboardStorage;
import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;
import org.apache.streampipes.storage.api.pipeline.IDataProcessorStorage;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineCanvasMetadataStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ICertificateStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceConfigurationStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.api.system.IImageStorage;
import org.apache.streampipes.storage.api.system.INotificationStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.system.ITransformationScriptTemplateStorage;
import org.apache.streampipes.storage.api.user.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IPrivilegeStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;

public interface INoSqlStorage {

  IGenericStorage getGenericStorage();

  IAdapterStorage getAdapterInstanceStorage();

  IAdapterStorage getAdapterDescriptionStorage();

  IImageStorage getImageStorage();

  IUserGroupStorage getUserGroupStorage();

  IPipelineStorage getPipelineStorageAPI();

  IUserStorage getUserStorageAPI();

  INotificationStorage getNotificationStorageApi();

  IDataLakeMeasureStorage getDataLakeStorage();

  IFileMetadataStorage getFileMetadataStorage();

  IDataExplorerDashboardStorage getDataExplorerDashboardStorage();

  IDataExplorerWidgetStorage getDataExplorerWidgetStorage();

  IPipelineElementTemplateStorage getPipelineElementTemplateStorage();

  IPipelineCanvasMetadataStorage getPipelineCanvasMetadataStorage();

  IPipelineElementDescriptionStorage getPipelineElementDescriptionStorage();

  IPermissionStorage getPermissionStorage();

  IDataProcessorStorage getDataProcessorStorage();

  IDataSinkStorage getDataSinkStorage();

  IDataStreamStorage getDataStreamStorage();

  IPasswordRecoveryTokenStorage getPasswordRecoveryTokenStorage();

  IUserActivationTokenStorage getUserActivationTokenStorage();

  IExtensionsServiceStorage getExtensionsServiceStorage();

  IExtensionsServiceConfigurationStorage getExtensionsServiceConfigurationStorage();

  ISpCoreConfigurationStorage getSpCoreConfigurationStorage();

  IRoleStorage getRoleStorage();

  IPrivilegeStorage getPrivilegeStorage();

  ICompactPipelineTemplateStorage getPipelineTemplateStorage();

  ICertificateStorage getCertificateStorage();

  IAssetStorage getAssetStorage();

  ITransformationScriptTemplateStorage getTransformationScriptTemplateStorage();
}
