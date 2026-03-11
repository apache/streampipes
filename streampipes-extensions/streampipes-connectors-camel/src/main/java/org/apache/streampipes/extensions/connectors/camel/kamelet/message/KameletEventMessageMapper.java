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

package org.apache.streampipes.extensions.connectors.camel.kamelet.message;

import org.apache.streampipes.dataformat.JsonDataFormatDefinition;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.field.AbstractField;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public class KameletEventMessageMapper {

  public static final String EVENT_JSON_HEADER = "streampipes.event.json";
  public static final String EVENT_BODY_TYPE_HEADER = "streampipes.event.bodyType";
  public static final String EVENT_BODY_FIELD_SELECTOR_HEADER = "streampipes.event.bodyFieldSelector";

  public CamelMessage mapEvent(Event event,
                               KameletMessageMapping messageMapping,
                               JsonDataFormatDefinition jsonDataFormatDefinition) {
    Map<String, Object> rawEvent = event.getRaw();
    String jsonPayload = new String(jsonDataFormatDefinition.fromMap(rawEvent), StandardCharsets.UTF_8);

    Object body = switch (messageMapping.payloadMode()) {
      case EVENT_MAP -> rawEvent;
      case EVENT_JSON -> jsonPayload;
      case MAPPED_FIELD -> extractEventValue(event, messageMapping.bodyFieldSelector());
    };

    Map<String, Object> headers = new LinkedHashMap<>();
    headers.put(EVENT_JSON_HEADER, jsonPayload);
    headers.put(EVENT_BODY_TYPE_HEADER, bodyType(messageMapping.payloadMode()));

    if (messageMapping.payloadMode() == KameletMessageMapping.PayloadMode.MAPPED_FIELD
        && messageMapping.bodyFieldSelector() != null
        && !messageMapping.bodyFieldSelector().isBlank()) {
      headers.put(EVENT_BODY_FIELD_SELECTOR_HEADER, messageMapping.bodyFieldSelector());
    }

    for (KameletHeaderMapping headerMapping : messageMapping.headerMappings()) {
      Object headerValue = extractEventValue(event, headerMapping.eventFieldSelector());
      if (headerValue != null) {
        headers.put(headerMapping.headerName(), headerValue);
      }
    }

    return new CamelMessage(body, headers);
  }

  private Object extractEventValue(Event event,
                                   String selector) {
    if (selector == null || selector.isBlank()) {
      return null;
    }

    return toPlainValue(event.getFieldBySelector(selector));
  }

  private Object toPlainValue(AbstractField<?> field) {
    if (field == null) {
      return null;
    }

    if (field.isPrimitive()) {
      return field.getRawValue();
    }

    if (field.isList()) {
      return field.getAsList()
          .getRawValue()
          .stream()
          .map(this::toPlainValue)
          .toList();
    }

    Map<String, Object> nested = new LinkedHashMap<>();
    field.getAsComposite()
        .getRawValue()
        .values()
        .forEach(child -> nested.put(child.getFieldNameIn(), toPlainValue(child)));
    return nested;
  }

  private String bodyType(KameletMessageMapping.PayloadMode payloadMode) {
    return switch (payloadMode) {
      case EVENT_MAP -> "map";
      case EVENT_JSON -> "json";
      case MAPPED_FIELD -> "mapped-field";
    };
  }
}
