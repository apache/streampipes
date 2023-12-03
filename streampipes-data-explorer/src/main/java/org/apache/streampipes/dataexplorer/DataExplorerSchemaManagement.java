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
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataExplorerSchemaManagement implements IDataExplorerSchemaManagement {

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerSchemaManagement.class);

  IDataLakeStorage dataLakeStorage;

  public DataExplorerSchemaManagement() {}

  public DataExplorerSchemaManagement(IDataLakeStorage dataLakeStorage) {
    this.dataLakeStorage = dataLakeStorage;
  }

  @Override
  public List<DataLakeMeasure> getAllMeasurements() {
    return DataExplorerUtils.getInfos();
  }

  @Override
  public DataLakeMeasure getById(String elementId) {
    return getDataLakeStorage().findOne(elementId);
  }

  /**
   * For new measurements an entry is generated in the database. For existing measurements the schema is updated
   * according to the provided update strategy.
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
   * Destiguishes between the update straregy for existing measurments
   */
  private void handleExistingMeasurement(
      DataLakeMeasure measure,
      DataLakeMeasure existingMeasure
  ) {
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
    return dataLakeStorage.getAllDataLakeMeasures()
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
    if (getDataLakeStorage().findOne(elementId) != null) {
      getDataLakeStorage().deleteDataLakeMeasure(elementId);
    } else {
      throw new IllegalArgumentException("Could not find measure with this ID");
    }
  }

  @Override
  public boolean deleteMeasurementByName(String measureName) {
    boolean isSuccess = false;
    CouchDbClient couchDbClient = Utils.getCouchDbDataLakeClient();
    List<JsonObject> docs = couchDbClient.view("_all_docs")
                                         .includeDocs(true)
                                         .query(JsonObject.class);

    for (JsonObject document : docs) {
      if (document.get("measureName")
                  .toString()
                  .replace("\"", "")
                  .equals(measureName)) {
        couchDbClient.remove(
            document.get("_id")
                    .toString()
                    .replace("\"", ""),
            document.get("_rev")
                    .toString()
                    .replace("\"", "")
        );
        isSuccess = true;
        break;
      }
    }

    try {
      couchDbClient.close();
    } catch (IOException e) {
      LOG.error("Could not close CouchDB client", e);
    }
    return isSuccess;
  }

  @Override
  public void updateMeasurement(DataLakeMeasure measure) {
    var existingMeasure = dataLakeStorage.findOne(measure.getElementId());
    if (existingMeasure != null) {
      measure.setRev(existingMeasure.getRev());
      dataLakeStorage.updateDataLakeMeasure(measure);
    } else {
      dataLakeStorage.storeDataLakeMeasure(measure);
    }
  }

  protected IDataLakeStorage getDataLakeStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore()
                                     .getDataLakeStorage();
  }


  private void setSchemaVersionAndStoreMeasurement(DataLakeMeasure measure) {
    measure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
    dataLakeStorage.storeDataLakeMeasure(measure);
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
    return new ArrayList<>(
        Stream
            .concat(
                measure1
                    .getEventSchema()
                    .getEventProperties()
                    .stream(),
                measure2
                    .getEventSchema()
                    .getEventProperties()
                    .stream()
            )
            .collect(Collectors.toMap(
                EventProperty::getRuntimeName,
                Function.identity(),
                (eventProperty, eventProperty2) -> eventProperty
            ))
            .values());

  }
}
