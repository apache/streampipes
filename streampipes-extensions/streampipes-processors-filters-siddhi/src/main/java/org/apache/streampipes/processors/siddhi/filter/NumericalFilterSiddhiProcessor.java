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
package org.apache.streampipes.processors.siddhi.filter;

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
import org.apache.streampipes.wrapper.siddhi.constants.SiddhiStreamSelector;
import org.apache.streampipes.wrapper.siddhi.engine.StreamPipesSiddhiProcessor;
import org.apache.streampipes.wrapper.siddhi.model.SiddhiProcessorParams;
import org.apache.streampipes.wrapper.siddhi.query.FromClause;
import org.apache.streampipes.wrapper.siddhi.query.InsertIntoClause;
import org.apache.streampipes.wrapper.siddhi.query.SelectClause;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expression;
import org.apache.streampipes.wrapper.siddhi.query.expression.Expressions;
import org.apache.streampipes.wrapper.siddhi.query.expression.RelationalOperator;
import org.apache.streampipes.wrapper.siddhi.query.expression.RelationalOperatorExpression;

public class NumericalFilterSiddhiProcessor extends StreamPipesSiddhiProcessor {

  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String VALUE = "value";
  private static final String OPERATION = "operation";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.numericalfilter")
        .category(DataProcessorType.FILTER)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                Labels.withId(NUMBER_MAPPING), PropertyScope.NONE).build())
        .requiredSingleValueSelection(Labels.withId(OPERATION), Options.from("<", "<=", ">",
            ">=", "==", "!="))
        .requiredFloatParameter(Labels.withId(VALUE), NUMBER_MAPPING)
        //.outputStrategy(OutputStrategies.keep())
        .outputStrategy(OutputStrategies.custom())
        .build();
  }

  private Expression makeProperty(String fieldName) {
    return Expressions.property(SiddhiStreamSelector.FIRST_INPUT_STREAM, fieldName);
  }

  @Override
  public SiddhiAppConfig makeStatements(SiddhiProcessorParams siddhiParams,
                                        String finalInsertIntoStreamName) {

    var extractor = siddhiParams.getParams().extractor();
    Double threshold = extractor.singleValueParameter(VALUE, Double.class);
    String stringOperation = extractor.selectedSingleValue(OPERATION, String.class);

    RelationalOperator operator = RelationalOperator.GREATER_THAN;

    if (stringOperation.equals("<=")) {
      operator = RelationalOperator.LESSER_EQUALS;
    } else if (stringOperation.equals("<")) {
      operator = RelationalOperator.LESSER_THAN;
    } else if (stringOperation.equals(">=")) {
      operator = RelationalOperator.GREATER_EQUALS;
    } else if (stringOperation.equals("==")) {
      operator = RelationalOperator.EQUALS;
    } else if (stringOperation.equals("!=")) {
      operator = RelationalOperator.NOT_EQUALS;
    }

    String filterProperty = extractor.mappingPropertyValue(NUMBER_MAPPING);

    // e.g. Filter for numberField value less than 10 and output all fields
    //
    // Siddhi query: from inputstreamname[numberField<10]
    //return "from " + siddhiParams.getInputStreamNames().get(0) +
    // "[" + filterProperty + filterOperator + filterParameters.getThreshold() +"]";

    FromClause fromClause = FromClause.create();
    Expression filter = new RelationalOperatorExpression(operator, Expressions.property(filterProperty),
        Expressions.staticValue(threshold));
    Expression stream = Expressions.filter(Expressions.stream(siddhiParams.getInputStreamNames().get(0)), filter);

    fromClause.add(stream);

    SelectClause selectClause = SelectClause.create();
    siddhiParams
        .getOutputEventKeys()
        .forEach(fieldName -> selectClause.addProperty(makeProperty(fieldName)));

    InsertIntoClause insertIntoClause = InsertIntoClause.create(finalInsertIntoStreamName);

    return SiddhiAppConfigBuilder
        .create()
        .addQuery(SiddhiQueryBuilder
            .create(fromClause, insertIntoClause)
            .withSelectClause(selectClause)
            .build())
        .build();
  }
}
