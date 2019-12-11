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

package org.apache.streampipes.processors.enricher.flink.processor.trigonometry;

import org.apache.streampipes.model.DataProcessorType;
import org.apache.streampipes.model.graph.DataProcessorDescription;
import org.apache.streampipes.model.graph.DataProcessorInvocation;
import org.apache.streampipes.model.schema.PropertyScope;
import org.apache.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.apache.streampipes.sdk.builder.ProcessingElementBuilder;
import org.apache.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.apache.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.apache.streampipes.sdk.helpers.*;
import org.apache.streampipes.sdk.utils.Assets;
import org.apache.streampipes.vocabulary.SO;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.apache.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class TrigonometryController extends FlinkDataProcessorDeclarer<TrigonometryParameters> {

    private final String OPERAND = "operand";
    private final String OPERATION = "operation";
    private final String RESULT_FIELD = "trigonometryResult";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.apache.streampipes.processors.enricher.flink.processor.trigonometry")
                .withAssets(Assets.DOCUMENTATION, Assets.ICON)
                .withLocales(Locales.EN)
                .category(DataProcessorType.ALGORITHM)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                                Labels.withId(OPERAND),
                                PropertyScope.NONE)
                        .build())
                .outputStrategy(
                        OutputStrategies.append(
                                EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.Number)))
                .requiredSingleValueSelection(Labels.withId(OPERATION),
                        Options.from("sin(a)", "cos(a)", "tan(a)" ))
                .build();
    }

    @Override
    public FlinkDataProcessorRuntime<TrigonometryParameters> getRuntime(DataProcessorInvocation graph, ProcessingElementParameterExtractor extractor) {
        String operand = extractor.mappingPropertyValue(OPERAND);
        String operation = extractor.selectedSingleValue(OPERATION, String.class);

        Operation trigonometryFunction = null;
        switch (operation) {
            case "sin(a)": trigonometryFunction = Operation.SIN;
                break;
            case "cos(a)": trigonometryFunction = Operation.COS;
                break;
            case "tan(a)": trigonometryFunction = Operation.TAN;

        }


        TrigonometryParameters parameters = new TrigonometryParameters(graph, operand, trigonometryFunction, RESULT_FIELD);

        return new TrigonometryProgram(parameters, EnricherFlinkConfig.INSTANCE.getDebug());
    }
}
