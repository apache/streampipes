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

import org.apache.streampipes.health.monitoring.AdapterHealthStatusStore;
import org.apache.streampipes.manager.api.extensions.AdapterHealthStatusManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterHealthStatus;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/adapter-health")
public class AdapterHealthResource extends AbstractAuthGuardedRestResource {

  private final IAdapterStorage adapterStorage;
  private final AdapterHealthStatusManager healthStatusManager;
  private final SpPermissionEvaluator permissionEvaluator;

  public AdapterHealthResource(IAdapterStorage adapterStorage,
                               SpPermissionEvaluator permissionEvaluator,
                               ExtensionServiceRequestManager extensionServiceRequestManager,
                               SpResourceManager resourceManager) {
    this.adapterStorage = adapterStorage;
    this.healthStatusManager = new AdapterHealthStatusManager(
        getNoSqlStorage().getExtensionsServiceStorage(),
        extensionServiceRequestManager,
        resourceManager
    );
    this.permissionEvaluator = permissionEvaluator;
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<List<AdapterHealthStatus>> getAllAdapterHealth() {
    var runningAdapters = adapterStorage.findAll().stream()
        .filter(AdapterDescription::isRunning)
        .filter(adapter -> checkAdapterPermission(adapter, "READ"))
        .toList();

    return ok(healthStatusManager.getHealthStatuses(runningAdapters));
  }

  @GetMapping(value = "/{adapterId}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<AdapterHealthStatus> getAdapterHealth(@PathVariable String adapterId) {
    var adapter = adapterStorage.getElementById(adapterId);
    if (adapter == null || !checkAdapterPermission(adapter, "READ")) {
      return ResponseEntity.notFound().build();
    }

    var healthStatus = healthStatusManager.getHealthStatus(adapter);
    AdapterHealthStatusStore.INSTANCE.updateHealthStatus(adapterId, healthStatus.getOverallStatus());
    return ok(healthStatus);
  }

  @PostMapping(value = "/{adapterId}/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<Void> triggerAdapterHealthCheck(@PathVariable String adapterId) {
    var adapter = adapterStorage.getElementById(adapterId);
    if (adapter != null && adapter.isRunning() && checkAdapterPermission(adapter, "READ")) {
      healthStatusManager.triggerHealthCheck(adapter);
    }
    return ResponseEntity.ok().build();
  }

  /**
   * required by Spring expression
   */
  public boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_READ_ADAPTER_VALUE);
  }

  private boolean checkAdapterPermission(AdapterDescription adapterDescription,
                                         String permission) {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    return permissionEvaluator.hasPermission(
        authentication,
        adapterDescription.getCorrespondingDataStreamElementId(),
        permission);
  }
}
