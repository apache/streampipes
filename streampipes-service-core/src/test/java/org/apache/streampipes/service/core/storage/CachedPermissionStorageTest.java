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
package org.apache.streampipes.service.core.storage;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.storage.api.user.IPermissionStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CachedPermissionStorageTest {

  private static final String PERMISSION_ID = "permission-id";
  private static final String OBJECT_ID = "object-id";

  private IPermissionStorage delegate;
  private CachedPermissionStorage storage;

  @BeforeEach
  void setUp() {
    delegate = mock(IPermissionStorage.class);
    var cacheManager = new ConcurrentMapCacheManager(
        CachedPermissionStorage.CACHE_NAME,
        CachedPermissionStorage.FIND_ALL_CACHE_NAME,
        CachedPermissionStorage.BY_OBJECT_CACHE_NAME,
        CachedPermissionStorage.BY_PRINCIPALS_CACHE_NAME
    );
    storage = new CachedPermissionStorage(delegate, cacheManager);
  }

  @Test
  void getUserPermissionsForObjectCachesSerializedCopies() {
    var permission = makePermission("owner");
    when(delegate.getUserPermissionsForObject(OBJECT_ID)).thenReturn(List.of(permission));

    var firstResult = storage.getUserPermissionsForObject(OBJECT_ID);
    firstResult.get(0).setOwnerSid("changed-owner");
    var secondResult = storage.getUserPermissionsForObject(OBJECT_ID);

    assertNotSame(firstResult, secondResult);
    assertNotSame(firstResult.get(0), secondResult.get(0));
    assertEquals("owner", secondResult.get(0).getOwnerSid());
    verify(delegate, times(1)).getUserPermissionsForObject(OBJECT_ID);
  }

  @Test
  void getObjectPermissionsUsesOrderIndependentPrincipalKey() {
    var firstOrder = new ArrayList<>(List.of("user", "group"));
    var secondOrder = new ArrayList<>(List.of("group", "user"));
    when(delegate.getObjectPermissions(firstOrder)).thenReturn(Set.of(OBJECT_ID));

    var firstResult = storage.getObjectPermissions(firstOrder);
    var secondResult = storage.getObjectPermissions(secondOrder);

    assertEquals(Set.of(OBJECT_ID), firstResult);
    assertEquals(Set.of(OBJECT_ID), secondResult);
    verify(delegate, times(1)).getObjectPermissions(firstOrder);
  }

  @Test
  void updateElementClearsAllQueryCaches() {
    var permission = makePermission("owner");
    var updatedPermission = makePermission("updated-owner");
    var sids = List.of("user");
    when(delegate.getElementById(PERMISSION_ID)).thenReturn(permission, updatedPermission);
    when(delegate.findAll()).thenReturn(List.of(permission), List.of(updatedPermission));
    when(delegate.getUserPermissionsForObject(OBJECT_ID))
        .thenReturn(List.of(permission), List.of(updatedPermission));
    when(delegate.getObjectPermissions(sids)).thenReturn(Set.of("old-object"), Set.of("new-object"));
    when(delegate.updateElement(updatedPermission)).thenReturn(updatedPermission);

    storage.getElementById(PERMISSION_ID);
    storage.findAll();
    storage.getUserPermissionsForObject(OBJECT_ID);
    storage.getObjectPermissions(sids);
    storage.updateElement(updatedPermission);

    assertEquals("updated-owner", storage.getElementById(PERMISSION_ID).getOwnerSid());
    assertEquals("updated-owner", storage.findAll().get(0).getOwnerSid());
    assertEquals("updated-owner", storage.getUserPermissionsForObject(OBJECT_ID).get(0).getOwnerSid());
    assertEquals(Set.of("new-object"), storage.getObjectPermissions(sids));
    verify(delegate, times(2)).getElementById(PERMISSION_ID);
    verify(delegate, times(2)).findAll();
    verify(delegate, times(2)).getUserPermissionsForObject(OBJECT_ID);
    verify(delegate, times(2)).getObjectPermissions(sids);
  }

  private Permission makePermission(String ownerSid) {
    var permission = new Permission();
    permission.setPermissionId(PERMISSION_ID);
    permission.setObjectInstanceId(OBJECT_ID);
    permission.setOwnerSid(ownerSid);
    return permission;
  }
}
