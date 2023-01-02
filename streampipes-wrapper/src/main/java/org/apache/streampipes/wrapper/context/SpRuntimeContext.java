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
package org.apache.streampipes.wrapper.context;

import org.apache.streampipes.client.StreamPipesClient;
import org.apache.streampipes.extensions.management.config.ConfigExtractor;
import org.apache.streampipes.extensions.management.monitoring.SpMonitoringManager;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;

import java.util.List;

public class SpRuntimeContext implements RuntimeContext {

  private List<SchemaInfo> inputSchemaInfo;
  private List<SourceInfo> sourceInfo;
  private String correspondingUser;
  private ConfigExtractor configExtractor;
  private StreamPipesClient streamPipesClient;
  private SpMonitoringManager spLogManager;

  public SpRuntimeContext(List<SourceInfo> sourceInfo,
                          List<SchemaInfo> inputSchemaInfo,
                          String correspondingUser,
                          ConfigExtractor configExtractor,
                          StreamPipesClient streamPipesClient,
                          SpMonitoringManager spLogManager) {
    this.inputSchemaInfo = inputSchemaInfo;
    this.sourceInfo = sourceInfo;
    this.correspondingUser = correspondingUser;
    this.configExtractor = configExtractor;
    this.streamPipesClient = streamPipesClient;
    this.spLogManager = spLogManager;
  }

  public SpRuntimeContext() {

  }

  @Override
  public SpMonitoringManager getLogger() {
    return spLogManager;
  }

  @Override
  public List<SchemaInfo> getInputSchemaInfo() {
    return inputSchemaInfo;
  }

  @Override
  public List<SourceInfo> getInputSourceInfo() {
    return sourceInfo;
  }

  @Override
  public String getCorrespondingUser() {
    return correspondingUser;
  }

  @Override
  public ConfigExtractor getConfigStore() {
    return this.configExtractor;
  }

  @Override
  public StreamPipesClient getStreamPipesClient() {
    return streamPipesClient;
  }
}
