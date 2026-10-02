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

package org.apache.streampipes.loadbalance.unit;

import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.user.management.jwt.JwtTokenProvider;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class InvokeHttpRequestTest {

  private final SpResourceManager resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);

  @BeforeEach
  void setUp() {
    SecurityContextHolder.clearContext();
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void createsTokenForOwnerFromInjectedPermissionStorage() {
    var permission = new Permission();
    permission.setOwnerSid("owner-1");
    var owner = mock(Principal.class);
    var permissions = resources.managePermissions().getDb();
    when(permissions.getUserPermissionsForObject("pipeline-1")).thenReturn(List.of(permission));
    when(resources.manageUsers().getDb().getUserById("owner-1")).thenReturn(owner);

    try (var providers = mockConstruction(JwtTokenProvider.class,
        (provider, context) -> when(provider.createToken(owner)).thenReturn("owner-token"))) {
      assertEquals("Bearer owner-token", InvokeHttpRequest.getAuthToken("pipeline-1", resources));
      verify(permissions).getUserPermissionsForObject("pipeline-1");
      verify(providers.constructed().getFirst()).createToken(owner);
    }
  }

  @Test
  void rejectsResourceWithoutOwner() {
    var permissions = resources.managePermissions().getDb();
    when(permissions.getUserPermissionsForObject("missing")).thenReturn(List.of());

    var error = assertThrows(IllegalArgumentException.class,
        () -> InvokeHttpRequest.getAuthToken("missing", resources));

    assertEquals("Could not find owner for resource missing", error.getMessage());
    verify(permissions).getUserPermissionsForObject("missing");
    verify(resources, never()).manageUsers();
  }

  @Test
  void rejectsUnauthenticatedRequestWithoutResource() {
    assertThrows(IllegalArgumentException.class, () -> InvokeHttpRequest.getAuthToken(null, resources));

    verify(resources, never()).managePermissions();
  }
}
