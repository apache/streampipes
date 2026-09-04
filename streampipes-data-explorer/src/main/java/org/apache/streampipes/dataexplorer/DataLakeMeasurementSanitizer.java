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

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.dataexplorer.api.IDataLakeMeasurementSanitizer;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;

/**
 * Base class with shared implementation that is common for all time series storage backends.
 * Leaves open the storage specific implementation
 */
public abstract class DataLakeMeasurementSanitizer implements IDataLakeMeasurementSanitizer {

  protected final DataLakeMeasure measure;
  protected final IStreamPipesClient client;

  public DataLakeMeasurementSanitizer(IStreamPipesClient client, DataLakeMeasure measure){
    this.client = client;
    this.measure = measure;
  }

  /**
   * Sanitizes the data lake measure and registers it with the data lake.
   * <p>
   * This method first sanitizes the data lake measure,
   * then registers it at the data lake.
   *
   * @return The sanitized and registered data lake measure.
   */
  @Override
  public DataLakeMeasure sanitizeAndRegister(){
    sanitizeDataLakeMeasure();
    registerAtDataLake();

    return measure;
  }

  /**
   * Sanitizes the data lake measure and updates it in the data lake.
   * <p>
   * This method first sanitizes the data lake measure,
   * then updates it at the data lake.
   *
   * @return The sanitized and updated data lake measure.
   */
  @Override
  public DataLakeMeasure sanitizeAndUpdate(){
    sanitizeDataLakeMeasure();
    updateAtDataLake();

    return measure;
  }



  private void registerAtDataLake() throws SpRuntimeException {
    client.dataLakeMeasureApi().create(measure);
  }

  private void updateAtDataLake() throws SpRuntimeException {
    client.dataLakeMeasureApi().update(measure);
  }

  private void sanitizeDataLakeMeasure() throws SpRuntimeException {
    removeTimestampsFromEventSchema();
    cleanDataLakeMeasure();
  }

  /**
   * Cleans the data lake measure to ensure compliance with the requirements of the respective time series storage.
   * <p>
   * This method performs the following steps:
   * <ol>
   *   <li>Sanitizes the name of the measure.</li>
   *   <li>Sanitizes all runtime names associated with the measure.</li>
   * </ol>
   * @throws SpRuntimeException if an error occurs during the cleaning process.
   */
  protected abstract void cleanDataLakeMeasure() throws SpRuntimeException;

  protected void removeTimestampsFromEventSchema() throws SpRuntimeException{
    var timestampField = measure.getTimestampField();

    if (timestampField == null){
      throw new SpRuntimeException("Data lake measurement does not have a timestamp field - timestamp field is null.");
    }

    List<EventProperty> eventPropertiesWithoutTimestamp = measure.getEventSchema()
                                                                 .getEventProperties()
                                                                 .stream()
                                                                 .filter(eventProperty -> !timestampField.endsWith(
                                                                   eventProperty.getRuntimeName()
                                                                 ))
                                                                 .toList();
    measure.getEventSchema().setEventProperties(eventPropertiesWithoutTimestamp);
  }
}
