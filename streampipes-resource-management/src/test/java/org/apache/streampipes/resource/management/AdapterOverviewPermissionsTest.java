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
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdapterOverviewPermissionsTest {

  @Test
  void adminDoesNotLoadObjectPermissions() {
    var storage = mock(IPermissionStorage.class);
    var principal = mock(PrincipalUserDetails.class);
    doReturn(List.of(new SimpleGrantedAuthority(DefaultRole.Constants.ROLE_ADMIN_VALUE)))
        .when(principal).getAuthorities();
    var auth = new UsernamePasswordAuthenticationToken(principal, null);

    assertTrue(new SpPermissionEvaluator(storage).hasPermission(auth, "adapter-stream", "READ"));
    verifyNoInteractions(storage);
  }

  @Test
  void bulkMetricsOnlyIncludesRequestedReadableAdapters() {
    var storage = mock(IAdapterStorage.class);
    var permissions = mock(IPermissionStorage.class);
    var permissionManager = mock(PermissionResourceManager.class);
    when(permissionManager.getDb()).thenReturn(permissions);
    var manager = new AdapterResourceManager(storage, null, permissionManager, null);
    var principal = mock(PrincipalUserDetails.class);
    when(principal.getAllObjectPermissions()).thenReturn(Set.of("stream-owned"));
    var auth = new UsernamePasswordAuthenticationToken(principal, null);
    var publicPermission = new Permission();
    publicPermission.setPublicElement(true);
    when(permissions.getUserPermissionsForObject("stream-public")).thenReturn(List.of(publicPermission));
    when(storage.findAll()).thenReturn(List.of(
        adapter("owned"), adapter("private"), adapter("public"), adapter("unrequested")));

    assertEquals(List.of("owned", "public"), manager.getReadableAdapterIds(
        auth, List.of("owned", "private", "public", "missing", "owned")));
  }

  @Test
  void emptyMetricsRequestDoesNotLoadAdapters() {
    var storage = mock(IAdapterStorage.class);
    var manager = new AdapterResourceManager(storage, null, mock(PermissionResourceManager.class), null);
    assertEquals(List.of(), manager.getReadableAdapterIds(null, List.of()));
    verifyNoInteractions(storage);
  }

  private AdapterDescription adapter(String id) {
    var adapter = new AdapterDescription();
    adapter.setElementId(id);
    adapter.setCorrespondingDataStreamElementId("stream-" + id);
    return adapter;
  }
}
