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

import org.apache.streampipes.client.api.AdminApi;
import org.apache.streampipes.client.api.CustomRequestApi;
import org.apache.streampipes.client.api.DataLakeMeasureApi;
import org.apache.streampipes.client.api.DataProcessorApi;
import org.apache.streampipes.client.api.DataSinkApi;
import org.apache.streampipes.client.api.DataStreamApi;
import org.apache.streampipes.client.api.FileApi;
import org.apache.streampipes.client.api.IAdminApi;
import org.apache.streampipes.client.api.ICustomRequestApi;
import org.apache.streampipes.client.api.IPipelineElementTemplateApi;
import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.client.api.NotificationsApi;
import org.apache.streampipes.client.api.PipelineApi;
import org.apache.streampipes.client.api.PipelineElementTemplateApi;
import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.client.api.credentials.CredentialsProvider;
import org.apache.streampipes.client.model.StreamPipesClientConfig;
import org.apache.streampipes.client.model.StreamPipesClientConnectionConfig;
import org.apache.streampipes.client.paths.ApiPath;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.dataformat.cbor.CborDataFormatFactory;
import org.apache.streampipes.dataformat.fst.FstDataFormatFactory;
import org.apache.streampipes.dataformat.json.JsonDataFormatFactory;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.model.mail.SpEmail;

public class StreamPipesClient implements
    IStreamPipesClient {

  private static final Integer SP_DEFAULT_PORT = 80;

  private StreamPipesClientConfig config;

  private StreamPipesClient(ClientConnectionUrlResolver connectionConfig) {
    this.config = new StreamPipesClientConfig(connectionConfig);
    this.registerDataFormat(new JsonDataFormatFactory());
    this.registerDataFormat(new FstDataFormatFactory());
    this.registerDataFormat(new CborDataFormatFactory());
  }

  private StreamPipesClient(String streamPipesHost,
                            Integer streamPipesPort,
                            CredentialsProvider credentials,
                            boolean httpsDisabled) {
    this(new StreamPipesClientConnectionConfig(credentials, streamPipesHost, streamPipesPort, httpsDisabled));
  }

  /**
   * Create a new StreamPipes API client with a runtime connection resolver
   *
   * @param connectionConfig A ClientConnectionConfigResolver providing connection details
   */
  public static StreamPipesClient create(ClientConnectionUrlResolver connectionConfig) {
    return new StreamPipesClient(connectionConfig);
  }

  /**
   * Create a new StreamPipes API client with default port and custom HTTPS settings
   *
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param credentials     The credentials object
   * @param httpsDisabled   Set true if the instance is not served over HTTPS
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         CredentialsProvider credentials,
                                         boolean httpsDisabled) {
    return new StreamPipesClient(streamPipesHost, SP_DEFAULT_PORT, credentials, httpsDisabled);
  }

  /**
   * Create a new StreamPipes API client with default port 80 and HTTPS settings (HTTPS enabled)
   *
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param credentials     The credentials object
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         CredentialsProvider credentials) {
    return new StreamPipesClient(streamPipesHost, SP_DEFAULT_PORT, credentials, false);
  }

  /**
   * Create a new StreamPipes API client with custom port and default HTTPS settings
   *
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param streamPipesPort The port of the StreamPipes instance
   * @param credentials     The credentials object
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         Integer streamPipesPort,
                                         CredentialsProvider credentials) {
    return new StreamPipesClient(streamPipesHost, streamPipesPort, credentials, false);
  }

  /**
   * Create a new StreamPipes API client with custom port and HTTPS settings
   *
   * @param streamPipesHost The hostname of the StreamPipes instance without scheme
   * @param streamPipesPort The port of the StreamPipes instance
   * @param credentials     The credentials object
   * @param httpsDisabled   Set true if the instance is not served over HTTPS
   */
  public static StreamPipesClient create(String streamPipesHost,
                                         Integer streamPipesPort,
                                         CredentialsProvider credentials,
                                         boolean httpsDisabled) {
    return new StreamPipesClient(streamPipesHost, streamPipesPort, credentials, httpsDisabled);
  }

  /**
   * Register a new data format that is used by the live API
   *
   * @param spDataFormatFactory The data format factory
   */
  @Override
  public void registerDataFormat(SpDataFormatFactory spDataFormatFactory) {
    this.config.addDataFormat(spDataFormatFactory);
  }

  @Override
  public void registerProtocol(SpProtocolDefinitionFactory<?> spProtocolDefinitionFactory) {
    this.config.addTransportProtocol(spProtocolDefinitionFactory);
  }

  @Override
  public CredentialsProvider getCredentials() {
    return config.getConnectionConfig().getCredentials();
  }

  @Override
  public StreamPipesClientConfig getConfig() {
    return config;
  }

  @Override
  public ClientConnectionUrlResolver getConnectionConfig() {
    return config.getConnectionConfig();
  }

  /**
   * Get API to work with pipelines
   *
   * @return {@link org.apache.streampipes.client.api.PipelineApi}
   */
  @Override
  public PipelineApi pipelines() {
    return new PipelineApi(config);
  }

  /**
   * Get API to work with pipline element templates
   *
   * @return {@link org.apache.streampipes.client.api.PipelineElementTemplateApi}
   */
  @Override
  public IPipelineElementTemplateApi pipelineElementTemplates() {
    return new PipelineElementTemplateApi(config);
  }

  /**
   * Get API to work with data sinks
   *
   * @return {@link org.apache.streampipes.client.api.DataSinkApi}
   */
  @Override
  public DataSinkApi sinks() {
    return new DataSinkApi(config);
  }

  /**
   * Get API to work with data streams
   *
   * @return {@link org.apache.streampipes.client.api.DataStreamApi}
   */
  @Override
  public DataStreamApi streams() {
    return new DataStreamApi(config);
  }

  /**
   * Get API to work with data processors
   *
   * @return {@link DataProcessorApi}
   */
  @Override
  public DataProcessorApi processors() {
    return new DataProcessorApi(config);
  }

  @Override
  public ICustomRequestApi customRequest() {
    return new CustomRequestApi(config);
  }

  @Override
  public IAdminApi adminApi() {
    return new AdminApi(config);
  }

  @Override
  public NotificationsApi notificationsApi() {
    return new NotificationsApi(config);
  }

  @Override
  public DataLakeMeasureApi dataLakeMeasureApi() {
    return new DataLakeMeasureApi(config);
  }

  @Override
  public void deliverEmail(SpEmail email) {
    ICustomRequestApi api = customRequest();
    api.sendPost(ApiPath.EMAIL_RESOURCE, email);
  }

  @Override
  public FileApi fileApi() {
    return new FileApi(config);
  }
}
