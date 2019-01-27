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

import java.util.List;
import java.util.stream.Collectors;

public class ListField extends AbstractField<List<Object>> {

  public ListField(String fieldNameIn, String fieldNameOut, List<Object> value) {
    super(fieldNameIn, fieldNameOut, value);
  }

  public <T> List<T> parseAsCustomType(FieldParser<Object, T> parser) {
    return value.stream()
            .map(f -> parser.parseField(value))
            .collect(Collectors.toList());
  }

  public <T> List<T> parseAsSimpleType(Class<T> type) {
    return value.stream()
            .map(f -> typeParser.parse(asString(f), type))
            .collect(Collectors.toList());
  }
}
