/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.runtime;

import org.streampipes.model.constants.PropertySelectorConstants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RuntimeTestUtils {

  public static Map<String, Object> simpleMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("timestamp", 1);

    return map;
  }

  public static Map<String, Object> listMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("list", Arrays.asList("1", "2", "3"));

    return map;
  }

  public static Map<String, Object> nestedListMap() {
    Map<String, Object> map = new HashMap<>();
    map.put("list", Arrays.asList("1", "2", "3"));

    return map;
  }

  public static Map<String, Object> nestedMap() {
    Map<String, Object> baseMap = simpleMap();
    Map<String, Object> nestedMap = simpleMap();
    nestedMap.put("timestamp2", 2);
    baseMap.put("nested", nestedMap);
    return baseMap;
  }

  public static SourceInfo getSourceInfo() {
    return new SourceInfo(UUID.randomUUID().toString(), PropertySelectorConstants
            .FIRST_STREAM_ID_PREFIX);
  }

  public static Event makeSimpleEvent(Map<String, Object> runtimeMap, SourceInfo sourceInfo) {
    return EventFactory.fromMap(runtimeMap, sourceInfo, new SchemaInfo(null, null));
  }
}
