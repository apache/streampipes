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

package org.apache.streampipes.manager.matching.v2.pipeline;

import org.apache.streampipes.model.DataSinkType;
import org.apache.streampipes.model.SpDataStream;
import org.apache.streampipes.model.graph.DataSinkInvocation;
import org.apache.streampipes.model.pipeline.Pipeline;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementChangeDetectorTest {

  private static final String STREAM_ID = "stream-1";
  private static final String DATA_LAKE_SINK_APP_ID = "org.apache.streampipes.sinks.internal.jvm.datalake";

  private final MeasurementChangeDetector detector = new MeasurementChangeDetector();

  @Test
  void hasCriticalMeasurementFieldChange_ShouldIgnoreChangesWithoutDatabaseSink() {
    var pipeline = makePipeline(makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)));
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.STRING));

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertFalse(result);
  }

  @Test
  void hasCriticalMeasurementFieldChange_ShouldDetectCriticalChangeForDataLakeSink() {
    var pipeline = makePipeline(
        makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)),
        makeDataLakeSink()
    );
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.STRING));

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertTrue(result);
  }

  @Test
  void hasCriticalMeasurementFieldChange_ShouldDetectCriticalChangeForDatabaseSinkCategory() {
    var pipeline = makePipeline(
        makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)),
        makeDatabaseSink()
    );
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.BOOLEAN));

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertTrue(result);
  }

  @Test
  void findCriticalMeasurementFieldChanges_ShouldReturnChangedNamesAndTypes() {
    var existingSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.INTEGER),
        makeMeasurementProperty("pressure", XSD.FLOAT)
    );
    var updatedSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.STRING),
        makeMeasurementProperty("pressure", XSD.BOOLEAN)
    );

    var result = detector.findCriticalMeasurementFieldChanges(existingSchema, updatedSchema);

    assertEquals(2, result.size());
    assertEquals("temperature", result.get(0).runtimeName());
    assertEquals(XSD.INTEGER.toString(), result.get(0).existingType());
    assertEquals(XSD.STRING.toString(), result.get(0).updatedType());
    assertEquals("pressure", result.get(1).runtimeName());
    assertEquals(XSD.FLOAT.toString(), result.get(1).existingType());
    assertEquals(XSD.BOOLEAN.toString(), result.get(1).updatedType());
  }

  @Test
  void hasCriticalMeasurementFieldChange_ShouldIgnoreIntegerToLongChange() {
    var pipeline = makePipeline(
        makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)),
        makeDatabaseSink()
    );
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.LONG));

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertFalse(result);
  }

  @Test
  void hasCriticalMeasurementFieldChange_ShouldIgnoreFloatToDoubleChange() {
    var pipeline = makePipeline(
        makeSchema(makeMeasurementProperty("temperature", XSD.FLOAT)),
        makeDatabaseSink()
    );
    var updatedSchema = makeSchema(makeMeasurementProperty("temperature", XSD.DOUBLE));

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertFalse(result);
  }

  @Test
  void hasCriticalMeasurementFieldChange_ShouldIgnoreAddedAndRemovedFields() {
    var pipeline = makePipeline(
        makeSchema(
            makeMeasurementProperty("temperature", XSD.INTEGER),
            makeMeasurementProperty("pressure", XSD.DOUBLE)
        ),
        makeDatabaseSink()
    );
    var updatedSchema = makeSchema(
        makeMeasurementProperty("temperature", XSD.INTEGER),
        makeMeasurementProperty("humidity", XSD.DOUBLE)
    );

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertFalse(result);
  }

  @Test
  void hasCriticalMeasurementFieldChange_ShouldIgnoreDimensionFieldChanges() {
    var pipeline = makePipeline(
        makeSchema(makeDimensionProperty("machineId", XSD.INTEGER)),
        makeDatabaseSink()
    );
    var updatedSchema = makeSchema(makeDimensionProperty("machineId", XSD.STRING));

    var result = detector.hasCriticalMeasurementFieldChange(pipeline, STREAM_ID, updatedSchema);

    assertFalse(result);
  }

  private Pipeline makePipeline(EventSchema eventSchema,
                                DataSinkInvocation... actions) {
    var dataStream = new SpDataStream();
    dataStream.setElementId(STREAM_ID);
    dataStream.setEventSchema(eventSchema);

    var pipeline = new Pipeline();
    pipeline.setStreams(List.of(dataStream));
    pipeline.setActions(List.of(actions));
    return pipeline;
  }

  private DataSinkInvocation makeDataLakeSink() {
    var sink = new DataSinkInvocation();
    sink.setAppId(DATA_LAKE_SINK_APP_ID);
    return sink;
  }

  private DataSinkInvocation makeDatabaseSink() {
    var sink = new DataSinkInvocation();
    sink.setCategory(List.of(DataSinkType.DATABASE.name()));
    return sink;
  }

  private EventSchema makeSchema(EventPropertyPrimitive... properties) {
    return new EventSchema(List.of(properties));
  }

  private EventPropertyPrimitive makeMeasurementProperty(String runtimeName,
                                                        URI runtimeType) {
    var property = new EventPropertyPrimitive(runtimeType.toString(), runtimeName, "", "");
    property.setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());
    return property;
  }

  private EventPropertyPrimitive makeDimensionProperty(String runtimeName,
                                                      URI runtimeType) {
    var property = new EventPropertyPrimitive(runtimeType.toString(), runtimeName, "", "");
    property.setPropertyScope(PropertyScope.DIMENSION_PROPERTY.name());
    return property;
  }
}
