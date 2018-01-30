package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.storage.api.IRdfEndpointStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class RdfEndpointStorageImpl extends AbstractDao<RdfEndpoint> implements IRdfEndpointStorage {

    public RdfEndpointStorageImpl() {
        super(Utils.getCouchDbRdfEndpointClient(), RdfEndpoint.class);
    }


    @Override
    public void addRdfEndpoint(RdfEndpoint rdfEndpoint) {
        persist(rdfEndpoint);
    }

    @Override
    public void removeRdfEndpoint(String rdfEndpointId) {
        delete(rdfEndpointId)
;    }

    @Override
    public List<RdfEndpoint> getRdfEndpoints() {
        return findAll();
    }

}
