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

public abstract class AbstractField<V> {

  protected String fieldNameIn;
  protected String fieldNameOut;
  protected V value;

  public AbstractField(String fieldNameIn, String fieldNameOut, V value) {
    this(fieldNameIn);
    this.value = value;
    this.fieldNameOut = fieldNameOut;
  }

  public AbstractField(String fieldNameIn) {
    this();
    this.fieldNameIn = fieldNameIn;
    this.fieldNameOut = fieldNameIn;
  }

  public AbstractField() {

  }

  protected String asString(Object field) {
    return String.valueOf(field);
  }

  public String getFieldNameIn() {
    return fieldNameIn;
  }

  public String getFieldNameOut() {
    return fieldNameOut;
  }

  public Boolean isComposite() {
    return isInstance(NestedField.class);
  }

  public Boolean isList() {
    return isInstance(ListField.class);
  }

  public Boolean isPrimitive() {
    return isInstance(PrimitiveField.class);
  }

  private Boolean isInstance(Class<? extends AbstractField> clazz) {
    return clazz.isInstance(this);
  }

  public NestedField getAsComposite() {
    return (NestedField) this;
  }

  public ListField getAsList() {
    return (ListField) this;
  }

  public PrimitiveField getAsPrimitive() {
    return (PrimitiveField) this;
  }

  public V getRawValue() {
    return value;
  }

  public <T> T parse(FieldParser<V, T> fieldParser) {
    return fieldParser.parseField(value);
  }

  public void setValue(V value) {
    this.value = value;
  }

  public void rename(String newFieldName) {
    this.fieldNameIn = newFieldName;
    this.fieldNameOut = newFieldName;
  }
}
