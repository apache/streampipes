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

import org.apache.streampipes.client.credentials.CredentialsProvider;
import org.apache.streampipes.client.credentials.StreamPipesTokenCredentials;
import org.apache.streampipes.client.model.ClientConnectionUrlResolver;
import org.apache.streampipes.commons.constants.DefaultEnvValues;
import org.apache.streampipes.commons.constants.Envs;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.commons.networking.Networking;
import org.apache.streampipes.svcdiscovery.SpServiceDiscovery;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceGroups;
import org.apache.streampipes.svcdiscovery.api.model.DefaultSpServiceTags;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class StreamPipesClientRuntimeConnectionResolver implements ClientConnectionUrlResolver {

  private static final Logger LOG = LoggerFactory.getLogger(StreamPipesClientRuntimeConnectionResolver.class);

  public StreamPipesClientRuntimeConnectionResolver() {

  }

  @Override
  public CredentialsProvider getCredentials() {
    return new StreamPipesTokenCredentials(getClientApiUser(), getClientApiSecret());
  }

  @Override
  public String getBaseUrl() throws SpRuntimeException {
    List<String> baseUrls = findClientServices();
    if (baseUrls.size() > 0) {
      if (Envs.SP_DEBUG.exists()) {
        try {
          return "http://" + Networking.getHostname() + ":" + 8030;
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
    if (Envs.SP_CLIENT_USER.exists()) {
      return Envs.SP_CLIENT_USER.getValue();
    } else {
      return DefaultEnvValues.INITIAL_CLIENT_USER_DEFAULT;
    }
  }

  private String getClientApiSecret() {
    if (Envs.SP_CLIENT_SECRET.exists()) {
      return Envs.SP_CLIENT_SECRET.getValue();
    } else {
      return DefaultEnvValues.INITIAL_CLIENT_SECRET_DEFAULT;
    }
  }

  private List<String> findClientServices() {
    return SpServiceDiscovery
        .getServiceDiscovery()
        .getServiceEndpoints(
            DefaultSpServiceGroups.CORE,
            true,
            Collections.singletonList(DefaultSpServiceTags.STREAMPIPES_CLIENT.asString())
        );
  }
}
