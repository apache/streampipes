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

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class DataExplorerQueryManagementIotDb implements IDataExplorerQueryManagement {

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerIotDbQueryExecutor.class);

  private final DataExplorerIotDbQueryExecutor queryExecutor;
  private final IDataExplorerSchemaManagement dataExplorerSchemaManagement;

  public DataExplorerQueryManagementIotDb(IDataExplorerSchemaManagement dataExplorerSchemaManagement,
                                          DataExplorerIotDbQueryExecutor queryExecutor) {
    this.dataExplorerSchemaManagement = dataExplorerSchemaManagement;
    this.queryExecutor = queryExecutor;
  }

  @Override
  public SpQueryResult getData(ProvidedRestQueryParams queryParams, boolean ignoreMissingData) throws IllegalArgumentException {
    return null;
  }

  @Override
  public void getDataAsStream(ProvidedRestQueryParams params, OutputFormat format, boolean ignoreMissingValues, OutputStream outputStream) throws IOException {

  }

  @Override
  public boolean deleteData(String measurementID) {
    var allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();

    var measureToDeleteOpt = allMeasurements.stream()
        .filter(measure -> measure.getMeasureName().equals(measurementID))
        .findFirst();
    return measureToDeleteOpt.filter(queryExecutor::deleteData).isPresent();
  }

  @Override
  public boolean deleteData(String measurementName, Long startDate, Long endDate) {
    var queryString = "DELETE FROM root.streampipes.%s.* WHERE time > %s AND time < %s".formatted(measurementName, startDate, endDate);
    return queryExecutor.executeNonQueryStatement(queryString);
  }

  @Override
  public boolean deleteAllData() {
    var allMeasurements = this.dataExplorerSchemaManagement.getAllMeasurements();

    return allMeasurements.stream()
                          .allMatch(queryExecutor::deleteData); // Check if all results are true else return false
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId, String fields) {
    LOG.error("Retrieval of tag values is not supported with IoTDB as storage");
    return Map.of();
  }
}
