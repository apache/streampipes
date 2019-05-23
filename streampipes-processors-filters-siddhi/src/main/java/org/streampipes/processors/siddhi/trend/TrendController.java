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
package org.streampipes.processors.siddhi.trend;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
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

public class TrendController extends StandaloneEventProcessingDeclarer<TrendParameters> {

    private static final String Mapping = "mapping";
    private static final String Increase = "increase";
    private static final String Operation = "operation";
    private static final String Duration = "duration";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.streampipes.processors.siddhi.increase")
                .withLocales(Locales.EN)
                .category(DataProcessorType.PATTERN_DETECT)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .requiredStream(StreamRequirementsBuilder.create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(), Labels.withId
                                (Mapping), PropertyScope.MEASUREMENT_PROPERTY)
                        .build())
                .requiredSingleValueSelection(Labels.withId(Operation), Options
                        .from("Increase", "Decrease"))
                .requiredIntegerParameter(Labels.withId(Increase), 0, 500, 1)
                .requiredIntegerParameter(Labels.withId(Duration))
                .outputStrategy(OutputStrategies.custom())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
                .build();
    }

    @Override
    public ConfiguredEventProcessor<TrendParameters> onInvocation(DataProcessorInvocation
                                                                             invocationGraph, ProcessingElementParameterExtractor extractor) {
        String operation = extractor.selectedSingleValue( Operation, String.class);
        int increase = extractor.singleValueParameter(Increase, Integer.class);
        int duration = extractor.singleValueParameter(Duration, Integer.class);
        String mapping = extractor.mappingPropertyValue(Mapping);
        TrendParameters params = new TrendParameters(invocationGraph, getOperation(operation), increase, duration, mapping);

        return new ConfiguredEventProcessor<>(params, Trend::new);
    }

    private TrendOperator getOperation(String operation) {
        if (operation.equals("Increase")) {
            return TrendOperator.INCREASE;
        } else {
            return TrendOperator.DECREASE;
        }
    }


}
