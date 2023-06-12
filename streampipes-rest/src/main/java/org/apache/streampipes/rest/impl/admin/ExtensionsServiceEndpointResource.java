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

package org.apache.streampipes.rest.impl.admin;

import org.apache.streampipes.manager.endpoint.EndpointFetcher;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpointItem;
import org.apache.streampipes.rest.core.base.impl.AbstractAuthGuardedRestResource;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.rest.shared.annotation.JacksonSerialized;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.storage.api.IExtensionsServiceEndpointStorage;

import org.apache.http.client.fluent.Request;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/rdfendpoints")
@Component
@PreAuthorize(AuthConstants.IS_ADMIN_ROLE)
public class ExtensionsServiceEndpointResource extends AbstractAuthGuardedRestResource {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getAllEndpoints() {
    //TODO: return the endpoint of passing services
    return ok(getEndpoints());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response addRdfEndpoint(ExtensionsServiceEndpoint extensionsServiceEndpoint) {
    getRdfEndpointStorage()
        .addExtensionsServiceEndpoint(extensionsServiceEndpoint);

    return Response.status(Response.Status.OK).build();
  }


  @DELETE
  @Path("/{rdfEndpointId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response removeRdfEndpoint(@PathParam("rdfEndpointId") String rdfEndpointId) {
    getRdfEndpointStorage()
        .removeExtensionsServiceEndpoint(rdfEndpointId);

    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("/items")
  @Produces(MediaType.APPLICATION_JSON)
  @JacksonSerialized
  public Response getEndpointContents() {
    List<ExtensionsServiceEndpoint> endpoints = getEndpoints();
    String username = getAuthenticatedUsername();

    List<ExtensionsServiceEndpointItem> items = Operations.getEndpointUriContents(endpoints);
    items.forEach(item -> item.setInstalled(isInstalled(item.getElementId())));

    // also add installed elements that are currently not running or available
    items.addAll(getAllDataStreamEndpoints(username, items));
    items.addAll(getAllDataProcessorEndpoints(username, items));
    items.addAll(getAllDataSinkEndpoints(username, items));

    return ok(items);
  }

  @POST
  @Path("/items/icon")
  @Produces("image/png")
  public Response getEndpointItemIcon(ExtensionsServiceEndpointItem endpointItem) {
    try {
      byte[] imageBytes = Request.Get(makeIconUrl(endpointItem)).execute().returnContent().asBytes();
      return ok(imageBytes);
    } catch (IOException e) {
      return fail();
    }
  }

  private String makeIconUrl(ExtensionsServiceEndpointItem endpointItem) {
    return endpointItem.getUri() + "/assets/icon";
  }

  private List<ExtensionsServiceEndpoint> getEndpoints() {
    return new EndpointFetcher().getEndpoints();
  }

  private boolean isInstalled(String elementId) {
    return getAllPipelineElements()
        .stream()
        .anyMatch(e -> e.equals(elementId));
  }

  private List<String> getAllPipelineElements() {
    List<String> elementUris = new ArrayList<>();
    elementUris.addAll(getAllDataStreamUris());
    elementUris.addAll(getAllDataProcessorUris());
    elementUris.addAll(getAllDataSinkUris());
    return elementUris;
  }

  private List<ExtensionsServiceEndpointItem> getAllDataStreamEndpoints(String username,
                                              List<ExtensionsServiceEndpointItem> existingItems) {
    return getAllDataStreamUris()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.equals(item.getElementId())))
        .map(s -> getPipelineElementStorage().getDataStreamById(s))
        .map(stream -> makeItem(stream, "stream"))
        .collect(Collectors.toList());
  }

  private List<ExtensionsServiceEndpointItem> getAllDataProcessorEndpoints(String username,
                                              List<ExtensionsServiceEndpointItem> existingItems) {
    return getAllDataProcessorUris()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.equals(item.getElementId())))
        .map(s -> getPipelineElementStorage().getDataProcessorById(s))
        .map(source -> makeItem(source, "sepa"))
        .collect(Collectors.toList());
  }

  private List<ExtensionsServiceEndpointItem> getAllDataSinkEndpoints(String username,
                                              List<ExtensionsServiceEndpointItem> existingItems) {
    return getAllDataSinkUris()
        .stream()
        .filter(s -> existingItems.stream().noneMatch(item -> s.equals(item.getElementId())))
        .map(s -> getPipelineElementStorage().getDataSinkById(s))
        .map(source -> makeItem(source, "action"))
        .collect(Collectors.toList());
  }

  private ExtensionsServiceEndpointItem makeItem(NamedStreamPipesEntity entity, String type) {
    ExtensionsServiceEndpointItem endpoint = new ExtensionsServiceEndpointItem();
    endpoint.setInstalled(true);
    endpoint.setDescription(entity.getDescription());
    endpoint.setName(entity.getName());
    endpoint.setAppId(entity.getAppId());
    endpoint.setType(type);
    endpoint.setAvailable(false);
    endpoint.setElementId(entity.getElementId());
    endpoint.setUri(entity.getElementId());
    endpoint.setEditable(!(entity.isInternallyManaged()));
    endpoint.setIncludesIcon(entity.isIncludesAssets() && entity.getIncludedAssets().contains(Assets.ICON));
    endpoint.setIncludesDocs(entity.isIncludesAssets() && entity.getIncludedAssets().contains(Assets.DOCUMENTATION));
    return endpoint;
  }

  private List<String> getAllDataStreamUris() {
    return getSpResourceManager().manageDataStreams().findAllIdsOnly();
  }

  private List<String> getAllDataProcessorUris() {
    return getSpResourceManager().manageDataProcessors().findAllIdsOnly();
  }

  private List<String> getAllDataSinkUris() {
    return getSpResourceManager().manageDataSinks().findAllIdsOnly();
  }

  private IExtensionsServiceEndpointStorage getRdfEndpointStorage() {
    return getNoSqlStorage().getRdfEndpointStorage();
  }
}
