package org.streampipes.storage.api;

import org.streampipes.model.client.endpoint.RdfEndpoint;

import java.util.List;

/**
 * Created by riemer on 05.10.2016.
 */
public interface RdfEndpointStorage {

    void addRdfEndpoint(RdfEndpoint rdfEndpoint);

    void removeRdfEndpoint(String rdfEndpointId);

    List<RdfEndpoint> getRdfEndpoints();
}
