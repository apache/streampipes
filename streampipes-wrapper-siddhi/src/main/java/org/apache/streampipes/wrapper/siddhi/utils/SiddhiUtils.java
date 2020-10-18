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
package org.apache.streampipes.wrapper.siddhi.utils;

import io.siddhi.core.event.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.wrapper.params.binding.EventProcessorBindingParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiddhiUtils {

  public static org.apache.streampipes.model.runtime.Event toSpEvent(Event event, List<String> outputEventKeys, SchemaInfo
          schemaInfo, SourceInfo sourceInfo) {
    Map<String, Object> outMap = new HashMap<>();

    for (int i = 0; i < outputEventKeys.size(); i++) {
      if (event.getData(i) instanceof List) {
        List<Object> tmp = (List<Object>) event.getData(i);
        outMap.put(outputEventKeys.get(i), tmp.get(0));
      }
      else {
        outMap.put(outputEventKeys.get(i), event.getData(i));
      }
    }
    return EventFactory.fromMap(outMap, sourceInfo, schemaInfo);
  }

  public static Object[] toObjArr(List<String> eventKeys, Map<String, Object> event) {
    Object[] result = new Object[eventKeys.size()];
    for (int i = 0; i < eventKeys.size(); i++) {
      result[i] = event.get(eventKeys.get(i));
    }

    return result;
  }

  public static String getOutputTopicName(EventProcessorBindingParams parameters) {
    return parameters
            .getGraph()
            .getOutputStream()
            .getEventGrounding()
            .getTransportProtocol()
            .getTopicDefinition()
            .getActualTopicName();
  }

  public static String prepareName(String eventName) {
    return eventName
            .replaceAll("\\.", "")
            .replaceAll("-", "")
            .replaceAll("::", "");
  }
}
