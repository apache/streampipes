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

import org.apache.streampipes.model.dataset.DatasetMeasure;

/**
 * The IDatasetMeasurementSanitizer interface defines methods for sanitizing and registering or
 * updating dataset measures.
 * Implementations of this interface provide functionality to ensure that the measurement complies to
 * the requirements of the underlying time series storage, e.g., to not contain any reserved symbols.
 */
public interface IDatasetMeasurementSanitizer {

  /**
   * Sanitizes and registers a dataset measure.
   * This method should perform any necessary data validation and cleanup operations
   * before registering the measure in the dataset storage.
   *
   * @return The sanitized and registered dataset measure.
   */
  DatasetMeasure sanitizeAndRegister();

  /**
   * Sanitizes and updates a dataset measure.
   * This method should perform any necessary data validation and cleanup operations
   * before updating the measure in the dataset storage.
   *
   * @return The sanitized and updated dataset measure.
   */
  DatasetMeasure sanitizeAndUpdate();

}
