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

import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class ResourceDeletionManagerTest {

  private final IGenericStorage genericStorage = mock(IGenericStorage.class);
  private final IAssetStorage storage = mock(IAssetStorage.class);
  private final ResourceDeletionManager manager = new ResourceDeletionManager(genericStorage);
  private final SpAssetModel asset = new SpAssetModel();

  @BeforeEach
  void setUp() {
    asset.setElementId("asset-1");
    when(storage.getElementById("asset-1")).thenReturn(asset);
  }

  @Test
  void removesAssetLinksBeforeDeletingTheDocument() throws IOException {
    manager.delete(storage, "asset-1");

    var order = inOrder(genericStorage, storage);
    order.verify(genericStorage).deleteAssetLinkToResource("asset-1");
    order.verify(storage).deleteElement(asset);
  }

  @Test
  void stillDeletesTheDocumentWhenAssetLinkCleanupFails() throws IOException {
    doThrow(new IOException("unavailable")).when(genericStorage).deleteAssetLinkToResource("asset-1");

    manager.delete(storage, "asset-1");

    verify(storage).deleteElement(asset);
  }

  @Test
  void resourceManagerDeletesPermissionsAfterDeletingTheDocument() throws IOException {
    var permissions = mock(PermissionResourceManager.class);
    var permission = new Permission();
    when(permissions.findForObjectId("asset-1")).thenReturn(List.of(permission));

    new AssetResourceManager(storage, permissions, manager).delete("asset-1");

    var order = inOrder(genericStorage, storage, permissions);
    order.verify(genericStorage).deleteAssetLinkToResource("asset-1");
    order.verify(storage).deleteElement(asset);
    order.verify(permissions).delete(permission);
  }

  @Test
  void doesNotCleanLinksWhenTheResourceDoesNotExist() {
    when(storage.getElementById("asset-1")).thenReturn(null);

    manager.delete(storage, "asset-1");

    verifyNoInteractions(genericStorage);
    verify(storage, never()).deleteElement(asset);
  }
}
