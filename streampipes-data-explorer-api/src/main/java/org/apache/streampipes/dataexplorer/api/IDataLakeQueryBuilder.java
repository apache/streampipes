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

package org.apache.streampipes.dataexplorer.api;



import org.apache.streampipes.model.datalake.AggregationFunction;
import org.apache.streampipes.model.datalake.DataLakeQueryOrdering;
import org.apache.streampipes.model.datalake.FilterCondition;

import java.util.List;

public interface IDataLakeQueryBuilder<T> {

  IDataLakeQueryBuilder<T> withAllColumns();

  IDataLakeQueryBuilder<T> withSimpleColumn(String columnName);

  IDataLakeQueryBuilder<T> withSimpleColumns(List<String> columnNames);

  IDataLakeQueryBuilder<T> withAggregatedColumn(String columnName,
                                            AggregationFunction aggregationFunction,
                                            String targetName);

  IDataLakeQueryBuilder<T> withAggregatedColumn(String columnName,
                                                AggregationFunction aggregationFunction);

  IDataLakeQueryBuilder<T> withStartTime(long startTime);

  IDataLakeQueryBuilder<T> withEndTime(long endTime);

  IDataLakeQueryBuilder<T> withEndTime(long endTime,
                                   boolean includeEndTime);

  IDataLakeQueryBuilder<T> withTimeBoundary(long startTime,
                                        long endTime);

  IDataLakeQueryBuilder<T> withFilter(String field,
                                  String operator,
                                  Object value);

  IDataLakeQueryBuilder<T> withExclusiveFilter(String field,
                                           String operator,
                                           List<?> values);

  IDataLakeQueryBuilder<T> withInclusiveFilter(String field,
                                            String operator,
                                            List<?> values);

  IDataLakeQueryBuilder<T> withInclusiveFilter(List<FilterCondition> filterConditions);

  IDataLakeQueryBuilder<T> withGroupByTime(String timeInterval);

  IDataLakeQueryBuilder<T> withGroupByTime(String timeInterval,
                                       String offsetInterval);

  IDataLakeQueryBuilder<T> withGroupBy(String column);

  IDataLakeQueryBuilder<T> withOrderBy(DataLakeQueryOrdering ordering);

  IDataLakeQueryBuilder<T> withLimit(int limit);

  IDataLakeQueryBuilder<T> withOffset(int offset);

  IDataLakeQueryBuilder<T> withFill(Object fill);

  T build();
}
