/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.rest.impl;

import org.streampipes.container.util.ConsulUtil;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.endpoint.RdfEndpointItem;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.IRdfEndpoint;
import org.streampipes.storage.api.IRdfEndpointStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/v2/users/{username}/rdfendpoints")
public class RdfEndpoint extends AbstractRestInterface implements IRdfEndpoint {

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getAllEndpoints() {
    //TODO: return the endpoint of passing services
    return ok(getEndpoints());
  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response addRdfEndpoint(org.streampipes.model.client.endpoint.RdfEndpoint rdfEndpoint) {
    getRdfEndpointStorage()
            .addRdfEndpoint(rdfEndpoint);

    return Response.status(Response.Status.OK).build();
  }


  @DELETE
  @Path("/{rdfEndpointId}")
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response removeRdfEndpoint(@PathParam("rdfEndpointId") String rdfEndpointId) {
    getRdfEndpointStorage()
            .removeRdfEndpoint(rdfEndpointId);

    return Response.status(Response.Status.OK).build();
  }

  @GET
  @Path("/items")
  @Produces(MediaType.APPLICATION_JSON)
  @GsonWithIds
  @Override
  public Response getEndpointContents(@PathParam("username") String username) {
    List<org.streampipes.model.client.endpoint.RdfEndpoint> endpoints = getEndpoints();

    List<RdfEndpointItem> items = Operations.getEndpointUriContents(endpoints);
    items.forEach(item -> item.setInstalled(isInstalled(item.getUri(), username)));
    return ok(items);

  }

  private List<org.streampipes.model.client.endpoint.RdfEndpoint> getEndpoints() {
    List<String> endpoints = ConsulUtil.getActivePEServicesEndPoints();
    List<org.streampipes.model.client.endpoint.RdfEndpoint> servicerdRdfEndpoints = new LinkedList<>();

    for (String endpoint : endpoints) {
      org.streampipes.model.client.endpoint.RdfEndpoint rdfEndpoint =
              new org.streampipes.model.client.endpoint.RdfEndpoint(endpoint);
      servicerdRdfEndpoints.add(rdfEndpoint);
    }
    List<org.streampipes.model.client.endpoint.RdfEndpoint> databasedRdfEndpoints = getRdfEndpointStorage()
            .getRdfEndpoints();

    List<org.streampipes.model.client.endpoint.RdfEndpoint> concatList =
            Stream.of(databasedRdfEndpoints, servicerdRdfEndpoints)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

    return concatList;
  }

  private boolean isInstalled(String elementUri, String username) {
    return getAllUserElements(username)
            .stream()
            .anyMatch(e -> e.equals(elementUri));
  }

  private List<String> getAllUserElements(String username) {
    List<String> elementUris = new ArrayList<>();
    elementUris.addAll(getUserService().getOwnSourceUris(username));
    elementUris.addAll(getUserService().getOwnActionUris(username));
    elementUris.addAll(getUserService().getOwnSepaUris(username));
    return elementUris;
  }

  private IRdfEndpointStorage getRdfEndpointStorage() {
    return getNoSqlStorage().getRdfEndpointStorage();
  }
}
