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
import org.apache.streampipes.model.shared.message.Notification;
import org.apache.streampipes.model.shared.message.NotificationType;
import org.apache.streampipes.model.client.messages.Notifications;
import org.apache.streampipes.model.graph.DataSinkDescription;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.rest.api.IPipelineElement;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.rest.shared.util.SpMediaType;
import org.apache.streampipes.storage.api.IPipelineElementDescriptionStorage;
import org.apache.streampipes.storage.rdf4j.filter.Filter;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/users/{username}/actions")
public class SemanticEventConsumer extends AbstractRestInterface implements IPipelineElement {

  @GET
  @Path("/available")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getAvailable(@PathParam("username") String username) {
    List<DataSinkDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllDataSinks(),
            getUserService().getAvailableActionUris(username));
    return ok(secs);
  }

  @GET
  @Path("/favorites")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getFavorites(@PathParam("username") String username) {
    List<DataSinkDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllDataSinks(),
            getUserService().getFavoriteActionUris(username));
    return ok(secs);
  }

  @GET
  @Path("/own")
  @RequiresAuthentication
  @Produces({MediaType.APPLICATION_JSON, SpMediaType.JSONLD})
  @JacksonSerialized
  @Override
  public Response getOwn(@PathParam("username") String username) {
    List<DataSinkDescription> secs = Filter.byUri(getPipelineElementRdfStorage().getAllDataSinks(),
            getUserService().getOwnActionUris(username));
    List<DataSinkInvocation> si = secs.stream().map(s -> new DataSinkInvocation(new DataSinkInvocation(s))).collect(Collectors.toList());
    return ok(si);
  }

  @POST
  @Path("/favorites")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response addFavorite(@PathParam("username") String username, @FormParam("uri") String elementUri) {
    getUserService().addActionAsFavorite(username, decode(elementUri));
    return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
  }

  @DELETE
  @Path("/favorites/{elementUri}")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response removeFavorite(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
    getUserService().removeActionFromFavorites(username, decode(elementUri));
    return statusMessage(Notifications.success(NotificationType.OPERATION_SUCCESS));
  }

  @DELETE
  @Path("/own/{elementUri}")
  @RequiresAuthentication
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response removeOwn(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
    try {
      IPipelineElementDescriptionStorage requestor = getPipelineElementRdfStorage();
      getUserService().deleteOwnAction(username, elementUri);
      requestor.deleteDataSink(requestor.getDataSinkById(elementUri));
    } catch (URISyntaxException e) {
      return constructErrorMessage(new Notification(NotificationType.STORAGE_ERROR.title(), NotificationType.STORAGE_ERROR.description(), e.getMessage()));
    }
    return constructSuccessMessage(NotificationType.STORAGE_SUCCESS.uiNotification());
  }

  @Path("/{elementUri}/jsonld")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Override
  public String getAsJsonLd(@PathParam("elementUri") String elementUri) {
    try {
      return toJsonLd(getPipelineElementRdfStorage().getDataSinkById(elementUri));
    } catch (URISyntaxException e) {
      return toJson(constructErrorMessage(new Notification(NotificationType.UNKNOWN_ERROR.title(), NotificationType.UNKNOWN_ERROR.description(), e.getMessage())));
    }
  }

  @Path("/{elementUri}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getElement(@PathParam("username") String username, @PathParam("elementUri") String elementUri) {
    try {
      return ok(new DataSinkInvocation(new DataSinkInvocation(getPipelineElementRdfStorage().getDataSinkById(elementUri))));
    } catch (URISyntaxException e) {
      return statusMessage(Notifications.error(NotificationType.UNKNOWN_ERROR, e.getMessage()));
    }
  }


}
