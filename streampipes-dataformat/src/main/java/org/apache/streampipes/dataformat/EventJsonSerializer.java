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

package org.apache.streampipes.dataformat;

import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.runtime.field.ListField;
import org.apache.streampipes.model.runtime.field.PrimitiveField;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

final class EventJsonSerializer extends JsonSerializer<EventJsonSerializer.Payload> {

  record Payload(Event event) {
  }

  @Override
  public void serialize(Payload payload, JsonGenerator generator, SerializerProvider provider) throws IOException {
    writeFields(payload.event().getFields(), generator, provider);
  }

  private void writeFields(Map<String, AbstractField> fields,
                           JsonGenerator generator,
                           SerializerProvider provider) throws IOException {
    var outputFields = fields;
    if (!hasUniqueOutputNames(fields)) {
      // Renames and merged streams can produce duplicate output names. Preserve
      // EventConverter's last-value-wins behavior without emitting duplicate keys.
      outputFields = new HashMap<>();
      for (var field : fields.values()) {
        outputFields.put(field.getFieldNameOut(), field);
      }
    }
    generator.writeStartObject();
    for (var field : outputFields.values()) {
      if (field.getFieldNameOut() == null) {
        provider.reportMappingProblem("Null output field name");
      }
      generator.writeFieldName(field.getFieldNameOut());
      writeField(field, generator, provider);
    }
    generator.writeEndObject();
  }

  private boolean hasUniqueOutputNames(Map<String, AbstractField> fields) {
    String firstKey = null;
    int prefixLength = 0;
    for (var entry : fields.entrySet()) {
      String key = entry.getKey();
      String name = entry.getValue().getFieldNameOut();
      if (key == null || name == null) {
        return false;
      }
      if (firstKey == null) {
        firstKey = key;
        prefixLength = key.length() - name.length();
        if (prefixLength < 0) {
          return false;
        }
      }
      // Normal EventFactory keys are a common selector prefix plus the output
      // name. Unique map keys then prove unique names without allocating a set.
      // Arbitrary keys, renames, and merged prefixes use the safe fallback above.
      if (key.length() != prefixLength + name.length()
          || !key.regionMatches(0, firstKey, 0, prefixLength)
          || !key.regionMatches(prefixLength, name, 0, name.length())) {
        return false;
      }
    }
    return true;
  }

  private void writeField(AbstractField field, JsonGenerator generator, SerializerProvider provider)
      throws IOException {
    if (field instanceof PrimitiveField) {
      // Keep Jackson's existing handling of numbers, nulls, binary values, and
      // other values supplied by processors instead of introducing coercions.
      provider.defaultSerializeValue(field.getRawValue(), generator);
    } else if (field instanceof ListField) {
      generator.writeStartArray();
      for (var item : field.getAsList().getRawValue()) {
        writeField(item, generator, provider);
      }
      generator.writeEndArray();
    } else {
      writeFields(field.getAsComposite().getRawValue(), generator, provider);
    }
  }
}
