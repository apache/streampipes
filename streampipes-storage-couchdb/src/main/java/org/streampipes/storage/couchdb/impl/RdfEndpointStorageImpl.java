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

package org.streampipes.storage.couchdb.impl;

import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.storage.api.IRdfEndpointStorage;
import org.streampipes.storage.couchdb.dao.AbstractDao;
import org.streampipes.storage.couchdb.utils.Utils;

import java.util.List;

public class RdfEndpointStorageImpl extends AbstractDao<RdfEndpoint> implements IRdfEndpointStorage {

    public RdfEndpointStorageImpl() {
        super(Utils::getCouchDbRdfEndpointClient, RdfEndpoint.class);
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
