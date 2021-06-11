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
import org.apache.streampipes.model.datalake.DataResult;
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

    public DataExplorerQueryV4(Map<String, QueryParamsV4> params) {
        this.params = params;
    }

    public DataResult executeQuery() throws RuntimeException {
        InfluxDB influxDB = DataExplorerUtils.getInfluxDBClient();
        List<QueryElement> queryElements = getQueryElements();

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
        for (int i = 0; i < columns.size(); i++) {
            String replacedColumnName = columns.get(i).replaceAll("mean_", "");
            columns.set(i, replacedColumnName);
        }
        List values = series.getValues();
        return new DataResult(values.size(), columns, values);
    }

    protected DataResult postQuery(QueryResult result) throws RuntimeException {
        return convertResult(result);
    }

    protected List<QueryElement> getQueryElements() {
        List<QueryElement> queryElements = new ArrayList<>();

        if (this.params.containsKey("SELECT")) {
            queryElements.add(new SelectFromStatement((SelectFromStatementParams) this.params.get("SELECT")));
        } else {
            queryElements.add(new DeleteFromStatement((DeleteFromStatementParams) this.params.get("DELETE")));
        }

        if (this.params.containsKey("WHERE")) {
            queryElements.add(new TimeBoundary((TimeBoundaryParams) this.params.get("WHERE")));
        }

        if (this.params.containsKey("GROUPBYTIME")) {
            queryElements.add(new GroupingByTime((GroupingByTimeParams) this.params.get("GROUPBYTIME")));

        } else if (this.params.containsKey("GROUPBY")) {
            queryElements.add(new GroupingByTags((GroupingByTagsParams) this.params.get("GROUPBY")));
        }

        if (this.params.containsKey("DESCENDING")) {
            queryElements.add(new OrderingByTime((OrderingByTimeParams) this.params.get("DESCENDING")));
        } else if (this.params.containsKey("SELECT")) {
            queryElements.add(new OrderingByTime(OrderingByTimeParams.from(this.params.get("SELECT").getIndex(), "ASC")));
        }

        if (this.params.containsKey("LIMIT")) {
            queryElements.add(new ItemLimitation((ItemLimitationParams) this.params.get("LIMIT")));
        }

        if (this.params.containsKey("OFFSET")) {
            queryElements.add(new Offset((OffsetParams) this.params.get("OFFSET")));
        }

        return queryElements;
    }
}
