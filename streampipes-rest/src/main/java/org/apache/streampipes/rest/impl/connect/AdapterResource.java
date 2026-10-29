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

import org.apache.streampipes.audit.events.adapter.AdapterAuditRecorder;
import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.commons.prometheus.adapter.AdapterMetricsManager;
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.AdapterUpdateManagement;
import org.apache.streampipes.connect.management.management.CompactAdapterManagement;
import org.apache.streampipes.connect.management.management.WorkerRestClient;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.manager.pipeline.update.DataStreamDeletedEvent;
import org.apache.streampipes.model.client.user.DefaultRole;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.AdapterSummaryDto;
import org.apache.streampipes.model.connect.adapter.PipelineUpdateInfo;
import org.apache.streampipes.model.connect.adapter.compact.CompactAdapter;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.message.SuccessMessage;
import org.apache.streampipes.model.resource.ResourceSummaryDto;
import org.apache.streampipes.model.util.ElementIdGenerator;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.permission.SpPermissionEvaluator;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.constants.SpMediaType;
import org.apache.streampipes.rest.shared.exception.ProblemError;
import org.apache.streampipes.rest.shared.exception.SpProblemException;
import org.apache.streampipes.storage.api.connect.IAdapterStorage;
import org.apache.streampipes.storage.api.pipeline.IPipelineStorage;
import org.apache.streampipes.storage.api.system.IExtensionsServiceStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/connect/master/adapters")
public class AdapterResource extends AbstractAdapterResource<AdapterMasterManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterResource.class);
  private final ExtensionServiceRequestManager requestManager;
  private final ApplicationEventPublisher eventPublisher;
  private final PermissionResourceManager permissionResourceManager;
  private final PipelineManager pipelineManager;
  private final AdapterUpdateManagement adapterUpdateManagement;
  private final SpResourceManager resourceManager;
  private final IAdapterStorage adapterDescriptionStorage;

  public AdapterResource(WorkerRestClient workerRestClient,
                         ExtensionServiceRequestManager requestManager,
                         SpResourceManager resourceManager,
                         @Qualifier("adapterDescriptionStorage") IAdapterStorage adapterDescriptionStorage,
                         IExtensionsServiceStorage extensionsServiceStorage) {
    this(workerRestClient, requestManager, null, resourceManager, adapterDescriptionStorage, extensionsServiceStorage);
  }

  @Autowired
  public AdapterResource(WorkerRestClient workerRestClient,
                         ExtensionServiceRequestManager requestManager,
                         ApplicationEventPublisher eventPublisher,
                         SpResourceManager resourceManager,
                         @Qualifier("adapterDescriptionStorage") IAdapterStorage adapterDescriptionStorage,
                         IExtensionsServiceStorage extensionsServiceStorage) {
    super(() -> new AdapterMasterManagement(
        resourceManager,
        AdapterMetricsManager.INSTANCE.getAdapterMetrics(),
        workerRestClient,
        extensionsServiceStorage,
        requestManager,
        new AdapterAuditRecorder(resourceManager.getAuditService())));
    this.requestManager = requestManager;
    this.resourceManager = resourceManager;
    this.adapterDescriptionStorage = adapterDescriptionStorage;
    this.eventPublisher = eventPublisher;
    this.permissionResourceManager = resourceManager.managePermissions();
    this.pipelineManager = new PipelineManager(
        resourceManager
    );
    this.adapterUpdateManagement = new AdapterUpdateManagement(
        managementService,
        requestManager,
        resourceManager,
        eventPublisher
    );
  }

  @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<SuccessMessage> addAdapter(@RequestBody AdapterDescription adapterDescription)
      throws AdapterException {
    var principalSid = getAuthenticatedUserSid();
    var username = getAuthenticatedUsername();
    LOG.info("User: {} starts adapter {}", username, adapterDescription.getElementId());

    var adapterId = ElementIdGenerator.makeElementId(adapterDescription);
    managementService.addAdapter(adapterDescription, adapterId, principalSid);

    LOG.info("Stream adapter with id {} successfully added", adapterId);
    return ok(Notifications.success(adapterId));
  }

  @PostMapping(path = "compact", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {
      MediaType.APPLICATION_JSON_VALUE,
      SpMediaType.YAML,
      SpMediaType.YML
  })
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<?> convertToCompactAdapter(@RequestBody AdapterDescription adapterDescription)
      throws Exception {
    return ok(new CompactAdapterManagement(List.of(), adapterDescriptionStorage).convertToCompactAdapter(adapterDescription));
  }

  @PutMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority() and hasPermission(#adapterDescription.correspondingDataStreamElementId, 'WRITE')")
  public ResponseEntity<SuccessMessage> updateAdapter(@RequestBody AdapterDescription adapterDescription)
      throws AdapterException {
    adapterUpdateManagement.updateAdapter(adapterDescription, getAuthenticatedUserSid());
    return ok(Notifications.success(adapterDescription.getElementId()));
  }

  @PutMapping(path = "pipeline-migration-preflight", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public ResponseEntity<List<PipelineUpdateInfo>> performPipelineMigrationPreflight(
      @RequestBody AdapterDescription adapterDescription) {
    var migrations = adapterUpdateManagement.checkPipelineMigrations(adapterDescription);

    return ok(migrations);
  }

  @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, SpMediaType.YAML, SpMediaType.YML})
  @PreAuthorize("this.hasReadAuthority()")
  public ResponseEntity<?> getAdapter(
      @PathVariable("id") String elementId,
      @RequestParam(value = "output", defaultValue = "full", required = false) String outputMode) throws Exception {

    // The adapter permission is checked here based on the corresponding data stream
    // and not based on the element id
    var adapterDescription = requireAdapter(elementId, "READ");

    if (outputMode.equalsIgnoreCase("compact")) {
      return ok(toCompactAdapterDescription(adapterDescription));
    } else {
      return ok(adapterDescription);
    }
  }

  /**
   * Checks if the current user has the permission to read the adapter
   */
  private boolean checkAdapterPermission(AdapterDescription adapterDescription,
                                         String permission) {
    var spPermissionEvaluator = new SpPermissionEvaluator(resourceManager.managePermissions().getDb());
    var authentication = SecurityContextHolder.getContext()
        .getAuthentication();
    return spPermissionEvaluator.hasPermission(
        authentication,
        adapterDescription.getCorrespondingDataStreamElementId(),
        permission);
  }

  @PostMapping(path = "/{id}/stop", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<SuccessMessage> stopAdapter(
      @PathVariable("id") String elementId,
      @RequestParam(value = "forceStop", defaultValue = "false") boolean forceStop) throws AdapterException {
    var adapter = requireAdapter(elementId, "WRITE");
    managementService.stopAdapter(adapter, forceStop, getAuthenticatedUserSid());
    return ok(Notifications.success("Adapter stopped"));
  }

  @PostMapping(path = "/{id}/start", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<SuccessMessage> startAdapter(@PathVariable("id") String elementId) throws AdapterException {
    var adapterDescription = requireAdapter(elementId, "WRITE");
    managementService.startAdapter(adapterDescription, getAuthenticatedUserSid());
    return ok(Notifications.success("Adapter started"));
  }

  @DeleteMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasWriteAuthority()")
  public ResponseEntity<SuccessMessage> deleteAdapter(
      @PathVariable("id") String elementId,
      @RequestParam(value = "deleteAssociatedPipelines", defaultValue = "false") boolean deleteAssociatedPipelines)
      throws AdapterException {
    var adapter = requireAdapter(elementId, "WRITE");
    List<String> pipelinesUsingAdapter = getPipelinesUsingAdapter(elementId);
    IPipelineStorage pipelineStorageAPI = resourceManager.managePipelines().getDb();

    if (pipelinesUsingAdapter.isEmpty()) {
      managementService.deleteAdapter(elementId);
      publishEvent(new DataStreamDeletedEvent(adapter.getCorrespondingDataStreamElementId()));

      return ok(Notifications.success("Adapter with id: " + elementId + " is deleted."));
    } else if (!deleteAssociatedPipelines) {
      List<String> namesOfPipelinesUsingAdapter = pipelinesUsingAdapter
          .stream()
          .map(pipelineId -> pipelineStorageAPI.getElementById(pipelineId).getName())
          .toList();
      throw usedByPipelines(elementId, namesOfPipelinesUsingAdapter,
          "Delete the pipelines first or request deleteAssociatedPipelines=true.");
    } else {
      // find out the names of pipelines that have an owner and the owner is not the
      // current user
      List<String> namesOfPipelinesNotOwnedByUser = pipelinesUsingAdapter
          .stream()
          .filter(pipelineId -> !permissionResourceManager.findForObjectId(pipelineId)
              .stream()
              .findFirst()
              .map(Permission::getOwnerSid)
              // if a pipeline has no owner, pretend the owner
              // is the user so the user can delete it
              .orElse(this.getAuthenticatedUserSid())
              .equals(this.getAuthenticatedUserSid()))
          .map(pipelineId -> pipelineStorageAPI.getElementById(pipelineId).getName())
          .toList();
      boolean isAdmin = SecurityContextHolder.getContext()
          .getAuthentication()
          .getAuthorities()
          .stream()
          .anyMatch(r -> r.getAuthority().equals(DefaultRole.ROLE_ADMIN.name()));
      // if the user is admin or owns all pipelines using this adapter,
      // the user can delete all associated pipelines and this adapter
      if (isAdmin || namesOfPipelinesNotOwnedByUser.isEmpty()) {
        for (String pipelineId : pipelinesUsingAdapter) {
          pipelineManager.stopPipeline(pipelineId, false, requestManager);
          pipelineManager.deletePipeline(pipelineId);
        }
        managementService.deleteAdapter(elementId);
        publishEvent(new DataStreamDeletedEvent(adapter.getCorrespondingDataStreamElementId()));

        return ok(Notifications.success("Adapter with id: " + elementId
            + " and all pipelines using the adapter are deleted."));
      } else {
        // otherwise, hint the user the names of pipelines using the adapter but not
        // owned by the user
        throw usedByPipelines(elementId, namesOfPipelinesNotOwnedByUser,
            "These pipelines belong to other users and can only be deleted by them or an admin.");
      }
    }
  }

  private SpProblemException usedByPipelines(String elementId,
                                             List<String> pipelineNames,
                                             String hint) {
    return SpProblemException.conflict(
        String.format("Adapter %s is used by the pipeline(s) %s. %s", elementId, String.join(", ", pipelineNames), hint),
        pipelineNames.stream().map(ProblemError::of).toList());
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  @PostFilter("hasPermission(filterObject.correspondingDataStreamElementId, 'READ')")
  public List<AdapterDescription> getAllAdapters() {
    return managementService.getAllAdapterInstances();
  }

  @GetMapping(path = "/summary", produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize("this.hasReadAuthority()")
  public ResourceSummaryDto<AdapterSummaryDto> getAdapterSummary() {
    return resourceManager.manageAdapters().getSummary(getAuthentication());
  }

  /**
   * Loads the adapter and checks the object permission, which is bound to the corresponding
   * data stream and therefore cannot be expressed in the {@code @PreAuthorize} expression.
   */
  private AdapterDescription requireAdapter(String elementId, String permission) {
    AdapterDescription adapterDescription;
    try {
      adapterDescription = managementService.getAdapter(elementId);
    } catch (AdapterException e) {
      throw SpProblemException.notFound("Adapter", elementId);
    }
    if (!checkAdapterPermission(adapterDescription, permission)) {
      LOG.warn("User {} is not allowed to {} adapter {}", getAuthenticatedUsername(), permission, elementId);
      throw SpProblemException.forbidden(String.format("Not allowed to %s adapter %s", permission.toLowerCase(), elementId));
    }
    return adapterDescription;
  }

  private CompactAdapter toCompactAdapterDescription(AdapterDescription adapterDescription) throws Exception {
    return new CompactAdapterManagement(List.of(), adapterDescriptionStorage).convertToCompactAdapter(adapterDescription);
  }

  private List<String> getPipelinesUsingAdapter(String adapterId) {
    return resourceManager.managePipelines().getDb()
        .getPipelinesUsingAdapter(adapterId);
  }

  private void publishEvent(Object event) {
    if (eventPublisher != null) {
      eventPublisher.publishEvent(event);
    }
  }

}
