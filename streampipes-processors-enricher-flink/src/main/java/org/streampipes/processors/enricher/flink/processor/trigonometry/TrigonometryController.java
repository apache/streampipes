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

package org.streampipes.processors.enricher.flink.processor.trigonometry;

import org.streampipes.model.DataProcessorType;
import org.streampipes.model.graph.DataProcessorDescription;
import org.streampipes.model.graph.DataProcessorInvocation;
import org.streampipes.model.schema.PropertyScope;
import org.streampipes.processors.enricher.flink.config.EnricherFlinkConfig;
import org.streampipes.sdk.builder.ProcessingElementBuilder;
import org.streampipes.sdk.builder.StreamRequirementsBuilder;
import org.streampipes.sdk.extractor.ProcessingElementParameterExtractor;
import org.streampipes.sdk.helpers.*;
import org.streampipes.vocabulary.SO;
import org.streampipes.wrapper.flink.FlinkDataProcessorDeclarer;
import org.streampipes.wrapper.flink.FlinkDataProcessorRuntime;

public class TrigonometryController extends FlinkDataProcessorDeclarer<TrigonometryParameters> {

    private final String OPERAND = "operand";
    private final String OPERATION = "operation";
    private final String RESULT_FIELD = "trigonometryResult";


    @Override
    public DataProcessorDescription declareModel() {
        return ProcessingElementBuilder.create("org.streampipes.processors.enricher.flink.processor.trigonometry",
                "Trigonometry","Performs Trigonometric function on event properties")
                .iconUrl(EnricherFlinkConfig.getIconUrl("trigonometry_icon"))
                .category(DataProcessorType.ALGORITHM)
                .requiredStream(StreamRequirementsBuilder
                        .create()
                        .requiredPropertyWithUnaryMapping(EpRequirements.numberReq(),
                                Labels.from(OPERAND, "Alpha", "Select the alpha parameter"),
                                PropertyScope.NONE)
                        .build())
                .outputStrategy(
                        OutputStrategies.append(
                                EpProperties.numberEp(Labels.empty(), RESULT_FIELD, SO.Number)))
                .requiredSingleValueSelection(Labels.from(OPERATION, "Select function", ""), Options.from("sin(a)", "cos(a)", "tan(a)" ))
                .supportedFormats(SupportedFormats.jsonFormat())
                .supportedProtocols(SupportedProtocols.kafka())
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
