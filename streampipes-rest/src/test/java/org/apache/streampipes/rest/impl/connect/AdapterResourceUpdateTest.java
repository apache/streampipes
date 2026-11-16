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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.connect.management.management.AdapterUpdateManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AdapterResourceUpdateTest {

  @Test
  void rejectsVictimAdapterWithOwnedStreamBeforeEitherOperation() throws Exception {
    checkDenied(adapter("victim", "foreign-stream"), adapter("victim", "owned-stream"));
  }

  @Test
  void rejectsOwnedAdapterWithChangedStreamBeforeEitherOperation() throws Exception {
    checkDenied(adapter("owned", "owned-stream"), adapter("owned", "foreign-stream"));
  }

  @Test
  void rejectsForeignAdapterEvenWithMatchingStream() throws Exception {
    checkDenied(adapter("victim", "foreign-stream"), adapter("victim", "foreign-stream"));
  }

  @Test
  void rejectsMissingAndMalformedTargetsIdentically() throws Exception {
    checkDenied(null, adapter("missing", "owned-stream"));
    checkDenied(adapter("owned", null), adapter("owned", null));
    checkDenied(null, adapter(null, "owned-stream"));
    checkDenied(null, null);
  }

  @Test
  void allowsAuthorizedUpdateAndPreflight() throws Exception {
    withUser(() -> {
      var submitted = adapter("owned", "owned-stream");
      var updates = mock(AdapterUpdateManagement.class);
      var resource = resource(adapter("owned", "owned-stream"), updates);
      when(updates.checkPipelineMigrations(submitted)).thenReturn(List.of());

      assertEquals(200, resource.updateAdapter(submitted).getStatusCode().value());
      assertEquals(200, resource.performPipelineMigrationPreflight(submitted).getStatusCode().value());
      verify(updates).updateAdapter(submitted, "user");
      verify(updates).checkPipelineMigrations(submitted);
    });
  }

  private void checkDenied(AdapterDescription stored, AdapterDescription submitted) throws Exception {
    withUser(() -> {
      var updates = mock(AdapterUpdateManagement.class);
      var resource = resource(stored, updates);
      var update = resource.updateAdapter(submitted);
      var preflight = resource.performPipelineMigrationPreflight(submitted);
      assertEquals(403, update.getStatusCode().value());
      assertEquals(403, preflight.getStatusCode().value());
      assertNull(update.getBody());
      assertNull(preflight.getBody());
      verifyNoInteractions(updates);
    });
  }

  private AdapterResource resource(AdapterDescription stored, AdapterUpdateManagement updates) throws Exception {
    var resources = mock(SpResourceManager.class, RETURNS_DEEP_STUBS);
    when(resources.manageAdapters().getDb().getElementById(anyString())).thenReturn(stored);
    when(resources.managePermissions().getDb().getUserPermissionsForObject(anyString())).thenReturn(List.of());
    var resource = mock(AdapterResource.class, CALLS_REAL_METHODS);
    for (var entry : java.util.Map.of("resourceManager", resources, "adapterUpdateManagement", updates).entrySet()) {
      var field = AdapterResource.class.getDeclaredField(entry.getKey());
      field.setAccessible(true);
      field.set(resource, entry.getValue());
    }
    return resource;
  }

  private static AdapterDescription adapter(String id, String stream) {
    var adapter = new AdapterDescription();
    adapter.setElementId(id);
    adapter.setCorrespondingDataStreamElementId(stream);
    return adapter;
  }

  private void withUser(CheckedAction action) throws Exception {
    var previous = SecurityContextHolder.getContext();
    try {
      var user = mock(PrincipalUserDetails.class, RETURNS_DEEP_STUBS);
      when(user.getDetails().getPrincipalId()).thenReturn("user");
      when(user.getAllObjectPermissions()).thenReturn(Set.of("owned-stream"));
      var context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(new UsernamePasswordAuthenticationToken(user, "unused", List.of()));
      SecurityContextHolder.setContext(context);
      action.run();
    } finally {
      SecurityContextHolder.setContext(previous);
    }
  }

  private interface CheckedAction {
    void run() throws Exception;
  }
}
