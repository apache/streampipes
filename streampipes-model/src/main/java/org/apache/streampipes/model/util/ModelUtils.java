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

package org.apache.streampipes.model.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelUtils {

  public static Class<?> getPrimitiveClass(String propertyType) {
    String xmlBaseURI = "http://www.w3.org/2001/XMLSchema#";
    if (propertyType.equals(xmlBaseURI + "string")) {
      return String.class;
    } else if (propertyType.equals(xmlBaseURI + "double")) {
      return Double.class;
    } else if (propertyType.equals(xmlBaseURI + "long")) {
      return Long.class;
    } else if (propertyType.equals(xmlBaseURI + "integer")) {
      return Integer.class;
    } else if (propertyType.equals(xmlBaseURI + "boolean")) {
      return Boolean.class;
    } else if (propertyType.equals(xmlBaseURI + "float")) {
      return Float.class;
    } else {
      return null;
    }
  }

  public static Class<?> getPrimitiveClassAsArray(String propertyType) {
    String xmlBaseURI = "http://www.w3.org/2001/XMLSchema#";
    if (propertyType.equals(xmlBaseURI + "string")) {
      return String[].class;
    } else if (propertyType.equals(xmlBaseURI + "double")) {
      return Double[].class;
    } else if (propertyType.equals(xmlBaseURI + "long")) {
      return Long[].class;
    } else if (propertyType.equals(xmlBaseURI + "integer")) {
      return Integer[].class;
    } else if (propertyType.equals(xmlBaseURI + "boolean")) {
      return Boolean[].class;
    } else if (propertyType.equals(xmlBaseURI + "float")) {
      return Float[].class;
    } else {
      return null;
    }
  }

  public static List<Map<String, Object>> asList(Map<String, Object> map) {
    List<Map<String, Object>> result = new ArrayList<>();
    result.add(map);
    return result;
  }

}
