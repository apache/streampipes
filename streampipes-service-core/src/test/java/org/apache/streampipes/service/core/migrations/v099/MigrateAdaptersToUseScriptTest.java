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

package org.apache.streampipes.service.core.migrations.v099;

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.AdapterDescription;
import org.apache.streampipes.model.connect.rules.TransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.DeleteRuleDescription;
import org.apache.streampipes.model.connect.rules.schema.RenameRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.RegexTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MigrateAdaptersToUseScriptTest {

  private IAdapterStorage mockStorage;
  private MigrateAdaptersToUseScript migration;

  @BeforeEach
  void setUp() {
    mockStorage = mock(IAdapterStorage.class);
    migration = new MigrateAdaptersToUseScript(mockStorage);
  }

  @Test
  void shouldExecute_ReturnsTrue_WhenAdapterHasRules() {
    // Arrange
    AdapterDescription adapter = new AdapterDescription();
    adapter.setTransformationConfig(null);

    when(mockStorage.findAll()).thenReturn(Collections.singletonList(adapter));

    boolean result = migration.shouldExecute();

    assertTrue(result);
  }

  @Test
  void executeMigration_RemoveAdditionalMetadata() throws IOException {
    // Arrange
    var adapter = new AdapterDescription();
    var eventPropertyPrimitive = new EventPropertyPrimitive();
    eventPropertyPrimitive.setAdditionalMetadata(Collections.singletonMap("key", "value"));
    var eventSchema = new EventSchema();
    eventSchema.setEventProperties(Collections.singletonList(eventPropertyPrimitive));
    var dataStream = new SpDataStream();
    dataStream.setEventSchema(eventSchema);
    adapter.setDataStream(dataStream);


    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    assertNotNull(adapter.getTransformationConfig());
    assertEquals(1, adapter.getDataStream().getEventSchema().getEventProperties().size());

    EventPropertyPrimitive updatedProperty = (EventPropertyPrimitive) adapter.getDataStream()
        .getEventSchema()
        .getEventProperties()
        .get(0);
    assertTrue(updatedProperty.getAdditionalMetadata().isEmpty());

    // Verify the storage was actually updated
    verify(mockStorage).updateElement(adapter);
  }

  @Test
  void executeMigration_TransformsRenameRuleToScript() throws IOException {
    // Arrange
    var adapter = createBaseAdapter(new RenameRuleDescription("old", "new"));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    assertNotNull(adapter.getTransformationConfig());
    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['new'] = event['old'];"));
    assertTrue(adapter.getRules().isEmpty()); // Ensure old rules are cleared

    // Verify the storage was actually updated
    verify(mockStorage).updateElement(adapter);
  }

  @Test
  void executeMigration_TransformsDeleteRule() throws IOException {
    var adapter = createBaseAdapter(new DeleteRuleDescription("unwantedField"));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));
    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("delete event['unwantedField'];"));
    verify(mockStorage).updateElement(adapter);
  }

  @Test
  void executeMigration_TransformsAddTimestampRule() throws IOException {
    AdapterDescription adapter = createBaseAdapter(new AddTimestampRuleDescription("timestamp"));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));
    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['timestamp'] = Date.now();"));
  }

  @Test
  void executeMigration_TransformsAddValueRule() throws IOException {
    AdapterDescription adapter = createBaseAdapter(new AddValueTransformationRuleDescription("key", "static-val"));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));
    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['key'] = 'static-val';"));
  }

  @Test
  void executeMigration_TransformsCorrectionValueRule() throws IOException {
    CorrectionValueTransformationRuleDescription rule = new CorrectionValueTransformationRuleDescription();
    rule.setRuntimeKey("temperature");
    rule.setOperator("MULTIPLY");
    rule.setCorrectionValue(1.8);

    AdapterDescription adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['temperature'] = Number(event['temperature']) * 1.8"));
  }

  @Test
  void executeMigration_TransformsRegexRule() throws IOException {
    RegexTransformationRuleDescription rule = new RegexTransformationRuleDescription();
    rule.setRuntimeKey("deviceId");
    rule.setRegex("ID-");
    rule.setReplaceWith("");

    AdapterDescription adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    migration.executeMigration();

    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains(".replace(new RegExp('ID-', 'g'), '')"));
  }

  @Test
  void executeMigration_TransformsTimestampFormatStringRule() throws IOException {
    // Arrange
    TimestampTranfsformationRuleDescription rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey("timestamp_str");
    rule.setMode("formatString");
    rule.setFormatString("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    AdapterDescription adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("new Date(event['timestamp_str']).getTime()"));
    assertTrue(script.contains("// Target format hint: yyyy-MM-dd"));
  }

  @Test
  void executeMigration_TransformsTimestampTimeUnitRule() throws IOException {
    // Arrange
    TimestampTranfsformationRuleDescription rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey("timestamp_sec");
    rule.setMode("timeUnit");
    rule.setMultiplier(1000L);

    AdapterDescription adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['timestamp_sec'] = Number(event['timestamp_sec']) * 1000;"));
  }

  private AdapterDescription createBaseAdapter(TransformationRuleDescription rule) {
    AdapterDescription adapter = new AdapterDescription();
    adapter.setRules(new ArrayList<>(Collections.singletonList(rule)));
    // Initialize other mandatory structures if your migration touches them
    adapter.setDataStream(new SpDataStream());
    adapter.getDataStream().setEventSchema(new EventSchema());
    return adapter;
  }
}