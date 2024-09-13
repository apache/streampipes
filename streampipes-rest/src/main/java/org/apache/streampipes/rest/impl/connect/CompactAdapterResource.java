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

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.compact.AdapterGenerationSteps;
import org.apache.streampipes.connect.management.compact.PersistPipelineHandler;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.AdapterUpdateManagement;
import org.apache.streampipes.connect.management.management.CompactAdapterManagement;
import org.apache.streampipes.manager.template.PipelineTemplateManagement;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.exception.BadRequestException;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/connect/compact-adapters")
public class CompactAdapterResource extends AbstractAdapterResource<AdapterMasterManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(CompactAdapterResource.class);
  private final AdapterGenerationSteps adapterGenerationSteps;
  private final AdapterUpdateManagement adapterUpdateManagement;

  public CompactAdapterResource() {
    super(() -> new AdapterMasterManagement(
        StorageDispatcher.INSTANCE.getNoSqlStore()
            .getAdapterInstanceStorage(),
        new SpResourceManager().manageAdapters(),
        new SpResourceManager().manageDataStreams(),
        AdapterMetricsManager.INSTANCE.getAdapterMetrics()
    ));
    this.adapterGenerationSteps = new AdapterGenerationSteps();
    this.adapterUpdateManagement = new AdapterUpdateManagement(managementService);
  }

  @PostMapping(
      consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          "application/yaml",
          "application/yml"
      }
  )
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public ResponseEntity<?> addAdapterCompact(
      @RequestBody CompactAdapter compactAdapter
  ) throws Exception {

    var adapterDescription = getGeneratedAdapterDescription(compactAdapter);
    var principalSid = getAuthenticatedUserSid();

    try {
      var adapterId = adapterDescription.getElementId();
      managementService.addAdapter(adapterDescription, adapterId, principalSid);
      if (compactAdapter.createOptions() != null) {
        if (compactAdapter.createOptions().persist()) {
          var storedAdapter = managementService.getAdapter(adapterId);
          var status = new PersistPipelineHandler(
              new PipelineTemplateManagement(),
              getAuthenticatedUserSid()
          ).createAndStartPersistPipeline(storedAdapter);
        }
        if (compactAdapter.createOptions().start()) {
          managementService.startStreamAdapter(adapterId);
        }
      }
      return ok(Notifications.success(adapterId));
    } catch (AdapterException e) {
      LOG.error("Error while starting adapter with id {}", adapterDescription.getAppId(), e);
      return ok(Notifications.error(e.getMessage()));
    }
  }

  @PutMapping(
      path = "{id}",
      consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          "application/yaml",
          "application/yml"
      }
  )
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public ResponseEntity<?> updateAdapterCompact(
      @PathVariable("id") String elementId,
      @RequestBody CompactAdapter compactAdapter
  ) throws Exception {

    var existingAdapter = managementService.getAdapter(elementId);
    if (existingAdapter != null) {
      var adapterDescription = getGeneratedAdapterDescription(compactAdapter, existingAdapter);

      try {
        adapterUpdateManagement.updateAdapter(adapterDescription);
      } catch (AdapterException e) {
        LOG.error("Error while updating adapter with id {}", adapterDescription.getElementId(), e);
        return ok(Notifications.error(e.getMessage()));
      }

      return ok(Notifications.success(adapterDescription.getElementId()));
    } else {
      throw new BadRequestException(String.format("Adapter with id %s not found", elementId));
    }
  }

  private AdapterDescription getGeneratedAdapterDescription(CompactAdapter compactAdapter) throws Exception {
    var generators = adapterGenerationSteps.getGenerators();
    return new CompactAdapterManagement(generators).convertToAdapterDescription(compactAdapter);
  }

  private AdapterDescription getGeneratedAdapterDescription(CompactAdapter compactAdapter,
                                                            AdapterDescription existingAdapter) throws Exception {
    var generators = adapterGenerationSteps.getGenerators();
    return new CompactAdapterManagement(generators).convertToAdapterDescription(compactAdapter, existingAdapter);
  }
}
