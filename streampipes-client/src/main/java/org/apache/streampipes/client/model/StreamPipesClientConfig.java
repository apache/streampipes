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

import org.apache.streampipes.client.api.config.ClientConnectionUrlResolver;
import org.apache.streampipes.client.api.config.IStreamPipesClientConfig;
import org.apache.streampipes.dataformat.SpDataFormatFactory;
import org.apache.streampipes.dataformat.SpDataFormatManager;
import org.apache.streampipes.messaging.SpProtocolDefinitionFactory;
import org.apache.streampipes.messaging.SpProtocolManager;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

public class StreamPipesClientConfig implements IStreamPipesClientConfig {

  private final ClientConnectionUrlResolver connectionConfig;
  private final ObjectMapper serializer;

  public StreamPipesClientConfig(ClientConnectionUrlResolver connectionConfig) {
    this.connectionConfig = connectionConfig;
    this.serializer = JacksonSerializer.getObjectMapper();
  }

  @Override
  public ObjectMapper getSerializer() {
    return serializer;
  }

  @Override
  public void addDataFormat(SpDataFormatFactory spDataFormatFactory) {
    SpDataFormatManager.INSTANCE.register(spDataFormatFactory);
  }

  @Override
  public void addTransportProtocol(SpProtocolDefinitionFactory<?> protocolDefinitionFactory) {
    SpProtocolManager.INSTANCE.register(protocolDefinitionFactory);
  }

  @Override
  public ClientConnectionUrlResolver getConnectionConfig() {
    return connectionConfig;
  }
}
