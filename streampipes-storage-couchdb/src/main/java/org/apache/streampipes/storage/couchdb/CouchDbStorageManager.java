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

import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;
import org.apache.streampipes.storage.api.pipeline.IDataProcessorStorage;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineCanvasMetadataStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementTemplateStorage;
import org.apache.streampipes.storage.api.system.ICertificateStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceConfigurationStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.api.system.IImageStorage;
import org.apache.streampipes.storage.api.system.ITransformationScriptTemplateStorage;
import org.apache.streampipes.storage.api.user.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.user.IPrivilegeStorage;
import org.apache.streampipes.storage.api.user.IRefreshTokenStorage;
import org.apache.streampipes.storage.api.user.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.storage.couchdb.impl.connect.AdapterDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.CompactPipelineTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.DataProcessorStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.DataSinkStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.DataStreamStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineCanvasMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineElementDescriptionStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.pipeline.PipelineElementTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.CertificateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ExtensionsServiceConfigurationStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ExtensionsServiceStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.FileMetadataStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.GenericStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.ImageStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.system.TransformationScriptTemplateStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PasswordRecoveryTokenStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.PrivilegeStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.RefreshTokenStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserActivationTokenStorageImpl;
import org.apache.streampipes.storage.couchdb.impl.user.UserStorage;

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
  public IImageStorage getImageStorage() {
    return new ImageStorageImpl();
  }

  @Override
  public IUserStorage getUserStorageAPI() {
    return new UserStorage();
  }

  @Override
  public IFileMetadataStorage getFileMetadataStorage() {
    return new FileMetadataStorageImpl();
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
  public ITransformationScriptTemplateStorage getTransformationScriptTemplateStorage() {
    return new TransformationScriptTemplateStorageImpl();
  }
}
