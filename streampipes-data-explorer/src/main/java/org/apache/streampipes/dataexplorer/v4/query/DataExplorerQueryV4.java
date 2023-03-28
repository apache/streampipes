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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxClientProvider;
import org.apache.streampipes.dataexplorer.v4.params.DeleteFromStatementParams;
import org.apache.streampipes.dataexplorer.v4.params.FillParams;
import org.apache.streampipes.dataexplorer.v4.params.GroupingByTagsParams;
import org.apache.streampipes.dataexplorer.v4.params.GroupingByTimeParams;
import org.apache.streampipes.dataexplorer.v4.params.ItemLimitationParams;
import org.apache.streampipes.dataexplorer.v4.params.OffsetParams;
import org.apache.streampipes.dataexplorer.v4.params.OrderingByTimeParams;
import org.apache.streampipes.dataexplorer.v4.params.QueryParamsV4;
import org.apache.streampipes.dataexplorer.v4.params.SelectFromStatementParams;
import org.apache.streampipes.dataexplorer.v4.params.WhereStatementParams;
import org.apache.streampipes.dataexplorer.v4.query.elements.DeleteFromStatement;
import org.apache.streampipes.dataexplorer.v4.query.elements.FillStatement;
import org.apache.streampipes.dataexplorer.v4.query.elements.GroupingByTags;
import org.apache.streampipes.dataexplorer.v4.query.elements.GroupingByTime;
import org.apache.streampipes.dataexplorer.v4.query.elements.ItemLimitation;
import org.apache.streampipes.dataexplorer.v4.query.elements.Offset;
import org.apache.streampipes.dataexplorer.v4.query.elements.OrderingByTime;
import org.apache.streampipes.dataexplorer.v4.query.elements.QueryElement;
import org.apache.streampipes.dataexplorer.v4.query.elements.SelectFromStatement;
import org.apache.streampipes.dataexplorer.v4.query.elements.WhereStatement;
import org.apache.streampipes.dataexplorer.v4.utils.DataLakeManagementUtils;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.apache.streampipes.model.datalake.SpQueryStatus;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DataExplorerQueryV4 {

  private static final Logger LOG = LoggerFactory.getLogger(DataExplorerQueryV4.class);

  protected Map<String, QueryParamsV4> params;

  protected int maximumAmountOfEvents;

  private boolean appendId = false;
  private String forId;

  private Environment env;

  public DataExplorerQueryV4() {

  }

  public DataExplorerQueryV4(Map<String, QueryParamsV4> params,
                             String forId) {
    this(params);
    this.appendId = true;
    this.forId = forId;
  }

  public DataExplorerQueryV4(Map<String, QueryParamsV4> params) {
    this.params = params;
    this.env = Environments.getEnvironment();
    this.maximumAmountOfEvents = -1;
  }

  public DataExplorerQueryV4(Map<String, QueryParamsV4> params, int maximumAmountOfEvents) {
    this(params);
    this.maximumAmountOfEvents = maximumAmountOfEvents;
  }

  public SpQueryResult executeQuery(boolean ignoreMissingValues) throws RuntimeException {
    try (final InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient()) {
      List<QueryElement<?>> queryElements = getQueryElements();

      if (this.maximumAmountOfEvents != -1) {
        QueryBuilder countQueryBuilder = QueryBuilder.create(getDatabaseName());
        Query countQuery = countQueryBuilder.build(queryElements, true);
        QueryResult countQueryResult = influxDB.query(countQuery);
        Double amountOfQueryResults = getAmountOfResults(countQueryResult);

        if (amountOfQueryResults > this.maximumAmountOfEvents) {
          SpQueryResult tooMuchData = new SpQueryResult();
          tooMuchData.setSpQueryStatus(SpQueryStatus.TOO_MUCH_DATA);
          tooMuchData.setTotal(amountOfQueryResults.intValue());
          return tooMuchData;
        }
      }

      QueryBuilder queryBuilder = QueryBuilder.create(getDatabaseName());
      Query query = queryBuilder.build(queryElements, false);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Data Lake Query (database:" + query.getDatabase() + "): " + query.getCommand());
      }

      QueryResult result = influxDB.query(query);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Data Lake Query Result: " + result.toString());
      }

      return postQuery(result, ignoreMissingValues);
    }
  }

  private double getAmountOfResults(QueryResult countQueryResult) {
    if (countQueryResult.getResults().get(0).getSeries() != null
        && countQueryResult.getResults().get(0).getSeries().get(0).getValues() != null) {
      return (double) countQueryResult.getResults().get(0).getSeries().get(0).getValues().get(0).get(1);
    } else {
      return 0.0;
    }
  }


  protected DataSeries convertResult(QueryResult.Series series,
                                     boolean ignoreMissingValues) {
    List<String> columns = series.getColumns();
    List<List<Object>> values = series.getValues();

    List<List<Object>> resultingValues = new ArrayList<>();

    values.forEach(v -> {
      if (ignoreMissingValues) {
        if (!v.contains(null)) {
          resultingValues.add(v);
        }
      } else {
        resultingValues.add(v);
      }

    });

    return new DataSeries(values.size(), resultingValues, columns, series.getTags());
  }

  protected SpQueryResult postQuery(QueryResult queryResult,
                                    boolean ignoreMissingValues) throws RuntimeException {
    SpQueryResult result = new SpQueryResult();

    if (hasResult(queryResult)) {
      result.setTotal(queryResult.getResults().get(0).getSeries().size());
      queryResult.getResults().get(0).getSeries().forEach(rs -> {
        DataSeries series = convertResult(rs, ignoreMissingValues);
        result.setHeaders(series.getHeaders());
        result.addDataResult(series);
      });
    }

    if (this.appendId) {
      result.setForId(this.forId);
    }

    return result;
  }

  private boolean hasResult(QueryResult queryResult) {
    return queryResult.getResults() != null
        && queryResult.getResults().size() > 0
        && queryResult.getResults().get(0).getSeries() != null;
  }

  protected List<QueryElement<?>> getQueryElements() {
    List<QueryElement<?>> queryElements = new ArrayList<>();

    if (this.params.containsKey(DataLakeManagementUtils.SELECT_FROM)) {
      queryElements.add(
          new SelectFromStatement((SelectFromStatementParams) this.params.get(DataLakeManagementUtils.SELECT_FROM)));
    } else {
      queryElements.add(
          new DeleteFromStatement((DeleteFromStatementParams) this.params.get(DataLakeManagementUtils.DELETE_FROM)));
    }

    if (this.params.containsKey(DataLakeManagementUtils.WHERE)) {
      queryElements.add(new WhereStatement((WhereStatementParams) this.params.get(DataLakeManagementUtils.WHERE)));
    }

    if (this.params.containsKey(DataLakeManagementUtils.GROUP_BY_TIME)) {
      queryElements.add(
          new GroupingByTime((GroupingByTimeParams) this.params.get(DataLakeManagementUtils.GROUP_BY_TIME)));

    } else if (this.params.containsKey(DataLakeManagementUtils.GROUP_BY_TAGS)) {
      queryElements.add(
          new GroupingByTags((GroupingByTagsParams) this.params.get(DataLakeManagementUtils.GROUP_BY_TAGS)));
    }

    if (this.params.containsKey(DataLakeManagementUtils.FILL)) {
      queryElements.add(new FillStatement((FillParams) this.params.get(DataLakeManagementUtils.FILL)));
    }

    if (this.params.containsKey(DataLakeManagementUtils.ORDER_DESCENDING)) {
      queryElements.add(
          new OrderingByTime((OrderingByTimeParams) this.params.get(DataLakeManagementUtils.ORDER_DESCENDING)));
    } else if (this.params.containsKey(DataLakeManagementUtils.SELECT_FROM)) {
      queryElements.add(new OrderingByTime(
          OrderingByTimeParams.from(this.params.get(DataLakeManagementUtils.SELECT_FROM).getIndex(), "ASC")));
    }

    if (this.params.containsKey(DataLakeManagementUtils.LIMIT)) {
      queryElements.add(new ItemLimitation((ItemLimitationParams) this.params.get(DataLakeManagementUtils.LIMIT)));
    }

    if (this.params.containsKey(DataLakeManagementUtils.OFFSET)) {
      queryElements.add(new Offset((OffsetParams) this.params.get(DataLakeManagementUtils.OFFSET)));
    }

    return queryElements;
  }

  private String getDatabaseName() {
    return env.getTsStorageBucket().getValueOrDefault();
  }
}
