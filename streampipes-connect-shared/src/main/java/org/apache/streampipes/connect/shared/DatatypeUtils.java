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
   * The decimal separator assumed by {@link Double#parseDouble(String)} and
   * {@link org.apache.commons.lang3.math.NumberUtils#isParsable(String)}.
   */
  public static final char DEFAULT_DECIMAL_SEPARATOR = '.';

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
    return convertValue(adapterName, value, targetDatatypeXsd, DEFAULT_DECIMAL_SEPARATOR, loggedConversionError);
  }

  /**
   * Converts the given value to a specified XSD datatype using the provided decimal separator.
   * When a non-default decimal separator is configured (e.g. the {@code ','} common in many
   * European locales), it is normalized to {@code '.'} before parsing. Values that use a different
   * separator than the configured one (e.g. {@code "100.000"} when {@code ','} is selected) are
   * treated as strings and left unchanged.
   *
   * @param adapterName The adapter whose event value should be converted.
   * @param value The value to be converted.
   * @param targetDatatypeXsd The target XSD datatype as a string.
   * @param decimalSeparator The decimal separator used in string representations of numbers.
   * @param loggedConversionError Guard that ensures a conversion error is logged at warn level only once.
   * @return The converted value, or the original value if conversion fails.
   */
  public static Object convertValue(String adapterName,
                                    Object value,
                                    String targetDatatypeXsd,
                                    char decimalSeparator,
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

    var normalized = normalizeDecimalSeparator(String.valueOf(value), decimalSeparator);
    if (normalized == null) {
      // The value is not a valid number under the configured decimal separator; keep it unchanged.
      return value;
    }

    try {
      return convertString(normalized, targetDatatypeXsd);
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
    return getXsdDatatype(value, preferFloat, DEFAULT_DECIMAL_SEPARATOR);
  }

  public static String getXsdDatatype(String value,
                                      boolean preferFloat,
                                      char decimalSeparator) {
    var clazz = getTypeClass(value, preferFloat, decimalSeparator);
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
    return getTypeClass(value, preferFloatingPointNumber, DEFAULT_DECIMAL_SEPARATOR);
  }

  public static Class<?> getTypeClass(String value,
                                      boolean preferFloatingPointNumber,
                                      char decimalSeparator) {
    var targetClass = String.class;
    if (value == null) {
      return targetClass;
    }

    var originalValue = value;
    // Returns null when the value is not a valid number under the configured decimal separator
    // (e.g. it uses a different separator), in which case it must be treated as a string.
    value = normalizeDecimalSeparator(value, decimalSeparator);

    if (value != null && NumberUtils.isParsable(value)) {
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

    if (originalValue.equalsIgnoreCase("true") || originalValue.equalsIgnoreCase("false")) {
      return Boolean.class;
    }

    return targetClass;
  }

  /**
   * Normalizes the decimal separator of a numeric string representation to {@code '.'} so that it
   * can be parsed by {@link Double#parseDouble(String)} and recognized by
   * {@link org.apache.commons.lang3.math.NumberUtils#isParsable(String)}.
   *
   * <p>Normalization is only applied when the provided separator is not the default {@code '.'}.
   * To avoid misinterpreting the separator when it is also used as a thousands/grouping separator
   * (e.g. {@code "1,000"}), normalization is skipped when the value contains more than one
   * occurrence of the separator.</p>
   *
   * @param value the raw string value
   * @param decimalSeparator the decimal separator configured for the data source
   * @return a string parseable with {@code '.'} as decimal separator, or {@code null} if the value
   *         must not be interpreted as a number under the configured separator
   */
  private static String normalizeDecimalSeparator(String value,
                                                  char decimalSeparator) {
    if (value == null) {
      return null;
    }

    // Default separator: keep the historical behavior (values are parsed as-is).
    if (decimalSeparator == DEFAULT_DECIMAL_SEPARATOR) {
      return value;
    }

    // A non-default separator is configured. A value that uses the default '.' is therefore not a
    // valid number in the configured locale and must stay a string (e.g. "100.000" with ',').
    if (value.indexOf(DEFAULT_DECIMAL_SEPARATOR) >= 0) {
      return null;
    }

    var firstIndex = value.indexOf(decimalSeparator);

    // No configured separator present: it is a plain integer-like token (e.g. "42") and can be
    // parsed as-is.
    if (firstIndex < 0) {
      return value;
    }

    // The configured separator appears more than once, so it is ambiguous (e.g. a grouping
    // separator like "1,000,000"); do not treat it as a single decimal number.
    if (firstIndex != value.lastIndexOf(decimalSeparator)) {
      return null;
    }

    return value.replace(decimalSeparator, DEFAULT_DECIMAL_SEPARATOR);
  }

}
