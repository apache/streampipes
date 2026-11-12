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

package org.apache.streampipes.storage.couchdb.compat;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.client.user.Principal;
import org.apache.streampipes.model.shared.api.Storable;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

/**
 * Builds populated instances of stored models by reflection, so the compatibility tests cover every field without
 * hand-written fixtures that go stale when a model changes.
 *
 * <p>Every non-transient field gets a value. Abstract field types get a concrete subtype from the model jars; a
 * list of an abstract type gets one element of every subtype near the root. Subtypes the storage's Gson cannot
 * round-trip on its own (no adapter registered for the declared type) are left out, because no stored document
 * can contain them.</p>
 */
final class ModelFixtures {

  enum Variant {
    /** Ordinary values in every field. */
    RICH,
    /** Large and small numbers, non-ASCII and HTML characters in strings. */
    EDGE,
    /** Only {@code _id}; everything else keeps the constructor defaults. */
    SPARSE
  }

  private static final String MODEL_PACKAGE = "org.apache.streampipes.model";
  private static final int MAX_DEPTH = 6;
  private static final int ALL_SUBTYPES_DEPTH = 3;
  private static final int MAX_OBJECTS = 4000;
  private static final Object NO_LEAF = new Object();
  private static final List<Class<?>> MODEL_CLASSES = scanModelClasses();

  private final Variant variant;
  private final Gson gson;
  private final Map<Class<?>, List<Class<?>>> subtypes = new HashMap<>();
  private final Map<Class<?>, Integer> rotation = new HashMap<>();
  private int counter;
  private int objects;

  private ModelFixtures(Variant variant, Gson gson) {
    this.variant = variant;
    this.gson = gson;
  }

  /**
   * Creates a populated instance.
   *
   * @param type    concrete model class
   * @param variant kind of values
   * @param id      value for the field serialized as {@code _id}
   * @param gson    Gson of the storage, used to leave out subtypes it cannot read back
   * @return the instance
   */
  static Object create(Class<?> type, Variant variant, String id, Gson gson) {
    var fixtures = new ModelFixtures(variant, gson);
    Object root = fixtures.instantiate(type);
    if (root == null) {
      throw new IllegalArgumentException("No no-arg constructor: " + type.getName());
    }
    fixtures.fill(root, type, Map.of(), 0, id);
    return root;
  }

  private void fill(Object target, Class<?> type, Map<TypeVariable<?>, Type> outer, int depth, String rootId) {
    Map<TypeVariable<?>, Type> bindings = bindings(type, outer);
    for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
      for (Field field : c.getDeclaredFields()) {
        int modifiers = field.getModifiers();
        if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || field.isSynthetic()) {
          continue;
        }
        String name = serializedName(field);
        if ("_rev".equals(name)) {
          set(field, target, null);
        } else if ("_id".equals(name) && depth == 0) {
          set(field, target, rootId);
        } else if (Modifier.isFinal(modifiers) || (name.startsWith("$") && get(field, target) != null)) {
          // Constants and type discriminators ($type, appDocType) that views select on keep their value.
          continue;
        } else if (variant != Variant.SPARSE) {
          Object value = value(field.getGenericType(), bindings, field.getName(), depth + 1);
          if (value != null) {
            set(field, target, value);
          }
        }
      }
    }
  }

  private Object value(Type type, Map<TypeVariable<?>, Type> bindings, String name, int depth) {
    if (type instanceof TypeVariable<?> variable) {
      Type bound = bindings.get(variable);
      return value(bound != null && !(bound instanceof TypeVariable<?>) ? bound : variable.getBounds()[0],
          bindings, name, depth);
    }
    if (type instanceof WildcardType wildcard) {
      return value(wildcard.getUpperBounds()[0], bindings, name, depth);
    }
    Class<?> raw = rawType(type);
    if (raw.isArray()) {
      Type component = type instanceof GenericArrayType array ? array.getGenericComponentType()
          : raw.getComponentType();
      Object element = value(component, bindings, name, depth);
      if (element == null) {
        return null;
      }
      Object array = Array.newInstance(rawType(component), 1);
      Array.set(array, 0, element);
      return array;
    }
    if (Collection.class.isAssignableFrom(raw)) {
      return collection(raw, typeArgument(type, 0, bindings), bindings, name, depth);
    }
    if (Map.class.isAssignableFrom(raw)) {
      return map(raw, typeArgument(type, 0, bindings), typeArgument(type, 1, bindings), bindings, name, depth);
    }
    Object leaf = leaf(raw, name);
    if (leaf != NO_LEAF) {
      return leaf;
    }
    if (!isModel(raw) || depth > MAX_DEPTH || objects >= MAX_OBJECTS) {
      return null;
    }
    Class<?> concrete = isAbstract(raw) ? nextSubtype(raw) : raw;
    return concrete == null ? null : model(concrete, type, bindings, depth);
  }

  private Object model(Class<?> concrete, Type declared, Map<TypeVariable<?>, Type> bindings, int depth) {
    if (concrete.isRecord()) {
      return record(concrete, bindings, depth);
    }
    Object instance = instantiate(concrete);
    if (instance == null) {
      return null;
    }
    Map<TypeVariable<?>, Type> own = new HashMap<>();
    if (declared instanceof ParameterizedType parameterized && parameterized.getRawType() == concrete) {
      TypeVariable<?>[] variables = concrete.getTypeParameters();
      for (int i = 0; i < variables.length; i++) {
        own.put(variables[i], resolve(parameterized.getActualTypeArguments()[i], bindings));
      }
    }
    fill(instance, concrete, own, depth, null);
    return instance;
  }

  /**
   * Records have no setters; Gson reads and writes them through their components.
   */
  private Object record(Class<?> type, Map<TypeVariable<?>, Type> bindings, int depth) {
    RecordComponent[] components = type.getRecordComponents();
    Class<?>[] types = new Class<?>[components.length];
    Object[] values = new Object[components.length];
    for (int i = 0; i < components.length; i++) {
      types[i] = components[i].getType();
      values[i] = depth > MAX_DEPTH ? null
          : value(components[i].getGenericType(), bindings, components[i].getName(), depth + 1);
      if (values[i] == null && types[i].isPrimitive()) {
        values[i] = Array.get(Array.newInstance(types[i], 1), 0);
      }
    }
    try {
      Constructor<?> constructor = type.getDeclaredConstructor(types);
      constructor.setAccessible(true);
      objects++;
      return constructor.newInstance(values);
    } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
      return null;
    }
  }

  private Collection<Object> collection(Class<?> raw,
                                        Type elementType,
                                        Map<TypeVariable<?>, Type> bindings,
                                        String name,
                                        int depth) {
    Collection<Object> result = newCollection(raw);
    if (result == null) {
      return null;
    }
    Class<?> elementRaw = rawType(elementType);
    if (isModel(elementRaw) && isAbstract(elementRaw) && depth <= ALL_SUBTYPES_DEPTH) {
      for (Class<?> subtype : subtypes(elementRaw)) {
        if (objects < MAX_OBJECTS) {
          Object element = model(subtype, subtype, bindings, depth);
          if (element != null) {
            result.add(element);
          }
        }
      }
    } else {
      Object element = value(elementType, bindings, name, depth);
      if (element != null) {
        result.add(element);
      }
    }
    return result;
  }

  private Map<Object, Object> map(Class<?> raw,
                                  Type keyType,
                                  Type valueType,
                                  Map<TypeVariable<?>, Type> bindings,
                                  String name,
                                  int depth) {
    Map<Object, Object> result = newMap(raw);
    if (result == null) {
      return null;
    }
    Class<?> keyRaw = rawType(keyType);
    Object key;
    if (keyRaw == String.class || keyRaw == Object.class) {
      key = "key-" + (++counter);
    } else if (keyRaw.isEnum()) {
      key = keyRaw.getEnumConstants()[0];
    } else if (keyRaw == Integer.class || keyRaw == Long.class) {
      key = keyRaw == Integer.class ? (Object) (++counter) : (Object) (long) (++counter);
    } else {
      return result;
    }
    Object value = value(valueType, bindings, name, depth);
    if (value != null) {
      result.put(key, value);
    }
    return result;
  }

  private Object leaf(Class<?> raw, String name) {
    boolean edge = variant == Variant.EDGE;
    if (raw == String.class) {
      int n = ++counter;
      return edge ? "Ünï <b>&amp;</b> \"q\" \\   😀 " + name + " " + n : name + "-" + n;
    }
    if (raw == boolean.class || raw == Boolean.class) {
      return Boolean.TRUE;
    }
    if (raw == int.class || raw == Integer.class) {
      return edge ? Integer.MIN_VALUE : ++counter;
    }
    if (raw == long.class || raw == Long.class) {
      return edge ? Long.MAX_VALUE - (++counter) : 10_000_000_000L + (++counter);
    }
    if (raw == double.class || raw == Double.class) {
      int n = ++counter;
      return edge ? (n % 2 == 0 ? 1.0E-7 : 1.0E21) : n + 0.25;
    }
    if (raw == float.class || raw == Float.class) {
      return edge ? Float.MAX_VALUE : (++counter) + 0.5f;
    }
    if (raw == short.class || raw == Short.class) {
      return (short) (++counter);
    }
    if (raw == byte.class || raw == Byte.class) {
      return (byte) (++counter);
    }
    if (raw == char.class || raw == Character.class) {
      return 'c';
    }
    if (raw == BigDecimal.class || raw == Number.class) {
      return edge ? new BigDecimal("12345678901234567890.123456789") : new BigDecimal((++counter) + ".10");
    }
    if (raw == BigInteger.class) {
      return edge ? new BigInteger("123456789012345678901234567890") : BigInteger.valueOf(++counter);
    }
    if (raw.isEnum()) {
      Object[] constants = raw.getEnumConstants();
      return constants.length == 0 ? null : constants[(++counter) % constants.length];
    }
    if (raw == URI.class) {
      return URI.create("http://example.org/" + name + "/" + (++counter));
    }
    if (raw == UUID.class) {
      int n = ++counter;
      return new UUID(n, n);
    }
    if (raw == Date.class) {
      return new Date(1_700_000_000_000L + (++counter));
    }
    if (raw == Object.class) {
      return mixedObject();
    }
    return NO_LEAF;
  }

  /**
   * Value for an {@code Object} field: Gson writes the runtime type and reads it back as maps, lists and doubles.
   */
  private Map<String, Object> mixedObject() {
    int n = ++counter;
    var result = new LinkedHashMap<String, Object>();
    result.put("text", "value-" + n);
    result.put("count", 2);
    result.put("ratio", 1.5);
    result.put("flag", true);
    result.put("items", List.of("a", 1));
    result.put("nested", Map.of("k", "v"));
    if (variant == Variant.EDGE) {
      result.put("big", Long.MAX_VALUE);
      result.put("tiny", 1.0E-7);
      result.put("unicode", "😀 <&>");
    }
    return result;
  }

  private Class<?> nextSubtype(Class<?> base) {
    List<Class<?>> candidates = subtypes(base);
    if (candidates.isEmpty()) {
      return null;
    }
    int index = rotation.merge(base, 1, Integer::sum) - 1;
    return candidates.get(index % candidates.size());
  }

  private List<Class<?>> subtypes(Class<?> base) {
    return subtypes.computeIfAbsent(base, b -> MODEL_CLASSES.stream()
        .filter(b::isAssignableFrom)
        .filter(ModelFixtures::isInstantiable)
        .filter(c -> roundTrips(b, c))
        .toList());
  }

  /**
   * Whether the storage's Gson writes and reads an instance of {@code subtype} declared as {@code base}.
   */
  private boolean roundTrips(Class<?> base, Class<?> subtype) {
    try {
      Object instance = subtype.isRecord() ? record(subtype, Map.of(), MAX_DEPTH + 1) : instantiate(subtype);
      objects--;
      return instance != null && gson.fromJson(gson.toJson(instance, base), base) != null;
    } catch (RuntimeException | StackOverflowError e) {
      return false;
    }
  }

  private Object instantiate(Class<?> type) {
    try {
      Constructor<?> constructor = type.getDeclaredConstructor();
      constructor.setAccessible(true);
      objects++;
      return constructor.newInstance();
    } catch (ReflectiveOperationException | RuntimeException | LinkageError e) {
      return null;
    }
  }

  /**
   * Names under which Gson writes the fields of a class and its superclasses.
   */
  static List<String> serializedFieldNames(Class<?> type) {
    var result = new ArrayList<String>();
    for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
      for (Field field : c.getDeclaredFields()) {
        int modifiers = field.getModifiers();
        if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers) && !field.isSynthetic()) {
          result.add(serializedName(field));
        }
      }
    }
    return result;
  }

  private static Object get(Field field, Object target) {
    try {
      field.setAccessible(true);
      return field.get(target);
    } catch (ReflectiveOperationException | RuntimeException e) {
      return null;
    }
  }

  private static void set(Field field, Object target, Object value) {
    try {
      field.setAccessible(true);
      field.set(target, value);
    } catch (ReflectiveOperationException | RuntimeException e) {
      // Value does not fit the field (unresolved type variable); keep the default.
    }
  }

  private static Collection<Object> newCollection(Class<?> raw) {
    if (raw.isInterface() || Modifier.isAbstract(raw.getModifiers())) {
      if (SortedSet.class.isAssignableFrom(raw)) {
        return new TreeSet<>();
      }
      if (Set.class.isAssignableFrom(raw)) {
        return new LinkedHashSet<>();
      }
      if (Queue.class.isAssignableFrom(raw)) {
        return new ArrayDeque<>();
      }
      return new ArrayList<>();
    }
    return instantiateJdk(raw);
  }

  private static Map<Object, Object> newMap(Class<?> raw) {
    if (raw.isInterface() || Modifier.isAbstract(raw.getModifiers())) {
      return SortedMap.class.isAssignableFrom(raw) ? new TreeMap<>() : new LinkedHashMap<>();
    }
    return instantiateJdk(raw);
  }

  @SuppressWarnings("unchecked")
  private static <T> T instantiateJdk(Class<?> raw) {
    try {
      return (T) raw.getDeclaredConstructor().newInstance();
    } catch (ReflectiveOperationException | RuntimeException e) {
      return null;
    }
  }

  private static String serializedName(Field field) {
    SerializedName annotation = field.getAnnotation(SerializedName.class);
    return annotation != null ? annotation.value() : field.getName();
  }

  private static Map<TypeVariable<?>, Type> bindings(Class<?> type, Map<TypeVariable<?>, Type> outer) {
    var result = new HashMap<>(outer);
    for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
      if (c.getGenericSuperclass() instanceof ParameterizedType parameterized
          && parameterized.getRawType() instanceof Class<?> superclass) {
        TypeVariable<?>[] variables = superclass.getTypeParameters();
        Type[] arguments = parameterized.getActualTypeArguments();
        for (int i = 0; i < variables.length; i++) {
          result.put(variables[i], resolve(arguments[i], result));
        }
      }
    }
    return result;
  }

  private static Type resolve(Type type, Map<TypeVariable<?>, Type> bindings) {
    if (type instanceof TypeVariable<?> variable && bindings.containsKey(variable)) {
      return bindings.get(variable);
    }
    return type;
  }

  private static Type typeArgument(Type type, int index, Map<TypeVariable<?>, Type> bindings) {
    if (type instanceof ParameterizedType parameterized && parameterized.getActualTypeArguments().length > index) {
      return resolve(parameterized.getActualTypeArguments()[index], bindings);
    }
    return Object.class;
  }

  private static Class<?> rawType(Type type) {
    if (type instanceof Class<?> c) {
      return c;
    }
    if (type instanceof ParameterizedType parameterized) {
      return (Class<?>) parameterized.getRawType();
    }
    if (type instanceof GenericArrayType array) {
      return Array.newInstance(rawType(array.getGenericComponentType()), 0).getClass();
    }
    if (type instanceof TypeVariable<?> variable) {
      return rawType(variable.getBounds()[0]);
    }
    if (type instanceof WildcardType wildcard) {
      return rawType(wildcard.getUpperBounds()[0]);
    }
    return Object.class;
  }

  private static boolean isModel(Class<?> type) {
    return type.getName().startsWith(MODEL_PACKAGE);
  }

  private static boolean isAbstract(Class<?> type) {
    return type.isInterface() || Modifier.isAbstract(type.getModifiers());
  }

  private static boolean isInstantiable(Class<?> type) {
    if (isAbstract(type) || type.isEnum() || type.isAnonymousClass() || type.isLocalClass()
        || (type.isMemberClass() && !Modifier.isStatic(type.getModifiers()))) {
      return false;
    }
    if (type.isRecord()) {
      return true;
    }
    try {
      type.getDeclaredConstructor();
      return true;
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  /**
   * Model classes from the jars (or class directories) of streampipes-model, -model-client and -model-shared.
   */
  private static List<Class<?>> scanModelClasses() {
    var names = new TreeSet<String>();
    for (Class<?> anchor : List.of(SpDataStream.class, Principal.class, Storable.class)) {
      try {
        Path location = Path.of(anchor.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (Files.isDirectory(location)) {
          try (Stream<Path> files = Files.walk(location)) {
            files.map(file -> location.relativize(file).toString().replace('\\', '/')).forEach(names::add);
          }
        } else {
          try (var jar = new JarFile(location.toFile())) {
            jar.stream().map(JarEntry::getName).forEach(names::add);
          }
        }
      } catch (IOException | URISyntaxException e) {
        throw new IllegalStateException("Cannot scan model classes next to " + anchor.getName(), e);
      }
    }
    var result = new ArrayList<Class<?>>();
    for (String name : names) {
      if (!name.endsWith(".class") || name.endsWith("package-info.class") || name.endsWith("module-info.class")) {
        continue;
      }
      String className = name.substring(0, name.length() - ".class".length()).replace('/', '.');
      if (!className.startsWith(MODEL_PACKAGE) || className.matches(".*\\$\\d.*")) {
        continue;
      }
      try {
        result.add(Class.forName(className, false, ModelFixtures.class.getClassLoader()));
      } catch (ClassNotFoundException | LinkageError e) {
        // Optional dependency of a model class is missing; it cannot be stored either.
      }
    }
    return List.copyOf(result);
  }
}
