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
package org.apache.streampipes.manager.migration;

import org.apache.streampipes.manager.api.extensions.ExtensionServiceOperationResult;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.util.AuthTokenProvider;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.configuration.SpCoreConfiguration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.model.extensions.transport.ExtensionServiceBrokerOperations;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.migration.ModelMigratorConfig;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.UserResourceManager;
import org.apache.streampipes.storage.api.pipeline.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.api.system.ISpCoreConfigurationStorage;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MigrationAssetUpdateTest {

  private static final String APP_ID = "org.apache.streampipes.sinks.internal.jvm.dataset";

  @TempDir
  Path directory;

  private SpResourceManager resources;
  private IPipelineElementDescriptionStorage descriptions;
  private ISpServiceDiscovery discovery;
  private SpServiceRegistration service;

  @BeforeEach
  void setUp() {
    resources = mock(SpResourceManager.class);
    descriptions = mock(IPipelineElementDescriptionStorage.class);
    discovery = mock(ISpServiceDiscovery.class);
    when(resources.getServiceDiscovery()).thenReturn(discovery);
    var configurationStorage = mock(ISpCoreConfigurationStorage.class);
    var configuration = new SpCoreConfiguration();
    configuration.setAssetDir(directory.toString());
    when(configurationStorage.get()).thenReturn(configuration);
    when(resources.getCoreConfigurationStorage()).thenReturn(configurationStorage);
    service = new SpServiceRegistration();
    service.setSvcId("streampipes-current-instance");
    service.setHost("localhost");
    service.setPort(8090);
  }

  @Test
  void migrationFetchesAssetsFromTheServiceProvidingTheDescription() throws Exception {
    var archive = assetArchive();
    ExtensionServiceRequestManager requests = request -> {
      assertEquals(service.getSvcId(), request.target().serviceId());
      if (request.target().operation().equals(ExtensionServiceBrokerOperations.PIPELINE_ELEMENT_ASSETS.operationId())) {
        return new ExtensionServiceOperationResult(200, archive);
      }
      return new ExtensionServiceOperationResult(200, description().getBytes(StandardCharsets.UTF_8));
    };
    var manager = new AbstractMigrationManager(requests, resources, descriptions) {
      @Override
      protected boolean isInstalled(SpServiceTagPrefix modelType, String appId) {
        return true;
      }
    };

    var users = mock(UserResourceManager.class);
    when(resources.manageUsers()).thenReturn(users);
    when(users.getServiceAdmin()).thenReturn(mock(Principal.class));
    try (var tokens = mockConstruction(AuthTokenProvider.class)) {
      manager.updateDescriptions(List.of(new ModelMigratorConfig(APP_ID, SpServiceTagPrefix.DATA_SINK, 1, 2)), service);
    }

    assertEquals("new icon", Files.readString(directory.resolve(APP_ID).resolve("icon.png")));
    verify(descriptions).update(any(DataSinkDescription.class));
    verifyNoInteractions(discovery);
  }

  private String description() {
    return "{\"@class\":\"" + DataSinkDescription.class.getCanonicalName()
        + "\",\"appId\":\"" + APP_ID + "\",\"elementId\":\"test-element\",\"includesAssets\":true}";
  }

  private byte[] assetArchive() throws IOException {
    var bytes = new ByteArrayOutputStream();
    try (var zip = new ZipOutputStream(bytes)) {
      zip.putNextEntry(new ZipEntry("icon.png"));
      zip.write("new icon".getBytes(StandardCharsets.UTF_8));
      zip.closeEntry();
      zip.putNextEntry(new ZipEntry("documentation.md"));
      zip.write("documentation".getBytes(StandardCharsets.UTF_8));
      zip.closeEntry();
    }
    return bytes.toByteArray();
  }
}
