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

package org.streampipes.processors.transformation.jvm.processor.booloperator.timekeeping;

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

public class BooleanTimekeepingController extends StandaloneEventProcessingDeclarer<BooleanTimekeepingParameters> {
  // Measures time and returns count

  public static final String LEFT_FIELD_ID = "left-field";
  public static final String RIGHT_FIELD_ID = "right-field";

  public static final String TIME_FIELD_ID = "time-field";
  public static final String COUNT_FIELD_ID = "count-field";

  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.transformation.jvm.booloperator.timekeeping")
            .withLocales(Locales.EN)
            .withAssets(Assets.DOCUMENTATION)
            .requiredStream(StreamRequirementsBuilder.create()
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.booleanReq(),
                            Labels.withId(LEFT_FIELD_ID),
                            PropertyScope.NONE)
                    .requiredPropertyWithUnaryMapping(
                            EpRequirements.booleanReq(),
                            Labels.withId(RIGHT_FIELD_ID),
                            PropertyScope.NONE)
                    .build())
            .outputStrategy(OutputStrategies.append(
                    EpProperties.numberEp(Labels.withId(TIME_FIELD_ID), "measured_time", "http://schema.org/Number"),
                    EpProperties.numberEp(Labels.withId(COUNT_FIELD_ID), "counter", "http://schema.org/Number")

            ))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<BooleanTimekeepingParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String leftFieldName = extractor.mappingPropertyValue(LEFT_FIELD_ID);
    String rightFieldName = extractor.mappingPropertyValue(LEFT_FIELD_ID);
    BooleanTimekeepingParameters params = new BooleanTimekeepingParameters(graph, leftFieldName, rightFieldName);

    return new ConfiguredEventProcessor<>(params, BooleanTimekeeping::new);
  }
}
