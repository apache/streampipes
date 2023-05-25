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
package org.apache.streampipes.client.model;

import org.apache.streampipes.client.api.config.ClientConnectionConfigResolver;
import org.apache.streampipes.client.api.credentials.CredentialsProvider;

public class StreamPipesClientConnectionConfig implements ClientConnectionConfigResolver {

  private CredentialsProvider credentialsProvider;
  private String streamPipesHost;
  private Integer streamPipesPort;
  private boolean httpsDisabled;

  public StreamPipesClientConnectionConfig(CredentialsProvider credentialsProvider,
                                           String streamPipesHost,
                                           Integer streamPipesPort,
                                           boolean httpsDisabled) {
    this.credentialsProvider = credentialsProvider;
    this.streamPipesHost = streamPipesHost;
    this.streamPipesPort = streamPipesPort;
    this.httpsDisabled = httpsDisabled;
  }

  @Override
  public CredentialsProvider getCredentials() {
    return credentialsProvider;
  }

  @Override
  public String getStreamPipesHost() {
    return streamPipesHost;
  }

  @Override
  public Integer getStreamPipesPort() {
    return streamPipesPort;
  }

  @Override
  public boolean isHttpsDisabled() {
    return httpsDisabled;
  }
}
