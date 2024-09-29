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
package org.apache.streampipes.extensions.connectors.plc.adapter.generic.config;

import org.apache.streampipes.commons.exceptions.connect.AdapterException;
import org.apache.streampipes.extensions.connectors.plc.adapter.s7.config.ConfigurationParser;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventSchemaProvider {

  public List<EventProperty> makeSchema(Map<String, String> nodes) throws AdapterException {
    List<EventProperty> allProperties = new ArrayList<>();
    for (Map.Entry<String, String> entry : nodes.entrySet()) {
      var datatype = new ConfigurationParser().getStreamPipesDataType(entry.getValue());

      var primitiveProperty = PrimitivePropertyBuilder.create(datatype, entry.getKey()).label(entry.getKey())
              .description("").build();

      // Check if the address configuration is an array
      var isArray = new ConfigurationParser().isPLCArray(entry.getValue());

      if (isArray) {
        var propertyList = new EventPropertyList();
        propertyList.setRuntimeName(entry.getKey());
        propertyList.setLabel(entry.getKey());
        propertyList.setEventProperty(primitiveProperty);
        allProperties.add(propertyList);
      } else {
        allProperties.add(primitiveProperty);
      }
    }
    return allProperties;
  }
}
