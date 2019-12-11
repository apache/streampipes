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
package org.apache.streampipes.pe.examples.jvm.staticproperty;

import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.pe.examples.jvm.base.DummyEngine;
import org.apache.streampipes.pe.examples.jvm.base.DummyParameters;
import org.apache.streampipes.sdk.StaticProperties;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.Alternatives;
import org.apache.streampipes.sdk.helpers.EpRequirements;
import org.apache.streampipes.sdk.helpers.Labels;
import org.apache.streampipes.sdk.helpers.Options;
import org.apache.streampipes.sdk.helpers.OutputStrategies;
import org.apache.streampipes.sdk.helpers.SupportedFormats;
import org.apache.streampipes.sdk.helpers.SupportedProtocols;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class StaticPropertyAlternativesController extends
        StandaloneEventProcessingDeclarer<DummyParameters> {

  private static final String KafkaHost = "kafka-host";
  private static final String KafkaPort = "kafka-port";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.apache.streampipes.examples.staticproperty" +
            ".alternatives", "Static property alternatives example", "")
            .requiredStream(StreamRequirementsBuilder.
                    create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.keep())
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .requiredTextParameter(Labels.from(KafkaHost, "Kafka Host", ""))

            .requiredAlternatives(Labels.from("window", "Window", ""),
                    Alternatives.from(Labels.from("count", "Count Window", ""),
                            StaticProperties.integerFreeTextProperty(Labels.from("count-window-size",
                                    "Count Window Size", ""))),
                    Alternatives.from(Labels.from("time", "Time Window", ""),
                            StaticProperties.group(Labels.from("group", "", ""),
                                    StaticProperties.integerFreeTextProperty(Labels.from("time" +
                                            "-window-size", "Time Window Size", "")),
                                    StaticProperties.singleValueSelection(Labels.from("time" +
                                            "-window-unit", "Time Unit", ""),
                                            Options.from("Seconds", "Minutes", "Hours")))))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<DummyParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String selectedAlternative = extractor.selectedAlternativeInternalId("window");
    if (selectedAlternative.equals("time")) {
      Integer timeWindowSize = extractor.singleValueParameter("time-window-size", Integer.class);
    }

    return new ConfiguredEventProcessor<>(new DummyParameters(graph), DummyEngine::new);
  }

}
