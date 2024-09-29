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

import org.apache.streampipes.manager.health.ServiceRegistrationManager;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/extensions-services")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ServiceRegistrationResource extends AbstractAuthGuardedRestResource {

  private final CRUDStorage<SpServiceRegistration> extensionsServiceStorage = getNoSqlStorage()
          .getExtensionsServiceStorage();

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SpServiceRegistration>> getRegisteredServices() {
    return ok(extensionsServiceStorage.findAll());
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> registerService(@RequestBody SpServiceRegistration serviceRegistration) {
    new ServiceRegistrationManager(extensionsServiceStorage).addService(serviceRegistration,
            SpServiceStatus.REGISTERED);
    return ok();
  }

  @PostMapping(path = "/{serviceId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> unregisterService(@PathVariable("serviceId") String serviceId) {
    try {
      new ServiceRegistrationManager(extensionsServiceStorage).removeService(serviceId);
      return ok();
    } catch (IllegalArgumentException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST,
              Notifications.error("Could not find registered service with id " + serviceId));
    }
  }
}
