package org.streampipes.rest.api;

import org.streampipes.model.client.endpoint.RdfEndpoint;

import javax.ws.rs.core.Response;


/**
 * Created by riemer on 05.10.2016.
 */
public interface IRdfEndpoint {

    Response getAllEndpoints();

    Response addRdfEndpoint(RdfEndpoint rdfEndpoint);

    Response removeRdfEndpoint(String rdfEndpointId);

    Response getEndpointContents(String username);
}
