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

import org.apache.streampipes.dataexplorer.QueryResultProvider;
import org.apache.streampipes.dataexplorer.StreamedQueryResultProvider;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDatasetMetadataManagement;
import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriterFactory;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.model.dataset.SpQueryResult;
import org.apache.streampipes.model.dataset.param.ProvidedRestQueryParams;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

public class DataExplorerQueryManagementIotDb implements IDataExplorerQueryManagement {

  private final DataExplorerIotDbQueryExecutor queryExecutor;
  private final IDatasetMetadataManagement datasetMetadataManagement;

  public DataExplorerQueryManagementIotDb(IDatasetMetadataManagement datasetMetadataManagement,
                                          DataExplorerIotDbQueryExecutor queryExecutor) {
    this.datasetMetadataManagement = datasetMetadataManagement;
    this.queryExecutor = queryExecutor;
  }

  @Override
  public SpQueryResult getData(ProvidedRestQueryParams queryParams, boolean ignoreMissingData) throws IllegalArgumentException {
    return new QueryResultProvider(queryParams, this, queryExecutor, datasetMetadataManagement,
        ignoreMissingData).getData();
  }

  @Override
  public void getDataAsStream(ProvidedRestQueryParams params,
                              OutputFormat format,
                              ConfiguredOutputWriterFactory outputWriterFactory,
                              boolean ignoreMissingValues,
                              OutputStream outputStream) throws IOException {
    new StreamedQueryResultProvider(params, format, outputWriterFactory, this, queryExecutor,
        datasetMetadataManagement, ignoreMissingValues).getDataAsStream(outputStream);
  }

  @Override
  public boolean deleteData(String measurementID) {
    var allMeasurements = this.datasetMetadataManagement.getAllMeasurements();

    var measureToDeleteOpt = allMeasurements.stream()
        .filter(measure -> measure.getMeasureName().equals(measurementID))
        .findFirst();
    return measureToDeleteOpt.filter(queryExecutor::deleteData).isPresent();
  }

  @Override
  public boolean deleteData(String measurementName, Long startDate, Long endDate) {
    queryExecutor.executeQuery(new DeleteQueryParams(measurementName, startDate, endDate));
    return true;
  }

  @Override
  public boolean deleteAllData() {
    var allMeasurements = this.datasetMetadataManagement.getAllMeasurements();

    return allMeasurements.stream()
                          .allMatch(queryExecutor::deleteData); // Check if all results are true else return false
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId, String fields) {
    return queryExecutor.getTagValues(measurementId, fields);
  }
}
