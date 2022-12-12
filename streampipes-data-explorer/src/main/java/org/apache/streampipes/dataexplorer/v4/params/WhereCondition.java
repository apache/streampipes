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

import java.util.StringJoiner;

public class WhereCondition {

  private String field;
  private String operator;
  private String condition;

  public WhereCondition(String field, String operator, String condition) {
    this.field = field;
    this.operator = operator;
    this.condition = condition;
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(" ");

    return joiner
        .add(field)
        .add(operator)
        .add(condition)
        .toString();
  }

  public String getField() {
    return field;
  }

  public String getOperator() {
    return operator;
  }

  public String getCondition() {
    return condition;
  }
}
