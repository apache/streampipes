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
package org.apache.streampipes.manager.endpoint;

import org.apache.streampipes.model.client.endpoint.ExtensionsServiceEndpoint;
import org.apache.streampipes.storage.management.StorageDispatcher;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EndpointFetcher {

  public List<ExtensionsServiceEndpoint> getEndpoints() {
    List<String> endpoints = SpServiceDiscovery.getServiceDiscovery().getActivePipelineElementEndpoints();
    List<ExtensionsServiceEndpoint> serviceExtensionsServiceEndpoints = new LinkedList<>();

    for (String endpoint : endpoints) {
      ExtensionsServiceEndpoint extensionsServiceEndpoint =
              new ExtensionsServiceEndpoint(endpoint);
      serviceExtensionsServiceEndpoints.add(extensionsServiceEndpoint);
    }
    List<ExtensionsServiceEndpoint> databasedExtensionsServiceEndpoints = StorageDispatcher.INSTANCE.getNoSqlStore()
            .getRdfEndpointStorage()
            .getExtensionsServiceEndpoints();

    List<ExtensionsServiceEndpoint> concatList =
            Stream.of(databasedExtensionsServiceEndpoints, serviceExtensionsServiceEndpoints)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());

    return concatList;
  }
}
