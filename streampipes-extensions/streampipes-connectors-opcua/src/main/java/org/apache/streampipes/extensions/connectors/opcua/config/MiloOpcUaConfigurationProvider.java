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

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpConfigurationException;

import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.stack.client.DiscoveryClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;

import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MiloOpcUaConfigurationProvider {

  public OpcUaClientConfig makeClientConfig(OpcUaConfig spOpcConfig)
      throws ExecutionException, InterruptedException, SpConfigurationException, URISyntaxException {
    String opcServerUrl = spOpcConfig.getOpcServerURL();
    String applicationUri = Environments.getEnvironment().getOpcUaApplicationUri().getValueOrDefault();
    List<EndpointDescription> endpoints = DiscoveryClient.getEndpoints(opcServerUrl).get();

    var builder = OpcUaClientConfig.builder()
        .setApplicationName(LocalizedText.english("Apache StreamPipes"))
        .setApplicationUri(applicationUri);

    spOpcConfig.getSecurityConfig().configureSecurityPolicy(opcServerUrl, endpoints, builder);
    spOpcConfig.getIdentityConfig().configureIdentity(builder);

    return builder.build();
  }
}
