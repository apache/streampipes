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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.influx.DataExplorerInfluxQueryExecutor;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParams;
import org.apache.streampipes.dataexplorer.query.DeleteDataQuery;
import org.apache.streampipes.dataexplorer.query.QueryResultProvider;
import org.apache.streampipes.dataexplorer.query.StreamedQueryResultProvider;
import org.apache.streampipes.dataexplorer.query.writer.OutputFormat;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.SpQueryResult;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataExplorerQueryManagement implements IDataExplorerQueryManagement {

  private final IDataExplorerSchemaManagement dataExplorerSchemaManagement;

  public DataExplorerQueryManagement(IDataExplorerSchemaManagement dataExplorerSchemaManagement) {
    this.dataExplorerSchemaManagement = dataExplorerSchemaManagement;
  }

  @Override
  public SpQueryResult getData(ProvidedRestQueryParams queryParams,
                               boolean ignoreMissingData) throws IllegalArgumentException {
    return new QueryResultProvider(queryParams, ignoreMissingData).getData();
  }

  @Override
  public void getDataAsStream(ProvidedRestQueryParams params,
                              OutputFormat format,
                              boolean ignoreMissingValues,
                              OutputStream outputStream) throws IOException {

    new StreamedQueryResultProvider(params, format, ignoreMissingValues).getDataAsStream(outputStream);
  }

  @Override
  public boolean deleteAllData() {
    List<DataLakeMeasure> allMeasurements = getAllMeasurements();

    for (DataLakeMeasure measure : allMeasurements) {
      QueryResult queryResult = new DeleteDataQuery(measure).executeQuery();
      if (queryResult.hasError() || queryResult.getResults().get(0).getError() != null) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean deleteData(String measurementID) {
    List<DataLakeMeasure> allMeasurements = getAllMeasurements();
    for (DataLakeMeasure measure : allMeasurements) {
      if (measure.getMeasureName().equals(measurementID)) {
        QueryResult queryResult = new DeleteDataQuery(new DataLakeMeasure(measurementID, null)).executeQuery();

        return !queryResult.hasError();
      }
    }
    return false;
  }

  @Override
  public SpQueryResult deleteData(String measurementID, Long startDate, Long endDate) {
    DeleteQueryParams params =
        ProvidedRestQueryParamConverter.getDeleteQueryParams(measurementID, startDate, endDate);
    return new DataExplorerInfluxQueryExecutor().executeQuery(params);
  }

  @Override
  public Map<String, Object> getTagValues(String measurementId,
                                          String fields) {
    InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient();
    String databaseName = getEnvironment().getTsStorageBucket().getValueOrDefault();
    Map<String, Object> tags = new HashMap<>();
    if (fields != null && !("".equals(fields))) {
      List<String> fieldList = Arrays.asList(fields.split(","));
      fieldList.forEach(f -> {
        String q =
            "SHOW TAG VALUES ON \"" + databaseName + "\" FROM \"" + measurementId
                + "\" WITH KEY = \"" + f + "\"";
        Query query = new Query(q);
        QueryResult queryResult = influxDB.query(query);
        queryResult.getResults().forEach(res -> {
          res.getSeries().forEach(series -> {
            if (series.getValues().size() > 0) {
              String field = series.getValues().get(0).get(0).toString();
              List<String> values =
                  series.getValues().stream().map(v -> v.get(1).toString()).collect(Collectors.toList());
              tags.put(field, values);
            }
          });
        });
      });
    }

    return tags;
  }

  private List<DataLakeMeasure> getAllMeasurements() {
    return this.dataExplorerSchemaManagement.getAllMeasurements();
  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }
}
