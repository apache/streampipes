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
import org.apache.streampipes.model.pipeline.PipelineElementValidationInfo;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasurementChangeValidationStepTest {

  private final MeasurementChangeValidationStep step = new MeasurementChangeValidationStep();

  @Test
  void apply_ShouldAddValidationInfoForCriticalDatabaseMeasurementChange() {
    var source = makeStream(makeSchema(makeMeasurementProperty("temperature", XSD.STRING)));
    var target = makeDatabaseSink(makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)));
    var validationInfos = new ArrayList<PipelineElementValidationInfo>();

    step.apply(source, target, Set.of(target), validationInfos);

    assertEquals(1, validationInfos.size());
    assertEquals(
        MeasurementChangeValidationStep.MEASUREMENT_UPDATE_REQUIRED
            + ": temperature (" + XSD.INTEGER + " -> " + XSD.STRING + ")",
        validationInfos.get(0).getMessage()
    );
  }

  @Test
  void apply_ShouldIgnoreNonDatabaseSinks() {
    var source = makeStream(makeSchema(makeMeasurementProperty("temperature", XSD.STRING)));
    var target = makeSink(makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)));
    var validationInfos = new ArrayList<PipelineElementValidationInfo>();

    step.apply(source, target, Set.of(target), validationInfos);

    assertTrue(validationInfos.isEmpty());
  }

  @Test
  void apply_ShouldIgnoreNonCriticalStorageTypeChanges() {
    var source = makeStream(makeSchema(makeMeasurementProperty("temperature", XSD.LONG)));
    var target = makeDatabaseSink(makeSchema(makeMeasurementProperty("temperature", XSD.INTEGER)));
    var validationInfos = new ArrayList<PipelineElementValidationInfo>();

    step.apply(source, target, Set.of(target), validationInfos);

    assertTrue(validationInfos.isEmpty());
  }

  private SpDataStream makeStream(EventSchema eventSchema) {
    var stream = new SpDataStream();
    stream.setEventSchema(eventSchema);
    return stream;
  }

  private DataSinkInvocation makeDatabaseSink(EventSchema inputSchema) {
    var sink = makeSink(inputSchema);
    sink.setCategory(List.of(DataSinkType.DATABASE.name()));
    return sink;
  }

  private DataSinkInvocation makeSink(EventSchema inputSchema) {
    var inputStream = new SpDataStream();
    inputStream.setEventSchema(inputSchema);

    var sink = new DataSinkInvocation();
    sink.setInputStreams(List.of(inputStream));
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
}
