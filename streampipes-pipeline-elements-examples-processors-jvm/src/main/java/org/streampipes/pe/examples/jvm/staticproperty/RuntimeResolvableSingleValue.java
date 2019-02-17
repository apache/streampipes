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

import org.streampipes.container.api.ResolvesContainerProvidedOptions;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.runtime.RuntimeOptions;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.pe.examples.jvm.base.DummyEngine;
import org.streampipes.pe.examples.jvm.base.DummyParameters;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.Arrays;
import java.util.List;

public class RuntimeResolvableSingleValue extends
        StandaloneEventProcessingDeclarer<DummyParameters> implements ResolvesContainerProvidedOptions {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.examples.staticproperty" +
            ".runtimeresolvablesingle", "Runtime-resolvable single value example", "")
            .requiredStream(StreamRequirementsBuilder.
                    create()
                    .requiredProperty(EpRequirements.anyProperty())
                    .build())
            .outputStrategy(OutputStrategies.keep())
            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())

            // create a single value selection parameter that is resolved at runtime
            .requiredSingleValueSelectionFromContainer(Labels.from("id", "Example Name", "Example " +
                    "Description"))

            .build();
  }

  @Override
  public ConfiguredEventProcessor<DummyParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    // Extract the text parameter value
    String selectedSingleValue = extractor.selectedSingleValue("id", String.class);

    // now the text parameter would be added to a parameter class (omitted for this example)

    return new ConfiguredEventProcessor<>(new DummyParameters(graph), DummyEngine::new);
  }

  @Override
  public List<RuntimeOptions> resolveOptions(String requestId, EventProperty linkedEventProperty) {
    return Arrays.asList(new RuntimeOptions("I was defined at runtime", ""));
  }
}
