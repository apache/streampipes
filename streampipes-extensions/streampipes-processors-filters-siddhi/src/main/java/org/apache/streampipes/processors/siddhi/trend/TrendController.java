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
package org.apache.streampipes.processors.siddhi.trend;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

import java.util.List;

public class TrendController extends StandaloneEventProcessingDeclarer<TrendParameters> {

    private static final String Mapping = "mapping";
    private static final String Increase = "increase";
    private static final String Operation = "operation";
    private static final String Duration = "duration";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.siddhi.increase")
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
                .build();
    }

    @Override
    public ConfiguredEventProcessor<TrendParameters> onInvocation(DataProcessorInvocation
                                                                             invocationGraph, ProcessingElementParameterExtractor extractor) {
        String operation = extractor.selectedSingleValue( Operation, String.class);
        int increase = extractor.singleValueParameter(Increase, Integer.class);
        int duration = extractor.singleValueParameter(Duration, Integer.class);
        String mapping = extractor.mappingPropertyValue(Mapping);
        List<String> outputFieldSelectors = extractor.outputKeySelectors();
        TrendParameters params = new TrendParameters(invocationGraph,
                getOperation(operation),
                increase,
                duration,
                mapping,
                outputFieldSelectors);

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
