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

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.message.NotificationType;
import org.apache.streampipes.model.message.Notifications;
import org.apache.streampipes.rest.api.IPipelineElement;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.couchdb.utils.Filter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/actions")
public class SemanticEventConsumer extends AbstractAuthGuardedRestResource implements IPipelineElement {

  @GET
  @Path("/available")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getAvailable() {
    List<DataSinkDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllDataSinks(),
            getUserService().getAvailableActionUris(getAuthenticatedUsername()));
    return ok(secs);
  }

  @GET
  @Path("/favorites")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getFavorites() {
    List<DataSinkDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllDataSinks(),
            getUserService().getFavoriteActionUris(getAuthenticatedUsername()));
    return ok(secs);
  }

  @GET
  @Path("/own")
  @RequiresAuthentication
  @Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
  @JacksonSerialized
  @Override
  public Response getOwn() {
    List<DataSinkDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllDataSinks(),
            getUserService().getOwnActionUris(getAuthenticatedUsername()));
    List<DataSinkInvocation> si = secs.stream().map(s -> new DataSinkInvocation(new DataSinkInvocation(s))).collect(Collectors.toList());
    return ok(si);
  }

  @POST
  @Path("/favorites")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response addFavorite(@FormParam("uri") String elementUri) {
    getUserService().addActionAsFavorite(getAuthenticatedUsername(), decode(elementUri));
    return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
  }

  @DELETE
  @Path("/favorites/{elementId}")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response removeFavorite(@PathParam("elementId") String elementId) {
    getUserService().removeActionFromFavorites(getAuthenticatedUsername(), decode(elementId));
    return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
  }

  @DELETE
  @Path("/own/{elementId}")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response removeOwn(@PathParam("elementId") String elementId) {
    IPipelineElementDescriptionStorage requestor = getPipelineElementRdfStorage();
    getUserService().deleteOwnAction(getAuthenticatedUsername(), elementId);
    requestor.deleteDataSink(requestor.getDataSinkById(elementId));
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }


  @Path("/{elementUri}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getElement(@PathParam("elementUri") String elementUri) {
    return ok(new DataSinkInvocation(new DataSinkInvocation(getPipelineElementRdfStorage().getDataSinkById(elementUri))));
  }
}
