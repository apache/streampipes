package org.streampipes.storage.api;

import org.streampipes.model.client.endpoint.RdfEndpoint;

import java.util.List;

public interface IRdfEndpointStorage {

    void addRdfEndpoint(RdfEndpoint rdfEndpoint);

    void removeRdfEndpoint(String rdfEndpointId);

    List<RdfEndpoint> getRdfEndpoints();
}
