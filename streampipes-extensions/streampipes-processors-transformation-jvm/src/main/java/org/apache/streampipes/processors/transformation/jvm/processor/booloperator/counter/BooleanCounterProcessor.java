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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.counter;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.extractor.IDataProcessorParameterExtractor;
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
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;

public class BooleanCounterProcessor implements IStreamPipesDataProcessor {
  public static final String FIELD_ID = "field";
  public static final String FLANK_ID = "flank";
  public static final String COUNT_FIELD_ID = "countField";
  public static final String COUNT_FIELD_RUNTIME_NAME = "counter";
  private static final String FLANK_UP = "FALSE -> TRUE";
  private static final String FLANK_DOWN = "TRUE -> FALSE";
  private static final String BOTH = "BOTH";
  private String fieldName;
  /**
   * Defines which boolean changes should be counted
   * 0: BOTH
   * 1: TRUE -> FALSE
   * 2: FALSE -> TRUE
   */
  private int flankUp;
  private boolean fieldValueOfLastEvent;
  private int counter;

  @Override
  public IDataProcessorConfiguration declareConfig() {
    return DataProcessorConfiguration.create(
        BooleanCounterProcessor::new,
        ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.booloperator.counter")
            .category(DataProcessorType.BOOLEAN_OPERATOR, DataProcessorType.COUNT_OPERATOR)
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION, Assets.ICON)
            .requiredStream(StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(
                    EpRequirements.booleanReq(),
                    Labels.withId(FIELD_ID),
                    PropertyScope.NONE)
                .build())

            .requiredSingleValueSelection(Labels.withId(FLANK_ID), Options.from(BOTH, FLANK_UP, FLANK_DOWN))
            .outputStrategy(OutputStrategies
                .append(EpProperties
                    .numberEp(
                        Labels.withId(COUNT_FIELD_ID),
                        COUNT_FIELD_RUNTIME_NAME,
                        "http://schema.org/Number")
                ))
            .build()
    );
  }

  @Override
  public void onPipelineStarted(IDataProcessorParameters parameters,
                                SpOutputCollector collector,
                                EventProcessorRuntimeContext runtimeContext) {
    IDataProcessorParameterExtractor extractor = parameters.extractor();
    String flank = extractor.selectedSingleValue(FLANK_ID, String.class);
    this.fieldName = extractor.mappingPropertyValue(FIELD_ID);
    this.flankUp = 0;

    if (flank.equals(FLANK_DOWN)) {
      this.flankUp = 1;
      this.fieldValueOfLastEvent = true;
    } else if (flank.equals(FLANK_UP)) {
      this.flankUp = 2;
      this.fieldValueOfLastEvent = false;
    }

    this.counter = 0;
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector out) throws SpRuntimeException {

    boolean value = inputEvent.getFieldBySelector(fieldName).getAsPrimitive().getAsBoolean();
    boolean updateCounter = false;

    if (this.flankUp == 2) {
      // detect up flanks
      if (!this.fieldValueOfLastEvent && value) {
        updateCounter = true;
      }
    } else if (this.flankUp == 1) {
      // detect up flanks
      if (this.fieldValueOfLastEvent && !value) {
        updateCounter = true;
      }
    } else {
      if (this.fieldValueOfLastEvent != value) {
        updateCounter = true;
      }
    }

    if (updateCounter) {
      this.counter++;
      inputEvent.addField(COUNT_FIELD_RUNTIME_NAME, this.counter);
      out.collect(inputEvent);
    }

    this.fieldValueOfLastEvent = value;
  }

  @Override
  public void onPipelineStopped() {

  }
}
