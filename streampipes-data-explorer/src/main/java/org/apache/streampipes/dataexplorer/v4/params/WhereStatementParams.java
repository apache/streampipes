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

import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

public class WhereStatementParams extends QueryParamsV4 {

  private static final String GT = ">";
  private static final String LT = "<";

  private List<WhereCondition> whereConditions;

  private WhereStatementParams(String index,
                               Long startTime,
                               Long endTime,
                               String whereConditions) {
    this(index, startTime, endTime);
    if (whereConditions != null) {
      buildConditions(whereConditions);
    }
  }

  private WhereStatementParams(String index,
                               Long startTime,
                               Long endTime) {
    super(index);
    this.whereConditions = new ArrayList<>();
    this.buildTimeConditions(startTime, endTime);
  }

  private WhereStatementParams(String index,
                               String whereConditions) {
    super(index);
    this.whereConditions = new ArrayList<>();
    if (whereConditions != null) {
      buildConditions(whereConditions);
    }
  }

  public static WhereStatementParams from(String measurementId,
                                          Long startTime,
                                          Long endTime) {
    return new WhereStatementParams(measurementId, startTime, endTime);
  }

  public static WhereStatementParams from(String measurementId,
                                          String whereConditions) {
    return new WhereStatementParams(measurementId, whereConditions);
  }

  public static WhereStatementParams from(String measurementId,
                                          Long startTime,
                                          Long endTime,
                                          String whereConditions) {
    return new WhereStatementParams(measurementId, startTime, endTime, whereConditions);
  }

  private void buildTimeConditions(Long startTime,
                                   Long endTime) {
    if (startTime == null) {
      this.whereConditions.add(buildTimeBoundary(endTime, LT));
    } else if (endTime == null) {
      this.whereConditions.add(buildTimeBoundary(startTime, GT));
    } else {
      this.whereConditions.add(buildTimeBoundary(endTime, LT));
      this.whereConditions.add(buildTimeBoundary(startTime, GT));
    }
  }

  private WhereCondition buildTimeBoundary(Long time, String operator) {
    return new WhereCondition("time", operator, String.valueOf(time * 1000000));
  }

  private void buildConditions(String whereConditions) {
    List<String[]> whereParts = DataLakeManagementUtils.buildConditions(whereConditions);
    // Add single quotes to strings except for true and false
    whereParts.forEach(singleCondition -> {

      this.whereConditions.add(
          new WhereCondition(singleCondition[0], singleCondition[1], this.returnCondition(singleCondition[2])));
    });
  }

  private String returnCondition(String inputCondition) {
    if (NumberUtils.isCreatable(inputCondition) || isBoolean(inputCondition)) {
      return inputCondition;
    } else if (inputCondition.equals("\"\"")) {
      return inputCondition;
    } else {
      return "'" + inputCondition + "'";
    }
  }

  private boolean isBoolean(String input) {
    return "true".equalsIgnoreCase(input) || "false".equalsIgnoreCase(input);
  }

  public List<WhereCondition> getWhereConditions() {
    return whereConditions;
  }
}
