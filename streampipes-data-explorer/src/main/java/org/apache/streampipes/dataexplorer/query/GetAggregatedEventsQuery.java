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

import org.apache.streampipes.dataexplorer.param.AggregatedTimeBoundQueryParams;
import org.apache.streampipes.dataexplorer.template.QueryTemplates;
import org.apache.streampipes.model.datalake.DataResult;
import org.influxdb.dto.QueryResult;

public class GetAggregatedEventsQuery extends ParameterizedDataExplorerQuery<AggregatedTimeBoundQueryParams, DataResult> {

  public GetAggregatedEventsQuery(AggregatedTimeBoundQueryParams queryParams) {
    super(queryParams);
  }

  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    queryBuilder.add(QueryTemplates.selectMeanFrom(params.getIndex()));
    queryBuilder.add(" WHERE time > " + params.getStartDate() * 1000000
            + " AND time < " + params.getEndDate() * 1000000);
    queryBuilder.add("GROUP BY time(" + params.getAggregationValue() + params.getAggregationUnit() + ")");
    queryBuilder.add("fill(none)");
    queryBuilder.add("ORDER BY time");
  }

  @Override
  protected DataResult postQuery(QueryResult result) {
    return convertResult(result);
  }
}
