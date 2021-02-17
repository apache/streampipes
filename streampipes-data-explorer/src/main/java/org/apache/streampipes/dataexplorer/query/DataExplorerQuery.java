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

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.model.datalake.DataResult;
import org.apache.streampipes.model.datalake.GroupedDataResult;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.List;

public abstract class DataExplorerQuery<OUT> {

  public OUT executeQuery() throws RuntimeException {
    InfluxDB influxDB = DataExplorerUtils.getInfluxDBClient();
    DataExplorerQueryBuilder queryBuilder = DataExplorerQueryBuilder.create(BackendConfig.INSTANCE.getInfluxDatabaseName());
    getQuery(queryBuilder);
    Query query = queryBuilder.toQuery();
    QueryResult result;
    if (queryBuilder.hasTimeUnit()) {
      result = influxDB.query(query, queryBuilder.getTimeUnit());;
    } else {
      result = influxDB.query(query);
    }

    OUT dataResult = postQuery(result);
    influxDB.close();

    return dataResult;
  }

  protected DataResult convertResult(QueryResult result) {
    if (result.getResults().get(0).getSeries() != null) {
      return convertResult(result.getResults().get(0).getSeries().get(0));
    } else {
      return new DataResult();
    }
  }

  protected DataResult convertResult(QueryResult.Series serie) {
    List<String> columns = serie.getColumns();
    for (int i = 0; i < columns.size(); i++) {
      String replacedColumnName = columns.get(i).replaceAll("mean_", "");
      columns.set(i, replacedColumnName);
    }
    List values = serie.getValues();
    return new DataResult(values.size(), columns, values);
  }

  protected GroupedDataResult convertMultiResult(QueryResult result) {
    GroupedDataResult groupedDataResult = new GroupedDataResult();
    if (result.getResults().get(0).getSeries() != null) {
      for (QueryResult.Series series : result.getResults().get(0).getSeries()) {
        String groupName = series.getTags().entrySet().toArray()[0].toString();
        DataResult dataResult = convertResult(series);
        groupedDataResult.addDataResult(groupName, dataResult);
      }
    }
    return groupedDataResult;

  }

  protected abstract void getQuery(DataExplorerQueryBuilder queryBuilder);

  protected abstract OUT postQuery(QueryResult result) throws RuntimeException;
}
