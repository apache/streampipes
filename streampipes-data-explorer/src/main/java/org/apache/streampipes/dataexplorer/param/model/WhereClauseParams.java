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
import org.apache.streampipes.model.datalake.FilterCondition;

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class WhereClauseParams implements IQueryStatement {

  private static final String GT = ">";
  private static final String LT = "<";

  private final List<FilterCondition> filterConditions;

  private WhereClauseParams(Long startTime,
                            Long endTime,
                            String whereConditions) {
    this(startTime, endTime);
    if (whereConditions != null) {
      buildConditions(whereConditions);
    }
  }

  private WhereClauseParams(Long startTime,
                            Long endTime) {
    this.filterConditions = new ArrayList<>();
    this.buildTimeConditions(startTime, endTime);
  }

  private WhereClauseParams(String whereConditions) {
    this.filterConditions = new ArrayList<>();
    if (whereConditions != null) {
      buildConditions(whereConditions);
    }
  }

  public static WhereClauseParams from(Long startTime,
                                       Long endTime) {
    return new WhereClauseParams(startTime, endTime);
  }

  public static WhereClauseParams from(String whereConditions) {
    return new WhereClauseParams(whereConditions);
  }

  public static WhereClauseParams from(Long startTime,
                                       Long endTime,
                                       String whereConditions) {
    return new WhereClauseParams(startTime, endTime, whereConditions);
  }

  private void buildTimeConditions(Long startTime,
                                   Long endTime) {
    if (startTime == null) {
      this.filterConditions.add(buildTimeBoundary(endTime, LT));
    } else if (endTime == null) {
      this.filterConditions.add(buildTimeBoundary(startTime, GT));
    } else {
      this.filterConditions.add(buildTimeBoundary(endTime, LT));
      this.filterConditions.add(buildTimeBoundary(startTime, GT));
    }
  }

  private FilterCondition buildTimeBoundary(Long time, String operator) {
    return new FilterCondition("time", operator, time * 1000000);
  }

  private void buildConditions(String whereConditions) {
    List<String[]> whereParts = ProvidedRestQueryParamConverter.buildConditions(whereConditions);
    // Add single quotes to strings except for true and false
    whereParts.forEach(singleCondition -> {

      this.filterConditions.add(
          new FilterCondition(singleCondition[0], singleCondition[1], this.returnCondition(singleCondition[2])));
    });
  }

  private Object returnCondition(String inputCondition) {
    if (NumberUtils.isParsable(inputCondition)) {
      return Double.parseDouble(inputCondition);
    } else if (isBoolean(inputCondition)) {
      return Boolean.parseBoolean(inputCondition);
    } else {
      return inputCondition;
    }
  }

  private boolean isBoolean(String input) {
    return "true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input);
  }

  public List<FilterCondition> getWhereConditions() {
    return filterConditions;
  }

  @Override
  public void buildStatement(IDataLakeQueryBuilder<?> builder) {
    builder.withInclusiveFilter(filterConditions);
  }
}
