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

package org.apache.streampipes.connect.transformer.js;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.graalvm.polyglot.proxy.ProxyArray;
import org.graalvm.polyglot.proxy.ProxyExecutable;
import org.graalvm.polyglot.proxy.ProxyObject;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Adapts Java-side objects so they can be consumed safely and predictably from GraalJS transformation scripts.
 *
 * <p>The bridge is used when StreamPipes wants to expose helper objects or context objects to a script without
 * giving unrestricted host access. Primitive values are passed through unchanged, collections and arrays are mapped
 * to Graal proxy arrays/objects, and regular POJOs are converted into map-like structures. For richer helper types,
 * only methods annotated with {@code ExposedToScripts} are exported, which creates a narrow script-facing API instead
 * of exposing the full Java object.
 *
 * <p>When a script invokes one of those exported methods, the bridge also converts polyglot arguments back into Java
 * types, supports overloaded methods by matching on arity and successful conversion, and wraps the return value back
 * into a script-friendly representation. This makes it possible to call selected Java helpers from JavaScript while
 * keeping the interop surface explicit and constrained.
 */
public final class ScriptHostObjectAdapter {

  private static final String EXPOSED_TO_SCRIPTS_ANNOTATION =
      "org.apache.streampipes.model.shared.annotation.ExposedToScripts";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final Map<Class<?>, Map<String, List<Method>>> EXPOSED_METHOD_CACHE = new ConcurrentHashMap<>();

  private ScriptHostObjectAdapter() {
  }

  /**
   * Converts a Java value into a GraalJS-compatible representation.
   *
   * <p>This is the main entry point for exposing Java objects to scripts.
   */
  public static Object wrap(Object value) {
    return toScriptValue(value);
  }

  private static Object toScriptValue(Object value) {
    if (value == null) {
      return null;
    }
    if (value instanceof Optional<?> optional) {
      return toScriptValue(optional.orElse(null));
    }
    if (value instanceof String || value instanceof Number || value instanceof Boolean) {
      return value;
    }
    if (value instanceof Map<?, ?> map) {
      Map<String, Object> converted = new LinkedHashMap<>();
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        converted.put(String.valueOf(entry.getKey()), toScriptValue(entry.getValue()));
      }
      return ProxyObject.fromMap(converted);
    }
    if (value instanceof Iterable<?> iterable) {
      List<Object> converted = new ArrayList<>();
      for (Object item : iterable) {
        converted.add(toScriptValue(item));
      }
      return ProxyArray.fromList(converted);
    }
    if (value.getClass().isArray()) {
      int length = Array.getLength(value);
      List<Object> converted = new ArrayList<>(length);
      for (int i = 0; i < length; i++) {
        converted.add(toScriptValue(Array.get(value, i)));
      }
      return ProxyArray.fromList(converted);
    }
    if (hasExposedMethods(value.getClass())) {
      return proxyFor(value);
    }

    Object converted = OBJECT_MAPPER.convertValue(value, Object.class);
    if (Objects.equals(converted, value)) {
      return value;
    }
    return toScriptValue(converted);
  }

  private static ProxyObject proxyFor(Object target) {
    Map<String, Object> members = new LinkedHashMap<>();
    exposedMethods(target.getClass()).forEach((name, methods) ->
        members.put(name, (ProxyExecutable) args -> invoke(target, methods, args)));
    return ProxyObject.fromMap(members);
  }

  private static Object invoke(Object target, List<Method> methods, Object[] args) {
    List<Method> matchingByArity = methods.stream()
        .filter(method -> method.getParameterCount() == args.length)
        .toList();
    IllegalArgumentException lastConversionError = null;

    for (Method method : matchingByArity) {
      try {
        Object[] convertedArgs = convertArguments(method, args);
        Object result = method.invoke(target, convertedArgs);
        return toScriptValue(result);
      } catch (IllegalArgumentException e) {
        // Try the next overload if argument conversion fails.
        lastConversionError = e;
      } catch (IllegalAccessException e) {
        throw new IllegalStateException("Unable to access method " + method.getName(), e);
      } catch (InvocationTargetException e) {
        throw rethrowTargetException(method, e);
      }
    }

    String message = "No overload of '%s' matched %d argument(s)."
        .formatted(methods.get(0).getName(), args.length);
    if (lastConversionError != null) {
      message += " Last error: " + lastConversionError.getMessage();
    }
    throw new IllegalArgumentException(message, lastConversionError);
  }

  private static RuntimeException rethrowTargetException(Method method, InvocationTargetException e) {
    Throwable targetException = e.getTargetException();
    return new IllegalStateException(
        "Invocation of method '%s' failed: %s: %s".formatted(
            method.getName(),
            targetException.getClass().getName(),
            targetException.getMessage()),
        targetException);
  }

  private static Object[] convertArguments(Method method, Object[] args) {
    Class<?>[] parameterTypes = method.getParameterTypes();
    Object[] convertedArgs = new Object[args.length];
    for (int i = 0; i < args.length; i++) {
      convertedArgs[i] = convertArgument(args[i], parameterTypes[i]);
    }
    return convertedArgs;
  }

  private static Object convertArgument(Object arg, Class<?> parameterType) {
    Object javaValue = PolyglotTypeConverter.toJavaValue(arg);
    if (javaValue == null) {
      return null;
    }
    Class<?> wrappedType = wrapPrimitive(parameterType);
    if (wrappedType.isInstance(javaValue)) {
      return javaValue;
    }
    if (parameterType == Object.class) {
      return javaValue;
    }
    try {
      return OBJECT_MAPPER.convertValue(javaValue, parameterType);
    } catch (IllegalArgumentException e) {
      if (javaValue instanceof Map<?, ?> map) {
        return convertMapToBean(map, parameterType, e);
      }
      throw new IllegalArgumentException(
          "Could not convert argument of type %s to %s".formatted(
              javaValue.getClass().getName(), parameterType.getName()), e);
    }
  }

  private static Object convertMapToBean(Map<?, ?> map, Class<?> parameterType, IllegalArgumentException original) {
    try {
      Object instance = parameterType.getDeclaredConstructor().newInstance();
      Map<String, PropertyDescriptor> properties = new LinkedHashMap<>();
      for (PropertyDescriptor descriptor : Introspector.getBeanInfo(parameterType).getPropertyDescriptors()) {
        properties.put(descriptor.getName(), descriptor);
      }
      for (Map.Entry<?, ?> entry : map.entrySet()) {
        PropertyDescriptor descriptor = properties.get(String.valueOf(entry.getKey()));
        if (descriptor == null || descriptor.getWriteMethod() == null) {
          continue;
        }
        Object propertyValue = adaptPropertyValue(PolyglotTypeConverter.toJavaValue(entry.getValue()), descriptor.getPropertyType());
        descriptor.getWriteMethod().invoke(instance, propertyValue);
      }
      return instance;
    } catch (ReflectiveOperationException | IntrospectionException ex) {
      throw new IllegalArgumentException(
          "Could not convert argument of type %s to %s".formatted(
              map.getClass().getName(), parameterType.getName()), original);
    }
  }

  private static Object adaptPropertyValue(Object value, Class<?> targetType) {
    if (value == null) {
      return null;
    }
    Class<?> wrappedType = wrapPrimitive(targetType);
    if (wrappedType.isInstance(value)) {
      return value;
    }
    if (value instanceof Map<?, ?> nestedMap) {
      return convertMapToBean(nestedMap, targetType, new IllegalArgumentException("Nested bean conversion"));
    }
    if (targetType.isEnum() && value instanceof String stringValue) {
      @SuppressWarnings({"unchecked", "rawtypes"})
      Object enumValue = Enum.valueOf((Class<? extends Enum>) targetType.asSubclass(Enum.class), stringValue);
      return enumValue;
    }
    return OBJECT_MAPPER.convertValue(value, targetType);
  }

  private static Class<?> wrapPrimitive(Class<?> type) {
    if (!type.isPrimitive()) {
      return type;
    }
    if (type == boolean.class) {
      return Boolean.class;
    }
    if (type == int.class) {
      return Integer.class;
    }
    if (type == long.class) {
      return Long.class;
    }
    if (type == double.class) {
      return Double.class;
    }
    if (type == float.class) {
      return Float.class;
    }
    if (type == short.class) {
      return Short.class;
    }
    if (type == byte.class) {
      return Byte.class;
    }
    if (type == char.class) {
      return Character.class;
    }
    return type;
  }

  private static boolean hasExposedMethods(Class<?> type) {
    return !exposedMethods(type).isEmpty();
  }

  private static Map<String, List<Method>> exposedMethods(Class<?> type) {
    return EXPOSED_METHOD_CACHE.computeIfAbsent(type, ignored -> {
      Map<String, List<Method>> methods = new LinkedHashMap<>();
      for (Method method : type.getDeclaredMethods()) {
        if (isScriptExposed(method)) {
          methods.computeIfAbsent(method.getName(), key -> new ArrayList<>()).add(method);
        }
      }
      return methods;
    });
  }

  private static boolean isScriptExposed(Method method) {
    if (!Modifier.isPublic(method.getModifiers())
        || Modifier.isStatic(method.getModifiers())
        || method.isBridge()
        || method.isSynthetic()) {
      return false;
    }

    return hasExposedAnnotation(method);
  }
  private static boolean hasExposedAnnotation(Method method) {
    return java.util.Arrays.stream(method.getAnnotations())
        .anyMatch(annotation -> annotation.annotationType().getName().equals(EXPOSED_TO_SCRIPTS_ANNOTATION));
  }
}
