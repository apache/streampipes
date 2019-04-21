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

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpProperties;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Locales;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.utils.Assets;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class PalletTransportDetectionController extends StandaloneEventProcessingDeclarer<PalletTransportDetectionParameters> {

  public static final String PALLET = "pallet";

  public static final String MS = "Milliseconds";
  public static final String SECONDS = "Seconds";
  public static final String MINUTES = "Minutes";
  public static final String HOURS = "Hours";

  public static final String FIRST_STREAM_KEEP_FIELD_ID = "firstStreamItems";
  public static final String SECOND_STREAM_KEEP_FIELD_ID = "secondStreamItems";

  public static final String FIRST_LOCATION_PALLET_FIELD_ID = "firstLocation";
  public static final String SECOND_LOCATION_PALLET_FIELD_ID = "secondLocation";
  public static final String FIRST_TS_FIELD_ID = "startTs";
  public static final String END_TS_FIELD_ID = "endTs";
  public static final String UNIT_FIELD_ID = "unit_field"; // hours,

  @Override
  public DataProcessorDescription declareModel() {
    //TODO: Add output strategy (check dashboard for how-to)?
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.processor.pallettransportdetection")
            .category(DataProcessorType.TRANSFORM)
            .withAssets(Assets.DOCUMENTATION)
            .withLocales(Locales.EN)
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                    Labels.withId(FIRST_LOCATION_PALLET_FIELD_ID),
                    PropertyScope.NONE)
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.withId(FIRST_TS_FIELD_ID ),
                    PropertyScope.NONE)
                .build())
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                    Labels.withId(SECOND_LOCATION_PALLET_FIELD_ID),
                    PropertyScope.NONE)
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.withId(END_TS_FIELD_ID ),
                    PropertyScope.NONE)
                .build())
            .requiredSingleValueSelection(Labels.withId(UNIT_FIELD_ID),
                Options.from(MS, SECONDS, MINUTES, HOURS))
            .outputStrategy(OutputStrategies.fixed(EpProperties.timestampProperty("startTime"),
                    EpProperties.timestampProperty("endTime")))
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.jms(), SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<PalletTransportDetectionParameters>
  onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String palletField1 = extractor.mappingPropertyValue(FIRST_LOCATION_PALLET_FIELD_ID);
    String palletField2 = extractor.mappingPropertyValue(SECOND_LOCATION_PALLET_FIELD_ID);

    String startTs = extractor.mappingPropertyValue(FIRST_TS_FIELD_ID);
    String endTs = extractor.mappingPropertyValue(END_TS_FIELD_ID);

    List<String> outputKeySelectors = extractor.outputKeySelectors();

    PalletTransportDetectionParameters staticParam = new PalletTransportDetectionParameters(
            graph, startTs, endTs, palletField1, palletField2);

    return new ConfiguredEventProcessor<>(staticParam, PalletTransportDetection::new);
  }

}
