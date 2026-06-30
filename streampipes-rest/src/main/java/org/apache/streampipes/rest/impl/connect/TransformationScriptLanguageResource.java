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

package org.apache.streampipes.rest.impl.connect;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.connect.transformer.api.TransformationEngines;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.model.connect.ScriptMetadata;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.extensions.svcdiscovery.SpServiceRegistration;
import org.apache.streampipes.rest.security.AuthConstants;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.ISpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTypes;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/v2/connect/master/script-languages")
public class TransformationScriptLanguageResource {

  private final ISpServiceDiscovery serviceDiscovery;

  public TransformationScriptLanguageResource() {
    this(SpServiceDiscovery.getServiceDiscovery());
  }

  TransformationScriptLanguageResource(ISpServiceDiscovery serviceDiscovery) {
    this.serviceDiscovery = serviceDiscovery;
  }

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @PreAuthorize(AuthConstants.HAS_WRITE_ADAPTER_PRIVILEGE)
  public List<ScriptMetadata> getAll(@RequestBody AdapterDescription adapterDescription) throws
                                                                                         NoServiceEndpointsAvailableException {
    var languagesSupportedByCore = TransformationEngines.INSTANCE.getAvailableEngineMetadata();
    var matchingServices = serviceDiscovery.getService(
        DefaultSpServiceTypes.EXT, true,
        ExtensionsServiceEndpointUtils.getDesiredServiceTags(
            adapterDescription.getAppId(),
            SpServiceUrlProvider.ADAPTER,
            adapterDescription.getDeploymentConfiguration()
                              .getDesiredServiceTags()
        )
    );

    if (!matchingServices.isEmpty()) {
      return getSupportedScriptLanguages(matchingServices.get(0))
                             .filter(metadata -> languagesSupportedByCore
                                 .stream()
                                 .anyMatch(coreLanguage -> coreLanguage.language()
                                                                       .equals(metadata.language()))
                             )
                             .toList();
    } else {
      throw new NoServiceEndpointsAvailableException(
          "Transformation capability unavailable: No service endpoints found supporting script languages.");
    }

  }

  private Stream<ScriptMetadata> getSupportedScriptLanguages(
      SpServiceRegistration matchingService
  ) {
    var supportedScriptLanguages = matchingService.getSupportedScriptLanguages();
    return supportedScriptLanguages == null ? Stream.empty() : supportedScriptLanguages.stream();
  }
}
