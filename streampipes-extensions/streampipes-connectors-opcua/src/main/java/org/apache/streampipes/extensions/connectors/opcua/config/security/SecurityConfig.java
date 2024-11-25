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

package org.apache.streampipes.extensions.connectors.opcua.config.security;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;

import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.client.security.DefaultClientCertificateValidator;
import org.eclipse.milo.opcua.stack.core.security.DefaultTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.List;

public class SecurityConfig {

  private final MessageSecurityMode securityMode;
  private final SecurityPolicy securityPolicy;

  public SecurityConfig(MessageSecurityMode securityMode,
                        SecurityPolicy securityPolicy) {
    this.securityMode = securityMode;
    this.securityPolicy = securityPolicy;
  }

  public void configureSecurityPolicy(String opcServerUrl,
                                      List<EndpointDescription> endpoints,
                                      OpcUaClientConfigBuilder builder)
      throws SpConfigurationException, URISyntaxException {
    String host = opcServerUrl.split("://")[1].split(":")[0];

    EndpointDescription tmpEndpoint = endpoints
        .stream()
        .filter(e -> e.getSecurityMode() == securityMode)
        .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
        .findFirst()
        .orElseThrow(() ->
            new SpConfigurationException("No endpoint available with security mode {} and security policy {}")
        );

    tmpEndpoint = updateEndpointUrl(tmpEndpoint, host);

    if (securityMode != MessageSecurityMode.None) {
      try {
        var env = Environments.getEnvironment();
        var securityDir = Paths.get(env.getOpcUaSecurityDir().getValueOrDefault());
        var trustListManager = new DefaultTrustListManager(securityDir.resolve("pki").toFile());

        var certificateValidator = new DefaultClientCertificateValidator(trustListManager);
        var loader = new KeyStoreLoader().load(env, securityDir);
        builder.setKeyPair(loader.getClientKeyPair());
        builder.setCertificate(loader.getClientCertificate());
        builder.setCertificateChain(loader.getClientCertificateChain());
        builder.setCertificateValidator(certificateValidator);
      } catch (Exception e) {
        throw new SpConfigurationException(
            "Failed to load keystore - check that all required environment variables "
                + "are defined and the keystore exists",
            e
        );
      }
    }

    builder.setEndpoint(tmpEndpoint);
  }

  private EndpointDescription updateEndpointUrl(EndpointDescription original,
                                                String hostname) throws URISyntaxException {

    URI uri = new URI(original.getEndpointUrl()).parseServerAuthority();

    String endpointUrl = String.format("%s://%s:%s%s", uri.getScheme(), hostname, uri.getPort(), uri.getPath());

    return new EndpointDescription(
        endpointUrl,
        original.getServer(),
        original.getServerCertificate(),
        original.getSecurityMode(),
        original.getSecurityPolicyUri(),
        original.getUserIdentityTokens(),
        original.getTransportProfileUri(),
        original.getSecurityLevel());
  }

  @Override
  public String toString() {
    return String.format("%s-%s", securityMode, securityPolicy);
  }
}
