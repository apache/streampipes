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

package org.apache.streampipes.dataexplorer.param.model;


import org.apache.streampipes.dataexplorer.api.IQueryStatement;
import org.apache.streampipes.dataexplorer.querybuilder.IDataLakeQueryBuilder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SelectClauseParams implements IQueryStatement {

  private List<SelectColumn> selectedColumns;
  private List<SelectColumn> selectedColumnsCountOnly;
  private boolean selectWildcard = false;

  public SelectClauseParams() {
    this.selectWildcard = true;
  }

  public SelectClauseParams(String columns,
                            boolean countOnly) {
    this.selectWildcard = false;
    this.selectedColumns = countOnly ? buildColumns(columns, AggregationFunction.COUNT.name()) : buildColumns(columns);
    this.selectedColumnsCountOnly = buildColumns(columns, AggregationFunction.COUNT.name());
  }

  public SelectClauseParams(String columns,
                            String aggregationFunction) {
    if (columns != null) {
      this.selectedColumns =
          aggregationFunction != null ? buildColumns(columns, aggregationFunction) : buildColumns(columns);
      this.selectedColumnsCountOnly = buildColumns(columns, AggregationFunction.COUNT.name());
    } else {
      this.selectWildcard = true;
    }
  }

  public static SelectClauseParams from(String columns,
                                        String aggregationFunction) {
    return new SelectClauseParams(columns, aggregationFunction);
  }

  public static SelectClauseParams from(String columns,
                                        boolean countOnly) {
    return new SelectClauseParams(columns, countOnly);
  }

  private List<SelectColumn> buildColumns(String rawQuery) {
    return Arrays.stream(rawQuery.split(",")).map(SelectColumn::fromApiQueryString).collect(Collectors.toList());
  }

  private List<SelectColumn> buildColumns(String rawQuery, String globalAggregationFunction) {
    return Arrays.stream(rawQuery.split(",")).map(qp -> SelectColumn.fromApiQueryString(qp, globalAggregationFunction))
        .collect(Collectors.toList());
  }

  @Override
  public void buildStatement(IDataLakeQueryBuilder<?> builder) {
    buildStatement(builder, selectedColumns);
  }

  public void buildCountStatement(IDataLakeQueryBuilder<?> builder) {
    buildStatement(builder, selectedColumnsCountOnly);
  }

  private void buildStatement(IDataLakeQueryBuilder<?> builder,
                              List<SelectColumn> columns) {
    if (selectWildcard) {
      builder.withAllColumns();
    } else {
      columns.forEach(c -> c.buildStatement(builder));
    }
  }
}
