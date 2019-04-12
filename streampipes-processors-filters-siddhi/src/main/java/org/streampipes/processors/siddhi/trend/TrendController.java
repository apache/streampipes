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
import org.streampipes.processors.siddhi.config.FilterSiddhiConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class TrendController extends StandaloneEventProcessingDeclarer<TrendParameters> {


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("increase", "Trend",
                "Detects the increase of a numerical field over a customizable time window. Example: A temperature value increases by 10 percent within 5 minutes.")

                .category(DataProcessorType.PATTERN_DETECT)
                .iconUrl(FilterSiddhiConfig.getIconUrl("increase-icon"))
                .requiredStream(StreamRequirementsBuilder.create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(), Labels.from
                                ("mapping", "Value to observe", "Specifies the value that should be " +
                                        "monitored."), PropertyScope.MEASUREMENT_PROPERTY)
                        .build())
                .requiredSingleValueSelection(Labels.from("operation", "Increase/Decrease",
                        "Specifies the type of operation the processor should perform."), Options
                        .from("Increase", "Decrease"))
                .requiredIntegerParameter(Labels.from("increase", "Percentage of Increase/Decrease",
                        "Specifies the increase in percent (e.g., 100 indicates an increase by 100 " +
                                "percent within the specified time window."), 0, 500, 1)
                .requiredIntegerParameter(Labels.from("duration", "Time Window Length (Seconds)", "Specifies the size of the time window in seconds."))
                .outputStrategy(OutputStrategies.custom())
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka(), SupportedProtocols.jms())
                .build();
    }

    @Override
    public ConfiguredEventProcessor<TrendParameters> onInvocation(DataProcessorInvocation
                                                                             invocationGraph, ProcessingElementParameterExtractor extractor) {
        String operation = extractor.selectedSingleValue( "operation", String.class);
        int increase = extractor.singleValueParameter("increase", Integer.class);
        int duration = extractor.singleValueParameter("duration", Integer.class);
        String mapping = extractor.mappingPropertyValue("mapping");
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
