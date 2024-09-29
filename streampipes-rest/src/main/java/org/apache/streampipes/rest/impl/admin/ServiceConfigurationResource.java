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

import org.apache.streampipes.model.extensions.configuration.SpServiceConfiguration;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.CRUDStorage;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/extensions-services-configurations")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ServiceConfigurationResource extends AbstractAuthGuardedRestResource {

  private final CRUDStorage<SpServiceConfiguration> extensionsServicesConfigStorage = getNoSqlStorage()
          .getExtensionsServiceConfigurationStorage();

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SpServiceConfiguration>> getAllServiceConfigurations() {
    return ok(extensionsServicesConfigStorage.findAll());
  }

  @GetMapping(path = "{serviceGroup}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SpServiceConfiguration> getServiceConfiguration(
          @PathVariable("serviceGroup") String serviceGroup) {
    var config = extensionsServicesConfigStorage.getElementById(serviceGroup);
    if (config != null) {
      return ok(config);
    } else {
      throw new SpMessageException(HttpStatus.NOT_FOUND, Notifications.error("Configuration not found"));
    }
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> registerServiceConfiguration(@RequestBody SpServiceConfiguration serviceConfiguration) {
    if (extensionsServicesConfigStorage.getElementById(serviceConfiguration.getServiceGroup()) == null) {
      extensionsServicesConfigStorage.persist(serviceConfiguration);
      return created();
    } else {
      return ok();
    }
  }

  @PutMapping(path = "{serviceGroup}", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> registerServiceConfiguration(@PathVariable("serviceGroup") String serviceGroup,
          @RequestBody SpServiceConfiguration serviceConfiguration) {
    if (extensionsServicesConfigStorage.getElementById(serviceGroup) != null) {
      extensionsServicesConfigStorage.updateElement(serviceConfiguration);
      return ok();
    } else {
      return badRequest();
    }
  }
}
