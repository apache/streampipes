/*
 * Copyright 2019 FZI Forschungszentrum Informatik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.streampipes.processors.transformation.jvm.processor.booloperator.timer;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class BooleanTimerController extends StandaloneEventProcessingDeclarer<BooleanTimerParameters> {

  public static final String FIELD_ID = "field";
  public static final String TIMER_FIELD_ID = "timerField";
  public static final String MEASURED_TIME_ID = "measuresTime";

  private static final String TRUE = "TRUE";
  private static final String FALSE = "FALSE";


  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.jvm.booloperator.timer")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.booleanReq(),
                            Labels.withId(FIELD_ID),
                            PropertyScope.NONE)
                    .build())
            .requiredSingleValueSelection(Labels.withId(TIMER_FIELD_ID), Options.from(TRUE, FALSE))
            .outputStrategy(OutputStrategies.append(
                    EpProperties.numberEp(Labels.withId(MEASURED_TIME_ID), "measured_time", "http://schema.org/Number")
            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<BooleanTimerParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String invertFieldName = extractor.mappingPropertyValue(FIELD_ID);
    String measureTrueString = extractor.selectedSingleValue(TIMER_FIELD_ID, String.class);
    boolean measureTrue = false;

    if (measureTrueString.equals(TRUE)) {
      measureTrue = true;
    }

    BooleanTimerParameters params = new BooleanTimerParameters(graph, invertFieldName, measureTrue);

    return new ConfiguredEventProcessor<>(params, BooleanTimer::new);
  }
}
