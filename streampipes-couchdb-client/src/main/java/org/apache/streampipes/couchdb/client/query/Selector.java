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

package org.apache.streampipes.couchdb.client.query;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Mango selector. Conditions on the same field are merged into one condition object, so
 * {@code Selector.of("ts", "$gte", 1).and("ts", "$lt", 2)} becomes {@code {"ts": {"$gte": 1, "$lt": 2}}}.
 *
 * <p>Field names may use dot notation for nested fields. Not thread-safe.</p>
 */
public final class Selector {

  private final Map<String, Object> conditions = new LinkedHashMap<>();

  private Selector() {
  }

  public static Selector empty() {
    return new Selector();
  }

  public static Selector eq(String field, Object value) {
    return of(field, "$eq", value);
  }

  public static Selector of(String field, String operator, Object value) {
    return new Selector().and(field, operator, value);
  }

  /**
   * Selector from a map in Mango syntax, as built by existing code.
   *
   * @param selector selector map; copied
   * @return the selector
   */
  public static Selector of(Map<String, Object> selector) {
    var result = new Selector();
    result.conditions.putAll(selector);
    return result;
  }

  public Selector andEq(String field, Object value) {
    return and(field, "$eq", value);
  }

  @SuppressWarnings("unchecked")
  public Selector and(String field, String operator, Object value) {
    Object existing = conditions.get(field);
    Map<String, Object> condition;
    if (existing instanceof Map<?, ?> map) {
      condition = new LinkedHashMap<>((Map<String, Object>) map);
    } else {
      condition = new LinkedHashMap<>();
    }
    condition.put(operator, value);
    conditions.put(field, condition);
    return this;
  }

  /**
   * Adds a raw condition for a field, replacing an existing one.
   *
   * @param field     field name
   * @param condition condition in Mango syntax, for example {@code {"$elemMatch": {...}}}
   * @return this selector
   */
  public Selector and(String field, Map<String, Object> condition) {
    conditions.put(field, condition);
    return this;
  }

  public Map<String, Object> toMap() {
    return Collections.unmodifiableMap(conditions);
  }
}
