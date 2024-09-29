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

import org.apache.streampipes.dataexplorer.api.IDataLakeQueryBuilder;
import org.apache.streampipes.dataexplorer.api.IQueryStatement;
import org.apache.streampipes.dataexplorer.param.ProvidedRestQueryParamConverter;
import org.apache.streampipes.model.datalake.AggregationFunction;

public class SelectColumn implements IQueryStatement {

  private final String originalField;
  private AggregationFunction aggregationFunction;
  private String targetField;

  private boolean simpleField = true;
  private boolean rename = false;

  public SelectColumn(String originalField) {
    this.originalField = originalField;
  }

  public SelectColumn(String originalField, AggregationFunction aggregationFunction) {
    this(originalField);
    this.aggregationFunction = aggregationFunction;
    this.simpleField = false;
  }

  public SelectColumn(String originalField, AggregationFunction aggregationFunction, String targetField) {
    this(originalField, aggregationFunction);
    this.targetField = targetField;
    this.rename = true;
  }

  public static SelectColumn fromApiQueryString(String queryString) {
    if (queryString.contains(";")) {
      String[] queryParts = ProvidedRestQueryParamConverter.buildSingleCondition(queryString);
      if (queryParts.length < 2) {
        throw new IllegalArgumentException("Wrong query format for query part " + queryString);
      } else {
        AggregationFunction aggregationFunction = AggregationFunction.valueOf(queryParts[1]);
        String targetField = queryParts.length == 3
                ? queryParts[2]
                : aggregationFunction.name().toLowerCase() + "_" + queryParts[0];
        return new SelectColumn(queryParts[0], aggregationFunction, targetField);
      }
    } else {
      return new SelectColumn(queryString);
    }
  }

  public static SelectColumn fromApiQueryString(String queryString, String globalAggregationFunction) {
    SelectColumn column = SelectColumn.fromApiQueryString(queryString);
    AggregationFunction aggregationFunction = AggregationFunction.valueOf(globalAggregationFunction);
    column.setAggregationFunction(aggregationFunction);
    column.setTargetField(aggregationFunction.name().toLowerCase() + "_" + column.getOriginalField());
    column.setRename(true);
    column.setSimpleField(false);
    return column;
  }

  public String getOriginalField() {
    return originalField;
  }

  public void setAggregationFunction(AggregationFunction aggregationFunction) {
    this.aggregationFunction = aggregationFunction;
  }

  public void setTargetField(String targetField) {
    this.targetField = targetField;
  }

  public void setSimpleField(boolean simpleField) {
    this.simpleField = simpleField;
  }

  public void setRename(boolean rename) {
    this.rename = rename;
  }

  @Override
  public void buildStatement(IDataLakeQueryBuilder<?> builder) {
    if (this.simpleField) {
      builder.withSimpleColumn(this.originalField);
    } else {
      if (this.rename) {
        builder.withAggregatedColumn(this.originalField, this.aggregationFunction, this.targetField);
      } else {
        builder.withAggregatedColumn(this.originalField, this.aggregationFunction);
      }
    }
  }
}
