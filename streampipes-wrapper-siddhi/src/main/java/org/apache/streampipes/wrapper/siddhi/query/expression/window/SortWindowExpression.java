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
package org.apache.streampipes.wrapper.siddhi.query.expression.window;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.PropertyExpression;

import io.siddhi.query.api.execution.query.selection.OrderByAttribute;

public class SortWindowExpression extends WindowExpression {

  private final PropertyExpression property;
  private final OrderByAttribute.Order order;

  public SortWindowExpression(Integer number, PropertyExpression property, OrderByAttribute.Order order) {
    super(number);
    this.property = property;
    this.order = order;
  }

  @Override
  public String toSiddhiEpl() {
    return join(SiddhiConstants.EMPTY,
        windowExpression(),
        "sort",
        windowValue(join(SiddhiConstants.COMMA,
            String.valueOf(windowValue),
            property.toSiddhiEpl(),
            join(SiddhiConstants.EMPTY, "'", order.toString().toLowerCase(), "'"))));
  }
}
