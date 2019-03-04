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
package org.streampipes.pe.examples.jvm.requirements;

import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.pe.examples.jvm.base.DummyParameters;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Datatypes;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class ListRequirementsController extends StandaloneEventProcessingDeclarer<DummyParameters> {

  @Override
  public DataProcessorDescription declareModel() {
    return ProcessingElementBuilder.create("org.streampipes.examples.requirements" +
            ".list", "List requirements specification examples", "")
            .requiredStream(StreamRequirementsBuilder.
                    create()
                    .requiredProperty(EpRequirements.listRequirement(Datatypes.Integer))
                    .requiredProperty(EpRequirements.listRequirement(Datatypes.Double))
                    .requiredProperty(EpRequirements.listRequirement(Datatypes.Boolean))
                    .requiredProperty(EpRequirements.listRequirement(Datatypes.String))
                    .build())


            .supportedProtocols(SupportedProtocols.kafka())
            .supportedFormats(SupportedFormats.jsonFormat())
            .outputStrategy(OutputStrategies.keep())

            .build();
  }

  @Override
  public ConfiguredEventProcessor<DummyParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
    return null;
  }
}
