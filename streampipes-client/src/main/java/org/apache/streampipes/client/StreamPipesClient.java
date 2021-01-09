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
package org.apache.streampipes.client;

import org.apache.streampipes.client.api.*;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.dataformat.SpDataFormatFactory;

public class StreamPipesClient implements SupportsPipelineApi,
        SupportsPipelineElementTemplateApi,
        SupportsDataSinkApi,
        SupportsDataStreamApi {

  private static final Integer SP_DEFAULT_PORT = 80;

  private String streamPipesHost;
  private Integer streamPipesPort;
  private boolean httpsDisabled;

  private StreamPipesClientConfig config;

  public static StreamPipesClient create(String streamPipesHost,
                                         StreamPipesCredentials credentials,
                                         boolean httpsDisabled) {
    return new StreamPipesClient(streamPipesHost, SP_DEFAULT_PORT, credentials, httpsDisabled);
  }

  public static StreamPipesClient create(String streamPipesHost,
                                         StreamPipesCredentials credentials) {
    return new StreamPipesClient(streamPipesHost, SP_DEFAULT_PORT, credentials, false);
  }

  public static StreamPipesClient create(String streamPipesHost,
                                         Integer streamPipesPort,
                                         StreamPipesCredentials credentials) {
    return new StreamPipesClient(streamPipesHost, streamPipesPort, credentials, false);
  }

  public static StreamPipesClient create(String streamPipesHost,
                                         Integer streamPipesPort,
                                         StreamPipesCredentials credentials,
                                         boolean httpsDisabled) {
    return new StreamPipesClient(streamPipesHost, streamPipesPort, credentials, httpsDisabled);
  }

  private StreamPipesClient(String streamPipesHost,
                            Integer streamPipesPort,
                            StreamPipesCredentials credentials,
                            boolean httpsDisabled) {
    this.streamPipesHost = streamPipesHost;
    this.streamPipesPort = streamPipesPort;
    this.config = new StreamPipesClientConfig(credentials, streamPipesHost, streamPipesPort, httpsDisabled);
  }

  public void registerDataFormat(SpDataFormatFactory spDataFormatFactory) {
    this.config.addDataFormat(spDataFormatFactory);
  }

  public String getStreamPipesHost() {
    return streamPipesHost;
  }

  public Integer getStreamPipesPort() {
    return streamPipesPort;
  }

  public StreamPipesCredentials getCredentials() {
    return config.getCredentials();
  }

  @Override
  public PipelineApi pipelines() {
    return new PipelineApi(config);
  }

  @Override
  public PipelineElementTemplateApi pipelineElementTemplates() {
    return new PipelineElementTemplateApi(config);
  }

  @Override
  public DataSinkApi sinks() {
    return new DataSinkApi(config);
  }

  @Override
  public DataStreamApi streams() {
    return new DataStreamApi(config);
  }
}
