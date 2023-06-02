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

package org.apache.streampipes.processors.transformation.jvm.processor.stringoperator.timer;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.logging.api.Logger;
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
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

import java.util.Objects;

public class StringTimerProcessor extends StreamPipesDataProcessor {

  private static Logger log;

  public static final String FIELD_ID = "field";
  public static final String MEASURED_TIME_ID = "measuredTime";
  public static final String FIELD_VALUE_ID = "fieldValue";

  public static final String OUTPUT_UNIT_ID = "outputUnit";
  private static final String MILLISECONDS = "Milliseconds";
  private static final String SECONDS = "Seconds";
  private static final String MINUTES = "Minutes";

  public static final String OUTPUT_FREQUENCY = "outputFrequency";
  private static final String ON_INPUT_EVENT = "On Input Event";
  private static final String ON_STRING_VALUE_CHANGE = "When String Value Changes";

  public static final String MEASURED_TIME_FIELD_RUNTIME_NAME = "measured_time";
  public static final String FIELD_VALUE_RUNTIME_NAME = "field_value";

  private String selectedFieldName;
  private long timestamp;

  private double outputDivisor;
  private boolean useInputFrequencyForOutputFrequency;

  private String fieldValueOfLastEvent;

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.stringoperator.timer")
        .category(DataProcessorType.STRING_OPERATOR, DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.stringReq(),
                Labels.withId(FIELD_ID),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(OUTPUT_UNIT_ID),
            Options.from(MILLISECONDS, SECONDS, MINUTES))
        .requiredSingleValueSelection(Labels.withId(OUTPUT_FREQUENCY),
            Options.from(ON_INPUT_EVENT, ON_STRING_VALUE_CHANGE))
        .outputStrategy(OutputStrategies.append(
            EpProperties.numberEp(Labels.withId(MEASURED_TIME_ID),
                MEASURED_TIME_FIELD_RUNTIME_NAME, "http://schema.org/Number"),
            EpProperties.stringEp(Labels.withId(FIELD_VALUE_ID),
                FIELD_VALUE_RUNTIME_NAME, "http://schema.org/String")
        ))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    log = parameters.getGraph().getLogger(StringTimerProcessor.class);
    ProcessingElementParameterExtractor extractor = parameters.extractor();

    this.selectedFieldName = extractor.mappingPropertyValue(FIELD_ID);
    String outputUnit = extractor.selectedSingleValue(OUTPUT_UNIT_ID, String.class);
    String outputFrequency = extractor.selectedSingleValue(OUTPUT_FREQUENCY, String.class);

    this.outputDivisor = 1.0;
    if (SECONDS.equals(outputUnit)) {
      this.outputDivisor = 1000.0;
    } else if (MINUTES.equals(outputUnit)) {
      this.outputDivisor = 60000.0;
    }
    useInputFrequencyForOutputFrequency = ON_INPUT_EVENT.equals(outputFrequency);
  }

  @Override
  public void onEvent(Event event, SpOutputCollector collector) throws SpRuntimeException {
    String value = event.getFieldBySelector(selectedFieldName).getAsPrimitive().getAsString();
    long currentTimeStamp = System.currentTimeMillis();

    if (Objects.isNull(this.fieldValueOfLastEvent)) {
      this.timestamp = currentTimeStamp;
    } else {
      // Define if result event should be emitted or not
      if (this.useInputFrequencyForOutputFrequency || !this.fieldValueOfLastEvent.equals(value)) {
        long diff = currentTimeStamp - this.timestamp;
        double result = diff / this.outputDivisor;

        event.addField(MEASURED_TIME_FIELD_RUNTIME_NAME, result);
        event.addField(FIELD_VALUE_RUNTIME_NAME, this.fieldValueOfLastEvent);
        collector.collect(event);
      }

      // if state changes reset timestamp
      if (!this.fieldValueOfLastEvent.equals(value)) {
        timestamp = currentTimeStamp;
      }
    }
    this.fieldValueOfLastEvent = value;
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
