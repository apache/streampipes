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

import org.apache.streampipes.loadbalance.pipeline.ExtensionsLogProvider;
import org.apache.streampipes.loadbalance.pipeline.ExtensionsServiceLogExecutor;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.user.DefaultPrivilege;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.monitoring.SpMetricsEntry;
import org.apache.streampipes.rest.security.SpPermissionEvaluator;
import org.apache.streampipes.storage.api.IAdapterStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/adapter-monitoring")
public class AdapterMonitoringResource extends AbstractMonitoringResource {

  private final IAdapterStorage adapterStorage;

  public AdapterMonitoringResource() {
    this.adapterStorage = StorageDispatcher.INSTANCE.getNoSqlStore().getAdapterInstanceStorage();
  }

  @GetMapping(
      path = "adapter/{elementId}/logs",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<?> getLogInfoForAdapter(
      @PathVariable("elementId") String elementId
  ) {
    var adapterDescription = getAdapter(elementId);
    if (checkAdapterPermission(adapterDescription, "READ")) {
      return ok(ExtensionsLogProvider.INSTANCE.getLogInfosForResource(elementId));
    } else {
      return unauthorized();
    }
  }

  @GetMapping(
      path = "adapter/{elementId}/metrics",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<?> getMetricsInfoForAdapter(
      @PathVariable("elementId") String elementId
  ) {
    var adapterDescription = getAdapter(elementId);
    if (checkAdapterPermission(adapterDescription, "READ")) {
      return ok(ExtensionsLogProvider.INSTANCE.getMetricInfosForResource(elementId));
    } else {
      return unauthorized();
    }
  }

  @GetMapping(
      path = "metrics",
      produces = MediaType.APPLICATION_JSON_VALUE
  )
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<Map<String, SpMetricsEntry>> getMetricsInfos(
      @RequestParam(value = "filter") List<String> elementIds
  ) {
    new ExtensionsServiceLogExecutor().triggerUpdate();
    var filteredElementIds = elementIds.stream()
        .map(adapterStorage::getElementById)
        .filter(a -> checkAdapterPermission(a, "READ"))
        .map(NamedStreamPipesEntity::getElementId)
        .toList();
    return ok(ExtensionsLogProvider.INSTANCE.getMetricsInfoForResources(filteredElementIds));
  }

  /**
   * required by Spring expression
   */
  public boolean hasReadAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_READ_ADAPTER_VALUE);
  }

  /**
   * required by Spring expression
   */
  public boolean hasWriteAuthority() {
    return isAdminOrHasAnyAuthority(DefaultPrivilege.Constants.PRIVILEGE_WRITE_ADAPTER_VALUE);
  }

  public AdapterDescription getAdapter(String elementId) {
    return adapterStorage.getElementById(elementId);
  }

  /**
   * Checks if the current user has the permission to read the adapter
   */
  private boolean checkAdapterPermission(AdapterDescription adapterDescription,
                                         String permission) {
    var spPermissionEvaluator = new SpPermissionEvaluator();
    var authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    return spPermissionEvaluator.hasPermission(
        authentication,
        adapterDescription.getCorrespondingDataStreamElementId(),
        permission);
  }
}
