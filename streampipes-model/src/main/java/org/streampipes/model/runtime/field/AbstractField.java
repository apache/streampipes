/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.model.runtime.field;

import com.github.drapostolos.typeparser.TypeParser;

public abstract class AbstractField<FV> {

  protected String fieldNameIn;
  protected String fieldNameOut;
  protected FV value;

  protected TypeParser typeParser;

  public AbstractField(String fieldNameIn, String fieldNameOut, FV value) {
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
    this.typeParser = TypeParser.newBuilder().build();
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

  public FV getRawValue() {
    return value;
  }

  public <T> T parse(FieldParser<FV, T> fieldParser) {
    return fieldParser.parseField(value);
  }

  public void setValue(FV value) {
    this.value = value;
  }
}
