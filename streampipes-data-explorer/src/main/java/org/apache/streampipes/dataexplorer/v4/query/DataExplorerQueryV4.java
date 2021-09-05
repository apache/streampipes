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

import org.apache.streampipes.config.backend.BackendConfig;
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.dataexplorer.v4.params.*;
import org.apache.streampipes.dataexplorer.v4.query.elements.*;
import org.apache.streampipes.dataexplorer.v4.utils.DataLakeManagementUtils;
import org.apache.streampipes.model.datalake.DataResult;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class DataExplorerQueryV4 {

    private static final Logger LOG = LoggerFactory.getLogger(DataExplorerQueryV4.class);
    private static final List<String> AGGREGATION_PREFIXES = Arrays.asList("mean_", "first_", "last_", "count_");

    protected Map<String, QueryParamsV4> params;

    public DataExplorerQueryV4(Map<String, QueryParamsV4> params) {
        this.params = params;
    }

    public DataResult executeQuery() throws RuntimeException {
        InfluxDB influxDB = DataExplorerUtils.getInfluxDBClient();
        List<QueryElement<?>> queryElements = getQueryElements();

        QueryBuilder queryBuilder = QueryBuilder.create(BackendConfig.INSTANCE.getInfluxDatabaseName());
        Query query = queryBuilder.build(queryElements);
        LOG.debug("Data Lake Query (database:" + query.getDatabase() + "): " + query.getCommand());

        QueryResult result = influxDB.query(query);
        LOG.debug("Data Lake Query Result: " + result.toString());
        DataResult dataResult = postQuery(result);

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

    protected DataResult convertResult(QueryResult.Series series) {
        List<String> columns = series.getColumns();
//        for (int i = 0; i < columns.size(); i++) {
//            String replacedColumnName = replacePrefixes(columns.get(i));
//            columns.set(i, replacedColumnName);
//        }
        List<List<Object>> values = series.getValues();

        List<List<Object>> resultingValues = new ArrayList<>();

        values.forEach(v -> {
            if (!v.contains(null)) {
                resultingValues.add(v);
            }
        });

        return new DataResult(values.size(), columns, resultingValues);
    }

    protected DataResult postQuery(QueryResult result) throws RuntimeException {
        return convertResult(result);
    }

    private String replacePrefixes(String columnName) {
        for (String prefix : AGGREGATION_PREFIXES) {
            if (columnName.startsWith(prefix)) {
                return columnName.replaceAll(prefix, "");
            }
        }
        return columnName;
    }

    protected List<QueryElement<?>> getQueryElements() {
        List<QueryElement<?>> queryElements = new ArrayList<>();

        if (this.params.containsKey(DataLakeManagementUtils.SELECT_FROM)) {
            queryElements.add(new SelectFromStatement((SelectFromStatementParams) this.params.get(DataLakeManagementUtils.SELECT_FROM)));
        } else {
            queryElements.add(new DeleteFromStatement((DeleteFromStatementParams) this.params.get(DataLakeManagementUtils.DELETE_FROM)));
        }

        if (this.params.containsKey(DataLakeManagementUtils.WHERE)) {
            queryElements.add(new WhereStatement((WhereStatementParams) this.params.get(DataLakeManagementUtils.WHERE)));
        }

        if (this.params.containsKey(DataLakeManagementUtils.GROUP_BY_TIME)) {
            queryElements.add(new GroupingByTime((GroupingByTimeParams) this.params.get(DataLakeManagementUtils.GROUP_BY_TIME)));

        } else if (this.params.containsKey(DataLakeManagementUtils.GROUP_BY_TAGS)) {
            queryElements.add(new GroupingByTags((GroupingByTagsParams) this.params.get(DataLakeManagementUtils.GROUP_BY_TAGS)));
        }

        if (this.params.containsKey(DataLakeManagementUtils.FILL)) {
            queryElements.add(new FillStatement((FillParams) this.params.get(DataLakeManagementUtils.FILL)));
        }

        if (this.params.containsKey(DataLakeManagementUtils.ORDER_DESCENDING)) {
            queryElements.add(new OrderingByTime((OrderingByTimeParams) this.params.get(DataLakeManagementUtils.ORDER_DESCENDING)));
        } else if (this.params.containsKey(DataLakeManagementUtils.SELECT_FROM)) {
            queryElements.add(new OrderingByTime(OrderingByTimeParams.from(this.params.get(DataLakeManagementUtils.SELECT_FROM).getIndex(), "ASC")));
        }

        if (this.params.containsKey(DataLakeManagementUtils.LIMIT)) {
            queryElements.add(new ItemLimitation((ItemLimitationParams) this.params.get(DataLakeManagementUtils.LIMIT)));
        }

        if (this.params.containsKey(DataLakeManagementUtils.OFFSET)) {
            queryElements.add(new Offset((OffsetParams) this.params.get(DataLakeManagementUtils.OFFSET)));
        }

        return queryElements;
    }
}
