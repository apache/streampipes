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

import org.apache.streampipes.dataexplorer.v4.AutoAggregationHandler;
import org.apache.streampipes.dataexplorer.v4.ProvidedQueryParams;
import org.apache.streampipes.dataexplorer.v4.params.QueryParamsV4;
import org.apache.streampipes.dataexplorer.v4.utils.DataLakeManagementUtils;
import org.apache.streampipes.model.datalake.SpQueryResult;

import java.util.Map;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_AUTO_AGGREGATE;
import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.QP_MAXIMUM_AMOUNT_OF_EVENTS;

public class QueryResultProvider {

  public static final String FOR_ID_KEY = "forId";
  protected final boolean ignoreMissingData;
  protected ProvidedQueryParams queryParams;

  public QueryResultProvider(ProvidedQueryParams queryParams,
                             boolean ignoreMissingData) {
    this.queryParams = queryParams;
    this.ignoreMissingData = ignoreMissingData;
  }

  public SpQueryResult getData() {
    if (queryParams.has(QP_AUTO_AGGREGATE)) {
      queryParams = new AutoAggregationHandler(queryParams).makeAutoAggregationQueryParams();
    }
    Map<String, QueryParamsV4> queryParts = DataLakeManagementUtils.getSelectQueryParams(queryParams);

    if (queryParams.getProvidedParams().containsKey(QP_MAXIMUM_AMOUNT_OF_EVENTS)) {
      int maximumAmountOfEvents = Integer.parseInt(queryParams.getProvidedParams().get(QP_MAXIMUM_AMOUNT_OF_EVENTS));
      return new DataExplorerQueryV4(queryParts, maximumAmountOfEvents).executeQuery(ignoreMissingData);
    }

    if (queryParams.getProvidedParams().containsKey(FOR_ID_KEY)) {
      String forWidgetId = queryParams.getProvidedParams().get(FOR_ID_KEY);
      return new DataExplorerQueryV4(queryParts, forWidgetId).executeQuery(ignoreMissingData);
    } else {
      return new DataExplorerQueryV4(queryParts).executeQuery(ignoreMissingData);
    }
  }
}
