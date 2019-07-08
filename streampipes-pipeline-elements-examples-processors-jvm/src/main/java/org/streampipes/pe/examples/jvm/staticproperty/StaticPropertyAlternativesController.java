/*
Copyright 2019 FZI Forschungszentrum Informatik

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package org.streampipes.pe.examples.jvm.staticproperty;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.examples.jvm.base.DummyEngine;
import org.streampipes.pe.examples.jvm.base.DummyParameters;
import org.streampipes.sdk.StaticProperties;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.Alternatives;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class StaticPropertyAlternativesController extends
        StandaloneEventProcessingDeclarer<DummyParameters> {

  private static final String KafkaHost = "kafka-host";
  private static final String KafkaPort = "kafka-port";

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.examples.staticproperty" +
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
                            StaticProperties.integerFreeTextProperty(Labels.from("time" +
                                    "-window-size", "Time Window Size", ""))))
            .build();
  }

  @Override
  public ConfiguredEventProcessor<DummyParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

System.out.println("incov");

    return new ConfiguredEventProcessor<>(new DummyParameters(graph), DummyEngine::new);
  }

}
