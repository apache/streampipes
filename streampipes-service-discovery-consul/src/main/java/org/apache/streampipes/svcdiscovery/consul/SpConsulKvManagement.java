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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.svcdiscovery.api.ISpKvManagement;

import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpConsulKvManagement extends AbstractConsulService implements ISpKvManagement {

  private static final Logger LOG = LoggerFactory.getLogger(SpConsulKvManagement.class);

  private static final String CONSUL_NAMESPACE = "/sp/v1/";

  public SpConsulKvManagement(Environment environment) {
    super(environment);
  }

  public Map<String, String> getKeyValue(String route) {
    var consul = consulInstance();

    Map<String, String> keyValues = new HashMap<>();
    Response<List<GetValue>> consulResponseWithValues = consul.getKVValues(route);

    if (consulResponseWithValues.getValue() != null) {
      for (GetValue value : consulResponseWithValues.getValue()) {
        String key = value.getKey();
        String v = "";
        if (value.getValue() != null) {
          v = value.getDecodedValue();
        }
        keyValues.put(key, v);
      }
    }
    return keyValues;
  }

  public void updateConfig(String key, String entry, boolean password) {
    var consul = consulInstance();
    if (!password) {
      LOG.info("Updated config - key:" + key + " value: " + entry);
      consul.setKVValue(key, entry);
    }
  }

  public void deleteConfig(String key) {
    var consul = consulInstance();
    LOG.info("Delete config: {}", key);
    consul.deleteKVValue(CONSUL_NAMESPACE + key);
  }
}
