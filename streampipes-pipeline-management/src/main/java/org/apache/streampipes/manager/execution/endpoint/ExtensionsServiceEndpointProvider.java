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

package org.apache.streampipes.manager.execution.endpoint;

import org.apache.streampipes.commons.constants.GlobalStreamPipesConstants;
import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.model.SpDataSet;
import org.apache.streampipes.model.base.InvocableStreamPipesEntity;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.Collections;
import java.util.List;

public class ExtensionsServiceEndpointProvider {

  public String findSelectedEndpoint(InvocableStreamPipesEntity g) throws NoServiceEndpointsAvailableException {
    return new ExtensionsServiceEndpointGenerator(
        g.getAppId(),
        ExtensionsServiceEndpointUtils.getPipelineElementType(g))
        .getEndpointResourceUrl();
  }

  public String findSelectedEndpoint(SpDataSet ds) throws NoServiceEndpointsAvailableException {
    String appId = ds.getAppId() != null ? ds.getAppId() : ds.getCorrespondingAdapterId();
    if (ds.isInternallyManaged()) {
      return getConnectMasterSourcesUrl();
    } else {
      return new ExtensionsServiceEndpointGenerator(appId, SpServiceUrlProvider.DATA_SET)
          .getEndpointResourceUrl();
    }
  }

  private String getConnectMasterSourcesUrl() throws NoServiceEndpointsAvailableException {
    List<String> connectMasterEndpoints = SpServiceDiscovery.getServiceDiscovery()
        .getServiceEndpoints(DefaultSpServiceGroups.CORE, true,
            Collections.singletonList(DefaultSpServiceTags.CONNECT_MASTER.asString()));
    if (connectMasterEndpoints.size() > 0) {
      return connectMasterEndpoints.get(0) + GlobalStreamPipesConstants.CONNECT_MASTER_SOURCES_ENDPOINT;
    } else {
      throw new NoServiceEndpointsAvailableException("Could not find any available connect master service endpoint");
    }
  }
}
