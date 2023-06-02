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
package org.apache.streampipes.processors.changedetection.jvm.welford;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.Arrays;

public class WelfordChangeDetection extends StreamPipesDataProcessor {

  private static final String NUMBER_MAPPING = "number-mapping";
  private static final String PARAM_K = "param-k";
  private static final String PARAM_H = "param-h";

  private String selectedNumberMapping;
  private Double k;
  private Double h;
  private Double cuSumLow;
  private Double cuSumHigh;
  private WelfordAggregate welfordAggregate;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.changedetection.jvm.welford")
        .category(DataProcessorType.VALUE_OBSERVER)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .withLocales(Locales.EN)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                Labels.withId(NUMBER_MAPPING),
                PropertyScope.NONE).build())
        .requiredFloatParameter(Labels.withId(PARAM_K), 0.0f, 0.0f, 100.0f, 0.01f)
        .requiredFloatParameter(Labels.withId(PARAM_H), 0.0f, 0.0f, 100.0f, 0.01f)
        .outputStrategy(
            OutputStrategies.append(
                Arrays.asList(
                    EpProperties.numberEp(Labels.empty(), WelfordEventFields.VAL_LOW.label, SO.NUMBER),
                    EpProperties.numberEp(Labels.empty(), WelfordEventFields.VAL_HIGH.label, SO.NUMBER),
                    EpProperties.booleanEp(Labels.empty(), WelfordEventFields.DECISION_LOW.label, SO.BOOLEAN),
                    EpProperties.booleanEp(Labels.empty(), WelfordEventFields.DECISION_HIGH.label, SO.BOOLEAN)
                )
            ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters, SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {

    ProcessingElementParameterExtractor extractor = parameters.extractor();
    this.selectedNumberMapping = extractor.mappingPropertyValue(NUMBER_MAPPING);
    this.k = extractor.singleValueParameter(PARAM_K, Double.class);
    this.h = extractor.singleValueParameter(PARAM_H, Double.class);
    this.cuSumLow = 0.0;
    this.cuSumHigh = 0.0;
    this.welfordAggregate = new WelfordAggregate();

  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {

    Double number = event.getFieldBySelector(selectedNumberMapping).getAsPrimitive().getAsDouble();
    welfordAggregate.update(number);  // update mean and standard deviation
    Double normalized = getZScoreNormalizedValue(number);
    updateStatistics(normalized);

    Boolean isChangeHigh = getTestResult(this.cuSumHigh, h);
    Boolean isChangeLow = getTestResult(this.cuSumLow, h);

    Event updatedEvent = updateEvent(event, this.cuSumLow, this.cuSumHigh, isChangeLow, isChangeHigh);
    collector.collect(updatedEvent);

    if (isChangeHigh || isChangeLow) {
      resetAfterChange();
    }

  }

  @Override
  public void onDetach() throws SpRuntimeException {
    this.cuSumLow = 0.0;
    this.cuSumHigh = 0.0;
  }

  private Double getZScoreNormalizedValue(Double value) {
    Double mean = welfordAggregate.getMean();
    Double std = welfordAggregate.getSampleStd();
    return (value - mean) / std;
  }

  private void updateStatistics(Double newValue) {
    if (newValue.isNaN()) {
      return;
    }
    this.cuSumHigh = Math.max(0, this.cuSumHigh + newValue - k);
    this.cuSumLow = Math.min(0, this.cuSumLow + newValue + k);
  }

  private Boolean getTestResult(Double cusum, Double h) {
    return Math.abs(cusum) > this.h;
  }

  private Event updateEvent(Event event, Double cusumLow, Double cusumHigh, Boolean decisionLow, Boolean decisionHigh) {
    event.addField(WelfordEventFields.VAL_LOW.label, cusumLow);
    event.addField(WelfordEventFields.VAL_HIGH.label, cusumHigh);
    event.addField(WelfordEventFields.DECISION_LOW.label, decisionLow);
    event.addField(WelfordEventFields.DECISION_HIGH.label, decisionHigh);
    return event;
  }

  private void resetAfterChange() {
    this.cuSumHigh = 0.0;
    this.cuSumLow = 0.0;
    welfordAggregate = new WelfordAggregate();
  }
}
