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
package org.apache.streampipes.manager.remote;

import org.apache.streampipes.commons.exceptions.NoServiceEndpointsAvailableException;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestManager;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTarget;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequestTargets;
import org.apache.streampipes.manager.api.extensions.ExtensionServiceRequests;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointGenerator;
import org.apache.streampipes.manager.execution.endpoint.ExtensionsServiceEndpointUtils;
import org.apache.streampipes.manager.util.AuthTokenProvider;
import org.apache.streampipes.model.runtime.RuntimeOptionsRequest;
import org.apache.streampipes.model.runtime.RuntimeOptionsResponse;
import org.apache.streampipes.resource.management.SpResourceManager;
import org.apache.streampipes.resource.management.secret.SecretDecrypter;
import org.apache.streampipes.resource.management.secret.SecretService;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.model.SpServiceUrlProvider;

import com.google.gson.JsonSyntaxException;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.util.Set;

public class ContainerProvidedOptionsHandler {

  private final ExtensionServiceRequestManager extensionRequestManager;
  private final SpResourceManager resourceManager;

  public ContainerProvidedOptionsHandler(ExtensionServiceRequestManager extensionRequestManager,
                                         SpResourceManager resourceManager) {
    this.extensionRequestManager = extensionRequestManager;
    this.resourceManager = resourceManager;
  }

  public RuntimeOptionsResponse fetchRemoteOptions(RuntimeOptionsRequest request)
      throws SpConfigurationException, SpRuntimeException {

    try {
      new SecretService(new SecretDecrypter()).applyConfig(request.getStaticProperties());
      var payload = JacksonSerializer.getObjectMapper().writeValueAsString(request);
      var requestTarget = getEndpointRequestTarget(request.getAppId());
      var authToken = new AuthTokenProvider(resourceManager).getAuthTokenForCurrentUser();
      var response = extensionRequestManager.request(
          ExtensionServiceRequests.containerProvidedOptions(requestTarget, payload, authToken)
      );

      if (response.isSuccess()) {
        return handleResponse(response.responseBody());
      } else if (response.statusCode() == HttpStatus.SC_BAD_REQUEST) {
        throw handleConfigurationError(response.responseBody());
      } else {
        throw new SpRuntimeException(
            "Could not resolve runtime options, status code: " + response.statusCode());
      }
    } catch (SpConfigurationException | SpRuntimeException e) {
      throw e;
    } catch (Exception e) {
      throw new SpRuntimeException("Could not resolve runtime options", e);
    }
  }

  private RuntimeOptionsResponse handleResponse(String responseBody) throws JsonSyntaxException, IOException {
    return JacksonSerializer.getObjectMapper().readValue(responseBody, RuntimeOptionsResponse.class);
  }

  private SpConfigurationException handleConfigurationError(String responseBody) throws IOException {
    var exception = JacksonSerializer
        .getObjectMapper()
        .readValue(responseBody, SpConfigurationException.class);

    return new SpConfigurationException(exception.getMessage(), exception.getCause());
  }

  private ExtensionServiceRequestTarget getEndpointRequestTarget(String appId)
      throws NoServiceEndpointsAvailableException {
    SpServiceUrlProvider provider = ExtensionsServiceEndpointUtils.getPipelineElementType(appId);
    var service = new ExtensionsServiceEndpointGenerator().selectService(appId, provider, Set.of());
    return ExtensionServiceRequestTargets.containerProvidedOptions(service, provider, appId);
  }
}
