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
package org.apache.streampipes.dataexplorer.commons;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxNameSanitizer;
import org.apache.streampipes.dataexplorer.commons.sanitizer.MeasureNameSanitizer;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;
import java.util.stream.Collectors;

public class DataExplorerUtils {
  /**
   * Sanitizes the event schema and stores the DataLakeMeasurement to the couchDB
   *
   * @param client  StreamPipes client to store measure
   * @param measure DataLakeMeasurement
   */
  public static DataLakeMeasure sanitizeAndRegisterAtDataLake(IStreamPipesClient client,
                                                              DataLakeMeasure measure) throws SpRuntimeException {
    sanitizeDataLakeMeasure(measure);
    registerAtDataLake(client, measure);

    return measure;
  }

  public static DataLakeMeasure sanitizeAndUpdateAtDataLake(IStreamPipesClient client,
                                                            DataLakeMeasure measure) throws SpRuntimeException {
    sanitizeDataLakeMeasure(measure);
    updateAtDataLake(client, measure);
    return measure;
  }

  private static void registerAtDataLake(IStreamPipesClient client,
                                         DataLakeMeasure measure) throws SpRuntimeException {
    client.dataLakeMeasureApi().create(measure);
  }

  public static void updateAtDataLake(IStreamPipesClient client,
                                      DataLakeMeasure measure) throws SpRuntimeException {
    client.dataLakeMeasureApi().update(measure);
  }


  private static void sanitizeDataLakeMeasure(DataLakeMeasure measure) throws SpRuntimeException {

    // Removes selected timestamp from event schema
    removeTimestampsFromEventSchema(measure);

    // Sanitize the data lake measure name
    measure.setMeasureName(new MeasureNameSanitizer().sanitize(measure.getMeasureName()));

    // Removes all spaces with _ and validates that no special terms are used as runtime names
    measure.getEventSchema()
        .getEventProperties()
        .forEach(ep -> ep.setRuntimeName(InfluxNameSanitizer.renameReservedKeywords(ep.getRuntimeName())));

  }

  private static void removeTimestampsFromEventSchema(DataLakeMeasure measure) {
    List<EventProperty> eventPropertiesWithoutTimestamp = measure.getEventSchema().getEventProperties()
        .stream()
        .filter(eventProperty -> !measure.getTimestampField().endsWith(eventProperty.getRuntimeName()))
        .collect(Collectors.toList());
    measure.getEventSchema().setEventProperties(eventPropertiesWithoutTimestamp);
  }

}
