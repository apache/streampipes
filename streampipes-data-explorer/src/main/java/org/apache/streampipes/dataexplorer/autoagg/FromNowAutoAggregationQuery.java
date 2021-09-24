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

import org.apache.streampipes.dataexplorer.param.AggregatedTimeUnitQueryParams;
import org.apache.streampipes.dataexplorer.param.TimeUnitQueryParams;
import org.apache.streampipes.dataexplorer.query.GetAggregatedEventsFromNowQuery;
import org.apache.streampipes.dataexplorer.query.GetEventsFromNowQuery;
import org.apache.streampipes.dataexplorer.query.GetNumberOfRecordsByTimeUnitQuery;
import org.apache.streampipes.model.datalake.SpQueryResult;

public class FromNowAutoAggregationQuery extends AbstractAutoAggregationQuery<TimeUnitQueryParams, SpQueryResult> {

  public FromNowAutoAggregationQuery(TimeUnitQueryParams params) {
    super(params, SpQueryResult::new);
  }

  @Override
  protected double getCount() {
    return new GetNumberOfRecordsByTimeUnitQuery(params).executeQuery();
  }

  @Override
  protected SpQueryResult getRawEvents() {
    return new GetEventsFromNowQuery(params).executeQuery();
  }

  @Override
  protected SpQueryResult getAggregatedEvents(Integer aggregationValue) {
    return new GetAggregatedEventsFromNowQuery(AggregatedTimeUnitQueryParams
            .from(params.getIndex(), params.getTimeUnit(), params.getTimeValue(), "ms", aggregationValue))
            .executeQuery();
  }
}
