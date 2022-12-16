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

import org.apache.streampipes.dataexplorer.v4.utils.DataLakeManagementUtils;

public class SelectColumn {

  private final String originalField;
  private ColumnFunction columnFunction;
  private String targetField;

  private boolean simpleField = true;
  private boolean rename = false;

  public SelectColumn(String originalField) {
    this.originalField = originalField;
  }

  public SelectColumn(String originalField,
                      ColumnFunction columnFunction) {
    this(originalField);
    this.columnFunction = columnFunction;
    this.simpleField = false;
  }

  public SelectColumn(String originalField,
                      ColumnFunction columnFunction,
                      String targetField) {
    this(originalField, columnFunction);
    this.targetField = targetField;
    this.rename = true;
  }

  public static SelectColumn fromApiQueryString(String queryString) {
    if (queryString.contains(";")) {
      String[] queryParts = DataLakeManagementUtils.buildSingleCondition(queryString);
      if (queryParts.length < 2) {
        throw new IllegalArgumentException("Wrong query format for query part " + queryString);
      } else {
        ColumnFunction columnFunction = ColumnFunction.valueOf(queryParts[1]);
        String targetField =
            queryParts.length == 3 ? queryParts[2] : columnFunction.name().toLowerCase() + "_" + queryParts[0];
        return new SelectColumn(queryParts[0], columnFunction, targetField);
      }
    } else {
      return new SelectColumn(queryString);
    }
  }

  public static SelectColumn fromApiQueryString(String queryString,
                                                String globalAggregationFunction) {
    ColumnFunction columnFunction = ColumnFunction.valueOf(globalAggregationFunction);
    String targetField = columnFunction.name().toLowerCase() + "_" + queryString;
    return new SelectColumn(queryString, ColumnFunction.valueOf(globalAggregationFunction), targetField);
  }

  private String makeField() {
    if (this.simpleField) {
      return "\"" + this.originalField + "\"";
    } else {
      return this.columnFunction.toDbName() + "(\"" + this.originalField + "\")";
    }
  }

  public String toQueryString() {
    String field = makeField();
    if (this.rename) {
      return field + " AS \"" + this.targetField + "\"";
    } else {
      return field;
    }
  }

  public String getOriginalField() {
    return originalField;
  }
}
