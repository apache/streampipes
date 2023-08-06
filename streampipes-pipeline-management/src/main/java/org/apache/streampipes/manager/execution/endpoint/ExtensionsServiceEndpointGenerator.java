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

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.model.base.NamedStreamPipesEntity;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

public class ExtensionsServiceEndpointGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceEndpointGenerator.class);

  private final String appId;
  private final SpServiceUrlProvider spServiceUrlProvider;

  public ExtensionsServiceEndpointGenerator(String appId,
                                            SpServiceUrlProvider spServiceUrlProvider) {
    this.appId = appId;
    this.spServiceUrlProvider = spServiceUrlProvider;
  }

  public ExtensionsServiceEndpointGenerator(NamedStreamPipesEntity entity) {
    this.appId = entity.getAppId();
    this.spServiceUrlProvider = ExtensionsServiceEndpointUtils.getPipelineElementType(entity);
  }

  public String getEndpointResourceUrl() throws NoServiceEndpointsAvailableException {
    return spServiceUrlProvider.getInvocationUrl(selectService(), appId);
  }

  public String getEndpointBaseUrl() throws NoServiceEndpointsAvailableException {
    return selectService();
  }

  private List<String> getServiceEndpoints() {
    return SpServiceDiscovery
        .getServiceDiscovery()
        .getServiceEndpoints(
            DefaultSpServiceTypes.EXT,
            true,
            Collections
                .singletonList(
                    this.spServiceUrlProvider
                        .getServiceTag(appId)
                        .asString()
                )
        );
  }

  private String selectService() throws NoServiceEndpointsAvailableException {
    List<String> serviceEndpoints = getServiceEndpoints();
    if (serviceEndpoints.size() > 0) {
      return getServiceEndpoints().get(0);
    } else {
      LOG.error("Could not find any service endpoints for appId {}, serviceTag {}", appId,
          this.spServiceUrlProvider.getServiceTag(appId).asString());
      throw new NoServiceEndpointsAvailableException(
          "Could not find any matching service endpoints - are all software components running?");
    }
  }
}
