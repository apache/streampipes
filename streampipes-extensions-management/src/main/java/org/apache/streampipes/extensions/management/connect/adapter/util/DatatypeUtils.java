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

package org.apache.streampipes.extensions.management.connect.adapter.util;

import org.apache.streampipes.vocabulary.XSD;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatatypeUtils {

  private static final Logger LOG = LoggerFactory.getLogger(DatatypeUtils.class);

  public static Object convertValue(Object value,
                                    String targetDatatypeXsd) {
    var stringValue = String.valueOf(value);
    if (XSD.STRING.toString().equals(targetDatatypeXsd)) {
      return stringValue;
    } else {
      try {
        if (XSD.DOUBLE.toString().equals(targetDatatypeXsd)) {
          return Double.parseDouble(stringValue);
        } else if (XSD.FLOAT.toString().equals(targetDatatypeXsd)) {
          return Float.parseFloat(stringValue);
        } else if (XSD.BOOLEAN.toString().equals(targetDatatypeXsd)) {
          return Boolean.parseBoolean(stringValue);
        } else if (XSD.INTEGER.toString().equals(targetDatatypeXsd)) {
          var floatingNumber = Float.parseFloat(stringValue);
          return Integer.parseInt(String.valueOf(Math.round(floatingNumber)));
        } else if (XSD.LONG.toString().equals(targetDatatypeXsd)) {
          var floatingNumber = Double.parseDouble(stringValue);
          return Long.parseLong(String.valueOf(Math.round(floatingNumber)));
        }
      } catch (NumberFormatException e) {
        LOG.error("Number format exception {}", value);
        return value;
      }
    }

    return value;
  }

  public static String getCanonicalTypeClassName(String value,
                                                 boolean preferFloat) {
    return getTypeClass(value, preferFloat).getCanonicalName();
  }

  public static String getXsdDatatype(String value,
                                      boolean preferFloat) {
    var clazz = getTypeClass(value, preferFloat);
    if (clazz.equals(Integer.class)) {
      return XSD.INTEGER.toString();
    } else if (clazz.equals(Long.class)) {
      return XSD.LONG.toString();
    } else if (clazz.equals(Float.class)) {
      return XSD.FLOAT.toString();
    } else if (clazz.equals(Double.class)) {
      return XSD.DOUBLE.toString();
    } else if (clazz.equals(Boolean.class)) {
      return XSD.BOOLEAN.toString();
    } else {
      return XSD.STRING.toString();
    }
  }

  public static Class<?> getTypeClass(String value,
                                      boolean preferFloat) {
    if (NumberUtils.isParsable(value)) {
      try {
        Integer.parseInt(value);
        return preferFloat ? Float.class : Integer.class;
      } catch (NumberFormatException ignored) {
      }

      try {
        Long.parseLong(value);
        return preferFloat ? Float.class : Long.class;
      } catch (NumberFormatException ignored) {
      }

      try {
        Double.parseDouble(value);
        return Float.class;
      } catch (NumberFormatException ignored) {
      }

    }

    if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
      return Boolean.class;
    }

    return String.class;
  }

  public static void main(String[] args) {
    long max = Long.MAX_VALUE;
    String className = getCanonicalTypeClassName(String.valueOf(max), true);
    System.out.println(className);
    System.out.println(convertValue(max, className));
  }
}
