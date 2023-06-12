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

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;


public class NumericalTextFilterProcessor extends StreamPipesDataProcessor {

  // number
  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String NUMBER_OPERATION = "number-operation";
  private static final String NUMBER_VALUE = "number-value";
  // text
  private static final String TEXT_MAPPING = "text-mapping";
  private static final String TEXT_OPERATION = "text-operation";
  private static final String TEXT_KEYWORD = "text-keyword";

  private double numberThreshold;
  private NumericalOperator numericalOperator;
  private String numberProperty;
  private String textKeyword;
  private StringOperator textOperator;
  private String textProperty;


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.numericaltextfilter")
        .category(DataProcessorType.FILTER, DataProcessorType.STRING_OPERATOR)
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
        .requiredTextParameterWithLink(Labels.withId(TEXT_KEYWORD), "text")
        .outputStrategy(OutputStrategies.keep())
        .build();

  }

  @Override
  public void onInvocation(ProcessorParams processorParams, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext eventProcessorRuntimeContext) throws SpRuntimeException {

    // number
    this.numberProperty = processorParams.extractor().mappingPropertyValue(NUMBER_MAPPING);
    this.numberThreshold = processorParams.extractor().singleValueParameter(NUMBER_VALUE, Double.class);
    String numberOperation = processorParams.extractor().selectedSingleValue(NUMBER_OPERATION, String.class);

    // text
    this.textProperty = processorParams.extractor().mappingPropertyValue(TEXT_MAPPING);
    this.textKeyword = processorParams.extractor().singleValueParameter(TEXT_KEYWORD, String.class);
    this.textOperator =
        StringOperator.valueOf(processorParams.extractor().selectedSingleValue(TEXT_OPERATION, String.class));

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

    this.numericalOperator = NumericalOperator.valueOf(numOperation);

  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    boolean satisfiesNumberFilter = false;
    boolean satisfiesTextFilter = false;

    Double numbervalue = event.getFieldBySelector(this.numberProperty)
        .getAsPrimitive()
        .getAsDouble();

    String value = event.getFieldBySelector(this.textProperty)
        .getAsPrimitive()
        .getAsString();

    Double threshold = this.numberThreshold;

    if (this.numericalOperator == NumericalOperator.EQ) {
      satisfiesNumberFilter = (Math.abs(numbervalue - threshold) < 0.000001);
    } else if (this.numericalOperator == NumericalOperator.GE) {
      satisfiesNumberFilter = (numbervalue >= threshold);
    } else if (this.numericalOperator == NumericalOperator.GT) {
      satisfiesNumberFilter = numbervalue > threshold;
    } else if (this.numericalOperator == NumericalOperator.LE) {
      satisfiesNumberFilter = (numbervalue <= threshold);
    } else if (this.numericalOperator == NumericalOperator.LT) {
      satisfiesNumberFilter = (numbervalue < threshold);
    } else if (this.numericalOperator == NumericalOperator.IE) {
      satisfiesNumberFilter = (Math.abs(numbervalue - threshold) > 0.000001);
    }

    if (this.textOperator == StringOperator.MATCHES) {
      satisfiesTextFilter = (value.equals(this.textKeyword));
    } else if (this.textOperator == StringOperator.CONTAINS) {
      satisfiesTextFilter = (value.contains(this.textKeyword));
    }

    if (satisfiesNumberFilter && satisfiesTextFilter) {
      spOutputCollector.collect(event);
    }
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
