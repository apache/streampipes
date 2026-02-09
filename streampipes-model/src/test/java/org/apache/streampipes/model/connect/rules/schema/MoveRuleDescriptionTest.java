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
package org.apache.streampipes.model.connect.rules.schema;

import org.apache.streampipes.model.connect.rules.ITransformationRuleVisitor;
import org.apache.streampipes.model.connect.rules.TransformationRulePriority;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@DisplayName("MoveRuleDescription Tests")
class MoveRuleDescriptionTest {

  @Mock
  private ITransformationRuleVisitor visitor;

  private MoveRuleDescription rule;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    rule = new MoveRuleDescription();
  }

  @Test
  @DisplayName("Create move rule with constructor")
  void testCreateMoveRule() {
    MoveRuleDescription rule = new MoveRuleDescription("temperature", "sensor.temperature");
    
    assertEquals("temperature", rule.getOldRuntimeKey());
    assertEquals("sensor.temperature", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Copy constructor creates independent copy")
  void testCopyConstructor() {
    MoveRuleDescription original = new MoveRuleDescription("temp", "sensor.temp");
    MoveRuleDescription copy = new MoveRuleDescription(original);

    assertEquals(original.getOldRuntimeKey(), copy.getOldRuntimeKey());
    assertEquals(original.getNewRuntimeKey(), copy.getNewRuntimeKey());

    // Modify original and verify copy is independent
    original.setOldRuntimeKey("humidity");
    assertEquals("temp", copy.getOldRuntimeKey());
  }

  @Test
  @DisplayName("Get and set old runtime key")
  void testSetAndGetOldRuntimeKey() {
    rule.setOldRuntimeKey("temperature");
    assertEquals("temperature", rule.getOldRuntimeKey());
  }

  @Test
  @DisplayName("Get and set new runtime key")
  void testSetAndGetNewRuntimeKey() {
    rule.setNewRuntimeKey("sensor.temperature");
    assertEquals("sensor.temperature", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Accept visitor with valid rule")
  void testAcceptVisitorWithValidRule() {
    rule.setOldRuntimeKey("temperature");
    rule.setNewRuntimeKey("sensor.temperature");

    rule.accept(visitor);

    verify(visitor).visit(rule);
  }

  @Test
  @DisplayName("Accept visitor throws exception when oldRuntimeKey is null")
  void testAcceptVisitorWithNullOldKey() {
    rule.setOldRuntimeKey(null);
    rule.setNewRuntimeKey("sensor.temperature");

    assertThrows(IllegalArgumentException.class, () -> rule.accept(visitor));
  }

  @Test
  @DisplayName("Accept visitor throws exception when oldRuntimeKey is empty")
  void testAcceptVisitorWithEmptyOldKey() {
    rule.setOldRuntimeKey("");
    rule.setNewRuntimeKey("sensor.temperature");

    assertThrows(IllegalArgumentException.class, () -> rule.accept(visitor));
  }

  @Test
  @DisplayName("Accept visitor throws exception when newRuntimeKey is null")
  void testAcceptVisitorWithNullNewKey() {
    rule.setOldRuntimeKey("temperature");
    rule.setNewRuntimeKey(null);

    assertThrows(IllegalArgumentException.class, () -> rule.accept(visitor));
  }

  @Test
  @DisplayName("Accept visitor throws exception when newRuntimeKey is empty")
  void testAcceptVisitorWithEmptyNewKey() {
    rule.setOldRuntimeKey("temperature");
    rule.setNewRuntimeKey("");

    assertThrows(IllegalArgumentException.class, () -> rule.accept(visitor));
  }

  @Test
  @DisplayName("Get rule priority")
  void testGetRulePriority() {
    int priority = rule.getRulePriority();
    assertEquals(TransformationRulePriority.MOVE.getCode(), priority);
  }

  @Test
  @DisplayName("Update nested paths - old prefix to new prefix")
  void testUpdatePathsAfterMoveBasic() {
    rule.setOldRuntimeKey("sensor.data.temperature");
    rule.setNewRuntimeKey("sensor.data.value");

    rule.updatePathsAfterMove("sensor", "device");

    // Old key should be updated
    assertEquals("device.data.temperature", rule.getOldRuntimeKey());
    assertEquals("device.data.value", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Update nested paths - only affects matching prefixes")
  void testUpdatePathsAfterMoveOnlyMatchingPrefixes() {
    rule.setOldRuntimeKey("sensor.temperature");
    rule.setNewRuntimeKey("other.temperature");

    rule.updatePathsAfterMove("sensor", "device");

    // Only the matching path should be updated
    assertEquals("device.temperature", rule.getOldRuntimeKey());
    assertEquals("other.temperature", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Update nested paths - no changes when prefix doesn't match")
  void testUpdatePathsAfterMoveNoMatchingPrefix() {
    rule.setOldRuntimeKey("sensor.temperature");
    rule.setNewRuntimeKey("sensor.newTemp");

    rule.updatePathsAfterMove("other", "device");

    // No changes should occur
    assertEquals("sensor.temperature", rule.getOldRuntimeKey());
    assertEquals("sensor.newTemp", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Update nested paths with null old prefix")
  void testUpdatePathsAfterMoveNullOldPrefix() {
    rule.setOldRuntimeKey("sensor.temperature");
    rule.setNewRuntimeKey("sensor.newTemp");

    rule.updatePathsAfterMove(null, "device");

    // No changes should occur
    assertEquals("sensor.temperature", rule.getOldRuntimeKey());
    assertEquals("sensor.newTemp", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Update nested paths with null new prefix")
  void testUpdatePathsAfterMoveNullNewPrefix() {
    rule.setOldRuntimeKey("sensor.temperature");
    rule.setNewRuntimeKey("sensor.newTemp");

    rule.updatePathsAfterMove("sensor", null);

    // No changes should occur
    assertEquals("sensor.temperature", rule.getOldRuntimeKey());
    assertEquals("sensor.newTemp", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Update nested paths - complex nested structure")
  void testUpdatePathsAfterMoveComplexNesting() {
    rule.setOldRuntimeKey("building.floor.room.temperature");
    rule.setNewRuntimeKey("building.floor.room.readings.temperature");

    rule.updatePathsAfterMove("building.floor", "facility.section");

    assertEquals("facility.section.room.temperature", rule.getOldRuntimeKey());
    assertEquals("facility.section.room.readings.temperature", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Update nested paths with null rule keys")
  void testUpdatePathsAfterMoveWithNullKeys() {
    rule.setOldRuntimeKey(null);
    rule.setNewRuntimeKey(null);

    // Should not throw exception
    rule.updatePathsAfterMove("sensor", "device");

    assertNull(rule.getOldRuntimeKey());
    assertNull(rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Handle paths with special characters")
  void testUpdatePathsWithSpecialCharacters() {
    rule.setOldRuntimeKey("sensor-data.temperature");
    rule.setNewRuntimeKey("sensor-data.temp_value");

    rule.updatePathsAfterMove("sensor-data", "device-info");

    assertEquals("device-info.temperature", rule.getOldRuntimeKey());
    assertEquals("device-info.temp_value", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Get default values of new instance")
  void testDefaultValues() {
    MoveRuleDescription newRule = new MoveRuleDescription();
    
    assertNull(newRule.getOldRuntimeKey());
    assertNull(newRule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Multiple sequential path updates")
  void testMultipleSequentialPathUpdates() {
    rule.setOldRuntimeKey("a.b.c");
    rule.setNewRuntimeKey("a.b.c.value");

    rule.updatePathsAfterMove("a", "x");
    assertEquals("x.b.c", rule.getOldRuntimeKey());
    assertEquals("x.b.c.value", rule.getNewRuntimeKey());

    rule.updatePathsAfterMove("x.b", "y.z");
    assertEquals("y.z.c", rule.getOldRuntimeKey());
    assertEquals("y.z.c.value", rule.getNewRuntimeKey());
  }

  @Test
  @DisplayName("Verify error message for missing oldRuntimeKey")
  void testErrorMessageMissingOldKey() {
    rule.setOldRuntimeKey(null);
    rule.setNewRuntimeKey("sensor.temperature");

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rule.accept(visitor));
    assertTrue(exception.getMessage().contains("oldRuntimeKey"));
  }

  @Test
  @DisplayName("Verify error message for missing newRuntimeKey")
  void testErrorMessageMissingNewKey() {
    rule.setOldRuntimeKey("temperature");
    rule.setNewRuntimeKey(null);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> rule.accept(visitor));
    assertTrue(exception.getMessage().contains("newRuntimeKey"));
  }
}
