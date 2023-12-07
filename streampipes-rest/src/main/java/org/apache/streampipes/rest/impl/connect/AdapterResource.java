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
import org.apache.streampipes.connect.management.management.AdapterMasterManagement;
import org.apache.streampipes.connect.management.management.AdapterUpdateManagement;
import org.apache.streampipes.manager.pipeline.PipelineManager;
import org.apache.streampipes.model.client.user.Permission;
import org.apache.streampipes.model.client.user.Role;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.model.monitoring.SpLogMessage;
import org.apache.streampipes.resource.management.PermissionResourceManager;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.api.IPipelineStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/connect/master/adapters")
public class AdapterResource extends AbstractAdapterResource<AdapterMasterManagement> {

  private static final Logger LOG = LoggerFactory.getLogger(AdapterResource.class);

  public AdapterResource() {
    super(AdapterMasterManagement::new);
  }

  @POST
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public Response addAdapter(AdapterDescription adapterDescription) {
    var principalSid = getAuthenticatedUserSid();
    var username = getAuthenticatedUsername();
    String adapterId;
    LOG.info("User: " + username + " starts adapter " + adapterDescription.getElementId());

    try {
      adapterId = managementService.addAdapter(adapterDescription, principalSid);
    } catch (AdapterException e) {
      LOG.error("Error while starting adapter with id " + adapterDescription.getAppId(), e);
      return ok(Notifications.error(e.getMessage()));
    }

    LOG.info("Stream adapter with id " + adapterId + " successfully added");
    return ok(Notifications.success(adapterId));
  }

  @PUT
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public Response updateAdapter(AdapterDescription adapterDescription) {
    var updateManager = new AdapterUpdateManagement(managementService);
    try {
      updateManager.updateAdapter(adapterDescription);
    } catch (AdapterException e) {
      LOG.error("Error while updating adapter with id " + adapterDescription.getElementId(), e);
      return ok(Notifications.error(e.getMessage()));
    }

    return ok(Notifications.success(adapterDescription.getElementId()));
  }

  @PUT
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  @Path("pipeline-migration-preflight")
  public Response performPipelineMigrationPreflight(AdapterDescription adapterDescription) {
    var updateManager = new AdapterUpdateManagement(managementService);
    var migrations = updateManager.checkPipelineMigrations(adapterDescription);

    return ok(migrations);
  }

  @GET
  @JacksonSerialized
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_READ_ADAPTER_PRIVILEGE)
  public Response getAdapter(@PathParam("id") String adapterId) {

    try {
      AdapterDescription adapterDescription = getAdapterDescription(adapterId);

      return ok(adapterDescription);
    } catch (AdapterException e) {
      LOG.error("Error while getting adapter with id " + adapterId, e);
      return fail();
    }
  }

  @POST
  @JacksonSerialized
  @Path("/{id}/stop")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public Response stopAdapter(@PathParam("id") String adapterId) {
    try {
      managementService.stopStreamAdapter(adapterId);
      return ok(Notifications.success("Adapter started"));
    } catch (AdapterException e) {
      LOG.error("Could not stop adapter with id " + adapterId, e);
      return serverError(SpLogMessage.from(e));
    }
  }

  @POST
  @JacksonSerialized
  @Path("/{id}/start")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public Response startAdapter(@PathParam("id") String adapterId) {
    try {
      managementService.startStreamAdapter(adapterId);
      return ok(Notifications.success("Adapter stopped"));
    } catch (AdapterException e) {
      LOG.error("Could not start adapter with id " + adapterId, e);
      return serverError(SpLogMessage.from(e));
    }
  }

  @DELETE
  @JacksonSerialized
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_DELETE_ADAPTER_PRIVILEGE)
  public Response deleteAdapter(@PathParam("id") String elementId,
                                @QueryParam("deleteAssociatedPipelines") @DefaultValue("false")
                                boolean deleteAssociatedPipelines) {
    List<String> pipelinesUsingAdapter = getPipelinesUsingAdapter(elementId);
    IPipelineStorage pipelineStorageAPI = StorageDispatcher.INSTANCE.getNoSqlStore().getPipelineStorageAPI();

    if (pipelinesUsingAdapter.isEmpty()) {
      try {
        managementService.deleteAdapter(elementId);
        return ok(Notifications.success("Adapter with id: " + elementId + " is deleted."));
      } catch (AdapterException e) {
        LOG.error("Error while deleting adapter with id " + elementId, e);
        return ok(Notifications.error(e.getMessage()));
      }
    } else if (!deleteAssociatedPipelines) {
      List<String> namesOfPipelinesUsingAdapter =
          pipelinesUsingAdapter.stream().map(pipelineId -> pipelineStorageAPI.getPipeline(pipelineId).getName())
              .collect(
                  Collectors.toList());
      return Response.status(HttpStatus.SC_CONFLICT).entity(String.join(", ", namesOfPipelinesUsingAdapter)).build();
    } else {
      PermissionResourceManager permissionResourceManager = new PermissionResourceManager();
      // find out the names of pipelines that have an owner and the owner is not the current user
      List<String> namesOfPipelinesNotOwnedByUser = pipelinesUsingAdapter.stream().filter(pipelineId ->
              !permissionResourceManager.findForObjectId(pipelineId).stream().findFirst().map(Permission::getOwnerSid)
                  // if a pipeline has no owner, pretend the owner is the user so the user can delete it
                  .orElse(this.getAuthenticatedUserSid()).equals(this.getAuthenticatedUserSid()))
          .map(pipelineId -> pipelineStorageAPI.getPipeline(pipelineId).getName()).collect(Collectors.toList());
      boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
          .anyMatch(r -> r.getAuthority().equals(
              Role.ROLE_ADMIN.name()));
      // if the user is admin or owns all pipelines using this adapter,
      // the user can delete all associated pipelines and this adapter
      if (isAdmin || namesOfPipelinesNotOwnedByUser.isEmpty()) {
        try {
          for (String pipelineId : pipelinesUsingAdapter) {
            PipelineManager.stopPipeline(pipelineId, false);
            PipelineManager.deletePipeline(pipelineId);
          }
          managementService.deleteAdapter(elementId);
          return ok(Notifications.success(
              "Adapter with id: " + elementId + " and all pipelines using the adapter are deleted."));
        } catch (Exception e) {
          LOG.error("Error while deleting adapter with id " + elementId + " and all pipelines using the adapter", e);
          return ok(Notifications.error(e.getMessage()));
        }
      } else {
        // otherwise, hint the user the names of pipelines using the adapter but not owned by the user
        return Response.status(HttpStatus.SC_CONFLICT).entity(String.join(", ", namesOfPipelinesNotOwnedByUser))
            .build();
      }
    }
  }

  @GET
  @JacksonSerialized
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_READ_ADAPTER_PRIVILEGE)
  public List<AdapterDescription> getAllAdapters() {
    try {
      return managementService.getAllAdapterInstances();
    } catch (AdapterException e) {
      LOG.error("Error while getting all adapters", e);
      throw new WebApplicationException(500);
    }
  }

  private AdapterDescription getAdapterDescription(String adapterId) throws AdapterException {
    return managementService.getAdapter(adapterId);
  }

  private List<String> getPipelinesUsingAdapter(String adapterId) {
    return StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getPipelineStorageAPI()
        .getPipelinesUsingAdapter(adapterId);
  }

}
