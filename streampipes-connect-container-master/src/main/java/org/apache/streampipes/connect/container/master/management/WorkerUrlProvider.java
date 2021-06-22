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
package org.apache.streampipes.connect.container.master.management;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.adapter.GenericAdapterDescription;
import org.apache.streampipes.model.connect.grounding.ProtocolDescription;
import org.apache.streampipes.model.connect.worker.ConnectWorkerContainer;
import org.apache.streampipes.storage.api.IConnectWorkerContainerStorage;
import org.apache.streampipes.storage.couchdb.impl.ConnectionWorkerContainerStorageImpl;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.List;

public class WorkerUrlProvider {

  private IConnectWorkerContainerStorage connectWorkerContainerStorage;

  public WorkerUrlProvider() {
      this.connectWorkerContainerStorage = new ConnectionWorkerContainerStorageImpl();
  }

  public String getWorkerUrlForAdapter(AdapterDescription adapterDescription) throws NoServiceEndpointsAvailableException {
    String id = "";

    if (adapterDescription instanceof GenericAdapterDescription) {
      id = ((GenericAdapterDescription) (adapterDescription)).getProtocolDescription().getAppId();
    } else {
      id = adapterDescription.getAppId();
    }

    return getWorkerUrl(id);
  }

  public String getWorkerUrlForProtocol(ProtocolDescription protocolDescription) throws NoServiceEndpointsAvailableException {
    String id =  protocolDescription.getAppId();

    return getWorkerUrl(id);
  }

  public String getWorkerUrl(String appId) throws NoServiceEndpointsAvailableException {
    return getEndpointGenerator(appId).getEndpointResourceUrl();
  }

  public String getWorkerBaseUrl(String appId) throws NoServiceEndpointsAvailableException {
    return getEndpointGenerator(appId).getEndpointBaseUrl();
  }

  private ExtensionsServiceEndpointGenerator getEndpointGenerator(String appId) {
    SpServiceUrlProvider provider = null;
    List<ConnectWorkerContainer> allConnectWorkerContainer = this.connectWorkerContainerStorage.getAllConnectWorkerContainers();

    for (ConnectWorkerContainer connectWorkerContainer : allConnectWorkerContainer) {
      if (connectWorkerContainer.getProtocols().stream().anyMatch(p -> p.getAppId().equals(appId))) {
        provider = SpServiceUrlProvider.PROTOCOL;
      } else if (connectWorkerContainer.getAdapters().stream().anyMatch(a -> a.getAppId().equals(appId))) {
        provider = SpServiceUrlProvider.ADAPTER;
      }
    }

    return new ExtensionsServiceEndpointGenerator(appId, provider);
  }

}
