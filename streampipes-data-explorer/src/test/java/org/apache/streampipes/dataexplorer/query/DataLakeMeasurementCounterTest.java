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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.model.datalake.DataLakeMeasure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DataLakeMeasurementCounterTest {

  private static final String MEASURE_1_NAME = "measure1";
  private static final String MEASURE_2_NAME = "measure2";
  private static final String UNKNOWN_MEASURE_NAME = "unknownMeasureName";

  private DataLakeMeasurementCounterTestImpl counter;

  private List<DataLakeMeasure> allMeasurements;

  @BeforeEach
  void setUp() {
    var measure1 = mock(DataLakeMeasure.class);
    var measure2 = mock(DataLakeMeasure.class);
    when(measure1.getMeasureName()).thenReturn(MEASURE_1_NAME);
    when(measure2.getMeasureName()).thenReturn(MEASURE_2_NAME);

    allMeasurements = List.of(measure1, measure2);
  }

  @Test
  void countMeasurementSizes_WithTwoEntries() {
    var measurementNames = List.of(MEASURE_1_NAME, MEASURE_2_NAME);

    counter = new DataLakeMeasurementCounterTestImpl(allMeasurements, measurementNames);
    var result = counter.countMeasurementSizes();
    assertEquals(2, result.size());
    assertEquals(1, result.get(MEASURE_1_NAME));
    assertEquals(1, result.get(MEASURE_2_NAME));
  }

  @Test
  void countMeasurementSizes_WithNotExistingMeasureName() {
    var measurementNames = List.of(UNKNOWN_MEASURE_NAME);
    counter = new DataLakeMeasurementCounterTestImpl(allMeasurements, measurementNames);

    var result = counter.countMeasurementSizes();
    assertEquals(0, result.size());
  }

  @Test
  void countMeasurementSizes_WithEmptyMeasureNames() {
    counter = new DataLakeMeasurementCounterTestImpl(allMeasurements, List.of());

    var result = counter.countMeasurementSizes();
    assertEquals(0, result.size());
  }

  @Test
  void countMeasurementSizes_WithEmptyMeasurementsAndMeasureNames() {
    counter = new DataLakeMeasurementCounterTestImpl(List.of(), List.of());

    var result = counter.countMeasurementSizes();
    assertEquals(0, result.size());
  }
}