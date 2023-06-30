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

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfig;
import org.apache.streampipes.wrapper.siddhi.SiddhiAppConfigBuilder;
import org.apache.streampipes.wrapper.siddhi.SiddhiQueryBuilder;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
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

import static org.apache.streampipes.wrapper.siddhi.utils.SiddhiUtils.prepareName;

public class TrendProcessor extends StreamPipesSiddhiProcessor {

  private static final String Mapping = "mapping";

  public static final String INCREASE = "increase";
  public static final String OPERATION = "operation";
  public static final String DURATION = "duration";

  public TrendProcessor() {
    super();
  }

  public TrendProcessor(SiddhiDebugCallback callback) {
    super(callback);
  }

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.increase")
        .withLocales(Locales.EN)
        .category(DataProcessorType.PATTERN_DETECT)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(), Labels.withId
                (Mapping), PropertyScope.MEASUREMENT_PROPERTY)
            .build())
        .requiredSingleValueSelection(Labels.withId(OPERATION), Options
            .from(TrendOperator.INCREASE.getLabel(), TrendOperator.DECREASE.getLabel()))
        .requiredIntegerParameter(Labels.withId(INCREASE), 0, 500, 1)
        .requiredIntegerParameter(Labels.withId(DURATION))
        .outputStrategy(OutputStrategies.custom())
        .build();
  }

  private TrendOperator getOperation(String operation) {
    if (operation.equals("Increase")) {
      return TrendOperator.INCREASE;
    } else {
      return TrendOperator.DECREASE;
    }
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
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

  public FromClause fromStatement(SiddhiProcessorParams siddhiParams) {
    var extractor = siddhiParams.getParams().extractor();
    var operation = getOperation(extractor.selectedSingleValue(OPERATION, String.class));
    double increase = extractor.singleValueParameter(INCREASE, Double.class);
    int duration = extractor.singleValueParameter(DURATION, Integer.class);
    String mapping = extractor.mappingPropertyValue(Mapping);


    String mappingProperty = prepareName(mapping);
    increase = (increase / 100) + 1;

    FromClause fromClause = FromClause.create();
    StreamExpression exp1 = Expressions.every(
        Expressions.stream("e1", siddhiParams.getInputStreamNames().get(0)));
    StreamExpression exp2 = Expressions.stream("e2", siddhiParams.getInputStreamNames().get(0));

    PropertyExpressionBase mathExp = operation == TrendOperator.INCREASE
        ? Expressions.divide(Expressions.property(mappingProperty), Expressions.staticValue(increase)) :
        Expressions.multiply(Expressions.property(mappingProperty), Expressions.staticValue(increase));

    RelationalOperatorExpression opExp = operation == TrendOperator.INCREASE
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

  private SelectClause selectStatement(SiddhiProcessorParams siddhiParams) {
    List<String> outputFieldSelectors = siddhiParams.getParams().extractor().outputKeySelectors();
    SelectClause selectClause = SelectClause.create();
    outputFieldSelectors
        .forEach(outputFieldSelector -> selectClause
            .addProperty(Expressions.property("e2", outputFieldSelector, "last")));

    return selectClause;
  }
}
