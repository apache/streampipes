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

import org.apache.streampipes.connect.api.exception.AdapterException;
import org.apache.streampipes.connect.container.master.management.AdapterMasterManagement;
import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.storage.management.StorageDispatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

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
    String principalSid = getAuthenticatedUserSid();
    String username = getAuthenticatedUsername();
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
    String principalSid = getAuthenticatedUserSid();
    String username = getAuthenticatedUsername();
    LOG.info("User: " + username + " updates adapter " + adapterDescription.getElementId());

    try {
      managementService.updateAdapter(adapterDescription, principalSid);
    } catch (AdapterException e) {
      LOG.error("Error while updating adapter with id " + adapterDescription.getElementId(), e);
      return ok(Notifications.error(e.getMessage()));
    }

    return ok(Notifications.success(adapterDescription.getElementId()));
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
      return serverError(StreamPipesErrorMessage.from(e));
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
      return serverError(StreamPipesErrorMessage.from(e));
    }
  }

  @DELETE
  @JacksonSerialized
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @PreAuthorize(AuthConstants.HAS_DELETE_ADAPTER_PRIVILEGE)
  public Response deleteAdapter(@PathParam("id") String elementId) {
    List<String> pipelinesUsingAdapter = getPipelinesUsingAdapter(elementId);

    if (pipelinesUsingAdapter.size() == 0) {
      try {
        managementService.deleteAdapter(elementId);
        return ok(Notifications.success("Adapter with id: " + elementId + " is deleted."));
      } catch (AdapterException e) {
        LOG.error("Error while deleting adapter with id " + elementId, e);
        return ok(Notifications.error(e.getMessage()));
      }
    } else {
      return Response.status(409).entity(pipelinesUsingAdapter).build();
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
