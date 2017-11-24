package org.streampipes.rest.impl;

import org.streampipes.container.util.ConsulUtil;
import org.streampipes.manager.operations.Operations;
import org.streampipes.model.client.endpoint.RdfEndpointItem;
import org.streampipes.rest.annotation.GsonWithIds;
import org.streampipes.rest.api.IRdfEndpoint;
import org.streampipes.storage.controller.StorageManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by riemer on 05.10.2016.
 */
@Path("/v2/users/{username}/rdfendpoints")
public class RdfEndpoint extends AbstractRestInterface implements IRdfEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getAllEndpoints() {
        //TODO: return the endpoint of passing services
        List<String> endpoints = ConsulUtil.getActivePEServicesEndPoints();
        List<org.streampipes.model.client.endpoint.RdfEndpoint> servicerdRdfEndpoints = new LinkedList<>();

        for(String endpoint: endpoints ) {
            org.streampipes.model.client.endpoint.RdfEndpoint rdfEndpoint =
                    new org.streampipes.model.client.endpoint.RdfEndpoint(endpoint);
            servicerdRdfEndpoints.add(rdfEndpoint);
        }
        List<org.streampipes.model.client.endpoint.RdfEndpoint> databasedRdfEndpoints = StorageManager
                .INSTANCE
                .getRdfEndpointStorage()
                .getRdfEndpoints();

        List<org.streampipes.model.client.endpoint.RdfEndpoint> concatList =
                Stream.of(databasedRdfEndpoints, servicerdRdfEndpoints)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return ok(concatList);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response addRdfEndpoint(org.streampipes.model.client.endpoint.RdfEndpoint rdfEndpoint) {
        StorageManager.INSTANCE
                .getRdfEndpointStorage()
                .addRdfEndpoint(rdfEndpoint);

        return Response.status(Response.Status.OK).build();
    }


    @DELETE
    @Path("/{rdfEndpointId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response removeRdfEndpoint(@PathParam("rdfEndpointId") String rdfEndpointId) {
        StorageManager.INSTANCE
                .getRdfEndpointStorage()
                .removeRdfEndpoint(rdfEndpointId);

        return Response.status(Response.Status.OK).build();
    }

    @GET
    @Path("/items")
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response getEndpointContents(@PathParam("username") String username) {
        List<org.streampipes.model.client.endpoint.RdfEndpoint> endpoints = StorageManager
                .INSTANCE
                .getRdfEndpointStorage()
                .getRdfEndpoints();

        List<RdfEndpointItem> items = Operations.getEndpointUriContents(endpoints);
        items.forEach(item -> item.setInstalled(isInstalled(item.getUri(), username)));
        return ok(items);

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
}
