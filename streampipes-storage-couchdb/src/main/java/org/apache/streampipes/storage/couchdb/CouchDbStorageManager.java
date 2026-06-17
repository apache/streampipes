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

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
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
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.system.ITransformationScriptTemplateStorage;
import org.apache.streampipes.storage.api.user.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IPrivilegeStorage;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterInstanceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.DataExplorerDashboardStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.DataExplorerWidgetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.explorer.DataLakeMeasureStorage;
import org.apache.streampipes.storage.couchdb.impl.pipeline.CompactPipelineTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.DataProcessorStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.DataSinkStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.DataStreamStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineCanvasMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineElementDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineElementTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.AssetStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.CertificateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.CoreConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ExtensionsServiceConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ExtensionsServiceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.FileMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.GenericStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ImageStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.TransformationScriptTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PasswordRecoveryTokenStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PermissionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PrivilegeStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.RefreshTokenStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.RoleStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserActivationTokenStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserGroupStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;

public class CouchDbStorageManager implements INoSqlStorage {

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
  public IUserGroupStorage getUserGroupStorage() {
    return new UserGroupStorageImpl();
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
  public IDataLakeMeasureStorage getDataLakeStorage() {
    return new DataLakeMeasureStorage(
        () -> Utils.getCouchDbGsonClient(Utils.DATA_LAKE_DB_NAME),
        DataLakeMeasure.class
    );
  }

  @Override
  public IFileMetadataStorage getFileMetadataStorage() {
    return new FileMetadataStorageImpl();
  }

  @Override
  public IDataExplorerDashboardStorage getDataExplorerDashboardStorage() {
    return new DataExplorerDashboardStorageImpl();
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
  public IPasswordRecoveryTokenStorage getPasswordRecoveryTokenStorage() {
    return new PasswordRecoveryTokenStorageImpl();
  }

  @Override
  public IUserActivationTokenStorage getUserActivationTokenStorage() {
    return new UserActivationTokenStorageImpl();
  }

  @Override
  public IRefreshTokenStorage getRefreshTokenStorage() {
    return new RefreshTokenStorageImpl();
  }

  @Override
  public IExtensionsServiceStorage getExtensionsServiceStorage() {
    return new ExtensionsServiceStorageImpl();
  }

  @Override
  public IExtensionsServiceConfigurationStorage getExtensionsServiceConfigurationStorage() {
    return new ExtensionsServiceConfigurationStorageImpl();
  }

  @Override
  public ISpCoreConfigurationStorage getSpCoreConfigurationStorage() {
    return new CoreConfigurationStorageImpl();
  }

  @Override
  public IRoleStorage getRoleStorage() {
    return new RoleStorageImpl();
  }

  @Override
  public IPrivilegeStorage getPrivilegeStorage() {
    return new PrivilegeStorageImpl();
  }

  @Override
  public ICompactPipelineTemplateStorage getPipelineTemplateStorage() {
    return new CompactPipelineTemplateStorageImpl();
  }

  @Override
  public ICertificateStorage getCertificateStorage() {
    return new CertificateStorageImpl();
  }

  @Override
  public IAssetStorage getAssetStorage() {
    return new AssetStorageImpl();
  }

  @Override
  public ITransformationScriptTemplateStorage getTransformationScriptTemplateStorage() {
    return new TransformationScriptTemplateStorageImpl();
  }
}
