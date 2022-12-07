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

package org.apache.streampipes.processors.transformation.jvm.processor.booloperator.timer;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
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
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class BooleanTimerController extends StandaloneEventProcessingDeclarer<BooleanTimerParameters> {

  public static final String FIELD_ID = "field";
  public static final String TIMER_FIELD_ID = "timerField";
  public static final String MEASURED_TIME_ID = "measuresTime";

  private static final String TRUE = "TRUE";
  private static final String FALSE = "FALSE";

  public static final String OUTPUT_UNIT_ID = "outputUnit";
  private static final String MILLISECONDS = "Milliseconds";
  private static final String SECONDS = "Seconds";
  private static final String MINUTES = "Minutes";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.transformation.jvm.booloperator.timer")
        .category(DataProcessorType.BOOLEAN_OPERATOR, DataProcessorType.TIME)
        .withLocales(Locales.EN)
        .withAssets(Assets.DOCUMENTATION, Assets.ICON)
        .requiredStream(StreamRequirementsBuilder.create()
            .requiredPropertyWithUnaryMapping(
                EpRequirements.booleanReq(),
                Labels.withId(FIELD_ID),
                PropertyScope.NONE)
            .build())
        .requiredSingleValueSelection(Labels.withId(TIMER_FIELD_ID), Options.from(TRUE, FALSE))
        .requiredSingleValueSelection(Labels.withId(OUTPUT_UNIT_ID), Options.from(MILLISECONDS, SECONDS, MINUTES))
        .outputStrategy(OutputStrategies.append(
            EpProperties.numberEp(Labels.withId(MEASURED_TIME_ID),
                "measured_time",
                "http://schema.org/Number")
        ))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<BooleanTimerParameters> onInvocation(
      DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String invertFieldName = extractor.mappingPropertyValue(FIELD_ID);
    String measureTrueString = extractor.selectedSingleValue(TIMER_FIELD_ID, String.class);
    String outputUnit = extractor.selectedSingleValue(OUTPUT_UNIT_ID, String.class);

    boolean measureTrue = false;

    if (measureTrueString.equals(TRUE)) {
      measureTrue = true;
    }

    double outputDivisor = 1.0;
    if (outputUnit.equals(SECONDS)) {
      outputDivisor = 1000.0;
    } else if (outputUnit.equals(MINUTES)) {
      outputDivisor = 60000.0;
    }

    BooleanTimerParameters params = new BooleanTimerParameters(graph, invertFieldName, measureTrue, outputDivisor);

    return new ConfiguredEventProcessor<>(params, BooleanTimer::new);
  }
}
