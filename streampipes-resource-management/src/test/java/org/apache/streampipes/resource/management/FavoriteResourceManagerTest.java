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
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.dashboard.DashboardModel;
import org.apache.streampipes.model.dataset.DataExplorerWidgetModel;
import org.apache.streampipes.model.favorites.Favorite;
import org.apache.streampipes.model.favorites.FavoriteResourceType;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.explorer.IChartStorage;
import org.apache.streampipes.storage.api.explorer.IDashboardStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.user.IFavoriteStorage;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class FavoriteResourceManagerTest {

  private final IFavoriteStorage favorites = mock(IFavoriteStorage.class);
  private final IAssetStorage assets = mock(IAssetStorage.class);
  private final IAdapterStorage adapters = mock(IAdapterStorage.class);
  private final IPipelineStorage pipelines = mock(IPipelineStorage.class);
  private final IDashboardStorage dashboards = mock(IDashboardStorage.class);
  private final IChartStorage charts = mock(IChartStorage.class);
  private final SpPermissionEvaluator permissions = mock(SpPermissionEvaluator.class);
  private final FavoriteResourceManager manager = new FavoriteResourceManager(
      favorites, assets, adapters, pipelines, dashboards, charts, permissions
  );
  private Authentication auth;

  @BeforeEach
  void setUp() {
    auth = authentication("alice", "ROLE_ADMIN");
    when(permissions.hasPermission(eq(auth), anyString(), eq("READ"))).thenReturn(true);
  }

  @Test
  void addsAllFiveResourceTypesWithAuthenticatedOwner() {
    var asset = new SpAssetModel();
    asset.setAssetName("Asset");
    when(assets.getElementById("resource")).thenReturn(asset);
    var adapter = mock(AdapterDescription.class);
    when(adapter.getName()).thenReturn("Adapter");
    when(adapter.getCorrespondingDataStreamElementId()).thenReturn("resource");
    when(adapters.getElementById("resource")).thenReturn(adapter);
    var pipeline = new Pipeline();
    pipeline.setName("Pipeline");
    when(pipelines.getElementById("resource")).thenReturn(pipeline);
    var dashboard = new DashboardModel();
    dashboard.setName("Dashboard");
    when(dashboards.getElementById("resource")).thenReturn(dashboard);
    var chart = new DataExplorerWidgetModel();
    chart.setBaseAppearanceConfig(Map.of("widgetTitle", "Chart"));
    when(charts.getElementById("resource")).thenReturn(chart);

    for (var type : FavoriteResourceType.values()) {
      assertTrue(manager.add(auth, type, "resource").available());
    }
    var captor = ArgumentCaptor.forClass(Favorite.class);
    verify(favorites, org.mockito.Mockito.times(5)).add(captor.capture());
    assertTrue(captor.getAllValues().stream().allMatch(f -> f.getUserId().equals("alice")));
    assertEquals(5, captor.getAllValues().stream().map(Favorite::getResourceType).distinct().count());
  }

  @Test
  void deletedResourceIsUnavailableAndCanStillBeRemoved() {
    when(favorites.findForUser("alice"))
        .thenReturn(List.of(new Favorite("alice", FavoriteResourceType.ASSET, "deleted")));
    var result = manager.list(auth).getFirst();
    assertFalse(result.available());
    assertNull(result.name());
    manager.remove(auth, FavoriteResourceType.ASSET, "deleted");
    verify(favorites).remove("alice", FavoriteResourceType.ASSET, "deleted");
  }

  @Test
  void revokedPermissionDoesNotResolveOrDiscloseResourceName() {
    when(favorites.findForUser("alice"))
        .thenReturn(List.of(new Favorite("alice", FavoriteResourceType.ASSET, "private")));
    when(permissions.hasPermission(auth, "private", "READ")).thenReturn(false);
    var result = manager.list(auth).getFirst();
    assertFalse(result.available());
    assertNull(result.name());
    verifyNoInteractions(assets);
    verify(favorites, never()).remove("alice", FavoriteResourceType.ASSET, "private");
    assertThrows(AccessDeniedException.class, () -> manager.add(auth, FavoriteResourceType.ASSET, "private"));
    verify(favorites, never()).add(any());
  }

  @Test
  void resourceReadPrivilegeIsRequiredEvenWithObjectPermission() {
    auth = authentication("alice", "PRIVILEGE_READ_ASSETS");
    assertThrows(AccessDeniedException.class, () -> manager.add(auth, FavoriteResourceType.PIPELINE, "pipeline"));
    verifyNoInteractions(pipelines);
  }

  @Test
  void listsOnlyCurrentUsersRelationships() {
    when(favorites.findForUser("alice")).thenReturn(List.of(
        new Favorite("bob", FavoriteResourceType.ASSET, "other-users-favorite")
    ));
    assertTrue(manager.list(auth).isEmpty());
    verifyNoInteractions(assets);
  }

  @Test
  void missingResourceCannotBeAdded() {
    assertThrows(NoSuchElementException.class, () -> manager.add(auth, FavoriteResourceType.ASSET, "deleted"));
    verify(favorites, never()).add(any());
  }

  @Test
  void rejectsBlankIdsAndUnauthenticatedRequests() {
    assertThrows(IllegalArgumentException.class, () -> manager.add(auth, FavoriteResourceType.ASSET, " "));
    assertThrows(AccessDeniedException.class, () -> manager.list(null));
    verifyNoInteractions(favorites);
  }

  private Authentication authentication(String id, String authority) {
    var principal = mock(PrincipalUserDetails.class, RETURNS_DEEP_STUBS);
    when(principal.getDetails().getPrincipalId()).thenReturn(id);
    return new UsernamePasswordAuthenticationToken(principal, null, List.of(new SimpleGrantedAuthority(authority)));
  }
}
