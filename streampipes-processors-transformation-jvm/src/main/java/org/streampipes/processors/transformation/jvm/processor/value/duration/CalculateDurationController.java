/*
 * Copyright 2018 FZI Forschungszentrum Informatik
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

package org.streampipes.processors.transformation.jvm.processor.value.duration;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.transformation.jvm.config.TransformationJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class CalculateDurationController extends StandaloneEventProcessingDeclarer<CalculateDurationParameters> {

  public static final String START_TS_FIELD_ID = "start_ts";
  public static final String END_TS_FIELD_ID = "end_ts";
  public static final String DURATION_FIELD_NAME = "duration";
  public static final String UNIT_FIELD_ID = "unit_field"; // hours,

  public static final String MS = "Milliseconds";
  public static final String SECONDS = "Seconds";
  public static final String MINUTES = "Minutes";
  public static final String HOURS = "Hours";


  //TODO: Change Icon
  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors" +
            ".transformation.jvm.duration-value", "Calculate Duration", "This processor calculates " +
            "the duration for a given stream with a start timestamp and an end timestamp.")
            .iconUrl(TransformationJvmConfig.getIconUrl("splitarray"))
            .requiredStream(StreamRequirementsBuilder.create()
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(START_TS_FIELD_ID, "Start timestamp", "The " +
                        "timestamp of the start event"),
                    PropertyScope.NONE)
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(END_TS_FIELD_ID, "End timestamp", "The " +
                        "timestamp of the end event"),
                    PropertyScope.NONE)
                    .build())
            .requiredSingleValueSelection(Labels.from(UNIT_FIELD_ID, "Timeunit", "Test"),
                Options.from(MS, SECONDS, MINUTES, HOURS))
            .outputStrategy(OutputStrategies.append(EpProperties.doubleEp(Labels.empty(), DURATION_FIELD_NAME,
                SO.Number)))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<CalculateDurationParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String start_ts = extractor.mappingPropertyValue(START_TS_FIELD_ID);
    String end_ts = extractor.mappingPropertyValue(END_TS_FIELD_ID);
    String unit = extractor.selectedSingleValue(UNIT_FIELD_ID, String.class);

    CalculateDurationParameters params = new CalculateDurationParameters(graph, start_ts, end_ts, unit, DURATION_FIELD_NAME);
    return new ConfiguredEventProcessor<>(params, CalculateDuration::new);
  }
}
