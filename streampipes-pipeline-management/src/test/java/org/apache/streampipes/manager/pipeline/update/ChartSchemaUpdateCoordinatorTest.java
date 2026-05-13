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

package org.apache.streampipes.manager.pipeline.update;

import org.apache.streampipes.model.datalake.DataExplorerWidgetHealthStatus;
import org.apache.streampipes.model.datalake.DataExplorerWidgetModel;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.serializers.json.JacksonSerializer;
import org.apache.streampipes.storage.api.explorer.IDataExplorerWidgetStorage;
import org.apache.streampipes.vocabulary.XSD;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ChartSchemaUpdateCoordinatorTest {

  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String DATA_LAKE_MEASUREMENT_FIELD = "db_measurement";
  private static final String MEASURE_NAME = "measure";
  private static final ObjectMapper OBJECT_MAPPER = JacksonSerializer.getObjectMapper();
  private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
  };

  @Test
  void makeUpdateInfo_ShouldSkipChartWhenAddedFieldsDoNotRemoveReferencedFields() {
    var widget = makeWidget(selectedField("temperature"));
    var updatedSchema = makeSchema(
        property("temperature", XSD.INTEGER),
        property("humidity", XSD.FLOAT)
    );

    var updateInfo = makeCoordinator().makeUpdateInfo(widget, Set.of(MEASURE_NAME), updatedSchema);

    assertTrue(updateInfo.isEmpty());
  }

  @Test
  void makeUpdateInfo_ShouldSkipChartWhenRemovedFieldIsNotReferenced() {
    var widget = makeWidget(selectedField("temperature"), unselectedField("humidity"));
    var updatedSchema = makeSchema(property("temperature", XSD.INTEGER));

    var updateInfo = makeCoordinator().makeUpdateInfo(widget, Set.of(MEASURE_NAME), updatedSchema);

    assertTrue(updateInfo.isEmpty());
  }

  @Test
  void makeUpdateInfo_ShouldRequireAttentionWhenSelectedQueryFieldWasRemoved() {
    var widget = makeWidget(selectedField("temperature"));
    var updatedSchema = makeSchema();

    var updateInfo = makeCoordinator().makeUpdateInfo(widget, Set.of(MEASURE_NAME), updatedSchema).orElseThrow();

    assertFalse(updateInfo.isCanAutoMigrate());
    assertTrue(updateInfo.getAffectedFields().contains("temperature"));
  }

  @Test
  void makeUpdateInfo_ShouldSkipChartWhenVisualizationFieldWasRemoved() {
    var widget = makeWidget();
    widget.setVisualizationConfig(Map.of(
        "firstField",
        Map.of("runtimeName", "temperature", "fullDbName", "temperature")
    ));
    var updatedSchema = makeSchema();

    var updateInfo = makeCoordinator().makeUpdateInfo(widget, Set.of(MEASURE_NAME), updatedSchema);

    assertTrue(updateInfo.isEmpty());
  }

  @Test
  void makeUpdateInfo_ShouldSkipChartForReferencedTypeChanges() {
    var widget = makeWidget(selectedField("temperature"));
    var updatedSchema = makeSchema(property("temperature", XSD.STRING));

    var updateInfo = makeCoordinator().makeUpdateInfo(widget, Set.of(MEASURE_NAME), updatedSchema);

    assertTrue(updateInfo.isEmpty());
  }

  @Test
  void updateCharts_ShouldUpdateMeasureSchemaWhenOnlyUnreferencedFieldWasRemoved() {
    var widgetStorage = mock(IDataExplorerWidgetStorage.class);
    var coordinator = new ChartSchemaUpdateCoordinator(widgetStorage);
    var widget = makeWidget(selectedField("temperature"), unselectedField("humidity"));
    var pipeline = makePipeline(MEASURE_NAME);
    var updatedSchema = makeSchema(property("temperature", XSD.INTEGER));
    when(widgetStorage.findAll()).thenReturn(List.of(widget));

    coordinator.updateCharts(pipeline, updatedSchema);

    var eventProperties = getMeasureEventProperties(widget);
    assertEquals(1, eventProperties.size());
    assertEquals("temperature", eventProperties.get(0).get("runtimeName"));
    verify(widgetStorage).updateElement(widget);
  }

  @Test
  void updateCharts_ShouldMarkWidgetAsRequiresAttentionWhenReferencedFieldWasRemoved() {
    var widgetStorage = mock(IDataExplorerWidgetStorage.class);
    var coordinator = new ChartSchemaUpdateCoordinator(widgetStorage);
    var widget = makeWidget(selectedField("temperature"));
    var pipeline = makePipeline(MEASURE_NAME);
    when(widgetStorage.findAll()).thenReturn(List.of(widget));

    coordinator.updateCharts(pipeline, makeSchema());

    assertTrue(getMeasureEventProperties(widget).isEmpty());
    assertEquals(DataExplorerWidgetHealthStatus.REQUIRES_ATTENTION, widget.getHealthStatus());
    assertEquals(
        List.of("temperature"),
        widget.getAffectedSchemaUpdateFields()
    );
    verify(widgetStorage).updateElement(widget);
  }

  @Test
  void updateCharts_ShouldUpdateAllMatchingSourceConfigs() {
    var widgetStorage = mock(IDataExplorerWidgetStorage.class);
    var coordinator = new ChartSchemaUpdateCoordinator(widgetStorage);
    var widget = makeWidget(MEASURE_NAME, selectedField("temperature"));
    var secondSourceConfig = new HashMap<String, Object>();
    secondSourceConfig.put("measureName", MEASURE_NAME);
    secondSourceConfig.put("queryConfig", Map.of("fields", List.of()));
    secondSourceConfig.put("measure", makeMeasure(makeSchema(property("old", XSD.INTEGER))));
    var sourceConfigs = new java.util.ArrayList<>(getSourceConfigs(widget));
    sourceConfigs.add(secondSourceConfig);
    widget.setDataConfig(Map.of("sourceConfigs", sourceConfigs));
    var pipeline = makePipeline(MEASURE_NAME);
    var updatedSchema = makeSchema(property("temperature", XSD.INTEGER));
    when(widgetStorage.findAll()).thenReturn(List.of(widget));

    coordinator.updateCharts(pipeline, updatedSchema);

    assertEquals("temperature", getMeasureEventProperties(widget).get(0).get("runtimeName"));
    assertEquals(
        "temperature",
        getMeasureEventProperties(secondSourceConfig).get(0).get("runtimeName")
    );
    verify(widgetStorage).updateElement(widget);
  }

  @Test
  void updateCharts_ShouldOnlyCheckWidgetsWithMatchingMeasureName() {
    var widgetStorage = mock(IDataExplorerWidgetStorage.class);
    var coordinator = new ChartSchemaUpdateCoordinator(widgetStorage);
    var unrelatedWidget = makeWidget("other-measure", selectedField("temperature"));
    var pipeline = makePipeline(MEASURE_NAME);
    when(widgetStorage.findAll()).thenReturn(List.of(unrelatedWidget));

    coordinator.updateCharts(pipeline, makeSchema());

    verify(widgetStorage, never()).updateElement(unrelatedWidget);
  }

  private ChartSchemaUpdateCoordinator makeCoordinator() {
    return new ChartSchemaUpdateCoordinator(mock(IDataExplorerWidgetStorage.class));
  }

  private DataExplorerWidgetModel makeWidget(Map<String, Object>... fieldConfigs) {
    return makeWidget(MEASURE_NAME, fieldConfigs);
  }

  private DataExplorerWidgetModel makeWidget(String measureName,
                                             Map<String, Object>... fieldConfigs) {
    var widget = new DataExplorerWidgetModel();
    widget.setElementId("chart-1");
    widget.setBaseAppearanceConfig(Map.of("widgetTitle", "Chart"));
    widget.setVisualizationConfig(new HashMap<>());

    var queryConfig = new HashMap<String, Object>();
    queryConfig.put("fields", List.of(fieldConfigs));
    queryConfig.put("groupBy", List.of());
    queryConfig.put("selectedFilters", List.of());

    var sourceConfig = new HashMap<String, Object>();
    sourceConfig.put("measureName", measureName);
    sourceConfig.put("queryConfig", queryConfig);
    sourceConfig.put("measure", makeMeasure(makeSchema(property("temperature", XSD.INTEGER))));

    widget.setDataConfig(Map.of("sourceConfigs", List.of(sourceConfig)));
    return widget;
  }

  private Map<String, Object> makeMeasure(EventSchema eventSchema) {
    var measure = new DataLakeMeasure(MEASURE_NAME, "s0::timestamp", eventSchema);
    measure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
    return OBJECT_MAPPER.convertValue(measure, MAP_TYPE);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getSourceConfigs(DataExplorerWidgetModel widget) {
    return (List<Map<String, Object>>) widget.getDataConfig().get("sourceConfigs");
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getMeasureEventProperties(DataExplorerWidgetModel widget) {
    return getMeasureEventProperties(getSourceConfigs(widget).get(0));
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getMeasureEventProperties(Map<String, Object> sourceConfig) {
    var measure = (Map<String, Object>) sourceConfig.get("measure");
    var eventSchema = (Map<String, Object>) measure.get("eventSchema");
    return (List<Map<String, Object>>) eventSchema.get("eventProperties");
  }

  private Map<String, Object> selectedField(String runtimeName) {
    var fieldConfig = new HashMap<String, Object>();
    fieldConfig.put("runtimeName", runtimeName);
    fieldConfig.put("selected", true);
    return fieldConfig;
  }

  private Map<String, Object> unselectedField(String runtimeName) {
    var fieldConfig = new HashMap<String, Object>();
    fieldConfig.put("runtimeName", runtimeName);
    fieldConfig.put("selected", false);
    return fieldConfig;
  }

  private Pipeline makePipeline(String measureName) {
    var pipeline = new Pipeline();
    var sink = new DataSinkInvocation();
    sink.setAppId(DATA_LAKE_SINK_APP_ID);
    sink.setStaticProperties(List.of(FreeTextStaticProperty.of(DATA_LAKE_MEASUREMENT_FIELD, measureName)));
    pipeline.setActions(List.of(sink));
    return pipeline;
  }

  private EventSchema makeSchema(EventProperty... properties) {
    return new EventSchema(List.of(properties));
  }

  private EventPropertyPrimitive property(String runtimeName,
                                          URI runtimeType) {
    var property = new EventPropertyPrimitive();
    property.setRuntimeName(runtimeName);
    property.setRuntimeType(runtimeType.toString());
    return property;
  }
}
