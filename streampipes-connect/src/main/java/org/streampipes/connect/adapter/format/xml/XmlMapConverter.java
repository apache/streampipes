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
package org.streampipes.connect.adapter.format.xml;

import java.util.List;
import java.util.Map;

public class XmlMapConverter {

  private Map<String, Object> inMap;

  public XmlMapConverter(Map<String, Object> inMap) {
    this.inMap = inMap;
  }

  public Map<String, Object> convert() {
    convert(inMap);
    return this.inMap;
  }

  @SuppressWarnings({"unchecked"})
  private void convert(Map<String, Object> map) {
    for (String key : map.keySet()) {
      Object value = map.get(key);
      if (value instanceof Map) {
        convert((Map<String, Object>) map.get(key));
      } else if (value instanceof String) {
        String stringValue = String.valueOf(value);
        if (isInteger(stringValue)) {
          map.put(key, Integer.parseInt(stringValue));
        }
      } else if (value instanceof List) {
        ((List) value).forEach(item -> convert((Map<String, Object>) item));
      }
    }
  }

  private Boolean isInteger(String value) {
    try {
      Integer integer = Integer.parseInt(value);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
}
