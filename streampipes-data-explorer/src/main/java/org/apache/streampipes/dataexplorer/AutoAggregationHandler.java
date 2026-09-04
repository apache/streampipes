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
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class AutoAggregationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AutoAggregationHandler.class);
  private static final int DEFAULT_MAX_RETURN_LIMIT = 2000;
  private static final String TIMESTAMP_FIELD = "time";
  private static final String COMMA = ",";

  // Date format for ISO 8601 timestamps with milliseconds
  private final SimpleDateFormat isoFormatWithMillis = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  // Date format for ISO 8601 timestamps without milliseconds
  private final SimpleDateFormat isoFormatOnlySeconds = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private final IDataExplorerQueryManagement dataLakeQueryManagement;
  private final ProvidedRestQueryParams queryParams;
  private final boolean ignoreMissingData;

  public AutoAggregationHandler(ProvidedRestQueryParams params,
                                IDataExplorerQueryManagement dataExplorerQueryManagement,
                                boolean ignoreMissingData) {
    this.queryParams = params;
    this.dataLakeQueryManagement = dataExplorerQueryManagement;
    this.ignoreMissingData = ignoreMissingData;
  }

  public ProvidedRestQueryParams makeAutoAggregationQueryParams() throws IllegalArgumentException {
    ProvidedRestQueryParams rewrittenParams = disableAutoAgg(new ProvidedRestQueryParams(queryParams));
    if (queryHasExplicitTimeAggregation() || !hasColumns()) {
      return rewrittenParams;
    }

    try {
      QueryBoundary boundary = getQueryBoundary();
      if (!boundary.hasData()) {
        return rewrittenParams;
      }

      if (!usesAggregation()) {
        return rewrittenParams;
      }

      int maxRows = getMaxReturnLimit();
      int rawRowCount = getRawRowCountUpTo(maxRows + 1);
      long interval = rawRowCount <= maxRows
          ? 1L
          : getAggregationValue(boundary.newest(), boundary.oldest(), maxRows);

      LOG.debug("Auto-aggregation selected time interval {} ms for max {} rows", interval, maxRows);
      rewrittenParams.update(SupportedRestQueryParams.QP_TIME_INTERVAL, interval + "ms");
      return rewrittenParams;
    } catch (ParseException e) {
      LOG.error("Parsing of timestamp failed during auto aggregation of query parameters: {}", e.getMessage());
      return rewrittenParams;
    }
  }

  private ProvidedRestQueryParams disableAutoAgg(ProvidedRestQueryParams params) {
    params.remove(SupportedRestQueryParams.QP_AUTO_AGGREGATE);
    return params;
  }

  private SpQueryResult fireQuery(ProvidedRestQueryParams params) {
    return dataLakeQueryManagement.getData(params, ignoreMissingData);
  }

  private long getAggregationValue(SpQueryResult newest,
                                   SpQueryResult oldest,
                                   int maxRows) throws ParseException {
    long timeRange = Math.max(0, extractTimestamp(newest) - extractTimestamp(oldest));
    long coveredRange = timeRange + 1;
    return Math.max(1L, ceilDiv(coveredRange, maxRows));
  }

  private QueryBoundary getQueryBoundary() {
    ProvidedRestQueryParams boundaryQuery = makeRawHelperQueryParams();
    boundaryQuery.update(SupportedRestQueryParams.QP_LIMIT, 1);
    SpQueryResult boundaryResult = fireQuery(boundaryQuery);
    if (boundaryResult.getTotal() == 0) {
      return QueryBoundary.empty();
    }

    ProvidedRestQueryParams newestQuery = makeRawHelperQueryParams();
    newestQuery.update(SupportedRestQueryParams.QP_LIMIT, 1);
    newestQuery.update(SupportedRestQueryParams.QP_ORDER, "DESC");
    return new QueryBoundary(boundaryResult, fireQuery(newestQuery));
  }

  private int getRawRowCountUpTo(int maxRows) {
    ProvidedRestQueryParams sampleQuery = makeRawHelperQueryParams();
    sampleQuery.update(SupportedRestQueryParams.QP_LIMIT, maxRows);
    return fireQuery(sampleQuery).getTotal();
  }

  private List<SelectColumn> getSelectedColumns() {
    return Arrays.stream(queryParams.getAsString(SupportedRestQueryParams.QP_COLUMNS).split(COMMA))
                 .map(String::trim)
                 .map(SelectColumn::fromApiQueryString)
                 .toList();
  }

  private boolean usesAggregation() {
    if (queryParams.has(SupportedRestQueryParams.QP_AGGREGATION_FUNCTION)) {
      return true;
    }

    return getSelectedColumns().stream().anyMatch(SelectColumn::isAggregated);
  }

  private boolean hasColumns() {
    return queryParams.has(SupportedRestQueryParams.QP_COLUMNS)
        && queryParams.getAsString(SupportedRestQueryParams.QP_COLUMNS) != null
        && !queryParams.getAsString(SupportedRestQueryParams.QP_COLUMNS).isBlank();
  }

  private boolean queryHasExplicitTimeAggregation() {
    return queryParams.has(SupportedRestQueryParams.QP_TIME_INTERVAL);
  }

  private int getMaxReturnLimit() {
    Integer providedLimit = queryParams.getAsInt(SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS);
    if (providedLimit == null || providedLimit <= 0) {
      return DEFAULT_MAX_RETURN_LIMIT;
    }
    return providedLimit;
  }

  private String transformColumnsToRawSelection(String rawQuery) {
    return Arrays.stream(rawQuery.split(COMMA))
                 .map(String::trim)
                 .map(SelectColumn::fromApiQueryString)
                 .map(SelectColumn::getOriginalField)
                 .collect(Collectors.joining(COMMA));
  }

  private long extractTimestamp(SpQueryResult result) throws ParseException {
    int timestampIndex = result.getHeaders()
                               .indexOf(TIMESTAMP_FIELD);
    Object timestampValue = result.getAllDataSeries()
                                  .get(0)
                                  .getRows()
                                  .get(0)
                                  .get(timestampIndex);

    if (timestampValue instanceof Number timestampNumber) {
      return timestampNumber.longValue();
    }

    String timestampString = timestampValue.toString();
    try {
      return Long.parseLong(timestampString);
    } catch (NumberFormatException e) {
      return tryParseDate(timestampString).getTime();
    }
  }

  private Date tryParseDate(String v) throws ParseException {
    try {
      return isoFormatWithMillis.parse(v);
    } catch (ParseException e) {
      return isoFormatOnlySeconds.parse(v);
    }
  }

  private ProvidedRestQueryParams makeBaseHelperQueryParams() {
    ProvidedRestQueryParams helperParams = disableAutoAgg(new ProvidedRestQueryParams(queryParams));
    helperParams.remove(SupportedRestQueryParams.QP_TIME_INTERVAL);
    helperParams.remove(SupportedRestQueryParams.QP_AGGREGATION_FUNCTION);
    helperParams.remove(SupportedRestQueryParams.QP_GROUP_BY);
    helperParams.remove(SupportedRestQueryParams.QP_LIMIT);
    helperParams.remove(SupportedRestQueryParams.QP_OFFSET);
    helperParams.remove(SupportedRestQueryParams.QP_PAGE);
    helperParams.remove(SupportedRestQueryParams.QP_ORDER);
    helperParams.remove(SupportedRestQueryParams.QP_COUNT_ONLY);
    helperParams.remove(SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS);
    return helperParams;
  }

  private ProvidedRestQueryParams makeRawHelperQueryParams() {
    ProvidedRestQueryParams helperParams = makeBaseHelperQueryParams();
    helperParams.update(SupportedRestQueryParams.QP_COLUMNS,
        transformColumnsToRawSelection(helperParams.getAsString(SupportedRestQueryParams.QP_COLUMNS)));
    return helperParams;
  }

  private long ceilDiv(long dividend, long divisor) {
    return (dividend + divisor - 1) / divisor;
  }

  private record QueryBoundary(SpQueryResult oldest, SpQueryResult newest) {
    private static QueryBoundary empty() {
      return new QueryBoundary(new SpQueryResult(), new SpQueryResult());
    }

    private boolean hasData() {
      return oldest.getTotal() > 0 && newest.getTotal() > 0;
    }
  }
}
