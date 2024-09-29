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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic.connection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.types.PlcResponseCode;
import org.apache.plc4x.java.api.value.PlcValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlcEventGenerator {

  private static final Logger LOG = LoggerFactory.getLogger(PlcEventGenerator.class);

  private final Map<String, String> nodes;

  public PlcEventGenerator(Map<String, String> nodes) {
    this.nodes = nodes;
  }

  public Map<String, Object> makeEvent(PlcReadResponse response) {
    var event = new HashMap<String, Object>();

    for (String key : nodes.keySet()) {
      if (response.getResponseCode(key) == PlcResponseCode.OK) {

        // if the response is a list, add each element to the result
        if (response.getObject(key) instanceof List) {
          event.put(key, response.getAsPlcValue().getValue(key).getList().stream().map(PlcValue::getObject).toList()
                  .toArray());
        } else {
          event.put(key, response.getObject(key));
        }
      } else {
        LOG.error("Error[" + key + "]: " + response.getResponseCode(key).name());
      }
    }
    return event;
  }
}
