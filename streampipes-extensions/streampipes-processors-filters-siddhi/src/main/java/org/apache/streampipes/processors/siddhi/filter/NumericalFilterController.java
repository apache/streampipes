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
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.siddhi.query.expression.RelationalOperator;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class NumericalFilterController extends StandaloneEventProcessingDeclarer<NumericalFilterParameters> {

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

  @Override
  public ConfiguredEventProcessor<NumericalFilterParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

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

    NumericalFilterParameters staticParam = new NumericalFilterParameters(graph,
            threshold,
            operator,
            filterProperty);

    return new ConfiguredEventProcessor<>(staticParam, NumericalFilter::new);
  }

}
