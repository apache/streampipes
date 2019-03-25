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
package org.streampipes.processors.filters.jvm.processor.pallettransportdetection;

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

public class PalletTransportDetectionController extends StandaloneEventProcessingDeclarer<PalletTransportDetectionParameters> {

  public static final String START_TS_FIELD_ID = "start_ts";
  public static final String END_TS_FIELD_ID = "end_ts";

  @Override
  public DataProcessorDescription declareModel() {
    //TODO: Add output strategy (check dashboard for how-to)?
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.mergerorg.streampipes.processors.filters.jvm.processor.mergestartandend",
            "PalletTransportDetection", "Merges two event streams if there is a start and an end")
            .category(DataProcessorType.TRANSFORM)
            .iconUrl(FiltersJvmConfig.getIconUrl("projection"))
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(START_TS_FIELD_ID, "Start timestamp",
                        "The timestamp of the start event"),
                    PropertyScope.NONE)
                .build())
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(START_TS_FIELD_ID, "Start timestamp",
                        "The timestamp of the start event"),
                    PropertyScope.NONE)
                .build())
            .outputStrategy(OutputStrategies.custom(true))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.jms(), SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<PalletTransportDetectionParameters>
  onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String startTs = extractor.mappingPropertyValue(START_TS_FIELD_ID);
    String endTs = extractor.mappingPropertyValue(END_TS_FIELD_ID);
    List<String> outputKeySelectors = extractor.outputKeySelectors();

    PalletTransportDetectionParameters staticParam = new PalletTransportDetectionParameters(
            graph, outputKeySelectors, startTs, endTs);

    return new ConfiguredEventProcessor<>(staticParam, PalletTransportDetection::new);
  }
}
