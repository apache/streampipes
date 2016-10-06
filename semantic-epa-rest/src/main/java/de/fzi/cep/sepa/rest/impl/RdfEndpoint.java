package de.fzi.cep.sepa.rest.impl;

import de.fzi.cep.sepa.manager.operations.Operations;
import de.fzi.cep.sepa.model.client.endpoint.RdfEndpointItem;
import de.fzi.cep.sepa.rest.annotation.GsonWithIds;
import de.fzi.cep.sepa.rest.api.IRdfEndpoint;
import de.fzi.cep.sepa.storage.controller.StorageManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

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
        return ok(StorageManager
                .INSTANCE
                .getRdfEndpointStorage()
                .getRdfEndpoints());
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @GsonWithIds
    @Override
    public Response addRdfEndpoint(de.fzi.cep.sepa.model.client.endpoint.RdfEndpoint rdfEndpoint) {
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
        List<de.fzi.cep.sepa.model.client.endpoint.RdfEndpoint> endpoints = StorageManager
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
