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

package org.apache.streampipes.dataexplorer;

import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.api.IDataLakeStorage;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DataExplorerSchemaManagementTest {

  @Test
  public void createMeasurement() {
    // Arrange
    var dataLakeStorage = mock(IDataLakeStorage.class);
    DataLakeMeasure measure = new DataLakeMeasure();
    measure.setMeasureName("testMeasure");
    when(dataLakeStorage.getAllDataLakeMeasures()).thenReturn(List.of());
    when(dataLakeStorage.storeDataLakeMeasure(any())).thenReturn(true);

    var schemaManagement = new DataExplorerSchemaManagement(dataLakeStorage);

    // Act
    var updatedMeasure = schemaManagement.createMeasurement(measure);

    // Assert
    assertEquals(measure.getMeasureName(), updatedMeasure.getMeasureName());
    verify(dataLakeStorage, Mockito.times(1))
        .storeDataLakeMeasure(any());

  }

  @Test
  public void createMeasurementUpdate() {
    // Arrange
    var dataLakeStorage = mock(IDataLakeStorage.class);
    DataLakeMeasure measure = new DataLakeMeasure();
    measure.setMeasureName("testMeasure");
    when(dataLakeStorage.getAllDataLakeMeasures()).thenReturn(List.of(measure));
    when(dataLakeStorage.storeDataLakeMeasure(any())).thenReturn(true);

    var schemaManagement = new DataExplorerSchemaManagement(dataLakeStorage);

    // Act
    var updatedMeasure = schemaManagement.createMeasurement(measure);

    // Assert
    assertEquals(measure.getMeasureName(), updatedMeasure.getMeasureName());
    verify(dataLakeStorage, Mockito.times(1))
        .storeDataLakeMeasure(any());

  }
}