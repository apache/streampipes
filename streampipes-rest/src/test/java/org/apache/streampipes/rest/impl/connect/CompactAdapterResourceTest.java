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

import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.CompactAdapterManagement;
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.connect.adapter.compact.CreateOptions;
import org.apache.streampipes.storage.api.core.INoSqlStorage;
import org.apache.streampipes.storage.api.pipeline.ICompactPipelineTemplateStorage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class CompactAdapterResourceTest {

  private static final String USER_SID = "user-sid";
  private static final String ADAPTER_ID = "adapter-id";

  private AdapterMasterManagement adapterManagement;
  private CompactAdapterManagement compactAdapterManagement;
  private INoSqlStorage noSqlStorage;
  private CompactAdapterResource resource;

  @BeforeEach
  void setUp() throws Exception {
    adapterManagement = mock(AdapterMasterManagement.class);
    compactAdapterManagement = mock(CompactAdapterManagement.class);
    noSqlStorage = mock(INoSqlStorage.class);

    var adapterDescription = new AdapterDescription();
    adapterDescription.setElementId(ADAPTER_ID);
    adapterDescription.setName("adapter");
    when(compactAdapterManagement.convertToAdapterDescription(any(), eq(USER_SID)))
        .thenReturn(adapterDescription);
    when(adapterManagement.getAdapter(ADAPTER_ID)).thenReturn(adapterDescription);
    // no persist pipeline template stored, so the persist step ends with an exception
    when(noSqlStorage.getPipelineTemplateStorage()).thenReturn(mock(ICompactPipelineTemplateStorage.class));

    // the protected base class methods cannot be stubbed from this package
    resource = mock(CompactAdapterResource.class, withSettings().defaultAnswer(invocation ->
        switch (invocation.getMethod().getName()) {
          case "getAuthenticatedUserSid" -> USER_SID;
          case "getNoSqlStorage" -> noSqlStorage;
          default -> invocation.callRealMethod();
        }));
    resource.managementService = adapterManagement;
    setField(resource, "compactAdapterManagement", compactAdapterManagement);
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void rejectsPersistWithoutPipelineWritePrivilegeBeforeCreatingAnything() throws Exception {
    authenticateWith(DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE);

    var response = resource.addAdapterCompact(compactAdapter(new CreateOptions(true, true)));

    assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    assertNull(response.getBody());
    verifyNoInteractions(compactAdapterManagement, adapterManagement, noSqlStorage);
  }

  @Test
  void createsAdapterWithoutPersistWhenPipelineWritePrivilegeIsMissing() throws Exception {
    authenticateWith(DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE);

    var response = resource.addAdapterCompact(compactAdapter(new CreateOptions(false, true)));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapterManagement).addAdapter(any(), eq(ADAPTER_ID), eq(USER_SID));
    verify(adapterManagement).startAdapter(ADAPTER_ID, USER_SID);
    verifyNoInteractions(noSqlStorage);
  }

  @Test
  void createsAdapterWithoutCreateOptionsWhenPipelineWritePrivilegeIsMissing() throws Exception {
    authenticateWith(DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE);

    var response = resource.addAdapterCompact(compactAdapter(null));

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(adapterManagement).addAdapter(any(), eq(ADAPTER_ID), eq(USER_SID));
    verify(adapterManagement, never()).startAdapter(any(String.class));
    verify(adapterManagement, never()).startAdapter(any(String.class), any());
  }

  @Test
  void reachesPersistPipelineWithPipelineWritePrivilege() throws Exception {
    authenticateWith(
        DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE,
        DefaultPrivilege.Constants.PRIVILEGE_WRITE_PIPELINE_VALUE
    );

    assertPersistPipelineIsReached();
  }

  @Test
  void reachesPersistPipelineAsAdmin() throws Exception {
    authenticateWith(DefaultRole.Constants.ROLE_ADMIN_VALUE);

    assertPersistPipelineIsReached();
  }

  private void assertPersistPipelineIsReached() throws Exception {
    var exception = assertThrows(
        IllegalArgumentException.class,
        () -> resource.addAdapterCompact(compactAdapter(new CreateOptions(true, false)))
    );

    assertEquals("Could not start persist pipeline", exception.getMessage());
    verify(adapterManagement).addAdapter(any(), eq(ADAPTER_ID), eq(USER_SID));
    verify(adapterManagement).getAdapter(ADAPTER_ID);
  }

  private static void authenticateWith(String... authorities) {
    SecurityContextHolder.getContext()
                         .setAuthentication(new TestingAuthenticationToken("user", null, authorities));
  }

  private static CompactAdapter compactAdapter(CreateOptions createOptions) {
    return new CompactAdapter(ADAPTER_ID, "adapter", null, "app-id", null, null, null, createOptions);
  }

  private static void setField(Object target, String name, Object value) throws Exception {
    Field field = CompactAdapterResource.class.getDeclaredField(name);
    field.setAccessible(true);
    field.set(target, value);
  }
}
