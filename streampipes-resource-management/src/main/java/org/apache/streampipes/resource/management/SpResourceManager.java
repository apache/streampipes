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

import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.api.pipeline.IDataProcessorStorage;
import org.apache.streampipes.storage.api.pipeline.IDataSinkStorage;
import org.apache.streampipes.storage.api.pipeline.IDataStreamStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ICertificateStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;
import org.apache.streampipes.storage.api.system.IFileMetadataStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IPasswordRecoveryTokenStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IPrivilegeStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserActivationTokenStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;

import java.util.Objects;

public class SpResourceManager {

  private final AuditService auditService;
  private final IPermissionStorage permissionStorage;
  private final IChartStorage chartStorage;
  private final IAdapterStorage adapterStorage;
  private final IAdapterStorage adapterDescriptionStorage;
  private final IAssetStorage assetStorage;
  private final IDashboardStorage dashboardStorage;
  private final IPipelineStorage pipelineStorage;
  private final IDatasetMetadataStorage datasetStorage;
  private final ISpCoreConfigurationStorage coreConfigurationStorage;
  private final IFileMetadataStorage fileMetadataStorage;
  private final IRoleStorage roleStorage;
  private final IUserGroupStorage userGroupStorage;
  private final IPrivilegeStorage privilegeStorage;
  private final IUserStorage userStorage;
  private final IDataProcessorStorage dataProcessorStorage;
  private final IDataSinkStorage dataSinkStorage;
  private final IDataStreamStorage dataStreamStorage;
  private final IPipelineElementDescriptionStorage descriptionStorage;
  private final IGenericStorage genericStorage;
  private final ResourceDeletionManager resourceDeletionManager;
  private final IUserActivationTokenStorage userActivationTokenStorage;
  private final IPasswordRecoveryTokenStorage passwordRecoveryTokenStorage;
  private final ICertificateStorage certificateStorage;
  private final IExtensionsServiceStorage extensionsServiceStorage;
  private final ISpServiceDiscovery serviceDiscovery;

  public SpResourceManager(IPermissionStorage permissionStorage,
                           IChartStorage chartStorage,
                           IAdapterStorage adapterStorage,
                           IAdapterStorage adapterDescriptionStorage,
                           IAssetStorage assetStorage,
                           IDashboardStorage dashboardStorage,
                           IPipelineStorage pipelineStorage,
                           IDatasetMetadataStorage datasetStorage,
                           ISpCoreConfigurationStorage coreConfigurationStorage,
                           IFileMetadataStorage fileMetadataStorage,
                           IRoleStorage roleStorage,
                           IUserGroupStorage userGroupStorage,
                           IPrivilegeStorage privilegeStorage,
                           IUserStorage userStorage,
                           IDataProcessorStorage dataProcessorStorage,
                           IDataSinkStorage dataSinkStorage,
                           IDataStreamStorage dataStreamStorage,
                           IPipelineElementDescriptionStorage descriptionStorage,
                           IGenericStorage genericStorage,
                           IUserActivationTokenStorage userActivationTokenStorage,
                           IPasswordRecoveryTokenStorage passwordRecoveryTokenStorage,
                           ICertificateStorage certificateStorage,
                           IExtensionsServiceStorage extensionsServiceStorage,
                           ISpServiceDiscovery serviceDiscovery) {
    this(permissionStorage, chartStorage, adapterStorage, adapterDescriptionStorage,
        assetStorage, dashboardStorage, pipelineStorage,
        datasetStorage, coreConfigurationStorage, fileMetadataStorage, roleStorage, userGroupStorage,
        privilegeStorage, userStorage, dataProcessorStorage, dataSinkStorage, dataStreamStorage, descriptionStorage, genericStorage,
        userActivationTokenStorage,
        passwordRecoveryTokenStorage, certificateStorage, extensionsServiceStorage, serviceDiscovery,
        AuditService.disabled());
  }

  public SpResourceManager(IPermissionStorage permissionStorage,
                           IChartStorage chartStorage,
                           IAdapterStorage adapterStorage,
                           IAdapterStorage adapterDescriptionStorage,
                           IAssetStorage assetStorage,
                           IDashboardStorage dashboardStorage,
                           IPipelineStorage pipelineStorage,
                           IDatasetMetadataStorage datasetStorage,
                           ISpCoreConfigurationStorage coreConfigurationStorage,
                           IFileMetadataStorage fileMetadataStorage,
                           IRoleStorage roleStorage,
                           IUserGroupStorage userGroupStorage,
                           IPrivilegeStorage privilegeStorage,
                           IUserStorage userStorage,
                           IDataProcessorStorage dataProcessorStorage,
                           IDataSinkStorage dataSinkStorage,
                           IDataStreamStorage dataStreamStorage,
                           IPipelineElementDescriptionStorage descriptionStorage,
                           IGenericStorage genericStorage,
                           IUserActivationTokenStorage userActivationTokenStorage,
                           IPasswordRecoveryTokenStorage passwordRecoveryTokenStorage,
                           ICertificateStorage certificateStorage,
                           IExtensionsServiceStorage extensionsServiceStorage,
                           ISpServiceDiscovery serviceDiscovery,
                           AuditService auditService) {
    this.auditService = Objects.requireNonNull(auditService);
    this.permissionStorage = permissionStorage;
    this.chartStorage = chartStorage;
    this.adapterStorage = adapterStorage;
    this.adapterDescriptionStorage = adapterDescriptionStorage;
    this.assetStorage = assetStorage;
    this.dashboardStorage = dashboardStorage;
    this.pipelineStorage = pipelineStorage;
    this.datasetStorage = datasetStorage;
    this.coreConfigurationStorage = coreConfigurationStorage;
    this.fileMetadataStorage = fileMetadataStorage;
    this.roleStorage = roleStorage;
    this.userGroupStorage = userGroupStorage;
    this.privilegeStorage = privilegeStorage;
    this.userStorage = userStorage;
    this.dataProcessorStorage = dataProcessorStorage;
    this.dataSinkStorage = dataSinkStorage;
    this.dataStreamStorage = dataStreamStorage;
    this.descriptionStorage = descriptionStorage;
    this.genericStorage = genericStorage;
    this.resourceDeletionManager = new ResourceDeletionManager(genericStorage);
    this.userActivationTokenStorage = userActivationTokenStorage;
    this.passwordRecoveryTokenStorage = passwordRecoveryTokenStorage;
    this.certificateStorage = certificateStorage;
    this.extensionsServiceStorage = extensionsServiceStorage;
    this.serviceDiscovery = serviceDiscovery;
  }

  public ResourceDeletionManager getResourceDeletionManager() {
    return resourceDeletionManager;
  }

  public IGenericStorage getGenericStorage() {
    return genericStorage;
  }

  public IPipelineElementDescriptionStorage getPipelineElementDescriptionStorage() {
    return descriptionStorage;
  }

  public ISpServiceDiscovery getServiceDiscovery() {
    return serviceDiscovery;
  }

  public IExtensionsServiceStorage getExtensionsServiceStorage() {
    return extensionsServiceStorage;
  }

  public AuditService getAuditService() {
    return auditService;
  }

  public AdapterDescriptionResourceManager manageAdapterDescriptions() {
    return new AdapterDescriptionResourceManager(adapterDescriptionStorage, managePermissions());
  }

  public DataSinkResourceManager manageDataSinks() {
    return new DataSinkResourceManager(dataSinkStorage, managePermissions());
  }

  public DataProcessorResourceManager manageDataProcessors() {
    return new DataProcessorResourceManager(dataProcessorStorage, managePermissions());
  }

  public DataStreamResourceManager manageDataStreams() {
    return new DataStreamResourceManager(dataStreamStorage, managePermissions(), resourceDeletionManager);
  }

  public AssetResourceManager manageAssets() {
    return new AssetResourceManager(assetStorage, managePermissions(), resourceDeletionManager);
  }

  public AdapterResourceManager manageAdapters() {
    return new AdapterResourceManager(adapterStorage, certificateStorage, managePermissions(), resourceDeletionManager);
  }

  public DatasetMetadataResourceManager manageDataLakeMeasures() {
    return new DatasetMetadataResourceManager(datasetStorage, pipelineStorage, managePermissions());
  }

  public PermissionResourceManager managePermissions() {
    return new PermissionResourceManager(permissionStorage);
  }

  public DashboardResourceManager manageDashboards() {
    return new DashboardResourceManager(dashboardStorage, chartStorage, datasetStorage, managePermissions(),
        resourceDeletionManager);
  }

  public ChartResourceManager manageCharts() {
    return new ChartResourceManager(manageDashboards(), chartStorage, managePermissions(), resourceDeletionManager);
  }

  public PipelineResourceManager managePipelines() {
    return new PipelineResourceManager(pipelineStorage, managePermissions(), resourceDeletionManager);
  }

  public ISpCoreConfigurationStorage getCoreConfigurationStorage() {
    return coreConfigurationStorage;
  }

  public IFileMetadataStorage getFileMetadataStorage() {
    return fileMetadataStorage;
  }

  public IRoleStorage getRoleStorage() {
    return roleStorage;
  }

  public IUserGroupStorage getUserGroupStorage() {
    return userGroupStorage;
  }

  public IPrivilegeStorage getPrivilegeStorage() {
    return privilegeStorage;
  }

  public UserResourceManager manageUsers() {
    return new UserResourceManager(
        userStorage, coreConfigurationStorage, userActivationTokenStorage, passwordRecoveryTokenStorage);
  }
}
