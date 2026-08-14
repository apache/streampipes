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

import org.apache.streampipes.manager.permission.DatasetPermissionManager;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.dataset.DatasetMeasureSchemaUpdateStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.storage.api.core.CRUDStorage;
import org.apache.streampipes.storage.api.user.IPermissionStorage;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DataExplorerSchemaManagementTest {
  public static final String NEW_PROPERTY = "newProperty";
  public static final String OLD_PROPERTY = "oldProperty";

  private CRUDStorage<DatasetMeasure> datasetStorageMock;
  private DatasetPermissionManager permissionManagerMock;
  private ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator;

  @BeforeEach
  public void setUp() {
    datasetStorageMock = mock(CRUDStorage.class);
    IPermissionStorage permissionStorageMock = mock(IPermissionStorage.class);
    this.permissionManagerMock = new DatasetPermissionManager(permissionStorageMock);
    this.chartSchemaUpdateCoordinator = mock(ChartSchemaUpdateCoordinator.class);
  }

  @Test
  public void createMeasurementThatNotExisted() {
    when(datasetStorageMock.findAll()).thenReturn(List.of());
    var schemaManagement = new DataExplorerSchemaManagement(
        datasetStorageMock,
        permissionManagerMock,
        chartSchemaUpdateCoordinator
    );

    var oldMeasure = getSampleMeasure(
        DatasetMeasureSchemaUpdateStrategy.UPDATE_SCHEMA,
        List.of()
    );
    var resultingMeasure = schemaManagement.createOrUpdateMeasurement(oldMeasure,null);

    assertEquals(oldMeasure.getMeasureName(), resultingMeasure.getMeasureName());
    verify(datasetStorageMock, Mockito.times(1))
        .persist(any());
  }


  @Test
  public void createMeasurementWithUpdateStrategy() {

    var oldMeasure = getSampleMeasure(
        DatasetMeasureSchemaUpdateStrategy.UPDATE_SCHEMA,
        List.of(
            getEventProperty(OLD_PROPERTY, XSD.STRING)
        )
    );

    when(datasetStorageMock.findAll()).thenReturn(List.of(oldMeasure));
    when(datasetStorageMock.getElementById(any())).thenReturn(oldMeasure);
    var schemaManagement = new DataExplorerSchemaManagement(
        datasetStorageMock,
        permissionManagerMock,
        chartSchemaUpdateCoordinator
    );

    var newMeasure = getNewMeasure(DatasetMeasureSchemaUpdateStrategy.UPDATE_SCHEMA);

    var resultMeasure = schemaManagement.createOrUpdateMeasurement(newMeasure,null);

    assertEquals(newMeasure.getMeasureName(), resultMeasure.getMeasureName());
    verify(datasetStorageMock, Mockito.times(1))
        .updateElement(any());
    assertFalse(containsPropertyWithName(resultMeasure, OLD_PROPERTY));
    assertTrue(containsPropertyWithName(resultMeasure, NEW_PROPERTY));

  }


  @Test
  public void createMeasurementWithExtendSchemaStrategy() {

    var oldMeasure = getSampleMeasure(
        DatasetMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA,
        List.of(
            getEventProperty(OLD_PROPERTY, XSD.STRING)
        )
    );
    when(datasetStorageMock.findAll()).thenReturn(List.of(oldMeasure));
    when(datasetStorageMock.getElementById(any())).thenReturn(oldMeasure);
    var schemaManagement = new DataExplorerSchemaManagement(
        datasetStorageMock,
        permissionManagerMock,
        chartSchemaUpdateCoordinator
    );
    var newMeasure = getNewMeasure(DatasetMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA);

    var resultMeasure = schemaManagement.createOrUpdateMeasurement(newMeasure,null);

    assertEquals(newMeasure.getMeasureName(), resultMeasure.getMeasureName());
    verify(datasetStorageMock, Mockito.times(1)).updateElement(any());
    assertTrue(containsPropertyWithName(resultMeasure, OLD_PROPERTY));
    assertTrue(containsPropertyWithName(resultMeasure, NEW_PROPERTY));
  }


  @Test
  public void createMeasurementWithExtendSchemaStrategyAndDifferentPropertyTypes() {
    var oldMeasure = getSampleMeasure(
        DatasetMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA,
        List.of(
            getEventProperty(OLD_PROPERTY, XSD.STRING),
            getEventProperty(NEW_PROPERTY, XSD.INTEGER)
        )
    );

    when(datasetStorageMock.findAll()).thenReturn(List.of(oldMeasure));
    when(datasetStorageMock.getElementById(any())).thenReturn(oldMeasure);

    var schemaManagement = new DataExplorerSchemaManagement(
        datasetStorageMock,
        permissionManagerMock,
        chartSchemaUpdateCoordinator
    );

    var newMeasure = getNewMeasure(DatasetMeasureSchemaUpdateStrategy.EXTEND_EXISTING_SCHEMA);

    var exception = assertThrows(
        RuntimeException.class,
        () -> schemaManagement.createOrUpdateMeasurement(newMeasure, null)
    );

    assertEquals(
        "Can't save measurement with critical field changes: newProperty ("
            + XSD.INTEGER + " -> " + XSD.STRING + ")",
        exception.getMessage()
    );
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

  private DatasetMeasure getNewMeasure(DatasetMeasureSchemaUpdateStrategy updateStrategy) {
    return getSampleMeasure(
        updateStrategy,
        List.of(getEventProperty(NEW_PROPERTY, XSD.STRING))
    );
  }

  private DatasetMeasure getSampleMeasure(
      DatasetMeasureSchemaUpdateStrategy updateStrategy,
      List<EventProperty> eventProperties
  ) {
    var measure = new DatasetMeasure();
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
      DatasetMeasure measure,
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
