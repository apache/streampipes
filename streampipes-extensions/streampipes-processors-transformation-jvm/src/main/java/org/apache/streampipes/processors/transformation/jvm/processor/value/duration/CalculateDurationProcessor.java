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

package org.apache.streampipes.processors.transformation.jvm.processor.value.duration;

import org.apache.streampipes.commons.exceptions.SpRuntimeException;
import org.apache.streampipes.extensions.api.pe.context.EventProcessorRuntimeContext;
import org.apache.streampipes.extensions.api.pe.routing.SpOutputCollector;
import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.runtime.Event;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.params.compat.ProcessorParams;
import org.apache.streampipes.wrapper.standalone.StreamPipesDataProcessor;

public class CalculateDurationProcessor extends StreamPipesDataProcessor {

  public static final String START_TS_FIELD_ID = "start-ts";
  public static final String END_TS_FIELD_ID = "end-ts";
  public static final String DURATION_FIELD_NAME = "duration";
  public static final String UNIT_FIELD_ID = "unit-field"; // hours,

  public static final String MS = "Milliseconds";
  public static final String SECONDS = "Seconds";
  public static final String MINUTES = "Minutes";
  public static final String HOURS = "Hours";

  private String startTs;
  private String endTs;
  private String unit;


  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.duration-value")
        .category(DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                Labels.withId(START_TS_FIELD_ID),
                PropertyScope.NONE)
            .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                Labels.withId(END_TS_FIELD_ID),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(UNIT_FIELD_ID),
            Options.from(MS, SECONDS, MINUTES, HOURS))
        .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.empty(), DURATION_FIELD_NAME,
            SO.NUMBER)))
        .build();
  }

  @Override
  public void onInvocation(ProcessorParams parameters,
                           SpOutputCollector spOutputCollector,
                           EventProcessorRuntimeContext runtimeContext) throws SpRuntimeException {
    var extractor = parameters.extractor();
    startTs = extractor.mappingPropertyValue(START_TS_FIELD_ID);
    endTs = extractor.mappingPropertyValue(END_TS_FIELD_ID);
    unit = extractor.selectedSingleValue(UNIT_FIELD_ID, String.class);
  }

  @Override
  public void onEvent(Event inputEvent, SpOutputCollector collector) throws SpRuntimeException {
    Long start = inputEvent.getFieldBySelector(startTs).getAsPrimitive().getAsLong();
    Long end = inputEvent.getFieldBySelector(endTs).getAsPrimitive().getAsLong();
    Long duration = end - start;

    if (unit.equals(CalculateDurationProcessor.MS)) {
      inputEvent.addField(DURATION_FIELD_NAME, duration);
    } else if (unit.equals(CalculateDurationProcessor.SECONDS)) {
      inputEvent.addField(DURATION_FIELD_NAME, (duration + 500) / 1000);
    } else if (unit.equals(CalculateDurationProcessor.MINUTES)) {
      inputEvent.addField(DURATION_FIELD_NAME, (duration + 30000) / 60000);
    } else {
      // Hours
      inputEvent.addField(DURATION_FIELD_NAME, (duration + 1800000) / 3600000);
    }
    collector.collect(inputEvent);
  }

  @Override
  public void onDetach() throws SpRuntimeException {

  }
}
