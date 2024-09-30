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

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import java.util.List;

public interface IDataExplorerManager {

  /**
   * Provide an instance of {@link IDataLakeMeasurementCounter} for counting the sizes of measurements within a data
   * lake.
   *
   * @param allMeasurements     A list of {@link DataLakeMeasure} objects representing all measurements in the data lake.
   * @param measurementsToCount A list of measurement names for which the sizes should be counted.
   * @return An instance of {@link IDataLakeMeasurementCounter} configured to count the sizes of the specified measurements.
   */
  IDataLakeMeasurementCounter getMeasurementCounter(
      List<DataLakeMeasure> allMeasurements,
      List<String> measurementsToCount
  );

  IDataExplorerQueryManagement getQueryManagement(IDataExplorerSchemaManagement dataExplorerSchemaManagement);

  IDataExplorerSchemaManagement getSchemaManagement();

  default ITimeSeriesStorage getTimeseriesStorage(DataLakeMeasure measure) {
    return getTimeseriesStorage(measure, false);
  }

  ITimeSeriesStorage getTimeseriesStorage(DataLakeMeasure measure, boolean ignoreDuplicates);

  IDataLakeMeasurementSanitizer getMeasurementSanitizer(IStreamPipesClient client, DataLakeMeasure measure);
}
