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

import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.connect.adapter.ChartSchemaUpdateInfo;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.staticproperty.FreeTextStaticProperty;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MeasurementUpdateManagementTest {

  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";
  private static final String DATA_LAKE_MEASUREMENT_FIELD = "db_measurement";

  @Test
  void checkPipelineMigrations_ShouldReturnEmptyListWhenNoWarningsExist() {
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var management = new MeasurementUpdateManagement(chartSchemaUpdateCoordinator);
    var storedPipeline = makePipeline(makeDataLakeSink(makeSchema(property("temperature", XSD.INTEGER))));
    var updatedPipeline = makePipeline(makeDataLakeSink(makeSchema(property("temperature", XSD.LONG))));
    when(chartSchemaUpdateCoordinator.checkChartMigrations(Collections.singleton(any()), any())).thenReturn(List.of());

    var result = management.checkPipelineMigrations(storedPipeline, updatedPipeline);

    assertTrue(result.isEmpty());
  }

  @Test
  void checkPipelineMigrations_ShouldReturnWarningForCriticalMeasurementFieldChange() {
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var management = new MeasurementUpdateManagement(chartSchemaUpdateCoordinator);
    var storedPipeline = makePipeline(makeDataLakeSink(makeSchema(property("temperature", XSD.INTEGER))));
    var updatedPipeline = makePipeline(makeDataLakeSink(makeSchema(property("temperature", XSD.STRING))));
    when(chartSchemaUpdateCoordinator.checkChartMigrations(Collections.singleton(any()), any())).thenReturn(List.of());

    var result = management.checkPipelineMigrations(storedPipeline, updatedPipeline);

    assertEquals(1, result.size());
    assertEquals("measure", result.get(0).getMeasurementName());
    assertTrue(result.get(0).getCriticalMeasurementFieldChanges().get(0).runtimeName().contains("temperature"));
  }

  @Test
  void checkPipelineMigrations_ShouldReturnWarningForAffectedCharts() {
    var chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
    var management = new MeasurementUpdateManagement(chartSchemaUpdateCoordinator);
    var storedPipeline = makePipeline(makeDataLakeSink(makeSchema(property("temperature", XSD.INTEGER))));
    var updatedPipeline = makePipeline(makeDataLakeSink(makeSchema()));
    var chartSchemaUpdateInfo = new ChartSchemaUpdateInfo();
    chartSchemaUpdateInfo.setChartId("chart-1");
    chartSchemaUpdateInfo.setChartTitle("Chart");
    chartSchemaUpdateInfo.setAffectedFields(List.of("temperature"));
    when(chartSchemaUpdateCoordinator.checkChartMigrations(Collections.singleton(any()), any())).thenReturn(List.of(chartSchemaUpdateInfo));

    var result = management.checkPipelineMigrations(storedPipeline, updatedPipeline);

    assertEquals(1, result.size());
    assertEquals(List.of(chartSchemaUpdateInfo), result.get(0).getChartSchemaUpdateInfos());
  }

  private Pipeline makePipeline(DataSinkInvocation dataSink) {
    var pipeline = new Pipeline();
    pipeline.setPipelineId("pipeline-1");
    pipeline.setName("Pipeline");
    pipeline.setActions(List.of(dataSink));
    pipeline.setStreams(dataSink.getInputStreams());
    return pipeline;
  }

  private DataSinkInvocation makeDataLakeSink(EventSchema eventSchema) {
    var stream = new SpDataStream();
    stream.setElementId("stream-1");
    stream.setEventSchema(eventSchema);

    var sink = new DataSinkInvocation();
    sink.setElementId("sink-1");
    sink.setName("Data Lake");
    sink.setAppId(DATA_LAKE_SINK_APP_ID);
    sink.setInputStreams(List.of(stream));
    sink.setStaticProperties(List.of(FreeTextStaticProperty.of(DATA_LAKE_MEASUREMENT_FIELD, "measure")));
    return sink;
  }

  private EventSchema makeSchema(EventPropertyPrimitive... eventProperties) {
    return new EventSchema(List.of(eventProperties));
  }

  private EventPropertyPrimitive property(String runtimeName,
                                          URI runtimeType) {
    var property = new EventPropertyPrimitive();
    property.setRuntimeName(runtimeName);
    property.setRuntimeType(runtimeType.toString());
    return property;
  }
}
