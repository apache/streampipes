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

import org.apache.streampipes.dataexplorer.param.TimeUnitQueryParams;
import org.apache.streampipes.dataexplorer.template.QueryTemplates;
import org.influxdb.dto.QueryResult;

public class GetNumberOfRecordsByTimeUnitQuery extends ParameterizedDataExplorerQuery<TimeUnitQueryParams, Double> {

  public GetNumberOfRecordsByTimeUnitQuery(TimeUnitQueryParams queryParams) {
    super(queryParams);
  }

  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    queryBuilder.add(QueryTemplates.selectCountFrom(params.getIndex()));
    queryBuilder.add("WHERE time > now() -"
            + params.getTimeValue()
            + params.getTimeUnit());
  }

  @Override
  protected Double postQuery(QueryResult result) {
    double numOfRecords = 0;
    if (result.getResults().get(0).getSeries() == null) {
      return numOfRecords;
    }

    for (Object item : result.getResults().get(0).getSeries().get(0).getValues().get(0)) {
      if (item instanceof Double) {
        numOfRecords = Double.parseDouble(item.toString());
      }
    }
    return numOfRecords;
  }
}
