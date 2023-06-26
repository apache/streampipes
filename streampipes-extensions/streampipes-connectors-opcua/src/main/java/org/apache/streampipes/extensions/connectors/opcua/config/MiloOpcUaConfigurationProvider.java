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

package org.apache.streampipes.extensions.connectors.opcua.config;

import org.apache.streampipes.commons.exceptions.SpConfigurationException;

import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.sdk.client.api.identity.UsernameProvider;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MiloOpcUaConfigurationProvider {

  public OpcUaClientConfig makeClientConfig(OpcUaConfig spOpcConfig)
      throws ExecutionException, InterruptedException, SpConfigurationException, URISyntaxException {
    String opcServerUrl = spOpcConfig.getOpcServerURL();
    List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(opcServerUrl).get();
    String host = opcServerUrl.split("://")[1].split(":")[0];

    EndpointDescription tmpEndpoint = endpoints
        .stream()
        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
        .findFirst()
        .orElseThrow(() -> new SpConfigurationException("No endpoint with security policy none"));

    tmpEndpoint = updateEndpointUrl(tmpEndpoint, host);
    endpoints = Collections.singletonList(tmpEndpoint);

    EndpointDescription endpoint = endpoints
        .stream()
        .filter(e -> e.getSecurityPolicyUri().equals(SecurityPolicy.None.getUri()))
        .findFirst().orElseThrow(() -> new SpConfigurationException("no desired endpoints returned"));

    return buildConfig(endpoint, spOpcConfig);
  }

  private OpcUaClientConfig buildConfig(EndpointDescription endpoint,
                                        OpcUaConfig spOpcConfig) {

    OpcUaClientConfigBuilder builder = defaultBuilder(endpoint);
    if (!spOpcConfig.isUnauthenticated()) {
      builder.setIdentityProvider(new UsernameProvider(spOpcConfig.getUsername(), spOpcConfig.getPassword()));
    }
    return builder.build();
  }

  private OpcUaClientConfigBuilder defaultBuilder(EndpointDescription endpoint) {
    return OpcUaClientConfig.builder()
        .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
        .setApplicationUri("urn:eclipse:milo:examples:client")
        .setEndpoint(endpoint);
  }

  private EndpointDescription updateEndpointUrl(
      EndpointDescription original, String hostname) throws URISyntaxException {

    URI uri = new URI(original.getEndpointUrl()).parseServerAuthority();

    String endpointUrl = String.format(
        "%s://%s:%s%s",
        uri.getScheme(),
        hostname,
        uri.getPort(),
        uri.getPath()
    );

    return new EndpointDescription(
        endpointUrl,
        original.getServer(),
        original.getServerCertificate(),
        original.getSecurityMode(),
        original.getSecurityPolicyUri(),
        original.getUserIdentityTokens(),
        original.getTransportProfileUri(),
        original.getSecurityLevel()
    );
  }
}
