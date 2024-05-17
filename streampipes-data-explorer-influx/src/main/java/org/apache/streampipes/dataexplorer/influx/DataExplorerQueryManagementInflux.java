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

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.dataexplorer.QueryResultProvider;
import org.apache.streampipes.dataexplorer.StreamedQueryResultProvider;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.model.datalake.SpQueryStatus;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.SpQueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class DataExplorerQueryManagementInflux implements IDataExplorerQueryManagement {

  private final IDataExplorerSchemaManagement dataExplorerSchemaManagement;

  public DataExplorerQueryManagementInflux(IDataExplorerSchemaManagement dataExplorerSchemaManagement) {
    this.dataExplorerSchemaManagement = dataExplorerSchemaManagement;
  }

  @Override
  public SpQueryResult getData(ProvidedRestQueryParams queryParams,
                               boolean ignoreMissingData) throws IllegalArgumentException {
    return new QueryResultProvider(queryParams,
                                   this,
                                   new DataExplorerInfluxQueryExecutor(),
                                   ignoreMissingData
    ).getData();
  }

  @Override
  public void getDataAsStream(ProvidedRestQueryParams params,
                              OutputFormat format,
                              boolean ignoreMissingValues,
                              OutputStream outputStream) throws IOException {

    new StreamedQueryResultProvider(params, format,
                                    this,
                                    new DataExplorerInfluxQueryExecutor(),
                                    ignoreMissingValues
    ).getDataAsStream(outputStream);
  }

  @Override
  public boolean deleteAllData() {
    List<DataLakeMeasure> allMeasurements = getAllMeasurements();
    var queryExecutor = new DataExplorerInfluxQueryExecutor();

    for (DataLakeMeasure measure : allMeasurements) {
      boolean success = queryExecutor.deleteData(measure);
      if (!success) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean deleteData(String measurementID) {
    List<DataLakeMeasure> allMeasurements = getAllMeasurements();

    var measureToDeleteOpt = allMeasurements.stream()
                                            .filter(measure -> measure.getMeasureName().equals(measurementID))
                                            .findFirst();

    return measureToDeleteOpt.filter(measure -> new DataExplorerInfluxQueryExecutor().deleteData(measure))
                             .isPresent();
  }

  @Override
  public boolean deleteData(String measurementName, Long startDate, Long endDate) {
    DeleteQueryParams params =
        ProvidedRestQueryParamConverter.getDeleteQueryParams(measurementName, startDate, endDate);
    return new DataExplorerInfluxQueryExecutor().executeQuery(params)
                                                .getSpQueryStatus()
                                                .equals(SpQueryStatus.OK);
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId,
                                          String fields) {
    return new DataExplorerInfluxQueryExecutor().getTagValues(measurementId, fields);
  }

  private List<DataLakeMeasure> getAllMeasurements() {
    return this.dataExplorerSchemaManagement.getAllMeasurements();
  }
}
