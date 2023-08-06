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
package org.apache.streampipes.extensions.management.client;

import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.client.api.credentials.CredentialsProvider;
import org.apache.streampipes.client.credentials.StreamPipesTokenCredentials;
import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.networking.Networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.List;

public class StreamPipesClientRuntimeConnectionResolver implements ClientConnectionUrlResolver {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesClientRuntimeConnectionResolver.class);
  private Environment env;

  public StreamPipesClientRuntimeConnectionResolver() {
    this.env = Environments.getEnvironment();
  }

  @Override
  public CredentialsProvider getCredentials() {
    return new StreamPipesTokenCredentials(getClientApiUser(), getClientApiSecret());
  }

  @Override
  public String getBaseUrl() throws SpRuntimeException {
    List<String> baseUrls = findClientServices();
    if (baseUrls.size() > 0) {
      if (env.getSpDebug().getValueOrDefault()) {
        try {
          return buildBaseUrl(Networking.getHostname());
        } catch (UnknownHostException e) {
          LOG.error("Could not auto-resolve host address - using http://localhost:8030");
          return "http://localhost:8030";
        }
      }
      return baseUrls.get(0);
    } else {
      return "";
    }
  }

  private String getClientApiUser() {
    return env.getClientUser().getValueOrDefault();
  }

  private String getClientApiSecret() {
    return env.getClientSecret().getValueOrDefault();
  }

  private List<String> findClientServices() {
    return List.of(buildBaseUrl(env.getSpCoreHost().getValueOrDefault()));
  }

  private String buildBaseUrl(String host) {
    return env.getSpCoreScheme().getValueOrDefault()
        + "://"
        + host
        + ":"
        + env.getSpCorePort().getValueOrDefault();
  }
}
