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
import org.apache.streampipes.dataexplorer.model.Order;
import org.apache.streampipes.model.datalake.DataResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.*;

public class AutoAggregationHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AutoAggregationHandler.class);
  private static final double MAX_RETURN_LIMIT = 2000;
  private static final String TIMESTAMP_FIELD = "time";

  private final SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private final SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private final DataLakeManagementV4 dataLakeManagement;
  private final ProvidedQueryParams queryParams;

  public AutoAggregationHandler(ProvidedQueryParams params) {
    this.queryParams = params;
    this.dataLakeManagement = new DataLakeManagementV4();
  }

  public ProvidedQueryParams makeAutoAggregationQueryParams() throws IllegalArgumentException {
    checkAllArgumentsPresent();
    Integer count = getCount();
    if (count <= MAX_RETURN_LIMIT) {
      LOG.debug("Auto-Aggregation disabled as {} results <= max return limit {}", count, MAX_RETURN_LIMIT);
      return disableAutoAgg(this.queryParams);
    } else {
      LOG.debug("Performing auto-aggregation");
      try {
        int aggValue = getAggregationValue();
        LOG.debug("Setting auto-aggregation value to {} ms", aggValue);
        queryParams.update(QP_TIME_INTERVAL, aggValue + "ms");
      } catch (ParseException e) {
        e.printStackTrace();
      }
      return disableAutoAgg(queryParams);
    }
  }

  private void checkAllArgumentsPresent() throws IllegalArgumentException {
    if (!this.queryParams.has(QP_AGGREGATION_FUNCTION)) {
      throw new IllegalArgumentException("Auto-Aggregate must provide one of the aggregationFunction parameters MEAN, FIRST, LAST.");
    }
  }

  private ProvidedQueryParams disableAutoAgg(ProvidedQueryParams params) {
    params.remove(QP_AUTO_AGGREGATE);
    return params;
  }

  public Integer getCount() {
    ProvidedQueryParams countParams = disableAutoAgg(new ProvidedQueryParams(queryParams));
    countParams.remove(QP_TIME_INTERVAL);
    countParams.remove(QP_AGGREGATION_FUNCTION);
    countParams.update(QP_COUNT_ONLY, true);

    DataResult result = new DataLakeManagementV4().getData(countParams);
    return result.getTotal() > 0 ? ((Double) result.getRows().get(0).get(1)).intValue() : 0;
  }

  private DataResult fireQuery(ProvidedQueryParams params) {
    return dataLakeManagement.getData(params);
  }

  private int getAggregationValue() throws ParseException {
    long timerange = getNewestTimestamp() - getOldestTimestamp();
    double v = timerange / MAX_RETURN_LIMIT;
    return Double.valueOf(v).intValue();
  }

  private long getNewestTimestamp() throws ParseException {
    return getTimestamp(Order.DESC);
  }

  private long getOldestTimestamp() throws ParseException {
    return getTimestamp(Order.ASC);
  }

  private long getTimestamp(Order order) throws ParseException {
    ProvidedQueryParams singleEvent = disableAutoAgg(new ProvidedQueryParams(queryParams));
    singleEvent.remove(QP_AGGREGATION_FUNCTION);
    singleEvent.update(QP_LIMIT, 1);
    singleEvent.update(QP_ORDER, order.toValue());

    DataResult result = fireQuery(singleEvent);
    int timestampIndex = result.getHeaders().indexOf(TIMESTAMP_FIELD);

    return tryParseDate(result.getRows().get(0).get(timestampIndex).toString()).getTime();
  }

  private Date tryParseDate(String v) throws ParseException {
    try {
      return dateFormat1.parse(v);
    } catch (ParseException e) {
      return dateFormat2.parse(v);
    }
  }
}
