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

import org.apache.streampipes.dataexplorer.param.RetentionPolicyQueryParams;
import org.apache.streampipes.dataexplorer.query.DeleteDataQuery;
import org.apache.streampipes.dataexplorer.query.EditRetentionPolicyQuery;
import org.apache.streampipes.dataexplorer.query.ShowRetentionPolicyQuery;
import org.apache.streampipes.dataexplorer.utils.DataExplorerUtils;
import org.apache.streampipes.dataexplorer.v4.params.DeleteFromStatementParams;
import org.apache.streampipes.dataexplorer.v4.params.QueryParamsV4;
import org.apache.streampipes.dataexplorer.v4.params.TimeBoundaryParams;
import org.apache.streampipes.dataexplorer.v4.query.DataExplorerQueryV4;
import org.apache.streampipes.model.datalake.DataLakeConfiguration;
import org.apache.streampipes.model.datalake.DataLakeMeasure;
import org.apache.streampipes.model.datalake.DataLakeRetentionPolicy;
import org.apache.streampipes.model.datalake.DataResult;
import org.influxdb.dto.QueryResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLakeManagementV4 {

    public List<DataLakeMeasure> getAllMeasurements() {
        List<DataLakeMeasure> allMeasurements = DataExplorerUtils.getInfos();
        return allMeasurements;
    }

    public boolean removeAllMeasurements() {
        List<DataLakeMeasure> allMeasurements = getAllMeasurements();

        for (DataLakeMeasure measure : allMeasurements) {
            QueryResult queryResult = new DeleteDataQuery(measure).executeQuery();
            if (queryResult.hasError() || queryResult.getResults().get(0).getError() != null) {
                return false;
            }
        }
        return true;
    }

    public DataResult deleteData(String measurementID, Long startDate, Long endDate) {
        Map<String, QueryParamsV4> queryParts = getDeleteQueryParams(measurementID, startDate, endDate);
        return new DataExplorerQueryV4(queryParts).executeQuery();
    }

    public DataLakeConfiguration getDataLakeConfiguration() {
        List<DataLakeRetentionPolicy> retentionPolicies = getAllExistingRetentionPolicies();
        return new DataLakeConfiguration(retentionPolicies);
    }

    public String editMeasurementConfiguration(DataLakeConfiguration config, boolean resetToDefault) {

        List<DataLakeRetentionPolicy> existingRetentionPolicies = getAllExistingRetentionPolicies();

        if (resetToDefault) {
            if (existingRetentionPolicies.size() > 1) {
                String drop = new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("custom", "0s"), "DROP").executeQuery();
            }
            String reset = new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("autogen", "0s"), "DEFAULT").executeQuery();
            return reset;
        } else {

            Integer batchSize = config.getBatchSize();
            Integer flushDuration = config.getFlushDuration();

            /**
             * TODO:
             * - Implementation of parameter update for batchSize and flushDuration
             * - Updating multiple retention policies
             */

            String operation = "CREATE";
            if (existingRetentionPolicies.size() > 1) {
                operation = "ALTER";
            }
            String result = new EditRetentionPolicyQuery(RetentionPolicyQueryParams.from("custom", "1d"), operation).executeQuery();
            return result;
        }
    }

    public List<DataLakeRetentionPolicy> getAllExistingRetentionPolicies() {
        /**
         * TODO:
         * - Implementation of parameter return for batchSize and flushDuration
         */
        return new ShowRetentionPolicyQuery(RetentionPolicyQueryParams.from("", "0s")).executeQuery();
    }

    public Map<String, QueryParamsV4> getDeleteQueryParams(String measurementID, Long startDate, Long endDate) {
        Map<String, QueryParamsV4> queryParts = new HashMap<>();
        queryParts.put("DELETE", DeleteFromStatementParams.from(measurementID));
        if (startDate != null || endDate != null) {
            queryParts.put("WHERE", TimeBoundaryParams.from(measurementID, startDate, endDate));
        }
        return queryParts;
    }
}
