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
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.storage.api.explorer.IDatasetMetadataStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MigrateDatasetMetadataMigrationTest {

  @Test
  void migratesMetadataAndPermissionClassNames() throws IOException {
    var datasetMetadataStorage = mock(IDatasetMetadataStorage.class);
    var permissionStorage = mock(IPermissionStorage.class);
    var datasetMetadata = new DatasetMetadata();
    var permission = mock(Permission.class);

    when(datasetMetadataStorage.findAll()).thenReturn(List.of(datasetMetadata));
    when(permissionStorage.findAll()).thenReturn(List.of(permission));
    when(permission.getObjectClassName()).thenReturn("org.apache.streampipes.model.datalake.DataExplorerWidgetModel");

    var migration = new MigrateDatasetMetadataMigration(datasetMetadataStorage, permissionStorage);

    assertTrue(migration.shouldExecute());
    migration.executeMigration();

    verify(datasetMetadataStorage).updateElement(datasetMetadata);
    verify(permission).setObjectClassName("org.apache.streampipes.model.dataset.DataExplorerWidgetModel");
    verify(permissionStorage).updateElement(permission);
  }
}
