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
package org.streampipes.processors.filters.jvm.processor.mergestartandend;

import java.util.List;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.filters.jvm.config.FiltersJvmConfig;
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

public class MergeStartAndEndController extends StandaloneEventProcessingDeclarer<MergeStartAndEndParameters> {

  public static final String START_TS_FIELD_ID = "start_ts";
  public static final String END_TS_FIELD_ID = "end_ts";

  @Override
  public DataProcessorDescription declareModel() {
    //TODO: Add output strategy (check dashboard for how-to)?
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.merger",
            "MergeStartAndEnd", "Merges two event streams if there is a start and an end")
            .category(DataProcessorType.TRANSFORM)
            .iconUrl(FiltersJvmConfig.getIconUrl("projection"))
            .requiredStream(StreamRequirementsBuilder
                    .create()
                    .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                        Labels.from(START_TS_FIELD_ID, "Startevent timestamp",
                            "The timestamp of the startevent"),
                        PropertyScope.NONE)
                    .build())
            .requiredStream(StreamRequirementsBuilder
                    .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(END_TS_FIELD_ID, "Endevent timestamp",
                        "The timestamp of the endevent"),
                    PropertyScope.NONE)
                .build())
            .outputStrategy(OutputStrategies.custom(true))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.jms(), SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<MergeStartAndEndParameters>
  onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    List<String> outputKeySelectors = extractor.outputKeySelectors();

    String timestampStart = extractor.mappingPropertyValue(START_TS_FIELD_ID);
    String timestampEnd = extractor.mappingPropertyValue(END_TS_FIELD_ID);


    MergeStartAndEndParameters staticParam = new MergeStartAndEndParameters(
            graph, outputKeySelectors, timestampStart, timestampEnd);

    return new ConfiguredEventProcessor<>(staticParam, MergeStartAndEnd::new);
  }
}
