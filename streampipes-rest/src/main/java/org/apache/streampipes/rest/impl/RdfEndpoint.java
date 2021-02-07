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

import org.apache.streampipes.manager.endpoint.EndpointFetcher;
import org.apache.streampipes.manager.operations.Operations;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.model.client.endpoint.RdfEndpointItem;
import org.apache.streampipes.rest.shared.annotation.GsonWithIds;
import org.apache.streampipes.storage.api.IRdfEndpointStorage;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/v2/users/{username}/rdfendpoints")
public class RdfEndpoint extends AbstractRestInterface {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  public Response getAllEndpoints() {
    //TODO: return the endpoint of passing services
    return ok(getEndpoints());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  public Response addRdfEndpoint(org.apache.streampipes.model.client.endpoint.RdfEndpoint rdfEndpoint) {
    getRdfEndpointStorage()
            .addRdfEndpoint(rdfEndpoint);

    return Response.status(Response.Status.OK).build();
  }


  @DELETE
  @Path("/{rdfEndpointId}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @GsonWithIds
  public Response removeRdfEndpoint(@PathParam("rdfEndpointId") String rdfEndpointId) {
    getRdfEndpointStorage()
            .removeRdfEndpoint(rdfEndpointId);

    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("/items")
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  public Response getEndpointContents(@PathParam("username") String username) {
    List<org.apache.streampipes.model.client.endpoint.RdfEndpoint> endpoints = getEndpoints();

    List<RdfEndpointItem> items = Operations.getEndpointUriContents(endpoints);
    items.forEach(item -> item.setInstalled(isInstalled(item.getUri(), username)));

    // also add installed elements that are currently not running or available
    items.addAll(getAllDataStreamEndpoints(username, items));
    items.addAll(getAllDataProcessorEndpoints(username, items));
    items.addAll(getAllDataSinkEndpoints(username, items));

    return ok(items);

  }

  private List<org.apache.streampipes.model.client.endpoint.RdfEndpoint> getEndpoints() {
    return new EndpointFetcher().getEndpoints();
  }

  private boolean isInstalled(String elementUri, String username) {
    return getAllUserElements(username)
            .stream()
            .anyMatch(e -> e.equals(elementUri));
  }

  private List<String> getAllUserElements(String username) {
    List<String> elementUris = new ArrayList<>();
    elementUris.addAll(getAllDataStreamUris(username));
    elementUris.addAll(getAllDataProcessorUris(username));
    elementUris.addAll(getAllDataSinkUris(username));
    return elementUris;
  }

  private List<RdfEndpointItem> getAllDataStreamEndpoints(String username, List<RdfEndpointItem> existingItems) {
    return getAllDataStreamUris(username)
            .stream()
            .filter(s -> existingItems.stream().noneMatch(item -> item.getUri().equals(s)))
            .map(s -> getTripleStorage().getPipelineElementStorage().getDataStreamById(s))
            .map(stream -> makeItem(stream, stream instanceof SpDataSet ? "set" : "stream"))
            .collect(Collectors.toList());
  }

  private List<RdfEndpointItem> getAllDataProcessorEndpoints(String username, List<RdfEndpointItem> existingItems) {
    return getAllDataProcessorUris(username)
            .stream()
            .filter(s -> existingItems.stream().noneMatch(item -> item.getUri().equals(s)))
            .map(s -> getTripleStorage().getPipelineElementStorage().getDataProcessorById(s))
            .map(source -> makeItem(source, "sepa"))
            .collect(Collectors.toList());
  }

  private List<RdfEndpointItem> getAllDataSinkEndpoints(String username, List<RdfEndpointItem> existingItems) {
    return getAllDataSinkUris(username)
            .stream()
            .filter(s -> existingItems.stream().noneMatch(item -> item.getUri().equals(s)))
            .map(s -> getTripleStorage().getPipelineElementStorage().getDataSinkById(s))
            .map(source -> makeItem(source, "action"))
            .collect(Collectors.toList());
  }

  private RdfEndpointItem makeItem(NamedStreamPipesEntity entity, String type) {
    RdfEndpointItem endpoint = new RdfEndpointItem();
    endpoint.setInstalled(true);
    endpoint.setDescription(entity.getDescription());
    endpoint.setName(entity.getName());
    endpoint.setAppId(entity.getAppId());
    endpoint.setType(type);
    endpoint.setUri(entity.getElementId());
    endpoint.setEditable(!(entity.isInternallyManaged()));
    return endpoint;
  }

  private List<String> getAllDataStreamUris(String username) {
    return getUserService().getOwnSourceUris(username);
  }

  private List<String> getAllDataProcessorUris(String username) {
    return getUserService().getOwnSepaUris(username);
  }

  private List<String> getAllDataSinkUris(String username) {
    return getUserService().getOwnActionUris(username);
  }

  private IRdfEndpointStorage getRdfEndpointStorage() {
    return getNoSqlStorage().getRdfEndpointStorage();
  }
}
