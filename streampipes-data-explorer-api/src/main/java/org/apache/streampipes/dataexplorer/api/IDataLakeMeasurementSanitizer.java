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
package org.apache.streampipes.dataexplorer.api;

import org.apache.streampipes.model.datalake.DataLakeMeasure;

/**
 * The IDataLakeMeasurementSanitizer interface defines methods for sanitizing and registering or
 * updating data lake measures.
 * Implementations of this interface provide functionality to ensure that the measurement complies to
 * the requirements of the underlying time series storage, e.g., to not contain any reserved symbols.
 */
public interface IDataLakeMeasurementSanitizer {

  /**
   * Sanitizes and registers a data lake measure.
   * This method should perform any necessary data validation and cleanup operations
   * before registering the measure in the data lake.
   *
   * @return The sanitized and registered data lake measure.
   */
  DataLakeMeasure sanitizeAndRegister();

  /**
   * Sanitizes and updates a data lake measure.
   * This method should perform any necessary data validation and cleanup operations
   * before updating the measure in the data lake.
   *
   * @return The sanitized and updated data lake measure.
   */
  DataLakeMeasure sanitizeAndUpdate();

}
