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
package org.apache.streampipes.processors.siddhi.stop;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.EpProperties;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Locales;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.Arrays;

public class StreamStopController extends StandaloneEventProcessingDeclarer<StreamStopParameters> {

  private static final String Duration = "duration";
  private static final String Message = "message";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.stop")
        .withLocales(Locales.EN)
        .category(DataProcessorType.FILTER)
        .withAssets(Assets.DOCUMENTATION)
        .requiredStream(StreamRequirementsBuilder
            .create()
            .requiredProperty(EpRequirements.anyProperty())
            .build())
        .outputStrategy(OutputStrategies.fixed(
            Arrays.asList(
                EpProperties.timestampProperty("timestamp"),
                EpProperties.stringEp(Labels.withId(Message),
                    "message", "http://schema.org/text")
            )
        ))
        .requiredIntegerParameter(Labels.withId(Duration))
        .build();
  }

  @Override
  public ConfiguredEventProcessor<StreamStopParameters> onInvocation(DataProcessorInvocation graph,
                                                                     ProcessingElementParameterExtractor extractor) {

    int duration = extractor.singleValueParameter(Duration, Integer.class);

    StreamStopParameters staticParam = new StreamStopParameters(graph, duration);

    return new ConfiguredEventProcessor<>(staticParam, StreamStop::new);
  }

}
