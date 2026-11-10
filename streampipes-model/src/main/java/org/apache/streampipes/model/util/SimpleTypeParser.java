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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * Converts string values (e.g., static property values) into simple Java types.
 *
 * <p>Supported target types are {@link String}, {@link Object}, the primitive types and their
 * wrappers, {@link BigDecimal}, {@link BigInteger} and enums. Behaviour follows the previously used
 * type-parser library: the input is trimmed before conversion (except for {@code String} and
 * {@code Object} targets), and the input {@code "null"} (case-insensitive, surrounding whitespace
 * ignored) is converted to {@code null}.
 */
public final class SimpleTypeParser {

  private static final Map<Class<?>, Function<String, ?>> PARSERS = Map.ofEntries(
      Map.entry(Integer.class, Integer::valueOf),
      Map.entry(Long.class, Long::valueOf),
      Map.entry(Short.class, Short::valueOf),
      Map.entry(Byte.class, Byte::valueOf),
      Map.entry(Float.class, Float::valueOf),
      Map.entry(Double.class, Double::valueOf),
      Map.entry(Boolean.class, SimpleTypeParser::parseBoolean),
      Map.entry(Character.class, SimpleTypeParser::parseCharacter),
      Map.entry(BigDecimal.class, BigDecimal::new),
      Map.entry(BigInteger.class, BigInteger::new)
  );

  private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_WRAPPER = Map.of(
      int.class, Integer.class,
      long.class, Long.class,
      short.class, Short.class,
      byte.class, Byte.class,
      float.class, Float.class,
      double.class, Double.class,
      boolean.class, Boolean.class,
      char.class, Character.class
  );

  private SimpleTypeParser() {
  }

  /**
   * Parses the given input into the target type.
   *
   * @throws IllegalArgumentException if the input cannot be converted or the target type is not supported
   */
  @SuppressWarnings("unchecked")
  public static <T> T parse(String input, Class<T> targetType) {
    Objects.requireNonNull(input, "input must not be null");
    Objects.requireNonNull(targetType, "targetType must not be null");

    if (isNullString(input)) {
      if (targetType.isPrimitive()) {
        throw new IllegalArgumentException(
            "Cannot convert 'null' to primitive type %s".formatted(targetType.getName()));
      }
      return null;
    }

    if (targetType == String.class || targetType == Object.class) {
      return (T) input;
    }

    var trimmed = input.trim();
    var boxedType = targetType.isPrimitive() ? PRIMITIVE_TO_WRAPPER.get(targetType) : targetType;

    var parser = PARSERS.get(boxedType);
    if (parser == null && !boxedType.isEnum()) {
      throw new IllegalArgumentException("Unsupported target type " + targetType.getName());
    }

    try {
      return boxedType.isEnum()
          ? (T) parseEnum(trimmed, boxedType.asSubclass(Enum.class))
          : (T) parser.apply(trimmed);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException(
          "Cannot convert '%s' to type %s".formatted(input, targetType.getName()), e);
    }
  }

  private static boolean isNullString(String input) {
    return "null".equalsIgnoreCase(input.trim());
  }

  private static Boolean parseBoolean(String value) {
    if ("true".equalsIgnoreCase(value)) {
      return Boolean.TRUE;
    } else if ("false".equalsIgnoreCase(value)) {
      return Boolean.FALSE;
    }
    throw new IllegalArgumentException("Not a boolean value: " + value);
  }

  private static Character parseCharacter(String value) {
    if (value.length() != 1) {
      throw new IllegalArgumentException("Not a single character: " + value);
    }
    return value.charAt(0);
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  private static Enum<?> parseEnum(String value, Class<? extends Enum> enumType) {
    return Enum.valueOf(enumType, value);
  }
}
