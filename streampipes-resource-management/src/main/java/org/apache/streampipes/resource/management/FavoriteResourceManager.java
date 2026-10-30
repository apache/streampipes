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

import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.favorites.Favorite;
import org.apache.streampipes.model.favorites.FavoriteResourceType;
import org.apache.streampipes.model.favorites.FavoriteSummary;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.user.IFavoriteStorage;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

public class FavoriteResourceManager {

  private final IFavoriteStorage favorites;
  private final IAssetStorage assets;
  private final IAdapterStorage adapters;
  private final IPipelineStorage pipelines;
  private final IDashboardStorage dashboards;
  private final IChartStorage charts;
  private final SpPermissionEvaluator permissions;

  public FavoriteResourceManager(IFavoriteStorage favorites,
                                 IAssetStorage assets,
                                 IAdapterStorage adapters,
                                 IPipelineStorage pipelines,
                                 IDashboardStorage dashboards,
                                 IChartStorage charts,
                                 SpPermissionEvaluator permissions) {
    this.favorites = favorites;
    this.assets = assets;
    this.adapters = adapters;
    this.pipelines = pipelines;
    this.dashboards = dashboards;
    this.charts = charts;
    this.permissions = permissions;
  }

  public List<FavoriteSummary> list(Authentication authentication) {
    String userId = userId(authentication);
    return favorites.findForUser(userId).stream()
        .filter(favorite -> userId.equals(favorite.getUserId()))
        .map(favorite -> summarize(authentication, favorite.getResourceType(), favorite.getResourceId()))
        .toList();
  }

  public FavoriteSummary add(Authentication authentication, FavoriteResourceType type, String resourceId) {
    validate(type, resourceId);
    String userId = userId(authentication);
    if (!canRead(authentication, type, resourceId)) {
      throw new AccessDeniedException("Resource is not accessible");
    }
    var summary = summarize(authentication, type, resourceId);
    if (!summary.available()) {
      throw new NoSuchElementException("Resource no longer exists");
    }
    favorites.add(new Favorite(userId, type, resourceId));
    return summary;
  }

  public void remove(Authentication authentication, FavoriteResourceType type, String resourceId) {
    validate(type, resourceId);
    favorites.remove(userId(authentication), type, resourceId);
  }

  private FavoriteSummary summarize(Authentication auth, FavoriteResourceType type, String id) {
    if (type == null || id == null || !canRead(auth, type, id)) {
      return new FavoriteSummary(type, id, null, false);
    }
    String name = switch (type) {
      case ASSET -> {
        var asset = assets.getElementById(id);
        yield asset == null ? null : Objects.requireNonNullElse(asset.getAssetName(), id);
      }
      case ADAPTER -> {
        var adapter = adapters.getElementById(id);
        yield adapter == null ? null : Objects.requireNonNullElse(adapter.getName(), id);
      }
      case PIPELINE -> {
        var pipeline = pipelines.getElementById(id);
        yield pipeline == null ? null : Objects.requireNonNullElse(pipeline.getName(), id);
      }
      case DASHBOARD -> {
        var dashboard = dashboards.getElementById(id);
        yield dashboard == null ? null : Objects.requireNonNullElse(dashboard.getName(), id);
      }
      case CHART -> {
        var chart = charts.getElementById(id);
        if (chart == null) {
          yield null;
        }
        var appearance = chart.getBaseAppearanceConfig();
        yield appearance != null && appearance.get("widgetTitle") instanceof String title ? title : id;
      }
    };
    return new FavoriteSummary(type, id, name, name != null);
  }

  boolean canRead(Authentication auth, FavoriteResourceType type, String id) {
    String privilege = switch (type) {
      case ASSET -> null;
      case ADAPTER -> "PRIVILEGE_READ_ADAPTER";
      case PIPELINE -> "PRIVILEGE_READ_PIPELINE";
      case DASHBOARD -> "PRIVILEGE_READ_DASHBOARD";
      case CHART -> "PRIVILEGE_READ_DATA_EXPLORER_VIEW";
    };
    boolean hasRole = privilege == null || auth.getAuthorities().stream()
        .anyMatch(authority -> authority.getAuthority().equals(privilege)
            || authority.getAuthority().equals(DefaultRole.Constants.ROLE_ADMIN_VALUE)
            || authority.getAuthority().equals(DefaultRole.Constants.ROLE_SERVICE_ADMIN_VALUE));
    if (!hasRole) {
      return false;
    }
    if (type == FavoriteResourceType.ADAPTER) {
      var adapter = adapters.getElementById(id);
      return adapter != null && adapter.getCorrespondingDataStreamElementId() != null
          && permissions.hasPermission(auth, adapter.getCorrespondingDataStreamElementId(), "READ");
    }
    return permissions.hasPermission(auth, id, "READ");
  }

  private String userId(Authentication auth) {
    if (auth == null || !auth.isAuthenticated()
        || !(auth.getPrincipal() instanceof PrincipalUserDetails<?> principal)) {
      throw new AccessDeniedException("Authentication required");
    }
    return principal.getDetails().getPrincipalId();
  }

  private void validate(FavoriteResourceType type, String resourceId) {
    if (type == null || resourceId == null || resourceId.isBlank() || resourceId.length() > 2048) {
      throw new IllegalArgumentException("A resource type and non-empty resource ID are required");
    }
  }
}
