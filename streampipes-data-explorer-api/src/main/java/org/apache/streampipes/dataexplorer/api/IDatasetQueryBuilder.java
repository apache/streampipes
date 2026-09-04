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



import org.apache.streampipes.model.dataset.AggregationFunction;
import org.apache.streampipes.model.dataset.DataLakeQueryOrdering;
import org.apache.streampipes.model.dataset.FilterCondition;
import org.apache.streampipes.model.dataset.FilterExpressionGroup;

import java.util.List;

public interface IDatasetQueryBuilder<T> {

  IDatasetQueryBuilder<T> withAllColumns();

  IDatasetQueryBuilder<T> withSimpleColumn(String columnName);

  IDatasetQueryBuilder<T> withSimpleColumns(List<String> columnNames);

  IDatasetQueryBuilder<T> withAggregatedColumn(String columnName,
                                            AggregationFunction aggregationFunction,
                                            String targetName);

  IDatasetQueryBuilder<T> withAggregatedColumn(String columnName,
                                                AggregationFunction aggregationFunction);

  IDatasetQueryBuilder<T> withStartTime(long startTime);

  IDatasetQueryBuilder<T> withEndTime(long endTime);

  IDatasetQueryBuilder<T> withEndTime(long endTime,
                                   boolean includeEndTime);

  IDatasetQueryBuilder<T> withTimeBoundary(long startTime,
                                        long endTime);

  IDatasetQueryBuilder<T> withFilter(String field,
                                  String operator,
                                  Object value);

  IDatasetQueryBuilder<T> withExclusiveFilter(String field,
                                           String operator,
                                           List<?> values);

  IDatasetQueryBuilder<T> withInclusiveFilter(String field,
                                            String operator,
                                            List<?> values);

  IDatasetQueryBuilder<T> withInclusiveFilter(List<FilterCondition> filterConditions);

  IDatasetQueryBuilder<T> withFilterExpression(FilterExpressionGroup filterExpression);

  IDatasetQueryBuilder<T> withGroupByTime(String timeInterval);

  IDatasetQueryBuilder<T> withGroupByTime(String timeInterval,
                                       String offsetInterval);

  IDatasetQueryBuilder<T> withGroupBy(String column);

  IDatasetQueryBuilder<T> withOrderBy(DataLakeQueryOrdering ordering);

  IDatasetQueryBuilder<T> withLimit(int limit);

  IDatasetQueryBuilder<T> withOffset(int offset);

  IDatasetQueryBuilder<T> withFill(Object fill);

  T build();
}
