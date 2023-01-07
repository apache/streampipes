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

package org.apache.streampipes.dataexplorer.v4.params;


import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SelectFromStatementParams extends QueryParamsV4 {

  private List<SelectColumn> selectedColumns;
  private boolean selectWildcard = false;

  public SelectFromStatementParams(String measurementID) {
    super(measurementID);
    this.selectWildcard = true;
    //this.selectedColumns = "*";
  }

  public SelectFromStatementParams(String measurementId,
                                   String columns,
                                   boolean countOnly) {
    this(measurementId);
    this.selectWildcard = false;
    this.selectedColumns = countOnly ? buildColumns(columns, ColumnFunction.COUNT.name()) : buildColumns(columns);
  }

  public SelectFromStatementParams(String measurementID,
                                   String columns,
                                   String aggregationFunction) {
    super(measurementID);

    if (columns != null) {
      this.selectedColumns =
          aggregationFunction != null ? buildColumns(columns, aggregationFunction) : buildColumns(columns);
    } else {
      this.selectWildcard = true;
    }
  }

  public static SelectFromStatementParams from(String measurementID,
                                               @Nullable String columns,
                                               @Nullable String aggregationFunction) {
    return new SelectFromStatementParams(measurementID, columns, aggregationFunction);
  }

  public static SelectFromStatementParams from(String measurementId,
                                               String columns,
                                               boolean countOnly) {
    return new SelectFromStatementParams(measurementId, columns, countOnly);
  }

  public List<SelectColumn> getSelectedColumns() {
    return selectedColumns;
  }

  //public String getAggregationFunction() {
  //return aggregationFunction;
  //}

  public boolean isSelectWildcard() {
    return selectWildcard;
  }

  private List<SelectColumn> buildColumns(String rawQuery) {
    return Arrays.stream(rawQuery.split(",")).map(SelectColumn::fromApiQueryString).collect(Collectors.toList());
  }

  private List<SelectColumn> buildColumns(String rawQuery, String globalAggregationFunction) {
    return Arrays.stream(rawQuery.split(",")).map(qp -> SelectColumn.fromApiQueryString(qp, globalAggregationFunction))
        .collect(Collectors.toList());
  }
}
