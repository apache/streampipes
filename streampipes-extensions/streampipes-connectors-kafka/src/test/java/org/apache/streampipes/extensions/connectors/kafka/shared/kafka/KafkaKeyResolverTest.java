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

package org.apache.streampipes.extensions.connectors.kafka.shared.kafka;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KafkaKeyResolverTest {

  private static final String SENSOR_ID_SELECTOR = "o::sensorId";

  @Test
  void testResolveKey_modeNone_returnsEmptyKey() {
    var resolver = new KafkaKeyResolver();

    assertTrue(resolver.resolveKey(makeEvent()).isEmpty());
  }

  @Test
  void testResolveKey_modeStatic_returnsConfiguredText() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.STATIC, "line-4");

    assertEquals("line-4", resolver.resolveKey(makeEvent()).orElseThrow());
  }

  @Test
  void testResolveKey_modeStaticWithBlankText_returnsEmptyKey() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.STATIC, "   ");

    assertTrue(resolver.resolveKey(makeEvent()).isEmpty());
  }

  @Test
  void testResolveKey_modeField_returnsFieldValue() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, SENSOR_ID_SELECTOR);

    assertEquals("flowrate01", resolver.resolveKey(makeEvent()).orElseThrow());
  }

  @Test
  void testResolveKey_modeFieldWithNumericField_returnsTextValue() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, "o::temperature");

    assertEquals("46.3", resolver.resolveKey(makeEvent()).orElseThrow());
  }

  @Test
  void testResolveKey_modeFieldWithNullValue_returnsEmptyKey() {
    var event = EventFactory.fromMap(makeEventMapWithNullSensorId());
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, SENSOR_ID_SELECTOR);

    assertTrue(resolver.resolveKey(event).isEmpty());
  }

  @Test
  void testResolveKey_modeFieldWithEmptyValue_returnsEmptyKey() {
    var event = EventFactory.fromMap(Map.of("sensorId", ""));
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, SENSOR_ID_SELECTOR);

    assertTrue(resolver.resolveKey(event).isEmpty());
  }

  @Test
  void testResolveKey_modeFieldWithUnknownField_throwsSpRuntimeException() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, "o::unknown");

    assertThrows(SpRuntimeException.class, () -> resolver.resolveKey(makeEvent()));
  }

  @Test
  void testResolveKey_modeFieldWithListField_throwsSpRuntimeException() {
    var event = EventFactory.fromMap(Map.of("readings", List.of(1, 2, 3)));
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, "o::readings");

    assertThrows(SpRuntimeException.class, () -> resolver.resolveKey(event));
  }

  @Test
  void testResolveKey_modeFieldWithNestedField_throwsSpRuntimeException() {
    var event = EventFactory.fromMap(Map.of("machine", Map.of("id", "m-1")));
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, "o::machine");

    assertThrows(SpRuntimeException.class, () -> resolver.resolveKey(event));
  }

  @Test
  void testResolveKey_modeExpression_returnsFilledExpression() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.EXPRESSION, "plant-1-#sensorId#");

    assertEquals("plant-1-flowrate01", resolver.resolveKey(makeEvent()).orElseThrow());
  }

  @Test
  void testResolveKey_modeExpressionWithUnknownPlaceholder_returnsUnchangedPlaceholder() {
    var resolver = new KafkaKeyResolver(KafkaMessageKeyMode.EXPRESSION, "plant-1-#unknown#");

    assertEquals("plant-1-#unknown#", resolver.resolveKey(makeEvent()).orElseThrow());
  }

  @Test
  void testConstructor_modeFieldWithoutDefinition_throwsSpRuntimeException() {
    assertThrows(SpRuntimeException.class,
        () -> new KafkaKeyResolver(KafkaMessageKeyMode.FIELD, ""));
  }

  @Test
  void testConstructor_modeExpressionWithoutDefinition_throwsSpRuntimeException() {
    assertThrows(SpRuntimeException.class,
        () -> new KafkaKeyResolver(KafkaMessageKeyMode.EXPRESSION, null));
  }

  @Test
  void testConstructor_modeStaticWithoutDefinition_doesNotThrow() {
    assertDoesNotThrow(() -> new KafkaKeyResolver(KafkaMessageKeyMode.STATIC, null));
  }

  private static Event makeEvent() {
    return EventFactory.fromMap(Map.of(
        "timestamp", 1756219200000L,
        "sensorId", "flowrate01",
        "temperature", 46.3,
        "sensor_fault_flags", false));
  }

  private static Map<String, Object> makeEventMapWithNullSensorId() {
    var event = new HashMap<String, Object>();
    event.put("sensorId", null);
    event.put("temperature", 46.3);
    return event;
  }
}
