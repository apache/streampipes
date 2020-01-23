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
package org.apache.streampipes.model.runtime.field;

import java.util.HashMap;
import java.util.Map;

public class NestedField extends AbstractField<Map<String, AbstractField>> {

  private Boolean hasKey;

  public NestedField(String fieldNameIn, String fieldNameOut, Map<String, AbstractField> value) {
    super(fieldNameIn, fieldNameOut, value);
    this.hasKey = true;
  }

  public NestedField(String fieldNameIn, Boolean hasKey) {
    super(fieldNameIn);
    this.hasKey = hasKey;
    this.value = new HashMap<>();
  }

  public NestedField() {
    super();
    this.value = new HashMap<>();
  }

  public AbstractField getFieldByRuntimeName(String runtimeName) {
    return value.get(runtimeName);
  }

  public void addField(String key, AbstractField field) {
    value.put(key, field);
  }

  public void addField(String runtimeName, Integer value) {
    addPrimitive(runtimeName, value);
  }

  public void addField(String runtimeName, Long value) {
    addPrimitive(runtimeName, value);
  }

  public void addField(String runtimeName, Object value) {
    addPrimitive(runtimeName, value);
  }

  public void addField(String runtimeName, Float value) {
    addPrimitive(runtimeName, value);
  }

  public void addField(String runtimeName, Double value) {
    addPrimitive(runtimeName, value);
  }

  public void addField(String runtimeName, Boolean value) {
    addPrimitive(runtimeName, value);
  }

  public void addField(String runtimeName, String value) {
    addPrimitive(runtimeName, value);
  }

  private void addPrimitive(String runtimeName, Object value) {
    this.value.put(runtimeName, new PrimitiveField(runtimeName, runtimeName, value));
  }

  public Boolean getHasKey() {
    return hasKey;
  }
}
