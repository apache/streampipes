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

import org.apache.streampipes.dataexplorer.param.PagingQueryParams;
import org.apache.streampipes.dataexplorer.template.QueryTemplates;
import org.apache.streampipes.model.datalake.PageResult;
import org.apache.streampipes.model.datalake.SpQueryResult;
import org.influxdb.dto.QueryResult;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class GetPagingEventsQuery extends ParameterizedDataExplorerQuery<PagingQueryParams, PageResult> {

  private TimeUnit timeUnit;

  public GetPagingEventsQuery(PagingQueryParams queryParams) {
    super(queryParams);
  }

  public GetPagingEventsQuery(PagingQueryParams queryParams, TimeUnit timeUnit) {
    super(queryParams);
    this.timeUnit = timeUnit;
  }

  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    if (this.timeUnit != null) {
      queryBuilder.withTimeUnit(timeUnit);
    }
    if (params.isFilterByDate()) {
      queryBuilder
              .add(QueryTemplates.selectWildcardFrom(params.getIndex()))
              .add(QueryTemplates.whereTimeWithin(params.getStartDate(), params.getEndDate()))
              .add("ORDER BY time LIMIT "
                      + params.getItemsPerPage()
                      + " OFFSET "
                      + params.getPage() * params.getItemsPerPage());
    } else {
      queryBuilder.add(QueryTemplates.selectWildcardFrom(params.getIndex()));
      queryBuilder.add("ORDER BY time LIMIT "
              + params.getItemsPerPage()
              + " OFFSET "
              + params.getPage() * params.getItemsPerPage());
    }
  }

  @Override
  protected PageResult postQuery(QueryResult result) {
    SpQueryResult dataResult = convertResult(result);
    int pageSum = new GetMaxPagesQuery(PagingQueryParams.from(params.getIndex(), params.getItemsPerPage()))
            .executeQuery();

    return new PageResult(dataResult.getTotal(), dataResult.getHeaders(), dataResult.getAllDataSeries().get(0).getRows(), params.getPage(), pageSum, new HashMap<>());
  }
}
