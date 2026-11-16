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

package org.apache.streampipes.rest.impl;

import org.apache.streampipes.audit.api.AuditOutcome;
import org.apache.streampipes.audit.api.AuditService;
import org.apache.streampipes.model.assets.SpAsset;
import org.apache.streampipes.model.assets.SpAssetModel;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.resource.management.AssetResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.storage.api.system.IAssetStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.user.management.model.PrincipalUserDetails;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AssetManagementResourceTest {

  @TempDir
  Path directory;

  private final IAssetStorage assetStorage = mock(IAssetStorage.class);
  private final AssetResourceManager assetResourceManager = mock(AssetResourceManager.class);
  private final AuditService auditService = mock(AuditService.class);

  private AssetManagementResource resource;
  private MockMvc mvc;
  private SpAssetModel model;

  @BeforeEach
  void setUp() {
    var configuration = new SpCoreConfiguration();
    configuration.setFilesDir(directory.toString());
    var configurationStorage = mock(ISpCoreConfigurationStorage.class);
    when(configurationStorage.get()).thenReturn(configuration);

    var resourceManager = mock(SpResourceManager.class);
    when(resourceManager.manageAssets()).thenReturn(assetResourceManager);
    when(resourceManager.getAuditService()).thenReturn(auditService);
    when(assetResourceManager.getDb()).thenReturn(assetStorage);

    model = new SpAssetModel();
    model.setElementId("document");
    model.setAssetId("root");
    when(assetStorage.getElementById("document")).thenReturn(model);
    when(assetResourceManager.find("document")).thenReturn(model);

    resource = new AssetManagementResource(resourceManager, configurationStorage);
    mvc = MockMvcBuilders.standaloneSetup(resource).build();
  }

  @AfterEach
  void clearAuthentication() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void answersNotFoundForAnImageTheAssetDoesNotReference() throws Exception {
    mvc.perform(get("/api/v2/assets/document/images/" + UUID.randomUUID()).param("assetId", "root"))
        .andExpect(status().isNotFound());
  }

  @Test
  void answersNotFoundWhenTheReferencedImageFileIsMissing() throws Exception {
    var imageId = UUID.randomUUID().toString();
    model.setImageId(imageId);

    mvc.perform(get("/api/v2/assets/document/images/" + imageId).param("assetId", "root"))
        .andExpect(status().isNotFound());
  }

  @Test
  void deletesTheAssetAndRecordsTheAuditEventEvenIfAnImageReferenceIsInvalid() {
    authenticateAs("user-sid");
    model.setImageId("not-an-image-id");
    var child = new SpAsset();
    child.setAssetId("child");
    child.setImageId("../../outside");
    model.setAssets(List.of(child));

    var response = resource.delete("document");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(assetResourceManager).delete("document");
    verify(auditService).record(any(), eq(AuditOutcome.SUCCEEDED), eq("user-sid"), eq("document"), any());
  }

  private void authenticateAs(String sid) {
    PrincipalUserDetails<?> principal = mock(PrincipalUserDetails.class, RETURNS_DEEP_STUBS);
    when(principal.getDetails().getPrincipalId()).thenReturn(sid);
    SecurityContextHolder.getContext()
        .setAuthentication(new UsernamePasswordAuthenticationToken(principal, null, List.of()));
  }
}
