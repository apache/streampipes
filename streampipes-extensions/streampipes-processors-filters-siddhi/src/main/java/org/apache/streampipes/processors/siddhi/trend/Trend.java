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
package org.apache.streampipes.processors.siddhi.trend;

import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.SiddhiEventEngine;
import org.apache.streampipes.wrapper.siddhi.engine.callback.SiddhiDebugCallback;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;
import org.apache.streampipes.wrapper.siddhi.query.expression.PropertyExpressionBase;
import org.apache.streampipes.wrapper.siddhi.query.expression.RelationalOperatorExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.SiddhiTimeUnit;
import org.apache.streampipes.wrapper.siddhi.query.expression.StreamExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.StreamFilterExpression;
import org.apache.streampipes.wrapper.siddhi.query.expression.pattern.PatternCountOperator;

import java.util.List;

public class Trend extends SiddhiEventEngine<TrendParameters> {

  public Trend() {
    super();
  }

  public Trend(SiddhiDebugCallback callback) {
    super(callback);
  }

  public FromClause fromStatement(SiddhiProcessorParams<TrendParameters> siddhiParams) {
    TrendParameters trendParameters = siddhiParams.getParams();

    String mappingProperty = prepareName(trendParameters.getMapping());
    int duration = trendParameters.getDuration();
    double increase = trendParameters.getIncrease();
    increase = (increase / 100) + 1;

    FromClause fromClause = FromClause.create();
    StreamExpression exp1 = Expressions.every(
        Expressions.stream("e1", siddhiParams.getInputStreamNames().get(0)));
    StreamExpression exp2 = Expressions.stream("e2", siddhiParams.getInputStreamNames().get(0));

    PropertyExpressionBase mathExp = trendParameters.getOperation() == TrendOperator.INCREASE
        ? Expressions.divide(Expressions.property(mappingProperty), Expressions.staticValue(increase)) :
        Expressions.multiply(Expressions.property(mappingProperty), Expressions.staticValue(increase));

    RelationalOperatorExpression opExp = trendParameters.getOperation() == TrendOperator.INCREASE
        ? Expressions.le(Expressions.property("e1", mappingProperty), mathExp) :
        Expressions.ge(Expressions.property("e1", mappingProperty), mathExp);

    StreamFilterExpression filterExp = Expressions
        .filter(exp2, Expressions.patternCount(1, PatternCountOperator.EXACTLY_N), opExp);

    Expression sequence = (Expressions.sequence(exp1,
        filterExp,
        Expressions.within(duration, SiddhiTimeUnit.SECONDS)));

    fromClause.add(sequence);

    return fromClause;
  }

  private SelectClause selectStatement(SiddhiProcessorParams<TrendParameters> siddhiParams) {
    SelectClause selectClause = SelectClause.create();
    List<String> outputFieldSelectors = siddhiParams.getParams().getOutputFieldSelectors();
    outputFieldSelectors
        .forEach(outputFieldSelector -> selectClause
            .addProperty(Expressions.property("e2", outputFieldSelector, "last")));

    return selectClause;
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams<TrendParameters> siddhiParams,
                                        String finalInsertIntoStreamName) {

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);

    return SiddhiAppConfigBuilder
        .create()
        .addQuery(SiddhiQueryBuilder
            .create(fromStatement(siddhiParams), insertIntoClause)
            .withSelectClause(selectStatement(siddhiParams))
            .build())
        .build();
  }
}
