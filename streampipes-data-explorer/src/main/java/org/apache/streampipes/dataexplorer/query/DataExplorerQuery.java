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

import org.apache.streampipes.commons.environment.Environment;
import org.apache.streampipes.commons.environment.Environments;
import org.apache.streampipes.dataexplorer.commons.influx.InfluxClientProvider;
import org.apache.streampipes.model.datalake.DataSeries;
import org.apache.streampipes.model.datalake.SpQueryResult;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public abstract class DataExplorerQuery<T> {

  public T executeQuery() throws RuntimeException {
    InfluxDB influxDB = InfluxClientProvider.getInfluxDBClient();
    var databaseName = getEnvironment().getTsStorageBucket().getValueOrDefault();
    DataExplorerQueryBuilder queryBuilder =
        DataExplorerQueryBuilder.create(databaseName);
    getQuery(queryBuilder);
    Query query = queryBuilder.toQuery();
    org.influxdb.dto.QueryResult result;
    if (queryBuilder.hasTimeUnit()) {
      result = influxDB.query(query, queryBuilder.getTimeUnit());
    } else {
      result = influxDB.query(query);
    }

    T dataResult = postQuery(result);
    influxDB.close();

    return dataResult;
  }

  protected SpQueryResult convertResult(org.influxdb.dto.QueryResult result) {
    if (result.getResults().get(0).getSeries() != null) {
      DataSeries dataSeries = convertResult(result.getResults().get(0).getSeries().get(0));
      return new SpQueryResult(1, dataSeries.getHeaders(), Arrays.asList(dataSeries));
    } else {
      return new SpQueryResult();
    }
  }

  protected DataSeries convertResult(org.influxdb.dto.QueryResult.Series serie) {
    List<String> columns = serie.getColumns();
    for (int i = 0; i < columns.size(); i++) {
      String replacedColumnName = columns.get(i).replaceAll("mean_", "");
      columns.set(i, replacedColumnName);
    }
    List values = serie.getValues();
    return new DataSeries(values.size(), values, columns, new HashMap<>());
  }

  protected SpQueryResult convertMultiResult(org.influxdb.dto.QueryResult result) {
    SpQueryResult groupedDataResult = new SpQueryResult();
    if (result.getResults().get(0).getSeries() != null) {
      for (org.influxdb.dto.QueryResult.Series series : result.getResults().get(0).getSeries()) {
        String groupName = series.getTags().entrySet().toArray()[0].toString();
        DataSeries dataResult = convertResult(series);
        groupedDataResult.addDataResult(dataResult);
      }
    }
    return groupedDataResult;

  }

  private Environment getEnvironment() {
    return Environments.getEnvironment();
  }

  protected abstract void getQuery(DataExplorerQueryBuilder queryBuilder);

  protected abstract T postQuery(org.influxdb.dto.QueryResult result) throws RuntimeException;
}
