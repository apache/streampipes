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

import org.apache.streampipes.model.util.EventUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListField extends AbstractField<List<AbstractField>> {

  public ListField(String fieldNameIn, String fieldNameOut, List<AbstractField> value) {
    super(fieldNameIn, fieldNameOut, value);
  }

  public ListField(String fieldNameIn) {
    super(fieldNameIn);
    this.value = new ArrayList<>();
  }

  public void add(AbstractField field) {
    this.value.add(field);
  }


  public <T> List<T> parseAsCustomType(FieldParser<AbstractField, T> parser) {
    return value.stream()
        .map(parser::parseField)
        .collect(Collectors.toList());
  }

  public <T> List<T> castItems(Class<T> clazz) {
    return value.stream()
        .map(v -> v.getAsPrimitive().getRawValue())
        .map(clazz::cast)
        .collect(Collectors.toList());
  }

  public <T> List<T> parseAsSimpleType(Class<T> type) {
    return value.stream()
        .map(v -> v.getAsPrimitive().getRawValue())
        .map(f -> EventUtils.TYPE_PARSER.parse(asString(f), type))
        .collect(Collectors.toList());
  }
}
