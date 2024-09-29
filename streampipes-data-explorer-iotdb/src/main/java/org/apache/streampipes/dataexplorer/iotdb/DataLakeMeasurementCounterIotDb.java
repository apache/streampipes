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
package org.apache.streampipes.dataexplorer.iotdb;

import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.query.DataLakeMeasurementCounter;
import org.apache.streampipes.model.datalake.DataLakeMeasure;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.iotdb.rpc.IoTDBConnectionException;
import org.apache.iotdb.rpc.StatementExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataLakeMeasurementCounterIotDb extends DataLakeMeasurementCounter {

  private static final Logger LOG = LoggerFactory.getLogger(DataLakeMeasurementCounterIotDb.class);

  public DataLakeMeasurementCounterIotDb(List<DataLakeMeasure> allMeasurements, List<String> measurementNames) {
    super(allMeasurements, measurementNames);
  }

  /**
   * Creates a CompletableFuture to execute a count query on a DataLakeMeasure asynchronously.
   *
   * @param measure
   *          The DataLakeMeasure object representing the measure to query.
   * @return A CompletableFuture<Integer> representing the count query result as a future.
   */
  @Override
  protected CompletableFuture<Integer> createQueryAsAsyncFuture(DataLakeMeasure measure) {
    var sessionPool = new IotDbSessionProvider().getSessionPool(Environments.getEnvironment());
    return CompletableFuture.supplyAsync(() -> {

      // We want to apply the count query to only one property of the measurement, as this is sufficient and
      // significantly reduces the complexity of the query compared to counting all available properties.
      // So we can just take the first measurement
      var propertyName = getFirstMeasurementProperty(measure);

      try (var result = new DataExplorerIotDbQueryExecutor(sessionPool).executeQuery(
              "Select count(%s) from root.streampipes.%s".formatted(propertyName, measure.getMeasureName()))) {
        var resultRecord = result.next();
        if (resultRecord == null) {
          LOG.error("Result of count query does not contain any row - empty result");
          return 0;
        }
        return (int) resultRecord.getFields().get(0).getLongV();
      } catch (IoTDBConnectionException | StatementExecutionException e) {
        LOG.error("Error during count query execution: {}", e.getMessage());
        return 0;
      }
    });
  }
}
