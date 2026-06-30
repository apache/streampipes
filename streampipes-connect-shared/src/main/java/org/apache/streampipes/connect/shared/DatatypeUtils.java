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

package org.apache.streampipes.connect.shared;

import org.apache.streampipes.vocabulary.XSD;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

public class DatatypeUtils {

  private static final Logger LOG = LoggerFactory.getLogger(DatatypeUtils.class);

  /**
   * Converts the given value to a specified XSD datatype.
   * This method attempts to convert the input value to the target datatype specified by the XSD string.
   * It supports conversion to string, double, float, boolean, integer, and long types.
   * If the conversion is not possible due to a format mismatch, the original value is returned.
   * A number format exception during conversion is logged as an error.
   *
   * @param adapterName The adapter whose event value should be converted.
   * @param value The value to be converted. It can be of any type.
   * @param targetDatatypeXsd The target XSD datatype as a string. Supported types are XSD.STRING,
   *                          XSD.DOUBLE, XSD.FLOAT, XSD.BOOLEAN, XSD.INTEGER, and XSD.LONG.
   * @return The converted value as an Object. If conversion fails, the original value is returned.
   */
  public static Object convertValue(String adapterName,
                                    Object value,
                                    String targetDatatypeXsd) {
    return convertValue(adapterName, value, targetDatatypeXsd, new AtomicBoolean(false));
  }

  public static Object convertValue(String adapterName,
                                    Object value,
                                    String targetDatatypeXsd,
                                    AtomicBoolean loggedConversionError) {
    if (value == null) {
      return null;
    }

    if (XSD.STRING.toString().equals(targetDatatypeXsd)) {
      return String.valueOf(value);
    }

    if (value instanceof Number number && isNumericDatatype(targetDatatypeXsd)) {
      return convertNumber(number, targetDatatypeXsd);
    }

    if (value instanceof Boolean booleanValue && XSD.BOOLEAN.toString().equals(targetDatatypeXsd)) {
      return booleanValue;
    }

    if (!isSupportedDatatype(targetDatatypeXsd)) {
      return value;
    }

    try {
      return convertString(String.valueOf(value), targetDatatypeXsd);
    } catch (NumberFormatException e) {
      logConversionError(adapterName, value, targetDatatypeXsd, loggedConversionError);
      return value;
    }
  }

  private static void logConversionError(String adapterName,
                                         Object value,
                                         String targetDatatypeXsd,
                                         AtomicBoolean loggedConversionError) {
    if (loggedConversionError.compareAndSet(false, true)) {
      LOG.warn(
          "Could not convert value '{}' to datatype '{}' for adapter '{}'. Further occurrences are logged at debug "
              + "level.",
          value,
          targetDatatypeXsd,
          adapterName
      );
    } else {
      LOG.debug(
          "Could not convert value '{}' to datatype '{}' for adapter '{}'",
          value,
          targetDatatypeXsd,
          adapterName
      );
    }
  }

  private static Object convertNumber(Number value,
                                      String targetDatatypeXsd) {
    if (XSD.DOUBLE.toString().equals(targetDatatypeXsd)) {
      return value.doubleValue();
    } else if (XSD.FLOAT.toString().equals(targetDatatypeXsd)) {
      return value.floatValue();
    } else if (XSD.INTEGER.toString().equals(targetDatatypeXsd)) {
      return value.intValue();
    } else if (XSD.LONG.toString().equals(targetDatatypeXsd)) {
      return Math.round(value.doubleValue());
    }

    return value;
  }

  private static Object convertString(String value,
                                      String targetDatatypeXsd) {
    if (XSD.DOUBLE.toString().equals(targetDatatypeXsd)) {
      return Double.parseDouble(value);
    } else if (XSD.FLOAT.toString().equals(targetDatatypeXsd)) {
      return Float.parseFloat(value);
    } else if (XSD.BOOLEAN.toString().equals(targetDatatypeXsd)) {
      return Boolean.parseBoolean(value);
    } else if (XSD.INTEGER.toString().equals(targetDatatypeXsd)) {
      return ((Double) Double.parseDouble(value)).intValue();
    } else if (XSD.LONG.toString().equals(targetDatatypeXsd)) {
      var floatingNumber = Double.parseDouble(value);
      return Long.parseLong(String.valueOf(Math.round(floatingNumber)));
    }

    return value;
  }

  private static boolean isSupportedDatatype(String targetDatatypeXsd) {
    return isNumericDatatype(targetDatatypeXsd) || XSD.BOOLEAN.toString().equals(targetDatatypeXsd);
  }

  private static boolean isNumericDatatype(String targetDatatypeXsd) {
    return XSD.DOUBLE.toString().equals(targetDatatypeXsd)
        || XSD.FLOAT.toString().equals(targetDatatypeXsd)
        || XSD.INTEGER.toString().equals(targetDatatypeXsd)
        || XSD.LONG.toString().equals(targetDatatypeXsd);
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
                                      boolean preferFloatingPointNumber) {
    var targetClass = String.class;
    if (value == null) {
      return targetClass;
    }

    if (NumberUtils.isParsable(value)) {
      Class<?> numberClass;
      try {
        long longValue = Long.parseLong(value);
        numberClass = longValue > Integer.MAX_VALUE ? Long.class : Integer.class;
        if (preferFloatingPointNumber) {
          return numberClass == Long.class ? Double.class : Float.class;
        } else {
          return numberClass;
        }
      } catch (NumberFormatException ignored) {
      }

      try {
        double doubleValue = Double.parseDouble(value);
        numberClass = doubleValue > Float.MAX_VALUE ? Double.class : Float.class;
        if (preferFloatingPointNumber) {
          return numberClass == Double.class ? Double.class : Float.class;
        } else {
          return numberClass;
        }
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

    return targetClass;
  }

}
