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

import org.apache.streampipes.model.configuration.ExportProviderSettings;
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

import java.util.ArrayList;
import java.util.List;
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
