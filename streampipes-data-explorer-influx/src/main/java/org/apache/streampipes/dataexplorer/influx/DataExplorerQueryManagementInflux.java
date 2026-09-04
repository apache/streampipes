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

import org.apache.streampipes.dataexplorer.QueryResultProvider;
import org.apache.streampipes.dataexplorer.StreamedQueryResultProvider;
import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.api.IDataExplorerSchemaManagement;
import org.apache.streampipes.dataexplorer.export.ConfiguredOutputWriterFactory;
import org.apache.streampipes.dataexplorer.export.OutputFormat;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.SpQueryStatus;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.schema.EventProperty;
import org.apache.streampipes.model.schema.EventPropertyPrimitive;


import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_END_DATE;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_LIMIT;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_MISSING_VALUE_BEHAVIOUR;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_ORDER;
import static org.apache.streampipes.model.datalake.param.SupportedRestQueryParams.QP_START_DATE;

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
                                   dataExplorerSchemaManagement,
                                   ignoreMissingData
    ).getData();
  }

  @Override
  public void getDataAsStream(ProvidedRestQueryParams params,
                              OutputFormat format,
                              ConfiguredOutputWriterFactory outputWriterFactory,
                              boolean ignoreMissingValues,
                              OutputStream outputStream) throws IOException {

    new StreamedQueryResultProvider(params, format, outputWriterFactory,
                                    this,
                                    new DataExplorerInfluxQueryExecutor(),
                                    dataExplorerSchemaManagement,
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

  @Override
  public Map<String, Long> getLatestTimestamps(List<String> measurementNames) {
    Map<String, Long> latestTimestamps = measurementNames.stream()
        .collect(Collectors.toMap(
            Function.identity(),
            measurementName -> 0L,
            (left, right) -> left
        ));
    var measurementFields = getLatestTimestampFields(measurementNames);

    if (!measurementFields.isEmpty()) {
      try {
        var batchedLatestTimestamps = new DataExplorerInfluxQueryExecutor().getLatestTimestamps(measurementFields);
        latestTimestamps.putAll(batchedLatestTimestamps);
        measurementFields.keySet()
            .stream()
            .filter(measurementName -> !batchedLatestTimestamps.containsKey(measurementName))
            .forEach(measurementName -> latestTimestamps.put(measurementName, getLatestTimestampFallback(measurementName)));
      } catch (RuntimeException e) {
        measurementFields.keySet()
            .forEach(measurementName ->
                latestTimestamps.put(measurementName, getLatestTimestampFallback(measurementName)));
      }
    }

    measurementNames.stream()
        .filter(measurementName -> !measurementFields.containsKey(measurementName))
        .forEach(measurementName -> latestTimestamps.put(measurementName, getLatestTimestampFallback(measurementName)));

    return latestTimestamps;
  }

  private Map<String, String> getLatestTimestampFields(List<String> measurementNames) {
    Map<String, String> measurementFields = new HashMap<>();
    Map<String, DataLakeMeasure> measuresByName = getAllMeasurements()
        .stream()
        .collect(Collectors.toMap(DataLakeMeasure::getMeasureName, Function.identity(), (left, right) -> left));

    measurementNames.forEach(measurementName -> findLatestTimestampField(measuresByName.get(measurementName))
        .ifPresent(field -> measurementFields.put(measurementName, field)));

    return measurementFields;
  }

  private Optional<String> findLatestTimestampField(DataLakeMeasure measure) {
    if (measure == null || measure.getEventSchema() == null || measure.getEventSchema().getEventProperties() == null) {
      return Optional.empty();
    }

    return measure.getEventSchema()
        .getEventProperties()
        .stream()
        .filter(Objects::nonNull)
        .filter(EventPropertyPrimitive.class::isInstance)
        .map(EventProperty::getRuntimeName)
        .filter(Objects::nonNull)
        .findFirst();
  }

  private Long getLatestTimestampFallback(String measurementName) {
    Map<String, String> queryParams = Map.of(
        QP_START_DATE, "0",
        QP_END_DATE, String.valueOf(System.currentTimeMillis()),
        QP_LIMIT, "1",
        QP_ORDER, "DESC",
        QP_MISSING_VALUE_BEHAVIOUR, "empty"
    );

    try {
      return getData(new ProvidedRestQueryParams(measurementName, queryParams), true).getLastTimestamp();
    } catch (RuntimeException e) {
      return 0L;
    }
  }

  private List<DataLakeMeasure> getAllMeasurements() {
    return this.dataExplorerSchemaManagement.getAllMeasurements();
  }
}
