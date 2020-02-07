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

package org.apache.streampipes.processors.filters.jvm.processor.numericaltextfilter;

import org.apache.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.model.staticproperty.Option;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.extractor.StaticPropertyExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class NumericalTextFilterController extends StandaloneEventProcessingDeclarer<NumericalTextFilterParameters> {

  // number
  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String NUMBER_OPERATION = "number-operation";
  private static final String NUMBER_VALUE = "number-value";
  // text
  private static final String TEXT_MAPPING = "text-mapping";
  private static final String TEXT_OPERATION = "text-operation";
  private static final String TEXT_KEYWORD = "text-keyword";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.numericaltextfilter")
            .category(DataProcessorType.FILTER)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                            Labels.withId(NUMBER_MAPPING),
                            PropertyScope.MEASUREMENT_PROPERTY)
                    .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                            Labels.withId(TEXT_MAPPING), PropertyScope.NONE)
                    .build())
            .requiredSingleValueSelection(Labels.withId(NUMBER_OPERATION), Options.from("<", "<=", ">",
                    ">=", "==", "!="))
            .requiredFloatParameter(Labels.withId(NUMBER_VALUE), NUMBER_MAPPING)
            .requiredSingleValueSelection(Labels.withId(TEXT_OPERATION), Options.from("MATCHES",
                    "CONTAINS"))
            .requiredTextParameter(Labels.withId(TEXT_KEYWORD), "text")
            .outputStrategy(OutputStrategies.keep())
            .build();

  }

  @Override
  public ConfiguredEventProcessor<NumericalTextFilterParameters> onInvocation
          (DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    // number
    String numberProperty = extractor.mappingPropertyValue(NUMBER_MAPPING);
    Double numberThreshold = extractor.singleValueParameter(NUMBER_VALUE, Double.class);
    String numberOperation = extractor.selectedSingleValue(NUMBER_OPERATION, String.class);

    // text
    String textProperty = extractor.mappingPropertyValue(TEXT_MAPPING);
    String textKeyword = extractor.singleValueParameter(TEXT_KEYWORD, String.class);
    String textOperation = extractor.selectedSingleValue(TEXT_OPERATION, String.class);

    String numOperation = "GT";

    if (numberOperation.equals("<=")) {
      numOperation = "LE";
    } else if (numberOperation.equals("<")) {
      numOperation = "LT";
    } else if (numberOperation.equals(">=")) {
      numOperation = "GE";
    } else if (numberOperation.equals("==")) {
      numOperation = "EQ";
    } else if (numberOperation.equals("!=")) {
      numOperation = "IE";
    }


    NumericalTextFilterParameters staticParam = new NumericalTextFilterParameters(
            graph,
            numberThreshold,
            NumericalOperator.valueOf(numOperation),
            numberProperty,
            textKeyword,
            StringOperator.valueOf(textOperation),
            textProperty);

    return new ConfiguredEventProcessor<>(staticParam, NumericalTextFilter::new);
  }

}
