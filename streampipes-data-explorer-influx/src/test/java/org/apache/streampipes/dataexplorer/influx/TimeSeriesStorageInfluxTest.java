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

package org.apache.streampipes.dataexplorer.influx;


import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.influx.client.InfluxClientProvider;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.runtime.EventFactory;
import org.apache.streampipes.model.runtime.SchemaInfo;
import org.apache.streampipes.model.runtime.SourceInfo;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.test.generator.EventPropertyListTestBuilder;
import org.apache.streampipes.test.generator.EventPropertyNestedTestBuilder;
import org.apache.streampipes.test.generator.EventPropertyPrimitiveTestBuilder;
import org.apache.streampipes.test.generator.EventSchemaTestBuilder;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.vocabulary.XSD;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimeSeriesStorageInfluxTest {

  private InfluxDB influxDBMock;

  private RawFieldSerializer serializer;

  private static final String EXPECTED_MEASUREMENT = "testMeasure";
  private static final String FIELD_NAME = "testId";
  private static final String TIMESTAMP = "timestamp";
  private static final long SAMPLE_TIMESTAMP = 1701270890000L;

  @BeforeEach
  public void setUp() {
    influxDBMock = Mockito.mock(InfluxDB.class);
    serializer = new RawFieldSerializer();
  }

  @Test
  public void onEventNull() {
    var influxStore = getInfluxStore(new EventSchema());
    assertThrows(SpRuntimeException.class, () -> influxStore.onEvent(null));
  }


  @Test
  public void onEventWithInteger() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1)
        .build();

    var actualPoint = testEventWithOneField(XSD.INTEGER, 1);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithIntegerFloatValue() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1.0f)
        .build();

    var actualPoint = testEventWithOneField(XSD.INTEGER, 1.0f);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithLong() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1L)
        .build();

    var actualPoint = testEventWithOneField(XSD.LONG, 1L);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithLongFloatValue() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1.0f)
        .build();

    var actualPoint = testEventWithOneField(XSD.LONG, 1.0f);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithFloat() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1.0f)
        .build();

    var actualPoint = testEventWithOneField(XSD.FLOAT, 1.0f);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithDouble() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1.0)
        .build();

    var actualPoint = testEventWithOneField(XSD.DOUBLE, 1.0);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithBoolean() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, true)
        .build();

    var actualPoint = testEventWithOneField(XSD.BOOLEAN, true);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithNumber() throws URISyntaxException {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, 1.0)
        .build();

    var actualPoint = testEventWithOneField(new URI(SO.NUMBER), 1.0);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithString() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, "testValue")
        .build();

    var actualPoint = testEventWithOneField(XSD.STRING, "testValue");

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventReservedKeyWord() {
    // name is a reserved keyword
    var fieldName = "name";
    var sanitizedFieldName = "name_";
    var value = 1;

    var expected = getPointBuilderWithTimestamp()
        .addField(sanitizedFieldName, value)
        .build();

    var eventSchema = getEventSchemaBuilderWithTimestamp()
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName(sanitizedFieldName)
                .withRuntimeType(XSD.INTEGER)
                .build())
        .build();

    var event = getEvent(eventSchema, Map.of(fieldName, value));

    var influxStore = getInfluxStore(eventSchema);

    var actualPoint = executeOnEvent(influxStore, event);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithTag() {
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, "value")
        .tag("id", "id1")
        .build();

    var eventSchema = getEventSchemaBuilderWithTimestamp()
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName(FIELD_NAME)
                .withRuntimeType(XSD.STRING)
                .build())
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName("id")
                .withRuntimeType(XSD.STRING)
                .withPropertyScope(PropertyScope.DIMENSION_PROPERTY)
                .build())
        .build();

    var event = getEvent(eventSchema, Map.of(FIELD_NAME, "value", "id", "id1"));

    var influxStore = getInfluxStore(eventSchema);

    var actualPoint = executeOnEvent(influxStore, event);

    assertEquals(expected, actualPoint);
  }


  @Test
  public void onEventWithNestedProperty() {
    Map<String, Object> value = new HashMap<>();
    value.put("one", "two");

    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, serializer.serialize(value))
        .build();

    var eventSchema = getEventSchemaBuilderWithTimestamp()
        .withEventProperty(
            EventPropertyNestedTestBuilder
                .create()
                .withRuntimeName(FIELD_NAME)
                .build())
        .build();

    var event = getEvent(eventSchema, Map.of(FIELD_NAME, value));

    var influxStore = getInfluxStore(eventSchema);

    var actualPoint = executeOnEvent(influxStore, event);

    assertEquals(expected, actualPoint);
  }

  @Test
  public void onEventWithListProperty() {
    String[] value = {"one", "two"};
    var expected = getPointBuilderWithTimestamp()
        .addField(FIELD_NAME, serializer.serialize(value))
        .build();

    var eventSchema = getEventSchemaBuilderWithTimestamp()
        .withEventProperty(
            EventPropertyListTestBuilder
                .create()
                .withRuntimeName(FIELD_NAME)
                .build())
        .build();

    var event = getEvent(eventSchema, Map.of(FIELD_NAME, value));

    var influxStore = getInfluxStore(eventSchema);

    var actualPoint = executeOnEvent(influxStore, event);

    assertEquals(expected, actualPoint);
  }

  /**
   * Initialize a Point builder with a timestamp
   */
  private Point.Builder getPointBuilderWithTimestamp() {
    return Point.measurement(EXPECTED_MEASUREMENT)
                .time(SAMPLE_TIMESTAMP, TimeUnit.MILLISECONDS);
  }


  /**
   * Executes the onEvent method and returns the resuting data point using an argument captor
   */
  private Point executeOnEvent(TimeSeriesStorageInflux timeSeriesStorageInflux, Event event) {
    timeSeriesStorageInflux.onEvent(event);
    var pointArgumentCaptor = ArgumentCaptor.forClass(Point.class);

    Mockito.verify(influxDBMock).write(pointArgumentCaptor.capture());

    return pointArgumentCaptor.getValue();
  }

  /**
   * Creates an event with the given data and a timestamp
   */
  private Event getEvent(EventSchema eventSchema, Map<String, Object> inputData) {
    Map<String, Object> data = new HashMap<>();
    data.put(TIMESTAMP, SAMPLE_TIMESTAMP);
    data.putAll(inputData);

    return EventFactory.fromMap(
        data,
        new SourceInfo("test-topic", "s0"),
        new SchemaInfo(eventSchema, new ArrayList<>())
    );
  }


  /**
   * This convenience method is used to test all cases for the different supported data types
   */
  private Point testEventWithOneField(
      URI type,
      Object value
  ) {

    var eventSchema = getEventSchemaBuilderWithTimestamp()
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName(FIELD_NAME)
                .withRuntimeType(type)
                .build())
        .build();


    var event = EventFactory.fromMap(
        Map.of(TIMESTAMP, SAMPLE_TIMESTAMP, FIELD_NAME, value),
        new SourceInfo("test-topic", "s0"),
        new SchemaInfo(eventSchema, new ArrayList<>())
    );

    var influxStore = getInfluxStore(eventSchema);

    influxStore.onEvent(event);
    var pointArgumentCaptor = ArgumentCaptor.forClass(Point.class);

    Mockito.verify(influxDBMock).write(pointArgumentCaptor.capture());
    return pointArgumentCaptor.getValue();
  }

  /**
   * Returns the event schema builder with a default timestamp property
   */
  private EventSchemaTestBuilder getEventSchemaBuilderWithTimestamp() {
    return EventSchemaTestBuilder
        .create()
        .withEventProperty(
            EventPropertyPrimitiveTestBuilder
                .create()
                .withRuntimeName(TIMESTAMP)
                .build());
  }

  /**
   * Initializes an influx store with the given event schema
   */
  private TimeSeriesStorageInflux getInfluxStore(EventSchema eventSchema) {

    DataLakeMeasure measure = new DataLakeMeasure(
        EXPECTED_MEASUREMENT,
        "s0::%s".formatted(TIMESTAMP),
        eventSchema
    );

    var influxClientProviderMock = Mockito.mock(InfluxClientProvider.class);
    Mockito.when(influxClientProviderMock.getSetUpInfluxDBClient((Environment) ArgumentMatchers.any()))
           .thenReturn(influxDBMock);

    return new TimeSeriesStorageInflux(measure, null, influxClientProviderMock);
  }

}