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
package org.apache.streampipes.wrapper.siddhi.query.expression.aggregation;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiConstants;
import org.apache.streampipes.wrapper.siddhi.query.expression.PropertyExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.PropertyExpressionBase;

public class SumExpression extends PropertyExpressionBase {

  private final PropertyExpression propertyExpression;

  public SumExpression(PropertyExpression property) {
    this.propertyExpression = property;
  }

  @Override
  public String toSiddhiEpl() {
    return join(SiddhiConstants.EMPTY,
        AggregationFunction.SUM.toAggregationFunction(),
        SiddhiConstants.PARENTHESIS_OPEN,
        propertyExpression.toSiddhiEpl(),
        SiddhiConstants.PARENTHESIS_CLOSE);
  }
}
