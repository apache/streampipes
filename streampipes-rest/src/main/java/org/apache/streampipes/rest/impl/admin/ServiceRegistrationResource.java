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

import org.apache.streampipes.health.monitoring.ExtensionInstanceDetailsProvider;
import org.apache.streampipes.health.monitoring.ExtensionInstanceRemovalService;
import org.apache.streampipes.health.monitoring.ResourceProvider;
import org.apache.streampipes.health.monitoring.ServiceRegistrationManager;
import org.apache.streampipes.health.monitoring.model.RunningExtensionInstances;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceStatus;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v2/extensions-services")
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ServiceRegistrationResource extends AbstractAuthGuardedRestResource {

  private final IExtensionsServiceStorage extensionsServiceStorage =
      getNoSqlStorage().getExtensionsServiceStorage();
  private final ExtensionServiceRequestManager extensionServiceRequestManager;
  private final SpResourceManager resourceManager;

  public ServiceRegistrationResource(ExtensionServiceRequestManager extensionServiceRequestManager,
                                     SpResourceManager resourceManager) {
    this.extensionServiceRequestManager = extensionServiceRequestManager;
    this.resourceManager = resourceManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SpServiceRegistration>> getRegisteredServices() {
    return ok(extensionsServiceStorage.findAll());
  }

  @GetMapping(path = "/{serviceId}/running-instances", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RunningExtensionInstances> getRunningInstances(@PathVariable("serviceId") String serviceId) {
    return ok(new ExtensionInstanceDetailsProvider(
        extensionsServiceStorage,
        makeResourceProvider(),
        extensionServiceRequestManager,
        resourceManager
    ).getRunningInstances(serviceId));
  }

  @DeleteMapping(path = "/{serviceId}/running-instances", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> removeAllRunningInstances(@PathVariable("serviceId") String serviceId) {
    try {
      makeRemovalService().removeAllInstances(serviceId);
      return ok();
    } catch (IOException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }

  @DeleteMapping(path = "/{serviceId}/running-instances/adapters/{instanceId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> removeAdapterInstance(@PathVariable("serviceId") String serviceId,
                                                    @PathVariable("instanceId") String instanceId) {
    try {
      makeRemovalService().removeAdapterInstance(serviceId, instanceId);
      return ok();
    } catch (IOException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }

  @DeleteMapping(path = "/{serviceId}/running-instances/pipeline-elements/{instanceId}",
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> removePipelineElementInstance(@PathVariable("serviceId") String serviceId,
                                                           @PathVariable("instanceId") String instanceId) {
    try {
      makeRemovalService().removePipelineElementInstance(serviceId, instanceId);
      return ok();
    } catch (IOException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
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

  private ResourceProvider makeResourceProvider() {
    return new ResourceProvider(
        resourceManager.managePipelines().getDb(),
        resourceManager.manageAdapters().getDb(),
        null
    );
  }

  private ExtensionInstanceRemovalService makeRemovalService() {
    return new ExtensionInstanceRemovalService(
        extensionsServiceStorage,
        makeResourceProvider(),
        extensionServiceRequestManager,
        resourceManager
    );
  }
}
