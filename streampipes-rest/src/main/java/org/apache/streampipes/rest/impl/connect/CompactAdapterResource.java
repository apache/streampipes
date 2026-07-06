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
import org.apache.streampipes.connect.management.management.GuessManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.pipeline.compact.CompactPipelineManagement;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.manager.pipeline.update.PipelineUpdateCoordinator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.rest.shared.constants.SpMediaType;
import org.apache.streampipes.rest.shared.exception.BadRequestException;
import org.apache.streampipes.rest.shared.exception.SpMessageException;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
  private final CompactAdapterManagement compactAdapterManagement;
  private final AdapterUpdateManagement adapterUpdateManagement;
  private final ExtensionServiceRequestManager requestManager;
  private final PipelineManager pipelineManager;

  public CompactAdapterResource(WorkerRestClient workerRestClient,
                                ExtensionServiceRequestManager requestManager,
                                SpResourceManager resourceManager) {
    super(() -> new AdapterMasterManagement(
        resourceManager,
        AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
        workerRestClient,
        StorageDispatcher.INSTANCE.getNoSqlStore().getExtensionsServiceStorage(),
        requestManager
    ));
    var guessManagement = new GuessManagement(
        new ExtensionsServiceEndpointGenerator(),
        requestManager,
        resourceManager
    );
    this.requestManager = requestManager;
    this.compactAdapterManagement = new CompactAdapterManagement(
        new AdapterGenerationSteps(guessManagement).getGenerators()
    );
    this.pipelineManager = new PipelineManager(
        resourceManager
    );
    var pipelineUpdateCoordinator = new PipelineUpdateCoordinator(
        requestManager,
        resourceManager,
        new ChartSchemaUpdateCoordinator(resourceManager.manageCharts().getDb()),
        pipelineManager
    );
    this.adapterUpdateManagement = new AdapterUpdateManagement(
        managementService,
        pipelineUpdateCoordinator,
        resourceManager);
  }

  @PostMapping(
      consumes = {
          MediaType.APPLICATION_JSON_VALUE,
          SpMediaType.YML,
          SpMediaType.YAML
      }
  )
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> addAdapterCompact(
      @RequestBody CompactAdapter compactAdapter
  ) throws Exception {

    var principalSid = getAuthenticatedUserSid();
    var adapterDescription = convertToAdapterDescription(compactAdapter, principalSid);

    var adapterId = adapterDescription.getElementId();

    try {
      managementService.addAdapter(adapterDescription, adapterId, principalSid);
    } catch (AdapterException e) {
      LOG.error(
          "Error while storing the adapterDescription with appId {}. An adapter with the given id already exists.",
          adapterDescription.getAppId(), e
      );
      return ResponseEntity.status(HttpStatus.CONFLICT)
                           .body(Notifications.error(e.getMessage()));
    }

    try {
      if (compactAdapter.createOptions() != null) {
        if (compactAdapter.createOptions()
                          .persist()) {
          var storedAdapter = managementService.getAdapter(adapterId);
          new PersistPipelineHandler(
              getNoSqlStorage().getPipelineTemplateStorage(),
              new CompactPipelineManagement(
                  getNoSqlStorage().getPipelineElementDescriptionStorage(),
                  requestManager
              ),
              pipelineManager,
              getAuthenticatedUserSid()
          ).createAndStartPersistPipeline(storedAdapter, requestManager);
        }
        if (compactAdapter.createOptions()
                          .start()) {
          managementService.startAdapter(adapterId);
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
  @PreAuthorize("this.hasWriteAuthority() and hasPermission(#elementId, 'WRITE')")
  public ResponseEntity<?> updateAdapterCompact(
      @PathVariable("id") String elementId,
      @RequestBody CompactAdapter compactAdapter
  ) throws Exception {

    var existingAdapter = managementService.getAdapter(elementId);
    if (existingAdapter != null) {
      var principalSid = getAuthenticatedUserSid();
      var adapterDescription = convertToAdapterDescription(
          compactAdapter,
          existingAdapter,
          principalSid
      );

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

  private AdapterDescription convertToAdapterDescription(
      CompactAdapter compactAdapter,
      String principalSid
  ) throws Exception {
    try {
      return compactAdapterManagement.convertToAdapterDescription(compactAdapter, principalSid);
    } catch (AdapterException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }

  private AdapterDescription convertToAdapterDescription(
      CompactAdapter compactAdapter,
      AdapterDescription existingAdapter,
      String principalSid
  ) throws Exception {
    try {
      return compactAdapterManagement.convertToAdapterDescription(compactAdapter, existingAdapter, principalSid);
    } catch (AdapterException e) {
      throw new SpMessageException(HttpStatus.BAD_REQUEST, Notifications.error(e.getMessage()));
    }
  }
}
