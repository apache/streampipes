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

package org.apache.streampipes.processors.filters.jvm.processor.numericalfilter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.IStreamPipesDataProcessor;
import org.apache.streampipes.extensions.api.pe.config.IDataProcessorConfiguration;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.param.IDataProcessorParameters;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.builder.processor.DataProcessorConfiguration;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;

public class NumericalFilterProcessor implements IStreamPipesDataProcessor {

  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String VALUE = "value";
  private static final String OPERATION = "operation";

  private double threshold;
  private NumericalOperator numericalOperator;
  private String filterProperty;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        NumericalFilterProcessor::new,
        ProcessingElementBuilder.create("org.apache.streampipes.processors.filters.jvm.numericalfilter")
            .category(DataProcessorType.FILTER)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                    Labels.withId(NUMBER_MAPPING),
                    PropertyScope.NONE).build())
            .outputStrategy(OutputStrategies.keep())
            .requiredSingleValueSelection(Labels.withId(OPERATION), Options.from("<", "<=", ">",
                ">=", "==", "!="))
            .requiredFloatParameter(Labels.withId(VALUE))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters params,
                                SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    this.threshold = params.extractor().singleValueParameter(VALUE, Double.class);
    String stringOperation = params.extractor().selectedSingleValue(OPERATION, String.class);
    String operation = "GT";

    if (stringOperation.equals("<=")) {
      operation = "LE";
    } else if (stringOperation.equals("<")) {
      operation = "LT";
    } else if (stringOperation.equals(">=")) {
      operation = "GE";
    } else if (stringOperation.equals("==")) {
      operation = "EQ";
    } else if (stringOperation.equals("!=")) {
      operation = "IE";
    }

    this.numericalOperator = NumericalOperator.valueOf(operation);

    this.filterProperty = params.extractor().mappingPropertyValue(NUMBER_MAPPING);

  }

  @Override
  public void onEvent(Event event, SpOutputCollector spOutputCollector) throws SpRuntimeException {
    Boolean satisfiesFilter = false;

    Double value = event.getFieldBySelector(this.filterProperty).getAsPrimitive()
        .getAsDouble();

    Double threshold = this.threshold;

    if (this.numericalOperator == NumericalOperator.EQ) {
      satisfiesFilter = (Math.abs(value - threshold) < 0.000001);
    } else if (this.numericalOperator == NumericalOperator.GE) {
      satisfiesFilter = (value >= threshold);
    } else if (this.numericalOperator == NumericalOperator.GT) {
      satisfiesFilter = value > threshold;
    } else if (this.numericalOperator == NumericalOperator.LE) {
      satisfiesFilter = (value <= threshold);
    } else if (this.numericalOperator == NumericalOperator.LT) {
      satisfiesFilter = (value < threshold);
    } else if (this.numericalOperator == NumericalOperator.IE) {
      satisfiesFilter = (Math.abs(value - threshold) > 0.000001);
    }

    if (satisfiesFilter) {
      spOutputCollector.collect(event);
    }
  }

  @Override
  public void onPipelineStopped() {

  }
}
