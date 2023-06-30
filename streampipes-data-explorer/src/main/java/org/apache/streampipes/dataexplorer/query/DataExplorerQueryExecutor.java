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

package org.apache.streampipes.dataexplorer.query;

import org.apache.streampipes.dataexplorer.influx.DataExplorerInfluxQueryExecutor;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.dataexplorer.param.SelectQueryParams;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.SpQueryStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class DataExplorerQueryExecutor<X, W> {

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerInfluxQueryExecutor.class);
  protected int maximumAmountOfEvents;

  protected boolean appendId = false;
  protected String forId;

  public DataExplorerQueryExecutor() {
    this.maximumAmountOfEvents = -1;
  }

  public DataExplorerQueryExecutor(String forId) {
    this();
    this.appendId = true;
    this.forId = forId;
  }

  public DataExplorerQueryExecutor(int maximumAmountOfEvents) {
    this();
    this.maximumAmountOfEvents = maximumAmountOfEvents;
  }

  public SpQueryResult executeQuery(SelectQueryParams params,
                                    boolean ignoreMissingValues) throws RuntimeException {

    if (this.maximumAmountOfEvents != -1) {
      X countQuery = makeCountQuery(params);
      W countQueryResult = executeQuery(countQuery);
      var limit = params.getLimit();
      var amountOfResults = getAmountOfResults(countQueryResult);
      Double amountOfQueryResults = limit == Integer.MIN_VALUE ? amountOfResults : Math.min(amountOfResults, limit);

      if (amountOfQueryResults > this.maximumAmountOfEvents) {
        SpQueryResult tooMuchData = new SpQueryResult();
        tooMuchData.setSpQueryStatus(SpQueryStatus.TOO_MUCH_DATA);
        tooMuchData.setTotal(amountOfQueryResults.intValue());
        return tooMuchData;
      }
    }

    X query = makeSelectQuery(params);
    return executeQuery(query, ignoreMissingValues);
  }

  public SpQueryResult executeQuery(DeleteQueryParams params) {
    return executeQuery(makeDeleteQuery(params), true);
  }

  public SpQueryResult executeQuery(X query,
                                    boolean ignoreMissingValues) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Data Lake Query " + asQueryString(query));
    }

    W result = executeQuery(query);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Data Lake Query Result: " + result.toString());
    }

    return postQuery(result, ignoreMissingValues);
  }

  protected abstract double getAmountOfResults(W countQueryResult);

  protected abstract SpQueryResult postQuery(W queryResult,
                                             boolean ignoreMissingValues);

  protected abstract W executeQuery(X query);

  protected abstract String asQueryString(X query);

  protected abstract X makeDeleteQuery(DeleteQueryParams params);

  protected abstract X makeCountQuery(SelectQueryParams params);

  protected abstract X makeSelectQuery(SelectQueryParams params);
}
