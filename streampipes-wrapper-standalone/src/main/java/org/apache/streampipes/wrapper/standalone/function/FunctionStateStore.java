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
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class FunctionStateStore<T> implements StateStore<T> {

  private static final Logger LOG = LoggerFactory.getLogger(FunctionStateStore.class);

  private final String functionId;
  private final StreamPipesClient client;
  private final Class<T> stateClass;
  private final ObjectMapper objectMapper;

  public FunctionStateStore(String functionId,
                            StreamPipesClient client,
                            Class<T> stateClass) {
    this.functionId = functionId;
    this.client = client;
    this.stateClass = stateClass;
    this.objectMapper = JacksonSerializer.getObjectMapper();
  }

  @Override
  public T load(T defaultState) {
    try {
      var functionState = client.adminApi().getFunctionState(functionId);
      if (functionState.isPresent()) {
        return objectMapper.convertValue(functionState.get(), stateClass);
      } else {
        return defaultState;
      }
    } catch (RuntimeException e) {
      LOG.warn("Could not load function state for {}: {}", functionId, e.getMessage());
      return defaultState;
    }
  }

  @Override
  public void persist(T state) {
    try {
      var payload = objectMapper.convertValue(state, new TypeReference<Map<String, Object>>() {
      });
      client.adminApi().persistFunctionState(functionId, payload);
    } catch (RuntimeException e) {
      LOG.warn("Could not persist function state for {}: {}", functionId, e.getMessage());
    }
  }
}
