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

package org.apache.streampipes.dataexplorer.sdk;

import org.apache.streampipes.dataexplorer.v4.params.ColumnFunction;

import org.influxdb.querybuilder.clauses.NestedClause;

import java.util.List;

public interface IDataLakeQueryBuilder<T> {
  IDataLakeQueryBuilder withSimpleColumn(String columnName);

  IDataLakeQueryBuilder withSimpleColumns(List<String> columnNames);

  IDataLakeQueryBuilder withAggregatedColumn(String columnName,
                                            ColumnFunction columnFunction,
                                            String targetName);

  IDataLakeQueryBuilder withStartTime(long startTime);

  IDataLakeQueryBuilder withEndTime(long endTime);

  IDataLakeQueryBuilder withEndTime(long endTime,
                                   boolean includeEndTime);

  IDataLakeQueryBuilder withTimeBoundary(long startTime,
                                        long endTime);

  IDataLakeQueryBuilder withFilter(String field,
                                  String operator,
                                  Object value);

  IDataLakeQueryBuilder withExclusiveFilter(String field,
                                           String operator,
                                           List<?> values);

  IDataLakeQueryBuilder withInclusiveFilter(String field,
                                            String operator,
                                            List<?> values);

  IDataLakeQueryBuilder withFilter(NestedClause clause);

  IDataLakeQueryBuilder withGroupByTime(String timeInterval);

  IDataLakeQueryBuilder withGroupByTime(String timeInterval,
                                       String offsetInterval);

  IDataLakeQueryBuilder withGroupBy(String column);

  IDataLakeQueryBuilder withOrderBy(DataLakeQueryOrdering ordering);

  IDataLakeQueryBuilder withLimit(int limit);

  IDataLakeQueryBuilder withOffset(int offset);

  T build();
}
