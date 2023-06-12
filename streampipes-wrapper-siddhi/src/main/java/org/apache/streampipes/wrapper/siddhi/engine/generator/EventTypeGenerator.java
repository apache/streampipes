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
package org.apache.streampipes.wrapper.siddhi.engine.generator;

import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class EventTypeGenerator {

  private final IDataProcessorParameters params;

  public EventTypeGenerator(IDataProcessorParameters params) {
    this.params = params;
  }

  public List<EventPropertyDef> generateOutEventTypes() {
    List<EventPropertyDef> sortedEventKeys = new ArrayList<>();
    params.getOutEventType().forEach((key, value) -> {
      sortedEventKeys.add(makeEventType(key, value));
      sortedEventKeys.sort(Comparator.comparing(EventPropertyDef::getFieldName));
    });
    return sortedEventKeys;
  }

  public Map<String, List<EventPropertyDef>> generateInEventTypes() {
    Map<String, List<EventPropertyDef>> listOfEventKeys = new HashMap<>();
    AtomicReference<Integer> currentStreamIndex = new AtomicReference<>(0);

    params.getInEventTypes().forEach((key, value) -> {
      List<EventPropertyDef> sortedEventKeys = new ArrayList<>();
      for (String propertyKey : value.keySet()) {
        sortedEventKeys.add(makeEventType(currentStreamIndex.get(), propertyKey, value.get(propertyKey)));
        sortedEventKeys.sort(Comparator.comparing(EventPropertyDef::getFieldName));
      }
      listOfEventKeys.put(key, sortedEventKeys);
      currentStreamIndex.getAndSet(currentStreamIndex.get() + 1);
    });

    return listOfEventKeys;
  }

  private EventPropertyDef makeEventType(String propertyName,
                                         Object propertyType) {
    return new EventPropertyDef(propertyName, toType((Class<?>) propertyType));
  }

  private EventPropertyDef makeEventType(Integer currentStreamIndex,
                                         String propertyName,
                                         Object propertyType) {
    return new EventPropertyDef(toSelectorPrefix(currentStreamIndex), propertyName, toType((Class<?>) propertyType));
  }

  private String toType(Class<?> o) {
    if (o.equals(Long.class)) {
      return SiddhiConstants.SIDDHI_LONG_TYPE;
    } else if (o.equals(Integer.class)) {
      return SiddhiConstants.SIDDHI_INT_TYPE;
    } else if (o.equals(Double.class)) {
      return SiddhiConstants.SIDDHI_DOUBLE_TYPE;
    } else if (o.equals(Float.class)) {
      return SiddhiConstants.SIDDHI_DOUBLE_TYPE;
    } else if (o.equals(Boolean.class)) {
      return SiddhiConstants.SIDDHI_BOOLEAN_TYPE;
    } else if (o.equals(String.class)) {
      return SiddhiConstants.SIDDHI_STRING_TYPE;
    } else {
      return SiddhiConstants.SIDDHI_OBJECT_TYPE;
    }
  }

  private String toSelectorPrefix(Integer currentStreamIndex) {
    return currentStreamIndex == 0 ? SiddhiConstants.FIRST_STREAM_PREFIX : SiddhiConstants.SECOND_STREAM_PREFIX;
  }
}
