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
package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.dataexplorer.export.ObjectStorge.ExportProviderFactory;
import org.apache.streampipes.dataexplorer.export.ObjectStorge.IObjectStorage;
import org.apache.streampipes.model.configuration.ExportProviderSettings;
import org.apache.streampipes.model.configuration.ProviderType;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.user.management.encryption.SecretEncryptionManager;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/admin/exportprovider-config")
public class ExportProviderConfigurationResource extends AbstractAuthGuardedRestResource {

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<List<ExportProviderSettings>> getExportProviderConfiguration() {
    return ok(getSpCoreConfigurationStorage().get().getExportProviderSettings());
  }

  @GetMapping(value = "/{providerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<ExportProviderSettings> getExportProviderSettingById(@PathVariable String providerId) {
    return getSpCoreConfigurationStorage().get().getExportProviderSettings().stream()
        .filter(setting -> setting.getProviderId().equalsIgnoreCase(providerId))
        .findFirst()
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping(value = "/test/{providerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<?> testExportProviderSettingById(@PathVariable String providerId) {
    // Get Export Provider Settings
    Optional<ExportProviderSettings> exportProviderSetting = getSpCoreConfigurationStorage().get()
        .getExportProviderSettings().stream()
        .filter(setting -> setting.getProviderId().equalsIgnoreCase(providerId))
        .findFirst();

    if (exportProviderSetting.isPresent()) {
      ExportProviderSettings setting = exportProviderSetting.get();
      ProviderType providerType = setting.getProviderType();

      try {

        IObjectStorage exportProvider = ExportProviderFactory.createExportProvider(
            providerType, "TEST", setting,
            "csv");
        
            String filePath = exportProvider.getFileName();

      String csvData = "Message\nThis Testfile was automatically creates as a connectivity test by Streampipes.\n";

      InputStream csvInputStream = new ByteArrayInputStream(csvData.getBytes());


      StreamingResponseBody responseBody = outputStream -> {
        byte[] buffer = new byte[1024];
        int length;
        while ((length = csvInputStream.read(buffer)) > 0) {
          outputStream.write(buffer, 0, length);
        }
      };
      try {
        exportProvider.store(responseBody);
      } catch (IOException e) {
        return serverError(SpLogMessage.from(e));

      }

           Map<String, Object> response = new HashMap<>();
            response.put("filePath", filePath);
            response.put("setting", setting);

      return ok(response);// ok(setting);



      } catch (Exception e) {
        return serverError(SpLogMessage.from(e));
      }

     
    } else {
      return serverError("No provider found.");
    }
  }

  @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> updateExportProviderConfiguration(@RequestBody ExportProviderSettings config) {

    if (!config.isSecretEncrypted()) {
      config.setSecretKey(SecretEncryptionManager.encrypt(config.getSecretKey()));
      config.setSecretEncrypted(true);
    }
    var storage = getSpCoreConfigurationStorage();
    var cfg = storage.get();

    List<ExportProviderSettings> providerSettings = cfg.getExportProviderSettings();
    if (providerSettings == null) {
      providerSettings = new ArrayList<>();
    }

    List<ExportProviderSettings> providerSettingsWithoutExisting = providerSettings.stream()
        .filter(existing -> existing != null && !existing.getProviderId().equals(config.getProviderId()))
        .collect(Collectors.toList());

    providerSettingsWithoutExisting.add(config);

    cfg.setExportProviderSettings(providerSettingsWithoutExisting);
    storage.updateElement(cfg);

    return ok();
  }

  @DeleteMapping(value = "/{providerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
  public ResponseEntity<Void> deleteExportProviderConfiguration(@PathVariable String providerId) {

    List<ExportProviderSettings> allProviders = getSpCoreConfigurationStorage().get().getExportProviderSettings();

    List<ExportProviderSettings> filteredProviders = allProviders.stream()
        .filter(provider -> !provider.getProviderId().equals(providerId))
        .collect(Collectors.toList());

    var storage = getSpCoreConfigurationStorage();
    var cfg = storage.get();
    cfg.setExportProviderSettings(filteredProviders);
    storage.updateElement(cfg);
    return ok();
  }

}
