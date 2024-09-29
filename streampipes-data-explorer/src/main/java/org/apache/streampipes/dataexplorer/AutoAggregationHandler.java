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

import org.apache.streampipes.dataexplorer.api.IDataExplorerQueryManagement;
import org.apache.streampipes.dataexplorer.param.model.SelectColumn;
import org.apache.streampipes.model.datalake.DataLakeQueryOrdering;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AutoAggregationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AutoAggregationHandler.class);
  private static final double MAX_RETURN_LIMIT = 2000;
  private static final String TIMESTAMP_FIELD = "time";
  private static final String COMMA = ",";

  // Date format for ISO 8601 timestamps with milliseconds
  private final SimpleDateFormat isoFormatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  // Date format for ISO 8601 timestamps without milliseconds
  private final SimpleDateFormat isoFormatOnlySeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private final IDataExplorerQueryManagement dataLakeQueryManagement;
  private final ProvidedRestQueryParams queryParams;

  public AutoAggregationHandler(ProvidedRestQueryParams params,
          IDataExplorerQueryManagement dataExplorerQueryManagement) {
    this.queryParams = params;
    this.dataLakeQueryManagement = dataExplorerQueryManagement;
  }

  public ProvidedRestQueryParams makeAutoAggregationQueryParams() throws IllegalArgumentException {
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
          queryParams.update(SupportedRestQueryParams.QP_TIME_INTERVAL, aggValue + "ms");
          return disableAutoAgg(queryParams);
        }
      } else {
        return disableAutoAgg(this.queryParams);
      }
    } catch (ParseException e) {
      LOG.error("Parsing of timestamp failed during auto aggregation of query parameters: {}", e.getMessage());
    }
    return null;
  }

  private ProvidedRestQueryParams disableAutoAgg(ProvidedRestQueryParams params) {
    params.remove(SupportedRestQueryParams.QP_AUTO_AGGREGATE);
    return params;
  }

  public Integer getCount(String fieldName) {
    ProvidedRestQueryParams countParams = disableAutoAgg(new ProvidedRestQueryParams(queryParams));
    countParams.remove(SupportedRestQueryParams.QP_TIME_INTERVAL);
    countParams.remove(SupportedRestQueryParams.QP_AGGREGATION_FUNCTION);
    countParams.update(SupportedRestQueryParams.QP_COUNT_ONLY, true);
    countParams.update(SupportedRestQueryParams.QP_COLUMNS, fieldName);

    SpQueryResult result = dataLakeQueryManagement.getData(countParams, true);

    return result.getTotal() > 0 ? ((Double) result.getAllDataSeries().get(0).getRows().get(0).get(1)).intValue() : 0;
  }

  private SpQueryResult fireQuery(ProvidedRestQueryParams params) {
    return dataLakeQueryManagement.getData(params, true);
  }

  private int getAggregationValue(SpQueryResult newest, SpQueryResult oldest) throws ParseException {
    long timerange = extractTimestamp(newest) - extractTimestamp(oldest);
    double v = timerange / MAX_RETURN_LIMIT;
    return Double.valueOf(v).intValue();
  }

  private SpQueryResult getSingleRecord(DataLakeQueryOrdering order) throws ParseException {
    ProvidedRestQueryParams singleEvent = disableAutoAgg(new ProvidedRestQueryParams(queryParams));
    singleEvent.remove(SupportedRestQueryParams.QP_AGGREGATION_FUNCTION);
    singleEvent.update(SupportedRestQueryParams.QP_LIMIT, 1);
    singleEvent.update(SupportedRestQueryParams.QP_ORDER, order.name());
    singleEvent.update(SupportedRestQueryParams.QP_COLUMNS,
            transformColumns(singleEvent.getAsString(SupportedRestQueryParams.QP_COLUMNS)));

    return fireQuery(singleEvent);
  }

  private String transformColumns(String rawQuery) {
    List<SelectColumn> columns = Arrays.stream(rawQuery.split(COMMA)).map(SelectColumn::fromApiQueryString).toList();
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
      return isoFormatWithMillis.parse(v);
    } catch (ParseException e) {
      return isoFormatOnlySeconds.parse(v);
    }
  }
}
