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

package org.apache.streampipes.rest.impl.pe;

import org.apache.streampipes.model.StreamPipesErrorMessage;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.resource.management.DataProcessorResourceManager;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/v2/sepas")
@Component
public class DataProcessorResource extends AbstractAuthGuardedRestResource {

  @GET
  @Path("/available")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_ELEMENT_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.elementId, 'READ')")
  public List<DataProcessorDescription> getAvailable() {
    return getDataProcessorResourceManager().findAll();
  }

  @GET
  @JacksonSerialized
  @Produces({MediaType.APPLICATION_JSON})
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_ELEMENT_PRIVILEGE)
  @PostFilter("hasPermission(filterObject.belongsTo, 'READ')")
  public List<DataProcessorInvocation> getOwn() {
    return getDataProcessorResourceManager().findAllAsInvocation();
  }

  @DELETE
  @Path("/{elementId}")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.HAS_DELETE_PIPELINE_ELEMENT_PRIVILEGE)
  public Response removeOwn(@PathParam("elementId") String elementId) {
    getDataProcessorResourceManager().delete(elementId);
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

  @Path("/{elementId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  @PreAuthorize(AuthConstants.HAS_READ_PIPELINE_ELEMENT_PRIVILEGE)
  public Response getElement(@PathParam("elementId") String elementId) {
    try {
      return ok(getDataProcessorResourceManager().findAsInvocation(elementId));
    } catch (IllegalArgumentException e) {
      return badRequest(StreamPipesErrorMessage.from(e));
    }
  }

  private DataProcessorResourceManager getDataProcessorResourceManager() {
    return getSpResourceManager().manageDataProcessors();
  }

}
