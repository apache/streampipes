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
import org.apache.streampipes.model.client.user.UserAccount;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dataset.DataExplorerWidgetModel;
import org.apache.streampipes.model.favorites.Favorite;
import org.apache.streampipes.model.favorites.FavoriteResourceType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.storage.api.user.IFavoriteStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.storage.api.user.IRoleStorage;
import org.apache.streampipes.storage.api.user.IUserGroupStorage;
import org.apache.streampipes.storage.api.user.IUserStorage;
import org.apache.streampipes.user.management.model.UserAccountDetails;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.HashSet;
import java.util.Map;
import java.util.stream.Collectors;

public class FavoriteCleanupManager {

  private static final Map<Class<?>, FavoriteResourceType> RESOURCE_TYPES = Map.of(
      SpAssetModel.class, FavoriteResourceType.ASSET,
      AdapterDescription.class, FavoriteResourceType.ADAPTER,
      Pipeline.class, FavoriteResourceType.PIPELINE,
      DashboardModel.class, FavoriteResourceType.DASHBOARD,
      DataExplorerWidgetModel.class, FavoriteResourceType.CHART
  );

  private final IFavoriteStorage favorites;
  private final FavoriteResourceManager manager;
  private final IUserStorage users;
  private final IPermissionStorage permissions;
  private final IRoleStorage roles;
  private final IUserGroupStorage groups;

  public FavoriteCleanupManager(IFavoriteStorage favorites,
                                FavoriteResourceManager manager,
                                IUserStorage users,
                                IPermissionStorage permissions,
                                IRoleStorage roles,
                                IUserGroupStorage groups) {
    this.favorites = favorites;
    this.manager = manager;
    this.users = users;
    this.permissions = permissions;
    this.roles = roles;
    this.groups = groups;
  }

  public void resourceDeleted(Class<?> resourceClass, String resourceId) {
    var type = RESOURCE_TYPES.get(resourceClass);
    if (type != null) {
      favorites.findAll().stream()
          .filter(favorite -> type == favorite.getResourceType() && resourceId.equals(favorite.getResourceId()))
          .forEach(this::remove);
    }
  }

  public void userDeleted(String userId) {
    favorites.findForUser(userId).forEach(this::remove);
  }

  public void permissionsChanged() {
    favorites.findAll().stream().map(Favorite::getUserId).distinct().forEach(this::userPermissionsChanged);
  }

  public void userPermissionsChanged(String userId) {
    var userFavorites = favorites.findForUser(userId);
    if (userFavorites.isEmpty()) {
      return;
    }
    var principal = users.getUserById(userId);
    if (principal == null) {
      userFavorites.forEach(this::remove);
      return;
    }

    var snapshot = new UserAccount();
    snapshot.setPrincipalId(userId);
    snapshot.setRoles(new HashSet<>(principal.getRoles()));
    snapshot.setGroups(principal.getGroups().stream()
        .filter(groupId -> groups.getElementById(groupId) != null)
        .collect(Collectors.toCollection(HashSet::new)));
    var details = new UserAccountDetails(snapshot, permissions, roles, groups);
    var auth = new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
    userFavorites.stream()
        .filter(favorite -> !manager.canRead(auth, favorite.getResourceType(), favorite.getResourceId()))
        .forEach(this::remove);
  }

  private void remove(Favorite favorite) {
    favorites.remove(favorite.getUserId(), favorite.getResourceType(), favorite.getResourceId());
  }
}

