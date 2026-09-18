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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.output.PropertyRenameRule;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventConverter;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.runtime.field.AbstractField;
import org.apache.streampipes.model.runtime.field.PrimitiveField;
import org.apache.streampipes.serializers.json.JacksonSerializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

  @Test
  void directSerializationPreservesNestedValuesAndScalarTypes() throws Exception {
    Map<String, Object> values = new LinkedHashMap<>();
    values.put("null", null);
    values.put("text\"\\", "ä 😀 \n\t\"\\");
    values.put("boolean", true);
    values.put("long", Long.MAX_VALUE);
    values.put("integer", Integer.MIN_VALUE);
    values.put("bigInteger", new BigInteger("123456789012345678901234567890"));
    values.put("decimal", new BigDecimal("1234567890.12345678901234567890"));
    values.put("float", 1.25f);
    values.put("negativeZero", -0.0d);
    values.put("nonFinite", Double.NaN);
    values.put("binary", new byte[]{0, 1, -1});
    values.put("array", new int[]{1, 2, 3});
    values.put("nested", Map.of("list", Arrays.asList(null, Map.of("x", 42), List.of(1, 2))));
    values.put("emptyObject", Map.of());
    values.put("emptyArray", List.of());
    assertMatchesMapSerialization(EventFactory.fromMap(values));
    assertMatchesMapSerialization(EventFactory.fromMap(Map.of()));
  }

  @Test
  void directSerializationPreservesRenamesAndLastValueWinsAtEveryObjectLevel() throws Exception {
    var values = Map.<String, Object>of("a", 1, "b", 2,
        "nested", Map.of("a", 3, "b", 4), "items", List.of(Map.of("a", 5, "b", 6)));
    var rules = List.of(
        new PropertyRenameRule("s0::a", "same"),
        new PropertyRenameRule("s0::b", "same"),
        new PropertyRenameRule("s0::nested", "renamed"),
        new PropertyRenameRule("s0::nested::a", "same"),
        new PropertyRenameRule("s0::nested::b", "same"),
        new PropertyRenameRule("s0::items::0::a", "same"),
        new PropertyRenameRule("s0::items::0::b", "same"));
    assertMatchesMapSerialization(EventFactory.fromMap(values,
        new SourceInfo("test", "s0"), new SchemaInfo(null, rules)));
  }

  @Test
  void directSerializationHandlesMergedStreamsAndArbitraryFieldKeys() throws Exception {
    Map<String, AbstractField> fields = new LinkedHashMap<>();
    fields.put("s0::value", new PrimitiveField("value", "value", 1));
    fields.put("s1::value", new PrimitiveField("value", "value", 2));
    fields.put("arbitrary", new PrimitiveField("other", "", "empty name"));
    assertMatchesMapSerialization(new Event(fields, null, null));
  }

  @Test
  void directSerializationRejectsNullFieldNamesLikeMapSerialization() {
    var event = new Event();
    event.getFields().put("key", new PrimitiveField("key", null, 1));
    var codec = new JsonDataFormatDefinition();
    assertThrows(SpRuntimeException.class, () -> codec.fromMap(new EventConverter(event).toMap()));
    assertThrows(SpRuntimeException.class, () -> codec.fromEvent(event));
  }

  @Test
  void otherFormatsKeepTheMapConversionFallback() {
    var json = new JsonDataFormatDefinition();
    SpDataFormatDefinition format = new SpDataFormatDefinition() {
      @Override
      public Map<String, Object> toMap(byte[] bytes) {
        return json.toMap(bytes);
      }

      @Override
      public byte[] fromMap(Map<String, Object> values) {
        return json.fromMap(values);
      }
    };
    var event = EventFactory.fromMap(Map.of("value", 42));
    assertEquals(Map.of("value", 42), format.toMap(format.fromEvent(event)));
  }

  @Test
  void directWriterDoesNotChangeFromMapSerializationOfEventValuedProperties() throws Exception {
    var values = Map.<String, Object>of("event", new Event());
    var mapper = JacksonSerializer.getObjectMapper();
    var codec = new JsonDataFormatDefinition();
    assertEquals(mapper.readTree(mapper.writeValueAsBytes(values)), mapper.readTree(codec.fromMap(values)));
    assertMatchesMapSerialization(EventFactory.fromMap(values));
  }

  @Test
  void fastDoubleParsingMatchesExistingNumericTypesAndBitPatterns() throws Exception {
    var codec = new JsonDataFormatDefinition();
    var reference = JacksonSerializer.getObjectMapper();
    reference.getFactory().disable(StreamReadFeature.USE_FAST_DOUBLE_PARSER.mappedFeature());
    var numbers = new ArrayList<>(List.of(
        "0", "-0", "0.0", "-0.0", "0.1", "1e0", "1E+10", "1e-400", "-1e-400", "1e400",
        "2147483647", "2147483648", "9223372036854775807", "9223372036854775808",
        "123456789012345678901234567890", "1.7976931348623157e308", "1.7976931348623159e308",
        "2.2250738585072014e-308", "2.2250738585072012e-308", "4.9e-324", "2.4703282292062327e-324",
        "1.00000000000000011102230246251565404236316680908203125"));
    var random = new Random(42);
    for (int i = 0; i < 2000; i++) {
      double value = Double.longBitsToDouble(random.nextLong());
      if (Double.isFinite(value)) {
        numbers.add(Double.toString(value));
      }
    }
    for (String number : numbers) {
      byte[] json = ("{\"value\":" + number + ",\"nested\":[" + number + "]}").getBytes(StandardCharsets.UTF_8);
      var expected = reference.readValue(json, Map.class);
      var actual = codec.toMap(json);
      assertEquals(expected, actual, number);
      assertEquals(expected.get("value").getClass(), actual.get("value").getClass(), number);
      if (expected.get("value") instanceof Double value) {
        assertEquals(Double.doubleToRawLongBits(value),
            Double.doubleToRawLongBits((Double) actual.get("value")), number);
      }
    }
    assertFalse(JacksonSerializer.getObjectMapper().getFactory()
        .isEnabled(StreamReadFeature.USE_FAST_DOUBLE_PARSER.mappedFeature()));
  }

  @Test
  void fastDoubleParsingStillRejectsMalformedAndNonJsonNumbers() {
    var codec = new JsonDataFormatDefinition();
    var reference = JacksonSerializer.getObjectMapper();
    reference.getFactory().disable(StreamReadFeature.USE_FAST_DOUBLE_PARSER.mappedFeature());
    for (String number : List.of("NaN", "Infinity", "-Infinity", "+1", "01", "1e", "1e+",
        "1.", ".1", "0x1.0p0")) {
      byte[] json = ("{\"value\":" + number + "}").getBytes(StandardCharsets.UTF_8);
      assertThrows(IOException.class, () -> reference.readValue(json, Map.class), number);
      assertThrows(SpRuntimeException.class, () -> codec.toMap(json), number);
    }
  }

  private void assertMatchesMapSerialization(Event event) throws Exception {
    var codec = new JsonDataFormatDefinition();
    var reader = JacksonSerializer.getObjectMapper();
    reader.enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    var expected = reader.readTree(codec.fromMap(new EventConverter(event).toMap()));
    assertEquals(expected, reader.readTree(codec.fromEvent(event)));
    // Writing twice also checks that serialization does not mutate the event.
    assertEquals(expected, reader.readTree(codec.fromEvent(event)));
  }

}
