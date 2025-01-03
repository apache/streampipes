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
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataLakeMeasureSchemaUpdateStrategy;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.storage.api.CRUDStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataExplorerSchemaManagement implements IDataExplorerSchemaManagement {

  CRUDStorage<DataLakeMeasure> dataLakeStorage;

  public DataExplorerSchemaManagement(CRUDStorage<DataLakeMeasure> dataLakeStorage) {
    this.dataLakeStorage = dataLakeStorage;
  }

  @Override
  public List<DataLakeMeasure> getAllMeasurements() {
    return DataExplorerUtils.getInfos();
  }

  @Override
  public DataLakeMeasure getById(String elementId) {
    return dataLakeStorage.getElementById(elementId);
  }

  @Override
  public DataLakeMeasure getByMeasureName(String measureName) {
    return this.getExistingMeasureByName(measureName).orElse(null);
  }

  /**
   * For new measurements an entry is generated in the database. For existing measurements the schema is updated
   * according to the update strategy defined by the measurement.
   */
  @Override
  public DataLakeMeasure createOrUpdateMeasurement(DataLakeMeasure measure) {

    setDefaultUpdateStrategyIfNoneProvided(measure);

    var existingMeasure = getExistingMeasureByName(measure.getMeasureName());

    if (existingMeasure.isEmpty()) {
      setSchemaVersionAndStoreMeasurement(measure);
    } else {
      handleExistingMeasurement(measure, existingMeasure.get());
    }

    return measure;
  }

  /**
   * Distinguishes between the update strategy for existing measurements
   */
  private void handleExistingMeasurement(
      DataLakeMeasure measure,
      DataLakeMeasure existingMeasure
  ) {
    measure.setElementId(existingMeasure.getElementId());
    if (DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA.equals(measure.getSchemaUpdateStrategy())) {
      // For the update schema strategy the old schema is overwritten with the new one
      updateMeasurement(measure);
    } else {
      // For the extent existing schema strategy the old schema is merged with the new one
      unifyEventSchemaAndUpdateMeasure(measure, existingMeasure);
    }
  }


  /**
   * Returns the existing measure that has the provided measure name
   */
  private Optional<DataLakeMeasure> getExistingMeasureByName(String measureName) {
    return dataLakeStorage.findAll()
                          .stream()
                          .filter(m -> m.getMeasureName()
                                        .equals(measureName))
                          .findFirst();
  }

  private static void setDefaultUpdateStrategyIfNoneProvided(DataLakeMeasure measure) {
    if (measure.getSchemaUpdateStrategy() == null) {
      measure.setSchemaUpdateStrategy(DataLakeMeasureSchemaUpdateStrategy.UPDATE_SCHEMA);
    }
  }

  @Override
  public void deleteMeasurement(String elementId) {
    if (dataLakeStorage.getElementById(elementId) != null) {
      dataLakeStorage.deleteElementById(elementId);
    } else {
      throw new IllegalArgumentException("Could not find measure with this ID");
    }
  }

  @Override
  public boolean deleteMeasurementByName(String measureName) {
    var measureToDeleteOpt = dataLakeStorage.findAll()
                                            .stream()
                                            .filter(measurement -> measurement.getMeasureName()
                                                                               .equals(measureName))
                                            .findFirst();

    return measureToDeleteOpt.map(measure -> {
      dataLakeStorage.deleteElementById(measure.getElementId());
      return true;
    }
    ).orElse(false);
  }

  @Override
  public void updateMeasurement(DataLakeMeasure measure) {
    var existingMeasure = dataLakeStorage.getElementById(measure.getElementId());
    if (existingMeasure != null) {
      measure.setRev(existingMeasure.getRev());
      dataLakeStorage.updateElement(measure);
    } else {
      dataLakeStorage.persist(measure);
    }
  }

  private void setSchemaVersionAndStoreMeasurement(DataLakeMeasure measure) {
    measure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
    dataLakeStorage.persist(measure);
  }

  /**
   * First the event schemas of the measurements are merged and then the measure is updated in the database
   */
  private void unifyEventSchemaAndUpdateMeasure(
      DataLakeMeasure measure,
      DataLakeMeasure existingMeasure
  ) {
    var properties = getUnifiedEventProperties(
        existingMeasure,
        measure
    );

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
      DataLakeMeasure measure1,
      DataLakeMeasure measure2
  ) {
// Combine the event properties from both measures into a single Stream
    var allMeasurementProperties = Stream.concat(
        measure1.getEventSchema()
                .getEventProperties()
                .stream(),
        measure2.getEventSchema()
                .getEventProperties()
                .stream()
    );

    // Filter event properties by removing duplicate runtime names
    // If there are duplicate keys, choose the first occurrence
    var unifiedEventProperties = allMeasurementProperties
        .collect(Collectors.toMap(
            EventProperty::getRuntimeName,
            Function.identity(),
            (eventProperty, eventProperty2) -> eventProperty
        ))
        .values();
    return new ArrayList<>(unifiedEventProperties);
  }
}
