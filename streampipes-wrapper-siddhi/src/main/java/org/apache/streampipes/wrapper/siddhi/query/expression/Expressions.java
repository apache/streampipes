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
package org.apache.streampipes.wrapper.siddhi.query.expression;

import org.apache.streampipes.wrapper.siddhi.constants.SiddhiStreamSelector;
import org.apache.streampipes.wrapper.siddhi.model.EventPropertyDef;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.AndExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.AverageExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.CountExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.DistinctCountExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.MaxExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.MaxForeverExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.MinExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.MinForeverExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.OrExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.StandardDeviationExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.SumExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.aggregation.UnionSetExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.list.CollectListExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.list.ContainsListExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.math.MathDivideExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.math.MathMultiplyExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.orderby.OrderByExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.pattern.EveryExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.pattern.PatternCountExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.pattern.PatternCountOperator;
import org.apache.streampipes.wrapper.siddhi.query.expression.window.BatchWindowExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.window.SortWindowExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.window.TimeBatchWindowExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.window.TimeWindowExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.window.WindowExpression;

import io.siddhi.query.api.execution.query.selection.OrderByAttribute;

import java.util.Arrays;

public class Expressions {

  public static Expression as(PropertyExpressionBase property, String targetName) {
    return new PropertyRenameExpression(property, targetName);
  }

  public static WindowExpression batchWindow(Integer windowSize) {
    return new BatchWindowExpression(windowSize);
  }

  public static PropertyExpressionBase collectList(PropertyExpression propertyExp) {
    return new CollectListExpression(propertyExp);
  }

  public static PropertyExpressionBase containsListItem(PropertyExpression listProperty, Object value) {
    return new ContainsListExpression(listProperty, value);
  }

  public static PropertyExpressionBase count(PropertyExpression property) {
    return new CountExpression(property);
  }

  public static PropertyExpressionBase distinctCount(PropertyExpression property) {
    return new DistinctCountExpression(property);
  }

  public static PropertyExpressionBase sum(PropertyExpression property) {
    return new SumExpression(property);
  }

  public static PropertyExpressionBase max(PropertyExpression property) {
    return new MaxExpression(property);
  }

  public static PropertyExpressionBase maxForever(PropertyExpression property) {
    return new MaxForeverExpression(property);
  }

  public static PropertyExpressionBase min(PropertyExpression property) {
    return new MinExpression(property);
  }

  public static PropertyExpressionBase minForever(PropertyExpression property) {
    return new MinForeverExpression(property);
  }

  public static PropertyExpressionBase avg(PropertyExpression property) {
    return new AverageExpression(property);
  }

  public static PropertyExpressionBase or(PropertyExpression property) {
    return new OrExpression(property);
  }

  public static PropertyExpressionBase and(PropertyExpression property) {
    return new AndExpression(property);
  }

  public static PropertyExpressionBase stdDev(PropertyExpression property) {
    return new StandardDeviationExpression(property);
  }

  public static PropertyExpressionBase unionSet(PropertyExpression property) {
    return new UnionSetExpression(property);
  }

  public static PropertyExpressionBase divide(PropertyExpressionBase op1, PropertyExpressionBase op2) {
    return new MathDivideExpression(op1, op2);
  }

  public static StreamExpression every(StreamExpression streamExpression) {
    return new EveryExpression(streamExpression);
  }

  public static RelationalOperatorExpression eq(PropertyExpressionBase exp1, PropertyExpressionBase exp2) {
    return new EqualsExpression(exp1, exp2);
  }

  public static StreamFilterExpression filter(StreamExpression streamExpression, Expression... filterExpressions) {
    return new StreamFilterExpression(streamExpression, Arrays.asList(filterExpressions));
  }

  public static StreamFilterExpression filter(StreamExpression streamExpression,
                                              PatternCountExpression patternCountExpression,
                                              Expression... filterExpressions) {
    return new StreamFilterExpression(streamExpression,
        Arrays.asList(filterExpressions),
        patternCountExpression);
  }

  public static RelationalOperatorExpression ge(PropertyExpressionBase exp1, PropertyExpressionBase exp2) {
    return new GreaterEqualsExpression(exp1, exp2);
  }

  public static RelationalOperatorExpression gt(PropertyExpressionBase exp1, PropertyExpressionBase exp2) {
    return new GreaterThanExpression(exp1, exp2);
  }

  public static RelationalOperatorExpression le(PropertyExpressionBase exp1, PropertyExpressionBase exp2) {
    return new LesserEqualsExpression(exp1, exp2);
  }

  public static RelationalOperatorExpression lt(PropertyExpressionBase exp1, PropertyExpressionBase exp2) {
    return new LesserThanExpression(exp1, exp2);
  }

  public static PropertyExpressionBase multiply(PropertyExpressionBase op1, PropertyExpressionBase op2) {
    return new MathMultiplyExpression(op1, op2);
  }

  public static OrderByExpression orderBy(PropertyExpression property, OrderByAttribute.Order order) {
    return new OrderByExpression(property, order);
  }

  public static PatternCountExpression patternCount(Integer value, PatternCountOperator op) {
    return new PatternCountExpression(value, op);
  }

  public static PropertyExpression property(String streamName, String propertyName) {
    return new PropertyExpression(streamName, propertyName);
  }

  public static PropertyExpression property(String streamName, String propertyName, String eventIndex) {
    return new PropertyExpression(streamName, propertyName, eventIndex);
  }

  public static PropertyExpression property(String propertyName) {
    return new PropertyExpression(propertyName);
  }

  public static PropertyExpression property(SiddhiStreamSelector selector, String propertyName) {
    return new PropertyExpression(selector, propertyName);
  }

  public static PropertyExpression property(EventPropertyDef propertyDef) {
    return new PropertyExpression(propertyDef);
  }

  public static Expression sequence(StreamExpression expression1, StreamExpression expression2) {
    return new SequenceExpression(Arrays.asList(expression1, expression2));
  }

  public static Expression sequence(StreamExpression expression1,
                                    StreamExpression expression2,
                                    WithinExpression withinExpression) {
    return new SequenceExpression(Arrays.asList(expression1, expression2), withinExpression);
  }

  public static WindowExpression sort(Integer number, PropertyExpression sortBy, OrderByAttribute.Order order) {
    return new SortWindowExpression(number, sortBy, order);
  }

  public static PropertyExpression staticValue(Number staticValue) {
    return new PropertyExpression(String.valueOf(staticValue));
  }

  public static PropertyRenameExpression staticValue(Number staticValue, String propertyName) {
    return new PropertyRenameExpression(Expressions.property(String.valueOf(staticValue)), propertyName);
  }

  public static PropertyRenameExpression staticValue(String staticValue, String propertyName) {
    return new PropertyRenameExpression(Expressions.property(makeStaticString(staticValue)), propertyName);
  }

  public static StreamExpression stream(String streamName) {
    return new StreamExpression(streamName);
  }

  public static StreamExpression stream(String streamName, WindowExpression windowExpression) {
    return new StreamExpression(streamName, windowExpression);
  }

  public static StreamExpression stream(String streamName, String streamAlias) {
    return new StreamExpression(streamName, streamAlias);
  }

  public static WindowExpression timeBatch(Integer windowSize, SiddhiTimeUnit timeUnit) {
    return new TimeBatchWindowExpression(windowSize, timeUnit);
  }

  public static WindowExpression timeWindow(Integer windowSize, SiddhiTimeUnit timeUnit) {
    return new TimeWindowExpression(windowSize, timeUnit);
  }

  private static String makeStaticString(String staticValue) {
    return "'" + staticValue + "'";
  }

  public static WithinExpression within(int duration, SiddhiTimeUnit timeUnit) {
    return new WithinExpression(duration, timeUnit);
  }
}
