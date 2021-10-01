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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.streampipes.client.credentials.CredentialsProvider;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import java.util.ArrayList;
import java.util.List;

public class StreamPipesClientConfig {

  private CredentialsProvider credentials;
  private String streamPipesHost;
  private Integer streamPipesPort;
  private ObjectMapper serializer;
  private boolean httpsDisabled;
  private List<SpDataFormatFactory> registeredDataFormats;

  public StreamPipesClientConfig(CredentialsProvider credentials,
                                 String streamPipesHost,
                                 Integer streamPipesPort,
                                 boolean httpsDisabled) {
    this.credentials = credentials;
    this.streamPipesHost = streamPipesHost;
    this.streamPipesPort = streamPipesPort;
    this.httpsDisabled = httpsDisabled;
    this.serializer = JacksonSerializer.getObjectMapper();
    this.registeredDataFormats = new ArrayList<>();
  }

  public CredentialsProvider getCredentials() {
    return credentials;
  }

  public String getStreamPipesHost() {
    return streamPipesHost;
  }

  public Integer getStreamPipesPort() {
    return streamPipesPort;
  }

  public ObjectMapper getSerializer() {
    return serializer;
  }

  public boolean isHttpsDisabled() {
    return httpsDisabled;
  }

  public void addDataFormat(SpDataFormatFactory spDataFormatFactory) {
    this.registeredDataFormats.add(spDataFormatFactory);
  }

  public List<SpDataFormatFactory> getRegisteredDataFormats() {
    return registeredDataFormats;
  }
}
