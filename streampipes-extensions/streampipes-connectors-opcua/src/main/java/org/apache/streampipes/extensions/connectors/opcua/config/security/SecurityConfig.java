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

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;
import org.apache.streampipes.extensions.connectors.opcua.config.OpcUaConfig;
import org.apache.streampipes.extensions.connectors.opcua.utils.OpcUaCertificateUtils;
import org.apache.streampipes.model.opcua.Certificate;

import org.eclipse.milo.opcua.sdk.client.OpcUaClientConfigBuilder;
import org.eclipse.milo.opcua.stack.core.security.FileBasedTrustListManager;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.enumerated.MessageSecurityMode;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SecurityConfig {

  private final MessageSecurityMode securityMode;
  private final SecurityPolicy securityPolicy;
  private final IStreamPipesClient streamPipesClient;
  private final boolean disallowInsecureEndpoints;

  public SecurityConfig(MessageSecurityMode securityMode,
                        SecurityPolicy securityPolicy,
                        IStreamPipesClient streamPipesClient) {
    this(
        securityMode,
        securityPolicy,
        streamPipesClient,
        Environments.getEnvironment().getOpcUaDisallowInsecureEndpoints().getValueOrDefault()
    );
  }

  SecurityConfig(MessageSecurityMode securityMode,
                 SecurityPolicy securityPolicy,
                 IStreamPipesClient streamPipesClient,
                 boolean disallowInsecureEndpoints) {
    this.securityMode = securityMode;
    this.securityPolicy = securityPolicy;
    this.streamPipesClient = streamPipesClient;
    this.disallowInsecureEndpoints = disallowInsecureEndpoints;
  }

  public void configureSecurityPolicy(OpcUaConfig config,
                                      List<EndpointDescription> endpoints,
                                      OpcUaClientConfigBuilder builder)
      throws SpConfigurationException, URISyntaxException {
    if (disallowInsecureEndpoints && (securityMode == MessageSecurityMode.None || securityPolicy == SecurityPolicy.None)) {
      throw new SpConfigurationException(
          "OPC UA connections with security mode None or security policy None are disabled by "
              + "SP_OPCUA_DISALLOW_INSECURE_ENDPOINTS"
      );
    }

    URI configuredServerUri = new URI(config.getOpcServerURL()).parseServerAuthority();

    EndpointDescription tmpEndpoint = endpoints
        .stream()
        .filter(e -> e.getSecurityMode() == securityMode)
        .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getUri()))
        .findFirst()
        .orElseThrow(() ->
            new SpConfigurationException(
                String.format(
                    "No endpoint available with security mode %s and security policy %s",
                    securityMode,
                    securityPolicy)
            )
        );

    tmpEndpoint = updateEndpointUrl(tmpEndpoint, configuredServerUri);

    if (securityMode != MessageSecurityMode.None) {
      try {
        var env = Environments.getEnvironment();
        var securityDir = Paths.get(env.getOpcUaSecurityDir().getValueOrDefault());
        var trustListManager = FileBasedTrustListManager.createAndInitialize(securityDir.resolve("pki"));

        var loadedCerts = new AtomicReference<>(fetchTrustedCertsFromRest());

        var compositeValidator = new CompositeCertificateValidator(
            config,
            trustListManager,
            loadedCerts.get(),
            List.of(),
            streamPipesClient
        );

        var loader = new KeyStoreLoader().load(env, securityDir);
        builder.setKeyPair(loader.getClientKeyPair());
        builder.setCertificate(loader.getClientCertificate());
        builder.setCertificateChain(loader.getClientCertificateChain());
        builder.setCertificateValidator(compositeValidator);
      } catch (Exception e) {
        throw new SpConfigurationException(
            "Failed to initialize OPC UA client security material - check that all required environment variables "
                + "are defined and the security directory is writable",
            e
        );
      }
    }

    builder.setEndpoint(tmpEndpoint);
  }

  EndpointDescription updateEndpointUrl(EndpointDescription original,
                                        URI configuredServerUri) throws URISyntaxException {

    URI uri = new URI(original.getEndpointUrl()).parseServerAuthority();

    String hostname = configuredServerUri.getHost() != null ? configuredServerUri.getHost() : uri.getHost();
    int port = configuredServerUri.getPort() != -1 ? configuredServerUri.getPort() : uri.getPort();
    String endpointUrl = String.format("%s://%s:%s%s", uri.getScheme(), hostname, port, uri.getPath());

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

  private List<X509Certificate> fetchTrustedCertsFromRest() throws SpConfigurationException {
    try {
      var response = streamPipesClient.customRequest().getList(OpcUaCertificateUtils.getCoreTrustedCertificatePath(), Certificate.class);
      return response
          .stream()
          .map(res -> {
            byte[] derBytes = Base64.getDecoder().decode(res.getCertificateDerBase64());
            CertificateFactory certFactory = null;
            try {
              certFactory = CertificateFactory.getInstance("X.509");
              try (ByteArrayInputStream in = new ByteArrayInputStream(derBytes)) {
                return (X509Certificate) certFactory.generateCertificate(in);
              }
            } catch (CertificateException | IOException e) {
              throw new RuntimeException(e);
            }
          })
          .toList();
    } catch (Exception e) {
      throw new SpConfigurationException("Could not fetch trusted certificates from REST API", e);
    }
  }

  @Override
  public String toString() {
    return String.format("%s-%s", securityMode, securityPolicy);
  }
}
