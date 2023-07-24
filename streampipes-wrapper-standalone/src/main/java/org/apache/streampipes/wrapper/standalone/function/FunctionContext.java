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

package org.apache.streampipes.wrapper.standalone.function;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.api.monitoring.IExtensionsLogger;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.extensions.management.monitoring.ExtensionsLogger;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.schema.EventSchema;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FunctionContext {

  private final Map<String, SpDataStream> streams;

  private StreamPipesClient client;
  private String functionId;
  private ConfigExtractor config;

  private Map<String, SpOutputCollector> outputCollectors;
  private IExtensionsLogger extensionsLogger;

  public FunctionContext() {
    this.streams = new HashMap<>();
  }

  public FunctionContext(String functionId,
                         String serviceGroup,
                         List<SpDataStream> streams,
                         Map<String, SpOutputCollector> outputCollectors,
                         StreamPipesClient client) {
    this();
    streams.forEach(stream -> this.streams.put(stream.getElementId(), stream));
    this.config = ConfigExtractor.from(serviceGroup);
    this.functionId = functionId;
    this.outputCollectors = outputCollectors;
    this.client = client;
    this.extensionsLogger = new ExtensionsLogger(functionId);
  }

  public Collection<SpDataStream> getStreams() {
    return streams.values();
  }

  public EventSchema getSchema(String streamId) {
    return this.streams.get(streamId).getEventSchema();
  }

  public StreamPipesClient getClient() {
    return client;
  }

  public ConfigExtractor getConfig() {
    return config;
  }

  public String getFunctionId() {
    return functionId;
  }

  public IExtensionsLogger getLogger() {
    return extensionsLogger;
  }

  public Map<String, SpOutputCollector> getOutputCollectors() {
    return outputCollectors;
  }
}
