package org.streampipes.model.util;

import org.streampipes.model.impl.eventproperty.EventProperty;
import org.streampipes.model.impl.eventproperty.EventPropertyList;
import org.streampipes.model.impl.eventproperty.EventPropertyNested;
import org.streampipes.model.impl.eventproperty.EventPropertyPrimitive;

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
