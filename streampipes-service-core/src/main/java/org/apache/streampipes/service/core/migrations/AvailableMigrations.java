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
import org.apache.streampipes.service.core.migrations.v099.RemoveObsoletePrivilegesMigration;
import org.apache.streampipes.service.core.migrations.v099.UniqueDashboardIdMigration;
import org.apache.streampipes.service.core.migrations.v099.connect.MigrateAdaptersToUseScript;

import java.util.Arrays;
import java.util.List;

public class AvailableMigrations {

  public List<Migration> getAvailableMigrations() {
    return Arrays.asList(
        new ModifyAssetLinksMigration(),
        new ModifyAssetLinkTypesMigration(),
        new AddDataLakeMeasureViewMigration(),
        new AddDefaultExportProviderMigration(),
        new FixImportedPermissionsMigration(),
        new AddAssetManagementViewMigration(),
        new MoveAssetContentMigration(),
        new CreateAssetPermissionMigration(),
        new CreateDatasetPermissionMigration(),
        new RemoveObsoletePrivilegesMigration(),
        new UniqueDashboardIdMigration(),
        new AddScriptTemplateViewMigration(),
        new ComputeCertificateThumbprintMigration(),
        new MigrateAdaptersToUseScript(),
        new ModifyAssetLinkIconMigration(),
        new RemoveDuplicatedAssetPermissions(),
        new AddFunctionStateViewMigration(),
        new AddRefreshTokenViewsMigration(),
        new RemoveAssetUserRoleMigration()
    );
  }
}
