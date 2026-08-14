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
import org.apache.streampipes.model.dataset.DatasetMeasure;
import org.apache.streampipes.model.schema.EventProperty;

import java.util.List;

/**
 * Base class with shared implementation that is common for all time series storage backends.
 * Leaves open the storage specific implementation
 */
public abstract class DatasetMeasurementSanitizer implements IDataLakeMeasurementSanitizer {

  protected final DatasetMeasure measure;
  protected final IStreamPipesClient client;

  public DatasetMeasurementSanitizer(IStreamPipesClient client, DatasetMeasure measure){
    this.client = client;
    this.measure = measure;
  }

  /**
   * Sanitizes the dataset measure and registers it.
   * <p>
   * This method first sanitizes the dataset measure,
   * then registers it.
   *
   * @return The sanitized and registered dataset measure.
   */
  @Override
  public DatasetMeasure sanitizeAndRegister(){
    sanitizeDataset();
    registerAtDataset();

    return measure;
  }

  /**
   * Sanitizes the dataset measure and updates it.
   * <p>
   * This method first sanitizes the dataset measure,
   * then updates it.
   *
   * @return The sanitized and updated dataset measure.
   */
  @Override
  public DatasetMeasure sanitizeAndUpdate(){
    sanitizeDataset();
    updateDataset();

    return measure;
  }



  private void registerAtDataset() throws SpRuntimeException {
    client.datasetMeasureApi().create(measure);
  }

  private void updateDataset() throws SpRuntimeException {
    client.datasetMeasureApi().update(measure);
  }

  private void sanitizeDataset() throws SpRuntimeException {
    removeTimestampsFromEventSchema();
    cleanDatasetMeasure();
  }

  /**
   * Cleans the dataset measure to ensure compliance with the requirements of the respective time series storage.
   * <p>
   * This method performs the following steps:
   * <ol>
   *   <li>Sanitizes the name of the measure.</li>
   *   <li>Sanitizes all runtime names associated with the measure.</li>
   * </ol>
   * @throws SpRuntimeException if an error occurs during the cleaning process.
   */
  protected abstract void cleanDatasetMeasure() throws SpRuntimeException;

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
