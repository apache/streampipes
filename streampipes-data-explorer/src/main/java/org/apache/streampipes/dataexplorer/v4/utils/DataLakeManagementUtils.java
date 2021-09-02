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

package org.apache.streampipes.dataexplorer.v4.utils;

import org.apache.streampipes.dataexplorer.v4.ProvidedQueryParams;
import org.apache.streampipes.dataexplorer.v4.params.*;

import java.util.HashMap;
import java.util.Map;

import static org.apache.streampipes.dataexplorer.v4.SupportedDataLakeQueryParameters.*;


public class DataLakeManagementUtils {

    public static final String SELECT_FROM = "SELECT";
    public static final String WHERE = "WHERE";
    public static final String GROUP_BY_TAGS = "GROUPBY";
    public static final String GROUP_BY_TIME = "GROUPBYTIME";
    public static final String ORDER_DESCENDING = "DESC";
    public static final String LIMIT = "LIMIT";
    public static final String OFFSET = "OFFSET";
    public static final String FILL = "FILL";

    public static final String DELETE_FROM = "DELETE";

    public static Map<String, QueryParamsV4> getSelectQueryParams(ProvidedQueryParams params) {
        Map<String, QueryParamsV4> queryParts = new HashMap<>();
        String measurementId = params.getMeasurementId();

        if (params.has(QP_COUNT_ONLY) && params.getAsBoolean(QP_COUNT_ONLY)) {
            queryParts.put(SELECT_FROM, SelectFromStatementParams.from(measurementId, params.getAsString(QP_COLUMNS), true));
        } else {
            queryParts.put(SELECT_FROM, SelectFromStatementParams.from(measurementId, params.getAsString(QP_COLUMNS), params.getAsString(QP_AGGREGATION_FUNCTION)));
        }

        String filterConditions = params.getAsString(QP_FILTER);

        if (hasTimeParams(params)) {
            queryParts.put(WHERE, WhereStatementParams.from(measurementId,
                    params.getAsLong(QP_START_DATE),
                    params.getAsLong(QP_END_DATE),
                    filterConditions));
        } else if (filterConditions != null) {
            queryParts.put(WHERE, WhereStatementParams.from(measurementId, filterConditions));
        }

        if (params.has(QP_TIME_INTERVAL) && params.has(QP_AGGREGATION_FUNCTION)) {
            String timeInterval = params.getAsString(QP_TIME_INTERVAL);
            if (!params.has(QP_GROUP_BY)) {
                queryParts.put(GROUP_BY_TIME, GroupingByTimeParams.from(measurementId, timeInterval));
            } else {
                params.update(QP_GROUP_BY, params.getAsString(QP_GROUP_BY) + ",time(" + timeInterval + ")");
            }

            queryParts.put(FILL, FillParams.from(measurementId));
        }

        if (params.has(QP_GROUP_BY)) {
            queryParts.put(GROUP_BY_TAGS, GroupingByTagsParams.from(measurementId, params.getAsString(QP_GROUP_BY)));
        }


        if (params.has(QP_ORDER)) {
            String order = params.getAsString(QP_ORDER);
            if (order.equals(ORDER_DESCENDING)) {
                queryParts.put(ORDER_DESCENDING, OrderingByTimeParams.from(measurementId, order));
            }
        }

        if (params.has(QP_LIMIT)) {
            queryParts.put(LIMIT, ItemLimitationParams.from(measurementId, params.getAsInt(QP_LIMIT)));
        }

        if (params.has(QP_OFFSET)) {
            queryParts.put(OFFSET, OffsetParams.from(measurementId, params.getAsInt(QP_OFFSET)));
        } else if (params.has(QP_LIMIT) && params.has(QP_PAGE)) {
            queryParts.put(OFFSET, OffsetParams.from(measurementId,
                    params.getAsInt(QP_PAGE) * params.getAsInt(QP_LIMIT)));
        }

        return queryParts;
    }

    public static Map<String, QueryParamsV4> getDeleteQueryParams(String measurementID,
                                                                  Long startDate,
                                                                  Long endDate) {
        Map<String, QueryParamsV4> queryParts = new HashMap<>();
        queryParts.put(DELETE_FROM, DeleteFromStatementParams.from(measurementID));
        if (startDate != null || endDate != null) {
            queryParts.put(WHERE, TimeBoundaryParams.from(measurementID, startDate, endDate));
        }
        return queryParts;
    }

    private static boolean hasTimeParams(ProvidedQueryParams params) {
        return params.has(QP_START_DATE) ||
                params.has(QP_END_DATE);
    }
}
