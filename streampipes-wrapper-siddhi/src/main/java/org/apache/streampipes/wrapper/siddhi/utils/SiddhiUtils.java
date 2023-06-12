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


import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;

import io.siddhi.core.event.Event;
import io.siddhi.query.api.definition.Attribute;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiddhiUtils {

  public static org.apache.streampipes.model.runtime.Event toSpEvent(List<Event> events,
                                                                     String listFieldName,
                                                                     SchemaInfo schemaInfo,
                                                                     SourceInfo sourceInfo,
                                                                     List<Attribute> streamAttributes) {
    List<Map<String, Object>> allEvents = new ArrayList<>();

    events.forEach(event -> allEvents.add(toMap(event, streamAttributes)));

    Map<String, Object> outMap = new HashMap<>();
    outMap.put(listFieldName, allEvents);

    return EventFactory.fromMap(outMap, sourceInfo, schemaInfo);
  }

  public static org.apache.streampipes.model.runtime.Event toSpEvent(Event event,
                                                                     SchemaInfo schemaInfo,
                                                                     SourceInfo sourceInfo,
                                                                     List<Attribute> streamAttributes) {
    Map<String, Object> outMap = toMap(event, streamAttributes);
    return EventFactory.fromMap(outMap, sourceInfo, schemaInfo);
  }

  public static Map<String, Object> toMap(Event event,
                                          List<Attribute> streamAttributes) {
    Map<String, Object> outMap = new HashMap<>();

    for (int i = 0; i < streamAttributes.size(); i++) {
      String outputKey = streamAttributes.get(i).getName();
      if (outputKey.startsWith(SiddhiConstants.FIRST_STREAM_PREFIX)
          || outputKey.startsWith(SiddhiConstants.SECOND_STREAM_PREFIX)) {
        outputKey = outputKey.substring(2);
      }
      Object data = event.getData(i);
      outMap.put(outputKey, data);
    }

    return outMap;
  }

  public static Object[] toObjArr(List<String> eventKeys, Map<String, Object> event) {
    Object[] result = new Object[eventKeys.size()];
    for (int i = 0; i < eventKeys.size(); i++) {
      result[i] = event.get(eventKeys.get(i));
    }

    return result;
  }

  public static String getPreparedOutputTopicName(IDataProcessorParameters params) {
    return prepareName(getOutputTopicName(params));
  }

  public static String getOutputTopicName(IDataProcessorParameters parameters) {
    return parameters
        .getModel()
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

  public static String prepareProperty(String propertyName) {
    return propertyName.replaceAll("::", "");
  }

}
