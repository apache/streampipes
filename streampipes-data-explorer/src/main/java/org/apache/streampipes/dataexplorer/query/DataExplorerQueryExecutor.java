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

import org.apache.streampipes.dataexplorer.api.query.DatasetQuery;
import org.apache.streampipes.dataexplorer.api.query.DatasetQueryCompiler;
import org.apache.streampipes.dataexplorer.param.DeleteQueryParams;
import org.apache.streampipes.model.dataset.DataSeries;
import org.apache.streampipes.model.dataset.DatasetMetadata;
import org.apache.streampipes.model.dataset.SpQueryResult;
import org.apache.streampipes.model.dataset.SpQueryStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;

public abstract class DataExplorerQueryExecutor<X, W> {

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerQueryExecutor.class);

  private SpQueryResult validateAndReturnQueryResult(SpQueryResult queryResult,
                                                     int limit,
                                                     int maximumAmountOfEvents) {
    var amountOfResults = queryResult.getAllDataSeries()
        .stream()
        .mapToInt(DataSeries::getTotal)
        .sum();

    var amountOfQueryResults = limit == Integer.MIN_VALUE ? amountOfResults : Math.min(amountOfResults, limit);
    if (amountOfQueryResults > maximumAmountOfEvents) {
      return makeTooMuchDataResult(amountOfQueryResults);
    } else {
      return queryResult;
    }
  }

  private SpQueryResult makeTooMuchDataResult(int amountOfQueryResults) {
    SpQueryResult tooMuchData = new SpQueryResult();
    tooMuchData.setSpQueryStatus(SpQueryStatus.TOO_MUCH_DATA);
    tooMuchData.setTotal(amountOfQueryResults);
    return tooMuchData;
  }

  public SpQueryResult executeQuery(DeleteQueryParams params) {
    return executeQuery(makeDeleteQuery(params), Optional.empty(), true);
  }

  public SpQueryResult executeQuery(X query,
                                    Optional<String> forIdOpt,
                                    boolean ignoreMissingValues) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Data Lake Query {}", asQueryString(query));
    }

    W result = executeQuery(query);
    if (LOG.isDebugEnabled()) {
      LOG.debug("Data Lake Query Result: {}", result.toString());
    }

    return postQuery(result, forIdOpt, ignoreMissingValues);
  }

  protected abstract SpQueryResult postQuery(W queryResult,
                                             Optional<String> forIdOpt,
                                             boolean ignoreMissingValues);

  public abstract W executeQuery(X query);

  protected abstract String asQueryString(X query);

  protected abstract X makeDeleteQuery(DeleteQueryParams params);

  protected abstract DatasetQueryCompiler<X> queryCompiler();

  public SpQueryResult executeQuery(DatasetQuery query,
                                    DatasetMetadata dataset,
                                    int maximumAmountOfEvents,
                                    Optional<String> forId,
                                    boolean ignoreMissingValues) {
    if (!query.datasetId().value().equals(dataset.getElementId())) {
      throw new IllegalArgumentException("Query dataset does not match resolved metadata");
    }
    var result = executeQuery(queryCompiler().compile(query.specification(), dataset.getMeasureName()),
        forId, ignoreMissingValues);
    return maximumAmountOfEvents == -1 ? result
        : validateAndReturnQueryResult(result, query.specification().limit().orElse(Integer.MIN_VALUE),
            maximumAmountOfEvents);
  }

  public abstract Map<String, Object> getTagValues(String measurementId, String fields);
  public abstract boolean deleteData(DatasetMetadata measure);
}
