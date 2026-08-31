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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RenameDataLakeMetadataToDatasetMigrationTest {

  private IPermissionStorage permissionStorage;
  private RenameDataLakeMetadataToDatasetMigration migration;

  @BeforeEach
  void setUp() {
    this.permissionStorage = mock(IPermissionStorage.class);
    this.migration = new RenameDataLakeMetadataToDatasetMigration(permissionStorage);
  }

  @Test
  void shouldExecuteReturnsTrueWhenOldDataLakeClassNamesExist() {
    var permission = permissionWithClassName(
        RenameDataLakeMetadataToDatasetMigration.DATA_LAKE_MEASURE_CLASS
    );
    when(permissionStorage.findAll()).thenReturn(List.of(permission));

    assertTrue(migration.shouldExecute());
  }

  @Test
  void shouldExecuteReturnsFalseWhenMetadataAlreadyUsesDatasetNaming() {
    var permission = permissionWithClassName(
        RenameDataLakeMetadataToDatasetMigration.DATASET_MEASURE_CLASS
    );
    when(permissionStorage.findAll()).thenReturn(List.of(permission));

    assertFalse(migration.shouldExecute());
  }

  @Test
  void executeMigrationRenamesDatasetAndWidgetPermissionMetadata() throws IOException {
    var measurePermission = permissionWithClassName(
        RenameDataLakeMetadataToDatasetMigration.DATA_LAKE_MEASURE_CLASS
    );
    var widgetPermission = permissionWithClassName(
        RenameDataLakeMetadataToDatasetMigration.DATA_LAKE_WIDGET_CLASS
    );
    var unchangedPermission = permissionWithClassName("org.apache.streampipes.model.dashboard.DashboardModel");

    when(permissionStorage.findAll()).thenReturn(List.of(
        measurePermission,
        widgetPermission,
        unchangedPermission
    ));

    migration.executeMigration();

    assertEquals(
        RenameDataLakeMetadataToDatasetMigration.DATASET_MEASURE_CLASS,
        measurePermission.getObjectClassName()
    );
    assertEquals(
        RenameDataLakeMetadataToDatasetMigration.DATASET_WIDGET_CLASS,
        widgetPermission.getObjectClassName()
    );
    assertEquals(
        "org.apache.streampipes.model.dashboard.DashboardModel",
        unchangedPermission.getObjectClassName()
    );

    verify(permissionStorage).updateElement(measurePermission);
    verify(permissionStorage).updateElement(widgetPermission);
    verify(permissionStorage, never()).updateElement(unchangedPermission);
  }

  private Permission permissionWithClassName(String className) {
    var permission = new Permission();
    permission.setObjectClassName(className);
    return permission;
  }
}
