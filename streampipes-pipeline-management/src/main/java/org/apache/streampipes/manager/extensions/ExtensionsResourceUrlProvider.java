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
package org.apache.streampipes.manager.extensions;

import org.apache.streampipes.manager.api.extensions.IExtensionsResourceUrlProvider;
import org.apache.streampipes.model.extensions.ExtensionItemDescription;
import org.apache.streampipes.model.extensions.ExtensionItemInstallationRequest;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTag;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceTagPrefix;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import java.util.List;

public class ExtensionsResourceUrlProvider implements IExtensionsResourceUrlProvider {

  private final ISpServiceDiscovery serviceDiscovery;

  public ExtensionsResourceUrlProvider(ISpServiceDiscovery serviceDiscovery) {
    this.serviceDiscovery = serviceDiscovery;
  }

  @Override
  public String getDescriptionUrl(ExtensionItemInstallationRequest installationReq) {
    var baseUrl = getMatchingServiceBaseUrl(installationReq.serviceTagPrefix(), installationReq.appId());
    var urlProvider = getServiceUrlProvider(installationReq.serviceTagPrefix());
    return urlProvider.getInvocationUrl(baseUrl, installationReq.appId());
  }

  @Override
  public String getIconUrl(ExtensionItemDescription endpointItem) {
    var baseUrl = getMatchingServiceBaseUrl(endpointItem.getServiceTagPrefix(), endpointItem.getAppId());
    var urlProvider = getServiceUrlProvider(endpointItem.getServiceTagPrefix());
    return urlProvider.getIconUrl(baseUrl, endpointItem.getAppId());
  }

  private SpServiceUrlProvider getServiceUrlProvider(SpServiceTagPrefix serviceTagPrefix) {
    return SpServiceUrlProvider.valueOf(serviceTagPrefix.name());
  }

  private String getMatchingServiceBaseUrl(SpServiceTagPrefix serviceTagPrefix, String appId) throws RuntimeException {
    var serviceTag = getServiceTag(serviceTagPrefix, appId);
    var services = serviceDiscovery.getServiceEndpoints(DefaultSpServiceTypes.EXT, true, List.of(serviceTag));
    if (!services.isEmpty()) {
      return services.get(0);
    } else {
      throw new RuntimeException("Could not find matching service");
    }
  }

  private String getServiceTag(SpServiceTagPrefix tagPrefix, String appId) {
    return SpServiceTag.create(tagPrefix, appId).asString();
  }
}
