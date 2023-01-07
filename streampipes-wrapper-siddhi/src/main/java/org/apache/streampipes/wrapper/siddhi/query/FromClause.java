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
package org.apache.streampipes.wrapper.siddhi.query;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FromClause extends SiddhiStatement {

  private final List<Expression> fromExpressions;

  private FromClause() {
    this.fromExpressions = new ArrayList<>();
  }

  public static FromClause create() {
    return new FromClause();
  }

  public void add(Expression expression) {
    this.fromExpressions.add(expression);
  }

  @Override
  public String toSiddhiEpl() {
    List<String> fromExpressions =
        this.fromExpressions.stream().map(Expression::toSiddhiEpl).collect(Collectors.toList());
    return join(SiddhiConstants.WHITESPACE, SiddhiConstants.FROM, join(SiddhiConstants.COMMA, fromExpressions));
  }
}
