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

import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.manager.matching.v2.pipeline.MeasurementChangeDetector;
import org.apache.streampipes.manager.permission.DatasetPermissionManager;
import org.apache.streampipes.manager.pipeline.update.ChartSchemaUpdateCoordinator;
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.dataset.DatasetMeasureSchemaUpdateStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.api.core.CRUDStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataExplorerSchemaManagement implements IDataExplorerSchemaManagement {

  CRUDStorage<DatasetMeasure> datasetStorage;
  private final DatasetPermissionManager permissionManager;
  private final ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator;

  public DataExplorerSchemaManagement(CRUDStorage<DatasetMeasure> datasetStorage,
                               DatasetPermissionManager permissionManager,
                               ChartSchemaUpdateCoordinator chartSchemaUpdateCoordinator) {
    this.datasetStorage = datasetStorage;
    this.permissionManager = permissionManager;
    this.chartSchemaUpdateCoordinator = chartSchemaUpdateCoordinator;
  }

  @Override
  public List<DatasetMeasure> getAllMeasurements() {
    return datasetStorage.findAll();
  }

  @Override
  public DatasetMeasure getById(String elementId) {
    return datasetStorage.getElementById(elementId);
  }

  /**
   * For new measurements an entry is generated in the database. For existing
   * measurements the schema is updated
   * according to the update strategy defined by the measurement.
   */
  @Override
  public DatasetMeasure createOrUpdateMeasurement(DatasetMeasure measure,
                                                   String principalSid) {

    setDefaultUpdateStrategyIfNoneProvided(measure);

    var existingMeasure = getExistingMeasureByName(measure.getMeasureName());

    if (existingMeasure.isEmpty()) {
      measure.setElementId(UUID.randomUUID().toString());
      setSchemaVersionAndStoreMeasurement(measure);
      permissionManager.makeAndPersistDatasetPermission(measure.getElementId(), principalSid);

    } else {
      handleExistingMeasurement(measure, existingMeasure.get());
    }

    return measure;
  }

  /**
   * Distinguishes between the update strategy for existing measurements
   */
  private void handleExistingMeasurement(
      DatasetMeasure measure,
      DatasetMeasure existingMeasure) {
    measure.setElementId(existingMeasure.getElementId());
    checkFieldChanges(existingMeasure.getEventSchema(), measure.getEventSchema());
    if (DatasetMeasureSchemaUpdateStrategy.UPDATE_SCHEMA.equals(measure.getSchemaUpdateStrategy())) {
      // For the update schema strategy the old schema is overwritten with the new one
      updateMeasurement(measure);
    } else {
      // For the extent existing schema strategy the old schema is merged with the new
      // one
      unifyEventSchemaAndUpdateMeasure(measure, existingMeasure);
    }
    chartSchemaUpdateCoordinator.updateCharts(Set.of(measure.getMeasureName()), measure.getEventSchema());
  }

  /**
   * Returns the existing measure that has the provided measure name
   */
  @Override
  public Optional<DatasetMeasure> getExistingMeasureByName(String measureName) {
    return datasetStorage.findAll()
        .stream()
        .filter(m -> m.getMeasureName()
            .equals(measureName))
        .findFirst();
  }

  private static void setDefaultUpdateStrategyIfNoneProvided(DatasetMeasure measure) {
    if (measure.getSchemaUpdateStrategy() == null) {
      measure.setSchemaUpdateStrategy(DatasetMeasureSchemaUpdateStrategy.UPDATE_SCHEMA);
    }
  }

  @Override
  public void deleteMeasurement(String elementId) {
    if (datasetStorage.getElementById(elementId) != null) {
      datasetStorage.deleteElementById(elementId);
    } else {
      throw new IllegalArgumentException("Could not find measure with this ID");
    }
  }

  @Override
  public boolean deleteMeasurementByName(String measureName) {
    var measureToDeleteOpt = datasetStorage.findAll()
        .stream()
        .filter(measurement -> measurement.getMeasureName()
            .equals(measureName))
        .findFirst();

    return measureToDeleteOpt.map(measure -> {
      datasetStorage.deleteElementById(measure.getElementId());
      return true;
    }).orElse(false);
  }

  @Override
  public void updateMeasurement(DatasetMeasure measure) {
    var existingMeasure = datasetStorage.getElementById(measure.getElementId());
    if (existingMeasure != null) {
      measure.setRev(existingMeasure.getRev());
      datasetStorage.updateElement(measure);
    } else {
      datasetStorage.persist(measure);
    }
  }

  private void setSchemaVersionAndStoreMeasurement(DatasetMeasure measure) {
    measure.setSchemaVersion(DatasetMeasure.CURRENT_SCHEMA_VERSION);
    datasetStorage.persist(measure);
  }

  /**
   * First the event schemas of the measurements are merged and then the measure
   * is updated in the database
   */
  private void unifyEventSchemaAndUpdateMeasure(
      DatasetMeasure measure,
      DatasetMeasure existingMeasure) {
    var properties = getUnifiedEventProperties(
        existingMeasure,
        measure);

    measure
        .getEventSchema()
        .setEventProperties(properties);

    updateMeasurement(measure);
  }

  /**
   * Returns the union of the unique event properties of the two measures.
   * They are unique by runtime name.
   */
  private List<EventProperty> getUnifiedEventProperties(
      DatasetMeasure measure1,
      DatasetMeasure measure2) {
    // Combine the event properties from both measures into a single Stream
    var allMeasurementProperties = Stream.concat(
        measure1.getEventSchema()
            .getEventProperties()
            .stream(),
        measure2.getEventSchema()
            .getEventProperties()
            .stream());

    // Filter event properties by removing duplicate runtime names
    // If there are duplicate keys, choose the first occurrence
    var unifiedEventProperties = allMeasurementProperties
        .collect(Collectors.toMap(
            EventProperty::getRuntimeName,
            Function.identity(),
            (eventProperty, eventProperty2) -> eventProperty))
        .values();
    return new ArrayList<>(unifiedEventProperties);
  }

  private void checkFieldChanges(EventSchema existingSchema, EventSchema schema) {
    var criticalFieldChanges = MeasurementChangeDetector.findCriticalMeasurementFieldChanges(
        existingSchema,
        schema
    );
    if (!criticalFieldChanges.isEmpty()) {
      throw new RuntimeException(
          "Can't save measurement with critical field changes: " + criticalFieldChanges
              .stream()
              .map(change -> "%s (%s -> %s)".formatted(
                  change.runtimeName(),
                  change.existingType(),
                  change.updatedType()
              ))
              .collect(Collectors.joining(", "))
      );
    }
  }
}
