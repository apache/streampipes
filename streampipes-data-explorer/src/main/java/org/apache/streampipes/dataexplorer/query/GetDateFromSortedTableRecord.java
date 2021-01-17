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

import org.apache.streampipes.dataexplorer.model.Order;
import org.apache.streampipes.dataexplorer.param.QueryParams;
import org.apache.streampipes.dataexplorer.template.QueryTemplates;
import org.influxdb.dto.QueryResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GetDateFromSortedTableRecord extends ParameterizedDataExplorerQuery<QueryParams, Long> {

  private SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  private SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private Order order;

  public GetDateFromSortedTableRecord(QueryParams queryParams, Order order) {
    super(queryParams);
    this.order = order;
  }

  @Override
  protected void getQuery(DataExplorerQueryBuilder queryBuilder) {
    queryBuilder.add(QueryTemplates.selectWildcardFrom(params.getIndex()));
    queryBuilder.add("ORDER BY "
            + order.toValue()
            + " LIMIT 1");
  }

  @Override
  protected Long postQuery(QueryResult result) throws RuntimeException {
    int timestampIndex = result.getResults().get(0).getSeries().get(0).getColumns().indexOf("time");
    String stringDate = result.getResults().get(0).getSeries().get(0).getValues().get(0).get(timestampIndex).toString();

    try {
      Date date = tryParseDate(stringDate);
      return date.getTime();
    } catch (ParseException e) {
      throw new RuntimeException("Could not parse date");
    }
  }

  private Date tryParseDate(String v) throws ParseException {
    try {
      return dateFormat1.parse(v);
    } catch (ParseException e) {
      return dateFormat2.parse(v);
    }
  }
}
