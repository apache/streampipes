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
package org.streampipes.manager.endpoint;

import org.streampipes.container.util.ConsulUtil;
import org.streampipes.model.client.endpoint.RdfEndpoint;
import org.streampipes.storage.management.StorageDispatcher;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EndpointFetcher {

  public List<RdfEndpoint> getEndpoints() {
    List<String> endpoints = ConsulUtil.getActivePEServicesEndPoints();
    List<org.streampipes.model.client.endpoint.RdfEndpoint> servicerdRdfEndpoints = new LinkedList<>();

    for (String endpoint : endpoints) {
      org.streampipes.model.client.endpoint.RdfEndpoint rdfEndpoint =
              new org.streampipes.model.client.endpoint.RdfEndpoint(endpoint);
      servicerdRdfEndpoints.add(rdfEndpoint);
    }
    List<org.streampipes.model.client.endpoint.RdfEndpoint> databasedRdfEndpoints = StorageDispatcher.INSTANCE.getNoSqlStore()
            .getRdfEndpointStorage()
            .getRdfEndpoints();

    List<org.streampipes.model.client.endpoint.RdfEndpoint> concatList =
            Stream.of(databasedRdfEndpoints, servicerdRdfEndpoints)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

    return concatList;
  }
}
