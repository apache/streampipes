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

import org.apache.streampipes.dataexplorer.v4.params.*;

import java.util.HashMap;
import java.util.Map;

public class DataLakeManagementUtils {

    public static final String SELECT_FROM = "SELECT";
    public static final String WHERE = "WHERE";
    public static final String GROUP_BY_TAGS = "GROUPBY";
    public static final String GROUP_BY_TIME = "GROUPBYTIME";
    public static final String ORDER_DESCENDING = "DESC";
    public static final String LIMIT = "LIMIT";
    public static final String OFFSET = "OFFSET";

    public static final String DELETE_FROM = "DELETE";

    public static Map<String, QueryParamsV4> getSelectQueryParams(String measurementID, Long startDate, Long endDate, Integer page, Integer limit, Integer offset, String groupBy, String order, String aggregationFunction, String timeInterval) {
        Map<String, QueryParamsV4> queryParts = new HashMap<>();

        queryParts.put(SELECT_FROM, SelectFromStatementParams.from(measurementID, aggregationFunction));

        if (startDate != null || endDate != null) {
            queryParts.put(WHERE, TimeBoundaryParams.from(measurementID, startDate, endDate));
        }


        if (timeInterval != null && aggregationFunction != null) {
            if (groupBy == null) {
                queryParts.put(GROUP_BY_TIME, GroupingByTimeParams.from(measurementID, timeInterval));
            } else {
                groupBy = groupBy + ",time(" + timeInterval + ")";
            }
        }

        if (groupBy != null) {
            queryParts.put(GROUP_BY_TAGS, GroupingByTagsParams.from(measurementID, groupBy));
        }

        if (order != null) {
            if (order.equals(ORDER_DESCENDING)) {
                queryParts.put(ORDER_DESCENDING, OrderingByTimeParams.from(measurementID, order));
            }
        }

        if (limit != null) {
            queryParts.put(LIMIT, ItemLimitationParams.from(measurementID, limit));
        }

        if (offset != null) {
            queryParts.put(OFFSET, OffsetParams.from(measurementID, offset));
        } else if (limit != null && page != null) {
            queryParts.put(OFFSET, OffsetParams.from(measurementID, page * limit));
        }

        return queryParts;
    }

    public static Map<String, QueryParamsV4> getDeleteQueryParams(String measurementID, Long startDate, Long endDate) {
        Map<String, QueryParamsV4> queryParts = new HashMap<>();
        queryParts.put(DELETE_FROM, DeleteFromStatementParams.from(measurementID));
        if (startDate != null || endDate != null) {
            queryParts.put(WHERE, TimeBoundaryParams.from(measurementID, startDate, endDate));
        }
        return queryParts;
    }
}
