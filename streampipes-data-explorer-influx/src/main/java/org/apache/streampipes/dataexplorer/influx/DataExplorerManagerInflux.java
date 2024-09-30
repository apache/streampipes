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

package org.apache.streampipes.dataexplorer.influx;

import org.apache.streampipes.client.api.IStreamPipesClient;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.DataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerManager;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.api.IDataLakeMeasurementCounter;
import org.apache.streampipes.dataexplorer.api.IDataLakeMeasurementSanitizer;
import org.apache.streampipes.dataexplorer.api.ITimeSeriesStorage;
import org.apache.streampipes.dataexplorer.influx.client.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.influx.sanitize.DataLakeMeasurementSanitizerInflux;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.storage.management.StorageDispatcher;

import java.util.List;

public class DataExplorerManagerInflux implements IDataExplorerManager {

  @Override
  public IDataLakeMeasurementCounter getMeasurementCounter(
      List<DataLakeMeasure> allMeasurements,
      List<String> measurementsToCount) {
    return new DataLakeMeasurementCounterInflux(allMeasurements, measurementsToCount);
  }

  @Override
  public IDataExplorerQueryManagement getQueryManagement(
      IDataExplorerSchemaManagement dataExplorerSchemaManagement
  ) {
    return new DataExplorerQueryManagementInflux(dataExplorerSchemaManagement);
  }

  @Override
  public IDataExplorerSchemaManagement getSchemaManagement() {
    return new DataExplorerSchemaManagement(StorageDispatcher.INSTANCE
        .getNoSqlStore()
        .getDataLakeStorage());
  }

  @Override
  public ITimeSeriesStorage getTimeseriesStorage(DataLakeMeasure measure, boolean ignoreDuplicates) {
    return new TimeSeriesStorageInflux(
        measure,
        ignoreDuplicates,
        Environments.getEnvironment(),
        new InfluxClientProvider()
    );
  }

  @Override
  public IDataLakeMeasurementSanitizer getMeasurementSanitizer(IStreamPipesClient client, DataLakeMeasure measure) {
    return new DataLakeMeasurementSanitizerInflux(client, measure);
  }
}
