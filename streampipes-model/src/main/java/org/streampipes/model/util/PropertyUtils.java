/*
 * Copyright 2018 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.model.util;

import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventPropertyList;
import org.streampipes.model.schema.EventPropertyNested;
import org.streampipes.model.schema.EventPropertyPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropertyUtils {

  public static Map<String, Object> getRuntimeFormat(EventProperty eventProperty) {
    return getUntypedRuntimeFormat(eventProperty);
  }

  public static Map<String, Object> getUntypedRuntimeFormat(EventProperty ep) {
    if (ep instanceof EventPropertyPrimitive) {
      Map<String, Object> result = new HashMap<>();
      result.put(ep.getRuntimeName(), ModelUtils.getPrimitiveClass(((EventPropertyPrimitive) ep).getRuntimeType()));
      return result;

    } else if (ep instanceof EventPropertyNested) {
      EventPropertyNested nestedEp = (EventPropertyNested) ep;
      Map<String, Object> propertyMap = new HashMap<String, Object>();
      Map<String, Object> subTypes = new HashMap<String, Object>();
      for(EventProperty p : nestedEp.getEventProperties())
      {
        subTypes.putAll(getUntypedRuntimeFormat(p));
      }
      propertyMap.put(nestedEp.getRuntimeName(), subTypes);
      return propertyMap;
    } else {
      EventPropertyList listEp = (EventPropertyList) ep;
      Map<String, Object> result = new HashMap<>();
      for(EventProperty p : listEp.getEventProperties())
      {
        if (p instanceof EventPropertyPrimitive && listEp.getEventProperties().size() == 1)
        {
          result.put(listEp.getRuntimeName(), ModelUtils.getPrimitiveClassAsArray(((EventPropertyPrimitive) p).getRuntimeType()));
          break;
        }
        else
          result.put(listEp.getRuntimeName(), ModelUtils.asList(PropertyUtils.getUntypedRuntimeFormat(p)));
      }
      return result;
    }

  }

  public static List<String> getFullPropertyName(EventProperty ep, String prefix) {
    if (ep instanceof EventPropertyPrimitive) {
      List<String> result = new ArrayList<String>();
      result.add(prefix + ep.getRuntimeName());
      return result;
    } else if (ep instanceof EventPropertyNested) {
      List<String> result = new ArrayList<String>();
      for(EventProperty p : ((EventPropertyNested) ep).getEventProperties())
      {
        result.addAll(getFullPropertyName(p, ep.getRuntimeName() +"."));
      }
      return result;
    } else {
      List<String> result = new ArrayList<String>();
      result.add(prefix + ep.getRuntimeName());
      return result;
    }
  }
}
