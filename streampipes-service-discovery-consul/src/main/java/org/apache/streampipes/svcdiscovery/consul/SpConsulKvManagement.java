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
package org.apache.streampipes.svcdiscovery.consul;

import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.svcdiscovery.api.ISpKvManagement;
import org.apache.streampipes.svcdiscovery.api.model.ConfigItem;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orbitz.consul.Consul;
import com.orbitz.consul.KeyValueClient;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.kv.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpConsulKvManagement extends ConsulProvider implements ISpKvManagement {

  private static final Logger LOG = LoggerFactory.getLogger(SpConsulKvManagement.class);

  private static final String CONSUL_NAMESPACE = "/sp/v1/";

  public <T> T getValueForRoute(String route, Class<T> type) {
    try {
      String entry = getKeyValue(route)
          .values()
          .stream()
          .findFirst()
          .orElse(null);

      if (type.equals(Integer.class)) {
        return (T) Integer.valueOf(JacksonSerializer.getObjectMapper().readValue(entry, ConfigItem.class).getValue());
      } else if (type.equals(Boolean.class)) {
        return (T) Boolean.valueOf(JacksonSerializer.getObjectMapper().readValue(entry, ConfigItem.class).getValue());
      } else {
        return type.cast(JacksonSerializer.getObjectMapper().readValue(entry, ConfigItem.class).getValue());
      }
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    throw new IllegalArgumentException("Cannot get entry from Consul");
  }

  public Map<String, String> getKeyValue(String route) {
    Consul consul = consulInstance();
    KeyValueClient keyValueClient = consul.keyValueClient();

    Map<String, String> keyValues = new HashMap<>();

    ConsulResponse<List<Value>> consulResponseWithValues = keyValueClient.getConsulResponseWithValues(route);

    if (consulResponseWithValues.getResponse() != null) {
      for (Value value : consulResponseWithValues.getResponse()) {
        String key = value.getKey();
        String v = "";
        if (value.getValueAsString().isPresent()) {
          v = value.getValueAsString().get();
        }
        keyValues.put(key, v);
      }
    }
    return keyValues;
  }

  public void updateConfig(String key, String entry, boolean password) {
    Consul consul = consulInstance();
    if (!password) {
      LOG.info("Updated config - key:" + key + " value: " + entry);
      consul.keyValueClient().putValue(key, entry);
    }
  }

  public void deleteConfig(String key) {
    Consul consul = consulInstance();
    LOG.info("Delete config: {}", key);
    consul.keyValueClient().deleteKeys(CONSUL_NAMESPACE + key);
  }
}
