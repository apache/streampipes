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
package org.apache.streampipes.dataexplorer.param;

import org.apache.streampipes.dataexplorer.param.model.FillClauseParams;
import org.apache.streampipes.dataexplorer.param.model.GroupByTagsClauseParams;
import org.apache.streampipes.dataexplorer.param.model.GroupByTimeClauseParams;
import org.apache.streampipes.dataexplorer.param.model.LimitClauseParams;
import org.apache.streampipes.dataexplorer.param.model.OffsetClauseParams;
import org.apache.streampipes.dataexplorer.param.model.OrderByClauseParams;
import org.apache.streampipes.dataexplorer.param.model.SelectClauseParams;
import org.apache.streampipes.dataexplorer.param.model.WhereClauseParams;
import org.apache.streampipes.model.datalake.param.ProvidedRestQueryParams;
import org.apache.streampipes.model.datalake.param.SupportedRestQueryParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProvidedRestQueryParamConverter {

  public static final String BRACKET_OPEN = "\\[";
  public static final String BRACKET_CLOSE = "\\]";

  public static final String ORDER_DESCENDING = "DESC";

  public static SelectQueryParams getSelectQueryParams(ProvidedRestQueryParams params) {
    SelectQueryParams queryParameters = new SelectQueryParams(params.getMeasurementId());

    if (params.has(SupportedRestQueryParams.QP_COUNT_ONLY)
            && params.getAsBoolean(SupportedRestQueryParams.QP_COUNT_ONLY)) {
      queryParameters
              .withSelectParams(SelectClauseParams.from(params.getAsString(SupportedRestQueryParams.QP_COLUMNS), true));
    } else {
      queryParameters.withSelectParams(SelectClauseParams.from(params.getAsString(SupportedRestQueryParams.QP_COLUMNS),
              params.getAsString(SupportedRestQueryParams.QP_AGGREGATION_FUNCTION)));
    }

    String filterConditions = params.getAsString(SupportedRestQueryParams.QP_FILTER);

    if (hasTimeParams(params)) {
      queryParameters.withWhereParams(WhereClauseParams.from(params.getAsLong(SupportedRestQueryParams.QP_START_DATE),
              params.getAsLong(SupportedRestQueryParams.QP_END_DATE), filterConditions));
    } else if (filterConditions != null) {
      queryParameters.withWhereParams(WhereClauseParams.from(filterConditions));
    }

    if (params.has(SupportedRestQueryParams.QP_TIME_INTERVAL)) {
      String timeInterval = params.getAsString(SupportedRestQueryParams.QP_TIME_INTERVAL);
      if (!params.has(SupportedRestQueryParams.QP_GROUP_BY)) {
        queryParameters.withGroupByTimeParams(GroupByTimeClauseParams.from(timeInterval));
      } else {
        params.update(SupportedRestQueryParams.QP_GROUP_BY,
                params.getAsString(SupportedRestQueryParams.QP_GROUP_BY) + ",time(" + timeInterval + ")");
      }

      queryParameters.withFillParams(FillClauseParams.from());
    }

    if (params.has(SupportedRestQueryParams.QP_GROUP_BY)) {
      queryParameters.withGroupByTagsParams(
              GroupByTagsClauseParams.from(params.getAsString(SupportedRestQueryParams.QP_GROUP_BY)));
    }

    if (params.has(SupportedRestQueryParams.QP_ORDER)) {
      String order = params.getAsString(SupportedRestQueryParams.QP_ORDER);
      if (order.equals(ORDER_DESCENDING)) {
        queryParameters.withOrderByParams(OrderByClauseParams.from(order));
      }
    }

    if (params.has(SupportedRestQueryParams.QP_LIMIT)) {
      queryParameters.withLimitParams(LimitClauseParams.from(params.getAsInt(SupportedRestQueryParams.QP_LIMIT)));
    }

    if (params.has(SupportedRestQueryParams.QP_OFFSET)) {
      queryParameters.withOffsetParams(OffsetClauseParams.from(params.getAsInt(SupportedRestQueryParams.QP_OFFSET)));
    } else if (params.has(SupportedRestQueryParams.QP_LIMIT) && params.has(SupportedRestQueryParams.QP_PAGE)) {
      queryParameters.withOffsetParams(OffsetClauseParams.from(
              params.getAsInt(SupportedRestQueryParams.QP_PAGE) * params.getAsInt(SupportedRestQueryParams.QP_LIMIT)));
    }

    return queryParameters;
  }

  public static DeleteQueryParams getDeleteQueryParams(String measurementName, Long startTime, Long endTime) {
    return new DeleteQueryParams(measurementName, startTime, endTime);
  }

  private static boolean hasTimeParams(ProvidedRestQueryParams params) {
    return params.has(SupportedRestQueryParams.QP_START_DATE) || params.has(SupportedRestQueryParams.QP_END_DATE);
  }

  public static List<String[]> buildConditions(String queryPart) {
    String[] conditions = queryPart.split(",");
    List<String[]> result = new ArrayList<>();

    Arrays.stream(conditions).forEach(condition -> {
      String[] singleCondition = buildSingleCondition(condition);
      result.add(singleCondition);
    });
    return result;
  }

  public static String[] buildSingleCondition(String queryPart) {
    return queryPart.replaceAll(BRACKET_OPEN, "").replaceAll(BRACKET_CLOSE, "").split(";");
  }
}
