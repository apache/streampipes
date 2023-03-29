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
package org.apache.streampipes.dataexplorer.v4;

import org.apache.streampipes.dataexplorer.DataLakeManagementV4;
import org.apache.streampipes.dataexplorer.sdk.DataLakeQueryOrdering;
import org.apache.streampipes.dataexplorer.v4.params.SelectColumn;
import org.apache.streampipes.model.datalake.SpQueryResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_AGGREGATION_FUNCTION;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_AUTO_AGGREGATE;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_COLUMNS;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_COUNT_ONLY;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_LIMIT;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_ORDER;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_TIME_INTERVAL;

public class AutoAggregationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AutoAggregationHandler.class);
  private static final double MAX_RETURN_LIMIT = 2000;
  private static final String TIMESTAMP_FIELD = "time";
  private static final String COMMA = ",";

  private final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private final DataLakeManagementV4 dataLakeManagement;
  private final ProvidedQueryParams queryParams;

  public AutoAggregationHandler(ProvidedQueryParams params) {
    this.queryParams = params;
    this.dataLakeManagement = new DataLakeManagementV4();
  }

  public ProvidedQueryParams makeAutoAggregationQueryParams() throws IllegalArgumentException {
    try {
      SpQueryResult newest = getSingleRecord(DataLakeQueryOrdering.DESC);
      SpQueryResult oldest = getSingleRecord(DataLakeQueryOrdering.ASC);
      if (newest.getTotal() > 0) {
        String sampleField = getSampleField(newest);
        Integer count = getCount(sampleField);
        if (count <= MAX_RETURN_LIMIT) {
          LOG.debug("Auto-Aggregation disabled as {} results <= max return limit {}", count, MAX_RETURN_LIMIT);
          return disableAutoAgg(this.queryParams);
        } else {
          LOG.debug("Performing auto-aggregation");

          int aggValue = getAggregationValue(newest, oldest);
          LOG.debug("Setting auto-aggregation value to {} ms", aggValue);
          queryParams.update(QP_TIME_INTERVAL, aggValue + "ms");
          return disableAutoAgg(queryParams);
        }
      } else {
        return disableAutoAgg(this.queryParams);
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return null;
  }

  private ProvidedQueryParams disableAutoAgg(ProvidedQueryParams params) {
    params.remove(QP_AUTO_AGGREGATE);
    return params;
  }

  public Integer getCount(String fieldName) {
    ProvidedQueryParams countParams = disableAutoAgg(new ProvidedQueryParams(queryParams));
    countParams.remove(QP_TIME_INTERVAL);
    countParams.remove(QP_AGGREGATION_FUNCTION);
    countParams.update(QP_COUNT_ONLY, true);
    countParams.update(QP_COLUMNS, fieldName);

    SpQueryResult result = new DataLakeManagementV4().getData(countParams, true);

    return result.getTotal() > 0 ? ((Double) result.getAllDataSeries().get(0).getRows().get(0).get(1)).intValue() : 0;
  }

  private SpQueryResult fireQuery(ProvidedQueryParams params) {
    return dataLakeManagement.getData(params, true);
  }

  private int getAggregationValue(SpQueryResult newest, SpQueryResult oldest) throws ParseException {
    long timerange = extractTimestamp(newest) - extractTimestamp(oldest);
    double v = timerange / MAX_RETURN_LIMIT;
    return Double.valueOf(v).intValue();
  }

  private SpQueryResult getSingleRecord(DataLakeQueryOrdering order) throws ParseException {
    ProvidedQueryParams singleEvent = disableAutoAgg(new ProvidedQueryParams(queryParams));
    singleEvent.remove(QP_AGGREGATION_FUNCTION);
    singleEvent.update(QP_LIMIT, 1);
    singleEvent.update(QP_ORDER, order.name());
    singleEvent.update(QP_COLUMNS, transformColumns(singleEvent.getAsString(QP_COLUMNS)));

    return fireQuery(singleEvent);
  }

  private String transformColumns(String rawQuery) {
    List<SelectColumn> columns =
        Arrays.stream(rawQuery.split(COMMA)).map(SelectColumn::fromApiQueryString).collect(Collectors.toList());
    return columns.stream().map(SelectColumn::getOriginalField).collect(Collectors.joining(COMMA));
  }

  private String getSampleField(SpQueryResult result) {
    for (String column : result.getHeaders()) {
      if (!column.equals(TIMESTAMP_FIELD)) {
        return column;
      }
    }
    throw new IllegalArgumentException("No columns present");
  }

  private long extractTimestamp(SpQueryResult result) throws ParseException {
    int timestampIndex = result.getHeaders().indexOf(TIMESTAMP_FIELD);
    return tryParseDate(result.getAllDataSeries().get(0).getRows().get(0).get(timestampIndex).toString()).getTime();
  }

  private Date tryParseDate(String v) throws ParseException {
    try {
      return dateFormat1.parse(v);
    } catch (ParseException e) {
      return dateFormat2.parse(v);
    }
  }
}
