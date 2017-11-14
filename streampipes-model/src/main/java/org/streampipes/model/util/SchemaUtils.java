package org.streampipes.model.util;

import org.streampipes.model.schema.EventProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SchemaUtils {

  public static Map<String, Object> toRuntimeMap(List<EventProperty> eps)
  {
    return toUntypedRuntimeMap(eps);
  }

  public static Map<String, Object> toUntypedRuntimeMap(List<EventProperty> eps)
  {
    Map<String, Object> propertyMap = new HashMap<String, Object>();

    for(EventProperty p : eps)
    {
      propertyMap.putAll(PropertyUtils.getUntypedRuntimeFormat(p));
    }
    return propertyMap;
  }

  public static List<String> toPropertyList(List<EventProperty> eps)
  {
    List<String> properties = new ArrayList<String>();

    for(EventProperty p : eps)
    {
      properties.addAll(PropertyUtils.getFullPropertyName(p, ""));
    }
    return properties;
  }
}
