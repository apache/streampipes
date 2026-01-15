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
import org.apache.streampipes.model.connect.rules.stream.EventRateTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.stream.RemoveDuplicatesTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddTimestampRuleDescription;
import org.apache.streampipes.model.connect.rules.value.AddValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.ChangeDatatypeTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.CorrectionValueTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.RegexTransformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.TimestampTranfsformationRuleDescription;
import org.apache.streampipes.model.connect.rules.value.UnitTransformRuleDescription;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.service.core.migrations.v099.connect.MigrateAdaptersToUseScript;
import org.apache.streampipes.storage.api.IAdapterStorage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    assertFalse(adapter.getTransformationConfig().isScriptActive());
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
    assertTrue(adapter.getTransformationConfig().isScriptActive());
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

    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_TransformsAddTimestampRule() throws IOException {
    var adapter = createBaseAdapter(new AddTimestampRuleDescription("timestamp"));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));
    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['timestamp'] = Date.now();"));
    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_TransformsAddValueRule() throws IOException {
    var adapter = createBaseAdapter(new AddValueTransformationRuleDescription("key", "static-val"));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));
    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['key'] = 'static-val';"));
    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_TransformsCorrectionValueRule() throws IOException {
    CorrectionValueTransformationRuleDescription rule = new CorrectionValueTransformationRuleDescription();
    rule.setRuntimeKey("temperature");
    rule.setOperator("MULTIPLY");
    rule.setCorrectionValue(1.8);

    var adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    migration.executeMigration();

    var script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['temperature'] = Number(event['temperature']) * 1.8"));
    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_TransformsRegexRule() throws IOException {
    RegexTransformationRuleDescription rule = new RegexTransformationRuleDescription();
    rule.setRuntimeKey("deviceId");
    rule.setRegex("ID-");
    rule.setReplaceWith("");

    var adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    migration.executeMigration();

    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains(".replace(new RegExp('ID-', 'g'), '')"));
    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_TransformsTimestampFormatStringRule() throws IOException {
    // Arrange
    TimestampTranfsformationRuleDescription rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey("timestamp_str");
    rule.setMode("formatString");
    rule.setFormatString("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    var adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("new Date(event['timestamp_str']).getTime()"));
    assertTrue(script.contains("// Target format hint: yyyy-MM-dd"));
    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_TransformsTimestampTimeUnitRule() throws IOException {
    // Arrange
    TimestampTranfsformationRuleDescription rule = new TimestampTranfsformationRuleDescription();
    rule.setRuntimeKey("timestamp_sec");
    rule.setMode("timeUnit");
    rule.setMultiplier(1000L);

    var adapter = createBaseAdapter(rule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    String script = adapter.getTransformationConfig().getScript();
    assertTrue(script.contains("event['timestamp_sec'] = Number(event['timestamp_sec']) * 1000;"));
    assertTrue(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_ShouldCorrectyMapEventRateRule() throws IOException {
    // Arrange
    var aggregationType = "mean";
    var timeWindow = 10;

    EventRateTransformationRuleDescription legacyRule = new EventRateTransformationRuleDescription();
    legacyRule.setAggregationType(aggregationType);
    legacyRule.setAggregationTimeWindow(timeWindow);

    var adapter = createBaseAdapter(legacyRule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    var resultConfig = adapter.getTransformationConfig();

    assertNotNull(resultConfig, "TransformationConfig should be initialized");
    assertNotNull(resultConfig.getReduceEventRateRule(), "ReduceEventRateRule should be mapped");

    // Verify specific fields are preserved
    assertEquals(aggregationType, resultConfig.getReduceEventRateRule().aggregationType(),
                 "Aggregation type should match legacy config");
    assertEquals(timeWindow, resultConfig.getReduceEventRateRule().aggregationTimeWindow(),
                 "Time window should match legacy config");

    // Ensure the script still contains the standard boilerplate even if this rule is stateful
    assertTrue(resultConfig.getScript().contains("function transform(event, out, ctx)"),
               "Script should still be generated as a container");
    assertFalse(adapter.getTransformationConfig().isScriptActive());
  }

  @Test
  void executeMigration_ShouldCorrectlyMapRemoveDuplicatesRule() throws IOException {
    // Arrange
    String filterTimeWindow = "500";

    RemoveDuplicatesTransformationRuleDescription legacyRule =
        new RemoveDuplicatesTransformationRuleDescription();
    legacyRule.setFilterTimeWindow(filterTimeWindow);

    var adapter = createBaseAdapter(legacyRule);
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    var resultConfig = adapter.getTransformationConfig();

    assertNotNull(resultConfig, "TransformationConfig should be created");
    assertNotNull(resultConfig.getRemoveDuplicateRule(), "RemoveDuplicateRule should be mapped");

    // Verify the specific configuration value
    assertEquals(filterTimeWindow, resultConfig.getRemoveDuplicateRule().filterTimeWindow(),
                 "The filter time window should be correctly migrated from the legacy rule");

    // Verify that the legacy rules list is cleared
    assertTrue(adapter.getRules().isEmpty(), "The legacy rules list must be cleared after migration");
    assertFalse(adapter.getTransformationConfig().isScriptActive());

    // Verify storage interaction
    verify(mockStorage).updateElement(adapter);
  }

  @Test
  void executeMigration_ShouldCorrectlyMapDataTypeRule() throws IOException {
    String eventPropertyKey = "sampleKey";
    String originDataType = "DOUBLE";
    String targetDataType = "INTEGER";

    var property = new EventPropertyPrimitive();
    property.setRuntimeName(eventPropertyKey);
    property.setRuntimeType(originDataType);

    var legacyRule =
        new ChangeDatatypeTransformationRuleDescription(eventPropertyKey, targetDataType);

    var adapter = createBaseAdapter(legacyRule);
    adapter.getDataStream().getEventSchema().setEventProperties(List.of(property));
    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    assertEquals(1, adapter.getDataStream().getEventSchema().getEventProperties().size());

    var propertyAfterMigration =
        (EventPropertyPrimitive) adapter.getDataStream().getEventSchema().getEventProperties().get(0);
    assertEquals(targetDataType, propertyAfterMigration.getRuntimeType(),
                 "The event property runtime type should be updated to the target data type");
    assertFalse(adapter.getTransformationConfig().isScriptActive());

    // Verify storage interaction
    verify(mockStorage).updateElement(adapter);
  }

  @Test
  void executeMigration_ShouldCorrectlyMapUnitTransformationRule() throws IOException, URISyntaxException {
    // Arrange
    String eventPropertyKey = "temperature";
    String oldUnit = "http://qudt.org/vocab/unit/DEG_F";
    String newUnit = "http://qudt.org/vocab/unit/DEG_C";

    var property = new EventPropertyPrimitive();
    property.setRuntimeName(eventPropertyKey);

    // Legacy rule to change unit
    var legacyRule = new UnitTransformRuleDescription(eventPropertyKey, oldUnit, newUnit);

    var adapter = createBaseAdapter(legacyRule);
    adapter.getDataStream().getEventSchema().setEventProperties(List.of(property));

    when(mockStorage.findAll()).thenReturn(List.of(adapter));

    // Act
    migration.executeMigration();

    // Assert
    // 1. Verify Schema Metadata Update
    var propertyAfterMigration = (EventPropertyPrimitive) adapter.getDataStream()
                                                                 .getEventSchema()
                                                                 .getEventProperties()
                                                                 .get(0);

    assertTrue(propertyAfterMigration.getAdditionalMetadata().containsKey("fromMeasurementUnit"));
    assertTrue(propertyAfterMigration.getAdditionalMetadata().containsKey("toMeasurementUnit"));

    assertEquals(oldUnit, propertyAfterMigration.getAdditionalMetadata().get("fromMeasurementUnit").toString());
    assertEquals(newUnit, propertyAfterMigration.getAdditionalMetadata().get("toMeasurementUnit").toString());

    assertFalse(adapter.getTransformationConfig().isScriptActive());

    // 3. Verify Persistence
    verify(mockStorage).updateElement(adapter);
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
