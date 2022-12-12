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
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyList;
import org.apache.streampipes.model.schema.EventPropertyNested;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;
import org.apache.streampipes.model.schema.EventSchema;
import org.apache.streampipes.storage.api.IDataLakeStorage;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@Deprecated
public class DataLakeNoUserManagementV3 {


  @Deprecated
  public boolean addDataLake(String measure, EventSchema eventSchema) {
    List<DataLakeMeasure> dataLakeMeasureList = getDataLakeStorage().getAllDataLakeMeasures();
    Optional<DataLakeMeasure> optional =
        dataLakeMeasureList.stream().filter(entry -> entry.getMeasureName().equals(measure)).findFirst();

    if (optional.isPresent()) {
      if (!compareEventProperties(optional.get().getEventSchema().getEventProperties(),
          eventSchema.getEventProperties())) {
        return false;
      }
    } else {
      DataLakeMeasure dataLakeMeasure = new DataLakeMeasure(measure, eventSchema);
      dataLakeMeasure.setSchemaVersion(DataLakeMeasure.CURRENT_SCHEMA_VERSION);
      getDataLakeStorage().storeDataLakeMeasure(dataLakeMeasure);
    }
    return true;
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

  private IDataLakeStorage getDataLakeStorage() {
    return StorageDispatcher.INSTANCE.getNoSqlStore().getDataLakeStorage();
  }
}
