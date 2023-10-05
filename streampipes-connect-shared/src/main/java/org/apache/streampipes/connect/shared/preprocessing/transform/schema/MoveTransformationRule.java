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

package org.apache.streampipes.connect.shared.preprocessing.transform.schema;

import org.apache.streampipes.connect.shared.preprocessing.transform.TransformationRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveTransformationRule implements TransformationRule {

  private final List<String> oldKey;
  private final List<String> newKey;

  public MoveTransformationRule(List<String> oldKey, List<String> newKey) {
    this.oldKey = oldKey;
    this.newKey = newKey;
  }

  @Override
  public Map<String, Object> apply(Map<String, Object> event) {

    Map<String, Object> objectToMove = (Map<String, Object>) ((HashMap<String, Object>) getItem(event, oldKey)).clone();


    Map<String, Object> resultEvent = addItem(event, newKey, objectToMove);
    resultEvent = deleteItem(resultEvent, oldKey);

    return resultEvent;
  }

  private Map<String, Object> addItem(Map<String, Object> event, List<String> keys, Map<String, Object> movedObject) {
    if (keys.isEmpty() || (keys.size() == 1 && keys.get(0).isEmpty())) {
      String key = (String) movedObject.keySet().toArray()[0];
      event.put(key, movedObject.get(key));
      return event;
    } else if (keys.size() == 1) {
      if (event.get(keys.get(0)) != null && event.get(keys.get(0)) instanceof HashMap) {
        String movedObjectKey = movedObject.keySet().iterator().next();
        ((Map<String, Object>) event.get(keys.get(0))).put(movedObjectKey, movedObject.get(movedObjectKey));
      } else {
        event.put(keys.get(0), movedObject);
      }
      return event;
    } else {
      String key = keys.get(0);
      List<String> newKeysTmpList = keys.subList(1, keys.size());

      Map<String, Object> newSubEvent =
          addItem((Map<String, Object>) event.get(keys.get(0)), newKeysTmpList, movedObject);

      event.remove(key);
      event.put(key, newSubEvent);
      return event;
    }
  }


  private Map<String, Object> getItem(Map<String, Object> event, List<String> keys) {
    if (keys.size() == 1) {
      Map<String, Object> res = new HashMap<>();
      res.put(keys.get(0), event.get(keys.get(0)));
      return res;
    } else {
      List<String> newKeysTmpList = keys.subList(1, keys.size());

      return getItem((Map<String, Object>) event.get(keys.get(0)), newKeysTmpList);
    }
  }

  private Map<String, Object> deleteItem(Map<String, Object> event, List<String> keys) {
    if (keys.size() == 1) {

      event.remove(keys.get(0));
      return event;
    } else {
      String key = keys.get(0);
      List<String> newKeysTmpList = keys.subList(1, keys.size());

      Map<String, Object> newSubEvent =
          deleteItem((Map<String, Object>) event.get(keys.get(0)), newKeysTmpList);

      event.remove(key);
      event.put(key, newSubEvent);
      return event;
    }
  }
}
