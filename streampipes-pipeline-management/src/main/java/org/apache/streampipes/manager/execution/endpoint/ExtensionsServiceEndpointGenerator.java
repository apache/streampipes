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
import org.apache.streampipes.manager.api.extensions.IExtensionsServiceEndpointGenerator;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ExtensionsServiceEndpointGenerator implements IExtensionsServiceEndpointGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(ExtensionsServiceEndpointGenerator.class);

  public ExtensionsServiceEndpointGenerator() {
  }

  public String getEndpointResourceUrl(String appId,
                                       SpServiceUrlProvider spServiceUrlProvider,
                                       Set<SpServiceTag> customServiceTags)
      throws NoServiceEndpointsAvailableException {
    return spServiceUrlProvider.getInvocationUrl(selectService(appId, spServiceUrlProvider, customServiceTags), appId);
  }

  public String getEndpointBaseUrl(String appId,
                                   SpServiceUrlProvider spServiceUrlProvider,
                                   Set<SpServiceTag> customServiceTags) throws NoServiceEndpointsAvailableException {
    return selectService(appId, spServiceUrlProvider, customServiceTags);
  }

  private String selectService(String appId,
                               SpServiceUrlProvider spServiceUrlProvider,
                               Set<SpServiceTag> customServiceTags) throws NoServiceEndpointsAvailableException {
    List<String> serviceEndpoints = getServiceEndpoints(appId, spServiceUrlProvider, customServiceTags);
    if (!serviceEndpoints.isEmpty()) {
      return serviceEndpoints.get(0);
    } else {
      LOG.error("Could not find any service endpoints for appId {}, serviceTag {}", appId,
          spServiceUrlProvider.getServiceTag(appId).asString());
      throw new NoServiceEndpointsAvailableException(
          "Could not find any matching service endpoints - are all software components running?");
    }
  }

  private List<String> getServiceEndpoints(String appId,
                                           SpServiceUrlProvider spServiceUrlProvider,
                                           Set<SpServiceTag> customServiceTags) {
    return SpServiceDiscovery
        .getServiceDiscovery()
        .getServiceEndpoints(
            DefaultSpServiceTypes.EXT,
            true,
            getDesiredServiceTags(appId, spServiceUrlProvider, customServiceTags)
        );
  }

  private List<String> getDesiredServiceTags(String appId,
                                             SpServiceUrlProvider serviceUrlProvider,
                                             Set<SpServiceTag> customServiceTags) {
    return Stream.concat(
            Stream.of(
                serviceUrlProvider.getServiceTag(appId)
            ),
            customServiceTags.stream()
        )
        .map(SpServiceTag::asString)
        .toList();
  }
}
