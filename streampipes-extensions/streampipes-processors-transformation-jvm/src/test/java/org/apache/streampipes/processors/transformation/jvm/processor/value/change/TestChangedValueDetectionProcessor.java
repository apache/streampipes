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

package org.apache.streampipes.processors.transformation.jvm.processor.value.change;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.messaging.InternalEventProcessor;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.MappingPropertyUnary;
import org.apache.streampipes.sdk.builder.PrimitivePropertyBuilder;
import org.apache.streampipes.sdk.builder.adapter.GuessSchemaBuilder;
import org.apache.streampipes.sdk.utils.Datatypes;
import org.apache.streampipes.test.generator.InvocationGraphGenerator;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class TestChangedValueDetectionProcessor {

  @Test
  public void getDimensionKeyForMultipleDimensionProperties() {
    var processor = new ChangedValueDetectionProcessor();
    var event = this.createTestEvent();

    assertEquals("l1sensor01", processor.getDimensionKey(event));
  }

  @Test
  public void getDimensionKeyForOneDimension() {
    var processor = new ChangedValueDetectionProcessor();
    var event = this.createTestEvent();

    // Create event with no DIMENSION_PROPERTY
    event.getSchemaInfo()
      .getEventSchema()
      .getEventProperties().get(0)
        .setPropertyScope(PropertyScope.MEASUREMENT_PROPERTY.name());

    assertEquals("l1", processor.getDimensionKey(event));
  }

  @Test
  public void getDimensionKeyForOneDimensionProperty() {
    var processor = new ChangedValueDetectionProcessor();
    var event = this.createTestEvent(4711);

    // Remove second dimension property
    event.removeFieldBySelector("location");
    event.getSchemaInfo().getEventSchema().getEventProperties().remove(1);

    assertEquals("sensor01", processor.getDimensionKey(event));
  }

  @Test
  public void detectChangedValue() {
    var processor = new ChangedValueDetectionProcessor();

    // Set "s0::value" as COMPARE_FIELD_ID
    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());

    graph.getStaticProperties().stream()
    .filter(p -> p instanceof MappingPropertyUnary)
    .map((p -> (MappingPropertyUnary) p))
    .findFirst().get().setSelectedProperty("s0::value");

    ProcessorParams params = new ProcessorParams(graph);
    processor.onInvocation(params, null, null);

    var event = this.createTestEvent();

    StoreEventCollector collector = new StoreEventCollector();

    processor.onEvent(event, collector);
    processor.onEvent(event, collector);
    processor.onEvent(event, collector);
    processor.onEvent(this.createTestEvent(4711), collector); // Change value
    processor.onEvent(event, collector);
    processor.onEvent(event, collector);
    processor.onEvent(event, collector);

    assertEquals(3, collector.getEvents().size());
  }

  @Test
  public void detectChangedValueMultiDim() {
    var processor = new ChangedValueDetectionProcessor();

    // Set "s0::value" as COMPARE_FIELD_ID
    DataProcessorInvocation graph = InvocationGraphGenerator.makeEmptyInvocation(processor.declareModel());

    graph.getStaticProperties().stream()
    .filter(p -> p instanceof MappingPropertyUnary)
    .map((p -> (MappingPropertyUnary) p))
    .findFirst().get().setSelectedProperty("s0::value");

    ProcessorParams params = new ProcessorParams(graph);
    processor.onInvocation(params, null, null);

    StoreEventCollector collector = new StoreEventCollector();

    processor.onEvent(this.createTestEvent(0, "loc_1"), collector);
    processor.onEvent(this.createTestEvent(0, "loc_2"), collector);
    processor.onEvent(this.createTestEvent(0, "loc_1"), collector);
    processor.onEvent(this.createTestEvent(0, "loc_1"), collector);
    processor.onEvent(this.createTestEvent(0, "loc_2"), collector);
    processor.onEvent(this.createTestEvent(0, "loc_2"), collector);
    processor.onEvent(this.createTestEvent(1, "loc_1"), collector);
    processor.onEvent(this.createTestEvent(1, "loc_2"), collector);

    assertEquals(4, collector.getEvents().size());
  }

  private Event createTestEvent() {
    return this.createTestEvent(12, "l1");
  }

  private Event createTestEvent(Integer value) {
    return this.createTestEvent(value, "l1");
  }

  private Event createTestEvent(Integer value, String location) {

    var eventSchema = GuessSchemaBuilder.create()
        .property(PrimitivePropertyBuilder
            .create(Datatypes.String, "sensorId")
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Double, "location")
            .scope(PropertyScope.DIMENSION_PROPERTY)
            .build())
        .property(PrimitivePropertyBuilder
            .create(Datatypes.Integer, "value")
            .scope(PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .build().eventSchema;

    Map<String, Object> map = new HashMap<>();
    map.put("sensorId", "sensor01");
    map.put("location", location);
    map.put("value", value);

    return EventFactory.fromMap(map, new SourceInfo("", "s0"), new SchemaInfo(eventSchema, new ArrayList<>()));
  }

  class StoreEventCollector implements SpOutputCollector {

    ArrayList<Event> events = new ArrayList<Event>();

    @Override
    public void registerConsumer(String routeId, InternalEventProcessor<Map<String, Object>> consumer) {}

    @Override
    public void unregisterConsumer(String routeId) {}

    @Override
    public void connect() throws SpRuntimeException {}

    @Override
    public void disconnect() throws SpRuntimeException {}

    @Override
    public void collect(Event event) {
      events.add(event);
    }

    public List<Event> getEvents() {
      return this.events;
    }

  }
}
