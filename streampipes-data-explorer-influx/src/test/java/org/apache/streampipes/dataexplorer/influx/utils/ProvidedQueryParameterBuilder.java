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
package org.apache.streampipes.dataexplorer.influx.utils;

import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProvidedQueryParameterBuilder {

  private final String measurementId;
  private final Map<String, String> queryParams = new HashMap<>();

  public static ProvidedQueryParameterBuilder create(String measurementId) {
    return new ProvidedQueryParameterBuilder(measurementId);
  }

  public ProvidedQueryParameterBuilder(String measurementId) {
    this.measurementId = measurementId;
  }

  public ProvidedQueryParameterBuilder withStartDate(long startDate) {
    this.queryParams.put(SupportedRestQueryParams.QP_START_DATE, String.valueOf(startDate));

    return this;
  }

  public ProvidedQueryParameterBuilder withEndDate(long endDate) {
    this.queryParams.put(SupportedRestQueryParams.QP_END_DATE, String.valueOf(endDate));

    return this;
  }

  public ProvidedQueryParameterBuilder withSimpleColumns(List<String> simpleColumns) {
    this.queryParams.put(SupportedRestQueryParams.QP_COLUMNS, String.join(",", simpleColumns));

    return this;
  }

  public ProvidedQueryParameterBuilder withQueryColumns(List<String> rawQueryColumns) {
    this.queryParams.put(SupportedRestQueryParams.QP_COLUMNS, String.join(",", rawQueryColumns));

    return this;
  }

  public ProvidedQueryParameterBuilder withGroupBy(List<String> groupBy) {
    this.queryParams.put(SupportedRestQueryParams.QP_GROUP_BY, String.join(",", groupBy));

    return this;
  }

  public ProvidedQueryParameterBuilder withFilter(String filter) {
    this.queryParams.put(SupportedRestQueryParams.QP_FILTER, filter);

    return this;
  }

  public ProvidedQueryParameterBuilder withPage(int page) {
    this.queryParams.put(SupportedRestQueryParams.QP_PAGE, String.valueOf(page));

    return this;
  }

  public ProvidedQueryParameterBuilder withLimit(int limit) {
    this.queryParams.put(SupportedRestQueryParams.QP_LIMIT, String.valueOf(limit));

    return this;
  }

  public ProvidedRestQueryParams build() {
    return new ProvidedRestQueryParams(measurementId, queryParams);
  }
}
