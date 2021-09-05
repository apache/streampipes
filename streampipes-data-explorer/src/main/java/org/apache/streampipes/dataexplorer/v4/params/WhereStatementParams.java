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

import java.util.ArrayList;
import java.util.List;

public class WhereStatementParams extends QueryParamsV4 {

  private static final String GT = ">";
  private static final String LT = "<";

  private List<WhereCondition> whereConditions;

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
    whereParts.forEach(singleCondition -> {
      this.whereConditions.add(new WhereCondition(singleCondition[0], singleCondition[1], singleCondition[2]));
    });
  }

  public List<WhereCondition> getWhereConditions() {
    return whereConditions;
  }
}
