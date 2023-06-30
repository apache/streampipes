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
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.couchdb.utils.Utils;
import org.apache.streampipes.storage.management.StorageDispatcher;

import com.google.gson.JsonObject;
import org.lightcouch.CouchDbClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class DataExplorerSchemaManagement implements IDataExplorerSchemaManagement {

  @Override
  public List<DataLakeMeasure> getAllMeasurements() {
    return DataExplorerUtils.getInfos();
  }

  @Override
  public DataLakeMeasure getById(String elementId) {
    return getDataLakeStorage().findOne(elementId);
  }

  @Override
  public DataLakeMeasure createMeasurement(DataLakeMeasure measure) {
    List<DataLakeMeasure> dataLakeMeasureList = getDataLakeStorage().getAllDataLakeMeasures();
    Optional<DataLakeMeasure> optional =
        dataLakeMeasureList.stream().filter(entry -> entry.getMeasureName().equals(measure.getMeasureName()))
            .findFirst();

    if (optional.isPresent()) {
      DataLakeMeasure oldEntry = optional.get();
      if (!compareEventProperties(oldEntry.getEventSchema().getEventProperties(),
          measure.getEventSchema().getEventProperties())) {
        return oldEntry;
      }
    } else {
      measure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
      getDataLakeStorage().storeDataLakeMeasure(measure);
      return measure;
    }

    return measure;
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
    List<JsonObject> docs = couchDbClient.view("_all_docs").includeDocs(true).query(JsonObject.class);

    for (JsonObject document : docs) {
      if (document.get("measureName").toString().replace("\"", "").equals(measureName)) {
        couchDbClient.remove(document.get("_id").toString().replace("\"", ""),
            document.get("_rev").toString().replace("\"", ""));
        isSuccess = true;
        break;
      }
    }

    try {
      couchDbClient.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return isSuccess;
  }

  @Override
  public void updateMeasurement(DataLakeMeasure measure) {
    var existingMeasure = getDataLakeStorage().findOne(measure.getElementId());
    if (existingMeasure != null) {
      measure.setRev(existingMeasure.getRev());
      getDataLakeStorage().updateDataLakeMeasure(measure);
    } else {
      getDataLakeStorage().storeDataLakeMeasure(measure);
    }
  }

  private IDataLakeStorage getDataLakeStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage();
  }

  private boolean compareEventProperties(List<EventProperty> prop1, List<EventProperty> prop2) {
    if (prop1.size() != prop2.size()) {
      return false;
    }

    return prop1.stream().allMatch(prop -> {

      for (EventProperty property : prop2) {
        if (prop.getRuntimeName().equals(property.getRuntimeName())) {

          //primitive
          if (prop instanceof EventPropertyPrimitive && property instanceof EventPropertyPrimitive) {
            if (((EventPropertyPrimitive) prop)
                .getRuntimeType()
                .equals(((EventPropertyPrimitive) property).getRuntimeType())) {
              return true;
            }

            //list
          } else if (prop instanceof EventPropertyList && property instanceof EventPropertyList) {
            return compareEventProperties(Collections.singletonList(((EventPropertyList) prop).getEventProperty()),
                Collections.singletonList(((EventPropertyList) property).getEventProperty()));

            //nested
          } else if (prop instanceof EventPropertyNested && property instanceof EventPropertyNested) {
            return compareEventProperties(((EventPropertyNested) prop).getEventProperties(),
                ((EventPropertyNested) property).getEventProperties());
          }
        }
      }
      return false;

    });
  }
}
