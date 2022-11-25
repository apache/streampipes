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

package org.apache.streampipes.dataexplorer.v4.query;

import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.dataexplorer.v4.ProvidedQueryParams;
import org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters;
import org.apache.streampipes.dataexplorer.v4.query.writer.ConfiguredOutputWriter;
import org.apache.streampipes.dataexplorer.v4.query.writer.OutputFormat;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.SpQueryResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_LIMIT;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_PAGE;

public class StreamedQueryResultProvider extends QueryResultProvider {

  private static final int MAX_RESULTS_PER_QUERY = 500000;
  private static final String TIME_FIELD = "time";

  private final OutputFormat format;

  public StreamedQueryResultProvider(ProvidedQueryParams params,
                                     OutputFormat format,
                                     boolean ignoreMissingValues) {
    super(params, ignoreMissingValues);
    this.format = format;
  }

  public void getDataAsStream(OutputStream outputStream) throws IOException {
    var usesLimit = queryParams.has(QP_LIMIT);
    var configuredWriter = ConfiguredOutputWriter
        .getConfiguredWriter(format, queryParams, ignoreMissingData);

    if (!queryParams.has(QP_LIMIT)) {
      queryParams.update(QP_LIMIT, MAX_RESULTS_PER_QUERY);
    }

    var limit = queryParams.getAsInt(QP_LIMIT);
    var measurement = findByMeasurementName(queryParams.getMeasurementId()).get();

    SpQueryResult dataResult;
    int page = 0;
    if (queryParams.has(QP_PAGE)) {
      page = queryParams.getAsInt(QP_PAGE);
    }

    boolean isFirstDataItem = true;
    configuredWriter.beforeFirstItem(outputStream);
    do {
      queryParams.update(SupportedDataLakeQueryParameters.QP_PAGE, String.valueOf(page));
      dataResult = getData();

      if (dataResult.getTotal() > 0) {
        changeTimestampHeader(measurement, dataResult);
        var columns = dataResult.getHeaders();
        for (List<Object> row : dataResult.getAllDataSeries().get(0).getRows()) {
          configuredWriter.writeItem(outputStream, row, columns, isFirstDataItem);
          isFirstDataItem = false;
        }
      }
      page++;
    } while (queryNextPage(dataResult.getTotal(), usesLimit, limit, page));
    configuredWriter.afterLastItem(outputStream);
  }

  private boolean queryNextPage(int lastResultsCount,
                                boolean usesLimit,
                                int limit,
                                int lastPage) {
    if (usesLimit) {
      return !(limit <= (lastPage) * MAX_RESULTS_PER_QUERY);
    } else {
      return lastResultsCount > 0;
    }
  }

  private Optional<DataLakeMeasure> findByMeasurementName(String measurementName) {
    return DataExplorerUtils.getInfos()
        .stream()
        .filter(measurement -> measurement.getMeasureName().equals(measurementName))
        .findFirst();
  }

  /**
   * Replaces the field 'time' of the data result with the actual timestamp field name of the measurement
   * @param measurement contains the actual timestamp name value
   * @param dataResult the query result of the database with 'time' as timestamp field name
   */
  private void changeTimestampHeader(DataLakeMeasure measurement,
                                     SpQueryResult dataResult) {
    var timeFieldIndex = dataResult.getHeaders().indexOf(TIME_FIELD);
    if (timeFieldIndex > -1) {
      dataResult.getHeaders().set(timeFieldIndex, measurement.getTimestampFieldName());
    }
  }
}
