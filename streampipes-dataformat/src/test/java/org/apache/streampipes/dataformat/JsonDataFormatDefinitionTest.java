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

import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonDataFormatDefinitionTest {

  @Test
  void writesCompactTransportJsonWithoutChangingSharedMapperDefaults() {
    var codec = new JsonDataFormatDefinition();
    var payload = Map.<String, Object>of("nested", Map.of("samples", List.of(1, 2, 3)), "text", "a b\nc");
    var encoded = codec.fromMap(payload);
    var json = new String(encoded, StandardCharsets.UTF_8);

    assertFalse(json.contains("\n"));
    assertFalse(json.contains(": "));
    assertEquals(payload, codec.toMap(encoded));
    assertTrue(JacksonSerializer.getObjectMapper(Map.of()).isEnabled(SerializationFeature.INDENT_OUTPUT));
  }

  @Test
  void stillReadsExistingPrettyPrintedMessages() {
    var codec = new JsonDataFormatDefinition();
    var input = "{\n  \"value\" : 42\n}";
    assertEquals(Map.of("value", 42), codec.toMap(input.getBytes(StandardCharsets.UTF_8)));
  }
}
