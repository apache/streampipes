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


package org.apache.streampipes.service.core.migrations;

import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.service.core.migrations.v0980.AddDataLakeMeasureViewMigration;
import org.apache.streampipes.service.core.migrations.v0980.AddDefaultExportProviderMigration;
import org.apache.streampipes.service.core.migrations.v0980.FixImportedPermissionsMigration;
import org.apache.streampipes.service.core.migrations.v0980.ModifyAssetLinkTypesMigration;
import org.apache.streampipes.service.core.migrations.v0980.ModifyAssetLinksMigration;
import org.apache.streampipes.service.core.migrations.v099.AddAssetManagementViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddFunctionStateViewMigration;
import org.apache.streampipes.service.core.migrations.v099.AddRefreshTokenViewsMigration;
import org.apache.streampipes.service.core.migrations.v099.AddScriptTemplateViewMigration;
import org.apache.streampipes.service.core.migrations.v099.ComputeCertificateThumbprintMigration;
import org.apache.streampipes.service.core.migrations.v099.CreateAssetPermissionMigration;
import org.apache.streampipes.service.core.migrations.v099.CreateDatasetPermissionMigration;
import org.apache.streampipes.service.core.migrations.v099.ModifyAssetLinkIconMigration;
import org.apache.streampipes.service.core.migrations.v099.MoveAssetContentMigration;
import org.apache.streampipes.service.core.migrations.v099.RemoveAssetUserRoleMigration;
import org.apache.streampipes.service.core.migrations.v099.RemoveDuplicatedAssetPermissions;
import org.apache.streampipes.service.core.migrations.v099.RemoveInternalNotificationSinkMigration;
import org.apache.streampipes.service.core.migrations.v099.RemoveObsoletePrivilegesMigration;
import org.apache.streampipes.service.core.migrations.v099.UniqueDashboardIdMigration;
import org.apache.streampipes.service.core.migrations.v099.connect.MigrateAdaptersToUseScript;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.explorer.IDataLakeMeasureStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IPrivilegeStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;

import java.util.Arrays;
import java.util.List;

public class AvailableMigrations {

  private final IChartStorage chartStorage;
  private final IPermissionStorage permissionStorage;
  private final IAdapterStorage adapterStorage;
  private final IDashboardStorage dashboardStorage;
  private final IAssetStorage assetStorage;
  private final IPipelineStorage pipelineStorage;
  private final IDataLakeMeasureStorage datasetStorage;
  private final ISpCoreConfigurationStorage coreConfigStorage;
  private final IRoleStorage roleStorage;
  private final IUserGroupStorage userGroupStorage;
  private final IPrivilegeStorage privilegeStorage;
  private final IUserStorage userStorage;

  public AvailableMigrations(SpResourceManager resourceManager) {
    this.chartStorage = resourceManager.manageCharts().getDb();
    this.permissionStorage = resourceManager.managePermissions().getDb();
    this.adapterStorage = resourceManager.manageAdapters().getDb();
    this.dashboardStorage = resourceManager.manageDashboards().getDb();
    this.assetStorage = resourceManager.manageAssets().getDb();
    this.pipelineStorage = resourceManager.managePipelines().getDb();
    this.datasetStorage = resourceManager.manageDataLakeMeasures().getDb();
    this.coreConfigStorage = resourceManager.getCoreConfigurationStorage();
    this.roleStorage = resourceManager.getRoleStorage();
    this.userGroupStorage = resourceManager.getUserGroupStorage();
    this.privilegeStorage = resourceManager.getPrivilegeStorage();
    this.userStorage = resourceManager.manageUsers().getDb();
  }

  public List<Migration> getAvailableMigrations() {
    return Arrays.asList(
        new ModifyAssetLinksMigration(),
        new ModifyAssetLinkTypesMigration(),
        new AddDataLakeMeasureViewMigration(),
        new AddDefaultExportProviderMigration(coreConfigStorage),
        new FixImportedPermissionsMigration(chartStorage, dashboardStorage, permissionStorage),
        new AddAssetManagementViewMigration(),
        new MoveAssetContentMigration(),
        new CreateAssetPermissionMigration(permissionStorage, assetStorage),
        new CreateDatasetPermissionMigration(permissionStorage, pipelineStorage, datasetStorage),
        new RemoveObsoletePrivilegesMigration(privilegeStorage),
        new UniqueDashboardIdMigration(dashboardStorage),
        new AddScriptTemplateViewMigration(),
        new ComputeCertificateThumbprintMigration(),
        new MigrateAdaptersToUseScript(adapterStorage),
        new ModifyAssetLinkIconMigration(),
        new RemoveDuplicatedAssetPermissions(permissionStorage, assetStorage),
        new AddFunctionStateViewMigration(),
        new AddRefreshTokenViewsMigration(),
        new RemoveAssetUserRoleMigration(roleStorage, userGroupStorage, userStorage),
        new RemoveInternalNotificationSinkMigration(pipelineStorage)
    );
  }
}
