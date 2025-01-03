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
import org.apache.streampipes.model.datalake.DataLakeMeasureSchemaUpdateStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.storage.api.CRUDStorage;
import org.apache.streampipes.test.generator.EventPropertyPrimitiveTestBuilder;
import org.apache.streampipes.test.generator.EventSchemaTestBuilder;
import org.apache.streampipes.vocabulary.XSD;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DataExplorerSchemaManagementTest {
  public static final String NEW_PROPERTY = "newProperty";
  public static final String OLD_PROPERTY = "oldProperty";

  private CRUDStorage<DataLakeMeasure> dataLakeStorageMock;
  private DataExplorerSchemaManagement dataExplorerSchemaManagement;

  @BeforeEach
  public void setUp() {
    dataLakeStorageMock = mock(CRUDStorage.class);
    dataExplorerSchemaManagement = new DataExplorerSchemaManagement(dataLakeStorageMock);
  }


  @Test
  public void getByMeasureName_ReturnsMeasureWhenMeasureExists() {
    var measureName = "existingMeasure";
    var existingMeasure = new DataLakeMeasure();
    existingMeasure.setMeasureName(measureName);

    when(dataLakeStorageMock.findAll()).thenReturn(List.of(existingMeasure));

    var result = dataExplorerSchemaManagement.getByMeasureName(measureName);

    assertEquals(existingMeasure, result);
  }

  @Test
  public void getByMeasureName_ReturnsNullWhenMeasureDoesNotExist() {
    var measureName = "nonExistingMeasure";

    when(dataLakeStorageMock.findAll()).thenReturn(List.of());

    var result = dataExplorerSchemaManagement.getByMeasureName(measureName);

    assertNull(result);
  }


  @Test
  public void createOrUpdateMeasuremente_ThatNotExisted() {
    when(dataLakeStorageMock.findAll()).thenReturn(List.of());

    var oldMeasure = getSampleMeasure(
        DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA,
        List.of()
    );
    var resultingMeasure = dataExplorerSchemaManagement.createOrUpdateMeasurement(oldMeasure);

    assertEquals(oldMeasure.getMeasureName(), resultingMeasure.getMeasureName());
    verify(dataLakeStorageMock, Mockito.times(1))
        .persist(any());
  }


  @Test
  public void createOrUpdateMeasuremente_WithUpdateStrategy() {

    var oldMeasure = getSampleMeasure(
        DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA,
        List.of(
            getEventProperty(OLD_PROPERTY, XSD.STRING)
        )
    );

    when(dataLakeStorageMock.findAll()).thenReturn(List.of(oldMeasure));
    when(dataLakeStorageMock.getElementById(any())).thenReturn(oldMeasure);

    var newMeasure = getNewMeasure(DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA);

    var resultMeasure = dataExplorerSchemaManagement.createOrUpdateMeasurement(newMeasure);

    assertEquals(newMeasure.getMeasureName(), resultMeasure.getMeasureName());
    verify(dataLakeStorageMock, Mockito.times(1))
        .updateElement(any());
    assertFalse(containsPropertyWithName(resultMeasure, OLD_PROPERTY));
    assertTrue(containsPropertyWithName(resultMeasure, NEW_PROPERTY));

  }


  @Test
  public void createOrUpdateMeasuremente_WithExtendSchemaStrategy() {

    var oldMeasure = getSampleMeasure(
        DataLakeMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA,
        List.of(
            getEventProperty(OLD_PROPERTY, XSD.STRING)
        )
    );
    when(dataLakeStorageMock.findAll()).thenReturn(List.of(oldMeasure));
    when(dataLakeStorageMock.getElementById(any())).thenReturn(oldMeasure);
    var newMeasure = getNewMeasure(DataLakeMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA);

    var resultMeasure = dataExplorerSchemaManagement.createOrUpdateMeasurement(newMeasure);

    assertEquals(newMeasure.getMeasureName(), resultMeasure.getMeasureName());
    verify(dataLakeStorageMock, Mockito.times(1)).updateElement(any());
    assertTrue(containsPropertyWithName(resultMeasure, OLD_PROPERTY));
    assertTrue(containsPropertyWithName(resultMeasure, NEW_PROPERTY));
  }


  @Test
  public void createOrUpdateMeasuremente_WithExtendSchemaStrategyAndDifferentPropertyTypes() {
    var oldMeasure = getSampleMeasure(
        DataLakeMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA,
        List.of(
            getEventProperty(OLD_PROPERTY, XSD.STRING),
            getEventProperty(NEW_PROPERTY, XSD.INTEGER)
        )
    );

    when(dataLakeStorageMock.findAll()).thenReturn(List.of(oldMeasure));
    when(dataLakeStorageMock.getElementById(any())).thenReturn(oldMeasure);

    var newMeasure = getNewMeasure(DataLakeMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA);

    var resultMeasure = dataExplorerSchemaManagement.createOrUpdateMeasurement(newMeasure);
    assertEquals(newMeasure.getMeasureName(), resultMeasure.getMeasureName());
    verify(dataLakeStorageMock, Mockito.times(1)).updateElement(any());
    assertEquals(
        2,
        resultMeasure.getEventSchema()
                     .getEventProperties()
                     .size()
    );
    assertTrue(containsPropertyWithName(resultMeasure, OLD_PROPERTY));
    assertTrue(containsPropertyWithName(resultMeasure, NEW_PROPERTY));
  }

  private EventProperty getEventProperty(
      String runtimeName,
      URI runtimeType
  ) {
    return EventPropertyPrimitiveTestBuilder
        .create()
        .withRuntimeName(runtimeName)
        .withRuntimeType(runtimeType)
        .build();
  }

  private DataLakeMeasure getNewMeasure(DataLakeMeasureSchemaUpdateStrategy updateStrategy) {
    return getSampleMeasure(
        updateStrategy,
        List.of(getEventProperty(NEW_PROPERTY, XSD.STRING))
    );
  }

  private DataLakeMeasure getSampleMeasure(
      DataLakeMeasureSchemaUpdateStrategy updateStrategy,
      List<EventProperty> eventProperties
  ) {
    var measure = new DataLakeMeasure();
    measure.setMeasureName("testMeasure");
    measure.setSchemaUpdateStrategy(updateStrategy);

    measure.setEventSchema(
        EventSchemaTestBuilder
            .create()
            .withEventProperties(
                eventProperties
            )
            .build()
    );

    return measure;
  }

  private boolean containsPropertyWithName(
      DataLakeMeasure measure,
      String runtimeName
  ) {
    return measure
        .getEventSchema()
        .getEventProperties()
        .stream()
        .anyMatch(
            eventProperty -> eventProperty.getRuntimeName()
                                          .equals(runtimeName)
        );
  }
}
