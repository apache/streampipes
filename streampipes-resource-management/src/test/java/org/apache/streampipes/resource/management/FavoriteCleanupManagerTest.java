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
import org.apache.streampipes.model.client.user.Group;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dataset.DataExplorerWidgetModel;
import org.apache.streampipes.model.favorites.Favorite;
import org.apache.streampipes.model.favorites.FavoriteResourceType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.ICertificateStorage;
import org.apache.streampipes.storage.api.system.IGenericStorage;
import org.apache.streampipes.storage.api.user.IFavoriteStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FavoriteCleanupManagerTest {

  private final IFavoriteStorage favorites = mock(IFavoriteStorage.class);
  private final IUserStorage users = mock(IUserStorage.class);
  private final IPermissionStorage permissions = mock(IPermissionStorage.class);
  private final IRoleStorage roles = mock(IRoleStorage.class);
  private final IUserGroupStorage groups = mock(IUserGroupStorage.class);
  private final IAdapterStorage adapters = mock(IAdapterStorage.class);
  private final FavoriteResourceManager manager = new FavoriteResourceManager(
      favorites, null, adapters, null, null, null, new SpPermissionEvaluator(permissions)
  );
  private final FavoriteCleanupManager cleanup =
      new FavoriteCleanupManager(favorites, manager, users, permissions, roles, groups);
  private final UserAccount user = new UserAccount();

  @BeforeEach
  void setUp() {
    user.setPrincipalId("alice");
    user.setRoles(Set.of("reader"));
    when(users.getUserById("alice")).thenReturn(user);
    var role = new Role();
    role.setPrivilegeIds(List.of("PRIVILEGE_READ_PIPELINE", "PRIVILEGE_READ_ADAPTER"));
    when(roles.getElementById("reader")).thenReturn(role);
    when(favorites.findForUser("alice")).thenReturn(
        List.of(new Favorite("alice", FavoriteResourceType.PIPELINE, "pipeline"))
    );
  }

  @Test
  void deletesOnlyMatchingResourceAcrossUsersForEverySupportedType() {
    var types = Map.of(
        SpAssetModel.class, FavoriteResourceType.ASSET,
        AdapterDescription.class, FavoriteResourceType.ADAPTER,
        Pipeline.class, FavoriteResourceType.PIPELINE,
        DashboardModel.class, FavoriteResourceType.DASHBOARD,
        DataExplorerWidgetModel.class, FavoriteResourceType.CHART
    );
    types.forEach((resourceClass, type) -> {
      when(favorites.findAll()).thenReturn(List.of(
          new Favorite("alice", type, "deleted"),
          new Favorite("bob", type, "deleted"),
          new Favorite("alice", type, "keep")
      ));
      cleanup.resourceDeleted(resourceClass, "deleted");
      verify(favorites).remove("alice", type, "deleted");
      verify(favorites).remove("bob", type, "deleted");
      verify(favorites, never()).remove("alice", type, "keep");
    });
  }

  @Test
  void userDeletionRemovesOnlyThatUsersFavorites() {
    cleanup.userDeleted("alice");
    verify(favorites).remove("alice", FavoriteResourceType.PIPELINE, "pipeline");
    verify(favorites, never()).findAll();
  }

  @Test
  void revokedObjectAccessRemovesFavoriteWithoutOpeningFavoritesPage() {
    cleanup.userPermissionsChanged("alice");
    verify(favorites).remove("alice", FavoriteResourceType.PIPELINE, "pipeline");
  }

  @Test
  void remainingGroupAccessPreservesFavoriteAndStoredPrincipal() {
    user.setGroups(Set.of("team"));
    when(groups.getElementById("team")).thenReturn(new Group());
    when(permissions.getObjectPermissions(anyList())).thenReturn(Set.of("pipeline"));
    cleanup.userPermissionsChanged("alice");
    verify(favorites, never()).remove("alice", FavoriteResourceType.PIPELINE, "pipeline");
    assertEquals(Set.of("team"), user.getGroups());
  }

  @Test
  void removedReadRoleRemovesFavoriteDespiteRemainingObjectAccess() {
    user.setRoles(Set.of());
    when(permissions.getObjectPermissions(anyList())).thenReturn(Set.of("pipeline"));
    cleanup.userPermissionsChanged("alice");
    verify(favorites).remove("alice", FavoriteResourceType.PIPELINE, "pipeline");
  }

  @Test
  void publicResourcePreservesFavoriteAfterPersonalGrantIsRemoved() {
    var permission = new Permission();
    permission.setPublicElement(true);
    when(permissions.getUserPermissionsForObject("pipeline")).thenReturn(List.of(permission));
    cleanup.userPermissionsChanged("alice");
    verify(favorites, never()).remove("alice", FavoriteResourceType.PIPELINE, "pipeline");
  }

  @Test
  void adapterUsesItsDataStreamPermission() {
    var adapter = mock(AdapterDescription.class);
    when(adapter.getCorrespondingDataStreamElementId()).thenReturn("stream");
    when(adapters.getElementById("adapter")).thenReturn(adapter);
    when(favorites.findForUser("alice")).thenReturn(
        List.of(new Favorite("alice", FavoriteResourceType.ADAPTER, "adapter"))
    );
    when(permissions.getObjectPermissions(anyList())).thenReturn(Set.of("stream"));
    cleanup.userPermissionsChanged("alice");
    verify(favorites, never()).remove("alice", FavoriteResourceType.ADAPTER, "adapter");
    when(permissions.getObjectPermissions(anyList())).thenReturn(Set.of());
    cleanup.userPermissionsChanged("alice");
    verify(favorites).remove("alice", FavoriteResourceType.ADAPTER, "adapter");
  }

  @Test
  void crudDeletionRunsCleanupAfterExistingDeletionLogic() throws Exception {
    var storage = mock(IPipelineStorage.class);
    var genericStorage = mock(IGenericStorage.class);
    var pipeline = new Pipeline();
    when(storage.getElementById("pipeline")).thenReturn(pipeline);
    var lifecycle = mock(FavoriteCleanupManager.class);
    var permissionManager = new PermissionResourceManager(permissions, lifecycle);
    var crud = new CrudResourceManager<>(storage, Pipeline.class, permissionManager,
        new ResourceDeletionManager(genericStorage));
    crud.delete("pipeline");
    var order = inOrder(storage, genericStorage, permissions, lifecycle);
    order.verify(genericStorage).deleteAssetLinkToResource("pipeline");
    order.verify(storage).deleteElement(pipeline);
    order.verify(permissions).getUserPermissionsForObject("pipeline");
    order.verify(lifecycle).resourceDeleted(Pipeline.class, "pipeline");
  }

  @Test
  void failedDeletionDoesNotRemoveFavorites() {
    var storage = mock(IPipelineStorage.class);
    var pipeline = new Pipeline();
    when(storage.getElementById("pipeline")).thenReturn(pipeline);
    var lifecycle = mock(FavoriteCleanupManager.class);
    var crud = new CrudResourceManager<>(storage, Pipeline.class,
        new PermissionResourceManager(permissions, lifecycle),
        new ResourceDeletionManager(mock(IGenericStorage.class)));
    doThrow(new IllegalStateException("database unavailable")).when(storage).deleteElement(pipeline);
    assertThrows(IllegalStateException.class, () -> crud.delete("pipeline"));
    verifyNoInteractions(lifecycle);
  }

  @Test
  void adapterDeletionKeepsCertificateCleanupAndThenDeletesFavorites() throws Exception {
    var lifecycle = mock(FavoriteCleanupManager.class);
    var certificates = mock(ICertificateStorage.class);
    var genericStorage = mock(IGenericStorage.class);
    var adapter = mock(AdapterDescription.class);
    when(adapters.getElementById("adapter")).thenReturn(adapter);
    var adapterManager = new AdapterResourceManager(adapters, certificates,
        new PermissionResourceManager(permissions, lifecycle), new ResourceDeletionManager(genericStorage));
    adapterManager.delete("adapter");
    var order = inOrder(certificates, genericStorage, adapters, lifecycle);
    order.verify(certificates).findAll();
    order.verify(genericStorage).deleteAssetLinkToResource("adapter");
    order.verify(adapters).deleteElement(adapter);
    order.verify(lifecycle).resourceDeleted(AdapterDescription.class, "adapter");
  }

  @Test
  void permissionUpdateReconcilesAfterSaving() {
    var lifecycle = mock(FavoriteCleanupManager.class);
    var permission = new Permission();
    permission.setObjectClassName(Pipeline.class.getCanonicalName());
    new PermissionResourceManager(permissions, lifecycle).update(permission);
    var order = inOrder(permissions, lifecycle);
    order.verify(permissions).updateElement(permission);
    order.verify(lifecycle).permissionsChanged();
  }
}

