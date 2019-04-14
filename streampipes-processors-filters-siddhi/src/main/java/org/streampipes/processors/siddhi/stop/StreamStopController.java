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
package org.streampipes.processors.siddhi.stop;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.Arrays;

public class StreamStopController extends StandaloneEventProcessingDeclarer<StreamStopParameters> {

  private static final String DURATION = "duration";


  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.processors.siddhi.stop", "Stream Stop Detection", "Triggers an event when the input data stream stops sending events")
            .category(DataProcessorType.FILTER)
            .iconUrl("Numerical_Filter_Icon_HQ")
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.fixed(
                    Arrays.asList(
                            EpProperties.timestampProperty("timestamp"),
                            EpProperties.stringEp(new Label("message", "Message", "Message that stream stopped"), "message", "kj")
                    )

            ))
            .requiredIntegerParameter(Labels.from(DURATION, "Time Window Length (Seconds)", "Specifies the size of the time window in seconds."))
            .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
            .supportedFormats(SupportedFormats.jsonFormat())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<StreamStopParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    int duration = extractor.singleValueParameter(DURATION, Integer.class);

    StreamStopParameters staticParam = new StreamStopParameters(graph, duration);

    return new ConfiguredEventProcessor<>(staticParam, StreamStop::new);
  }

}
