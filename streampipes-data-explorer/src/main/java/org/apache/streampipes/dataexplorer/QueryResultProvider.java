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
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
import org.apache.streampipes.dataexplorer.query.DataExplorerQueryExecutor;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import java.util.Optional;


public class QueryResultProvider {

  public static final String FOR_ID_KEY = "forId";
  protected final boolean ignoreMissingData;
  protected final IDataExplorerQueryManagement dataExplorerQueryManagement;
  protected final DataExplorerQueryExecutor<?, ?> queryExecutor;
  protected ProvidedRestQueryParams queryParams;

  public QueryResultProvider(ProvidedRestQueryParams queryParams,
                             IDataExplorerQueryManagement dataExplorerQueryManagement,
                             DataExplorerQueryExecutor<?, ?> queryExecutor,
                             boolean ignoreMissingData) {
    this.queryParams = queryParams;
    this.ignoreMissingData = ignoreMissingData;
    this.dataExplorerQueryManagement = dataExplorerQueryManagement;
    this.queryExecutor = queryExecutor;
  }

  public SpQueryResult getData() {
    if (queryParams.has(SupportedRestQueryParams.QP_AUTO_AGGREGATE)) {
      queryParams = new AutoAggregationHandler(queryParams,
                                               dataExplorerQueryManagement).makeAutoAggregationQueryParams();
    }
    SelectQueryParams qp = ProvidedRestQueryParamConverter.getSelectQueryParams(queryParams);

    if (queryParams.getProvidedParams().containsKey(SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS)) {
      int maximumAmountOfEvents = Integer.parseInt(queryParams.getProvidedParams()
                                                              .get(SupportedRestQueryParams.QP_MAXIMUM_AMOUNT_OF_EVENTS)
      );
      return queryExecutor.executeQuery(qp, maximumAmountOfEvents, Optional.empty(), ignoreMissingData);
    }

    if (queryParams.getProvidedParams().containsKey(FOR_ID_KEY)) {
      String forWidgetId = queryParams.getProvidedParams().get(FOR_ID_KEY);
      return queryExecutor.executeQuery(qp, -1, Optional.of(forWidgetId), ignoreMissingData);
    } else {
      return queryExecutor.executeQuery(qp, -1, Optional.empty(), ignoreMissingData);
    }
  }
}
