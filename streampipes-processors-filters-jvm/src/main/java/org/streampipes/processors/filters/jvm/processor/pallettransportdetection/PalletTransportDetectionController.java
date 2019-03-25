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

import java.util.ArrayList;
import java.util.List;
import org.streampipes.commons.exceptions.SpRuntimeException;
import org.streampipes.container.api.ResolvesContainerProvidedOutputStrategy;
import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.EventProperty;
import org.streampipes.model.schema.EventSchema;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.filters.jvm.config.FiltersJvmConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.EpRequirements;
import org.streampipes.sdk.helpers.Labels;
import org.streampipes.sdk.helpers.Options;
import org.streampipes.sdk.helpers.OutputStrategies;
import org.streampipes.sdk.helpers.SupportedFormats;
import org.streampipes.sdk.helpers.SupportedProtocols;
import org.streampipes.sdk.helpers.TransformOperations;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class PalletTransportDetectionController extends StandaloneEventProcessingDeclarer<PalletTransportDetectionParameters>
    /*implements ResolvesContainerProvidedOutputStrategy<DataProcessorInvocation, ProcessingElementParameterExtractor>*/ {

  public static final String PALLET = "pallet";

  public static final String MS = "Milliseconds";
  public static final String SECONDS = "Seconds";
  public static final String MINUTES = "Minutes";
  public static final String HOURS = "Hours";

  public static final String FIRST_LOCATION_PALLET_FIELD_ID = "firstLocation";
  public static final String SECOND_LOCATION_PALLET_FIELD_ID = "secondLocation";
  public static final String FIRST_TS_FIELD_ID = "startTs";
  public static final String END_TS_FIELD_ID = "endTs";
  public static final String UNIT_FIELD_ID = "unit_field"; // hours,

  @Override
  public DataProcessorDescription declareModel() {
    //TODO: Add output strategy (check dashboard for how-to)?
    return ProcessingElementBuilder.create("org.streampipes.processors.filters.jvm.mergerorg.streampipes.processors.filters.jvm.processor.mergestartandend",
            "PalletTransportDetection", "Merges two event streams if there is a start and an end")
            .category(DataProcessorType.TRANSFORM)
            .iconUrl(FiltersJvmConfig.getIconUrl("projection"))
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                    Labels.from(FIRST_LOCATION_PALLET_FIELD_ID, "Pallet detection first",
                        "String which says \"" + PALLET + "\" if the pallet is"
                            + "on the first location. Otherwise it is not."),
                    PropertyScope.NONE)
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(FIRST_TS_FIELD_ID , "Start timestamp",
                        "Timestamp of the first stream"),
                    PropertyScope.NONE)
                .build())
            .requiredStream(StreamRequirementsBuilder
                .create()
                .requiredPropertyWithUnaryMapping(EpRequirements.stringReq(),
                    Labels.from(SECOND_LOCATION_PALLET_FIELD_ID, "Pallet detection first",
                        "String which says \"" + PALLET + "\" if the pallet is"
                            + "on the first location. Otherwise it is not."),
                    PropertyScope.NONE)
                .requiredPropertyWithUnaryMapping(EpRequirements.timestampReq(),
                    Labels.from(END_TS_FIELD_ID , "End timestamp",
                        "Timestamp of the second stream"),
                    PropertyScope.NONE)
                .build())
            .requiredSingleValueSelection(Labels.from(UNIT_FIELD_ID,
                "Timeunit",
                "The unit in which the duration is calculated"),
                Options.from(MS, SECONDS, MINUTES, HOURS))
            .outputStrategy(OutputStrategies.custom(true))
//            .outputStrategy(OutputStrategies.transform(TransformOperations
//                .dynamicRuntimeNameTransformation(FIRST_TS_FIELD_ID, "StartTs")))
//            .outputStrategy(OutputStrategies.customTransformation())
            .supportedFormats(SupportedFormats.jsonFormat())
            .supportedProtocols(SupportedProtocols.jms(), SupportedProtocols.kafka())
            .build();
  }

  @Override
  public ConfiguredEventProcessor<PalletTransportDetectionParameters>
  onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

    String startTs = extractor.mappingPropertyValue(FIRST_LOCATION_PALLET_FIELD_ID);
    String endTs = extractor.mappingPropertyValue(SECOND_LOCATION_PALLET_FIELD_ID);
    List<String> outputKeySelectors = extractor.outputKeySelectors();

    PalletTransportDetectionParameters staticParam = new PalletTransportDetectionParameters(
            graph, outputKeySelectors, startTs, endTs);

    return new ConfiguredEventProcessor<>(staticParam, PalletTransportDetection::new);
  }

/*  @Override
  public EventSchema resolveOutputStrategy(DataProcessorInvocation processingElement,
      ProcessingElementParameterExtractor extractor) throws SpRuntimeException {
    String arrayFieldSelector = extractor.mappingPropertyValue(ARRAY_FIELD_ID);
    List<String> keepPropertySelectors = extractor.mappingPropertyValues(KEEP_PROPERTIES_ID);

    List<EventProperty> outProperties = new ArrayList<>();
    EventProperty arrayProperty = extractor.getEventPropertyBySelector(arrayFieldSelector);
    List<EventProperty> keepProperties = extractor.getEventPropertiesBySelector
        (keepPropertySelectors);
    outProperties.add(arrayProperty);
    outProperties.addAll(keepProperties);

    return new EventSchema(outProperties);
  }*/
}
