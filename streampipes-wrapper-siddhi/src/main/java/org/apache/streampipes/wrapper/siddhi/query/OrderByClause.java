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
import org.apache.streampipes.wrapper.siddhi.query.expression.orderby.OrderByExpression;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class OrderByClause extends SiddhiStatement {

  private final List<OrderByExpression> orderByExpressions;

  public static OrderByClause create(OrderByExpression... orderByExpressions) {
    return new OrderByClause(Arrays.asList(orderByExpressions));
  }

  private OrderByClause(List<OrderByExpression> orderByExpressions) {
    this.orderByExpressions = orderByExpressions;
  }

  @Override
  public String toSiddhiEpl() {
    return join(SiddhiConstants.WHITESPACE,
            "order by",
            join(SiddhiConstants.COMMA, orderByExpressions
                    .stream()
                    .map(OrderByExpression::toSiddhiEpl)
                    .collect(Collectors.toList())));
  }
}
