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

package org.apache.streampipes.processors.changedetection.jvm.cusum;

import java.util.Arrays;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.standalone.ConfiguredEventProcessor;
import org.apache.streampipes.wrapper.standalone.declarer.StandaloneEventProcessingDeclarer;

public class CusumController extends StandaloneEventProcessingDeclarer<CusumParameters> {

    private static final String NUMBER_MAPPING = "number-mapping";
    private static final String PARAM_K = "param-k";
    private static final String PARAM_H = "param-h";

    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.changedetection.jvm.cusum")
                .category(DataProcessorType.FILTER)
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                                Labels.withId(NUMBER_MAPPING),
                                PropertyScope.NONE).build())
                .requiredFloatParameter(Labels.withId(PARAM_K), 0.0f, 0.0f, 100.0f, 0.01f)
                .requiredFloatParameter(Labels.withId(PARAM_H), 0.0f, 0.0f, 100.0f, 0.01f)
                .outputStrategy(
                        OutputStrategies.append(
                                Arrays.asList(
                                        EpProperties.numberEp(Labels.empty(), CusumEventFields.VAL_LOW, SO.Number),
                                        EpProperties.numberEp(Labels.empty(), CusumEventFields.VAL_HIGH, SO.Number),
                                        EpProperties.booleanEp(Labels.empty(), CusumEventFields.DECISION_LOW, SO.Boolean),
                                        EpProperties.booleanEp(Labels.empty(), CusumEventFields.DECISION_HIGH, SO.Boolean)
                                )
                        ))
                .build();
    }

    @Override
    public ConfiguredEventProcessor<CusumParameters> onInvocation(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {

        String selectedNumberField = extractor.mappingPropertyValue(NUMBER_MAPPING);
        Double paramK = extractor.singleValueParameter(PARAM_K, Double.class);
        Double paramH = extractor.singleValueParameter(PARAM_H, Double.class);

        CusumParameters params = new CusumParameters(graph, selectedNumberField, paramK, paramH);

        return new ConfiguredEventProcessor<>(params, Cusum::new);
    }
}
