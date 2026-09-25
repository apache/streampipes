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
import org.apache.streampipes.dataexplorer.api.query.DatasetAdministrationBackend;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryBackend;
import org.apache.streampipes.model.dataset.DatasetMetadata;

import java.util.List;
import java.util.function.BiConsumer;

public interface IDataExplorerManager {

  /**
   * Provide an instance of {@link IDatasetMetadataCounter} for counting the sizes of measurements within a data
   * lake.
   *
   * @param allMeasurements     A list of {@link DatasetMetadata} objects representing all measurements in the data lake.
   * @param measurementsToCount A list of measurement names for which the sizes should be counted.
   * @return An instance of {@link IDatasetMetadataCounter} configured to count the sizes of the specified measurements.
   */
  IDatasetMetadataCounter getMeasurementCounter(
      List<DatasetMetadata> allMeasurements,
      List<String> measurementsToCount,
      int daysBack
  );

  DatasetQueryBackend getQueryBackend();

  DatasetAdministrationBackend getAdministrationBackend();

  default ITimeSeriesStorage getTimeseriesStorage(DatasetMetadata measure) {
    return getTimeseriesStorage(measure, false);
  }

  ITimeSeriesStorage getTimeseriesStorage(DatasetMetadata measure, boolean ignoreDuplicates);

  /**
   * Creates storage with a reporter for recoverable warnings (title and details).
   * Implementations that do not report warnings retain their existing behavior.
   */
  default ITimeSeriesStorage getTimeseriesStorage(DatasetMetadata measure,
                                                  boolean ignoreDuplicates,
                                                  BiConsumer<String, String> warningReporter) {
    return getTimeseriesStorage(measure, ignoreDuplicates);
  }

  IDatasetMetadataSanitizer getMeasurementSanitizer(IStreamPipesClient client, DatasetMetadata measure);
}
