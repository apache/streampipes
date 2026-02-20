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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class FunctionStateStore<T> implements StateStore<T> {

  private static final Logger LOG = LoggerFactory.getLogger(FunctionStateStore.class);
  private static final String STATE_PAYLOAD_KEY = "payload";

  private final String functionId;
  private final StreamPipesClient client;
  private final Class<T> stateClass;
  private final ObjectMapper objectMapper;
  private Map<String, Object> persistedStatePayload;

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
        var persistedState = functionState.get();
        if (persistedState.containsKey(STATE_PAYLOAD_KEY)) {
          return objectMapper.convertValue(persistedState.get(STATE_PAYLOAD_KEY), stateClass);
        } else {
          // Backward-compatible path for states stored before payload wrapping was introduced.
          return objectMapper.convertValue(persistedState, stateClass);
        }
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
      Map<String, Object> payload = new HashMap<>();
      payload.put(STATE_PAYLOAD_KEY, objectMapper.convertValue(state, Object.class));
      this.persistedStatePayload = payload;
    } catch (RuntimeException e) {
      LOG.warn("Could not persist function state for {}: {}", functionId, e.getMessage());
    }
  }

  public Map<String, Object> getPersistedStatePayload() {
    return persistedStatePayload;
  }
}
