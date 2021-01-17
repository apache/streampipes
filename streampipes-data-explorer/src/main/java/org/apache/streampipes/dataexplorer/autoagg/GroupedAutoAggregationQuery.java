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
package org.apache.streampipes.dataexplorer.autoagg;

import org.apache.streampipes.dataexplorer.param.GroupedAggregatedTimeBoundQueryParams;
import org.apache.streampipes.dataexplorer.param.GroupedQueryParams;
import org.apache.streampipes.dataexplorer.query.GetGroupedAggregatedEventsQuery;
import org.apache.streampipes.dataexplorer.query.GetGroupedEventsQuery;
import org.apache.streampipes.dataexplorer.query.GetNumberOfRecordsQuery;
import org.apache.streampipes.model.datalake.GroupedDataResult;

public class GroupedAutoAggregationQuery extends AbstractAutoAggregationQuery<GroupedQueryParams, GroupedDataResult> {

  public GroupedAutoAggregationQuery(GroupedQueryParams params) {
    super(params, GroupedDataResult::new);
  }

  @Override
  protected double getCount() {
    return new GetNumberOfRecordsQuery(params).executeQuery();
  }

  @Override
  protected GroupedDataResult getRawEvents() {
    return new GetGroupedEventsQuery(params).executeQuery();
  }

  @Override
  protected GroupedDataResult getAggregatedEvents(Integer aggregationValue) {
    return new GetGroupedAggregatedEventsQuery(GroupedAggregatedTimeBoundQueryParams.from(params.getIndex(),
            params.getStartDate(), params.getEndDate(), "ms", aggregationValue, params.getGroupingTag())).executeQuery();
  }
}
